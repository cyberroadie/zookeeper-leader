package net.spike.zookeeper;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

import static org.apache.zookeeper.CreateMode.PERSISTENT;

/**
 * User: cyberroadie
 * Date: 21/11/2011
 */
public class NodeMonitor implements Watcher, AsyncCallback.ChildrenCallback {

    final Logger logger = LoggerFactory.getLogger(NodeMonitor.class);

    private ZooKeeper zooKeeper;
    private long sequenceNumber;
    private static final String ROOT = "/ELECTION";
    private NodeMonitorListener listener = null;
    private String znode;

    /**
     * Start method to give the listener a change to set itself so
     * as to receive all messages
     * @throws IOException
     */
    public void start() throws IOException {
        this.zooKeeper = new ZooKeeper("localhost:2181", 3000, this);
    }

    public void setZooKeeper(ZooKeeper zooKeeper) {
        this.zooKeeper = zooKeeper;
    }

    public void setListener(NodeMonitorListener monitor) {
        this.listener = monitor;
        this.znode = ROOT + "/" + listener.getProcessName();
    }

    @Override
    public void processResult(int rc, String path, Object o, List<String> children) {
        logger.debug(listener.getProcessName() + "Children callback: " +
                listener.getProcessName() + ":" + children.toString());
        if(rc == KeeperException.Code.Ok) {
            if(getLowestNumber(children) == sequenceNumber)
                listener.startSpeaking();
            else
                listener.stopSpeaking();
        } else {
            listener.stopSpeaking();
        }
    }

    public long getLowestNumber(List<String> children) {
        long lowest = sequenceNumber;
        for (String child : children) {
            long current = parseSequenceNumber(child);
            if(current < lowest) lowest = current;
        }
        return lowest;
    }

    public long parseSequenceNumber(String znode) {
        return Integer.parseInt(znode.substring(znode.lastIndexOf(".") + 1));
    }

    public interface NodeMonitorListener {
        public void startSpeaking();

        public void stopSpeaking();

        public String getProcessName();

    }

    @Override
    public void process(WatchedEvent event) {
        logger.debug(listener.getProcessName() + "Processing event for znode " + event.getPath());
        if (event.getType() == Watcher.Event.EventType.None) {
            processNoneEvent(event);
        } else {
            String path = event.getPath();
            if (path != null && path.equals(ROOT)) {
                logger.debug(listener.getProcessName() + "Something changed on root node");
                zooKeeper.getChildren(ROOT, true, this, null);
            }
        }
    }

    public void processNoneEvent(WatchedEvent event) {
        switch (event.getState()) {
            case SyncConnected:
                logger.info(listener.getProcessName() + " is connected to Zookeeper");
                try {
                    createRootIfNotExists();
                    logger.debug(listener.getProcessName() + ": Putting a watch on " + ROOT);
                    zooKeeper.getChildren(ROOT, true, this, null);
                    sequenceNumber = createZnode();
                } catch (InterruptedException e) {
                    logger.error("Interrupted after connection", e);
                    System.exit(1);
                } catch (KeeperException e) {
                    logger.error("Interrupted after connection", e);
                    System.exit(1);
                }
                break;
            case Disconnected:
                logger.debug("Disconnected");
                listener.stopSpeaking();
                break;
            case Expired:
                logger.debug("Expired");
                listener.stopSpeaking();
                break;
        }
    }

    public long createZnode() throws InterruptedException, KeeperException {
        znode = zooKeeper.create(znode, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        logger.debug("Created znode: " + znode);
        return parseSequenceNumber(znode);
    }

    public void createRootIfNotExists() throws InterruptedException, KeeperException {
        Stat stat = zooKeeper.exists(ROOT, false);
        if (stat == null) {
            try {
                zooKeeper.create(ROOT, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, PERSISTENT);
            } catch (KeeperException.NodeExistsException ex) {
                // If znode gets created in between exists and create by another client ignore error
            }
        }
    }

    public void setSequenceNumber(long sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }
}
