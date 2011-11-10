package net.spike.zookeeper;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

/**
 * User: cyberroadie
 * Date: 07/11/2011
 */
public class SpeakerMonitor implements Watcher, AsyncCallback.StatCallback {

    ZooKeeper zooKeeper;
    String znode;
    SpeakerMonitorListener speakerMonitorListener;

    public SpeakerMonitor(ZooKeeper zooKeeper, String znode, SpeakerMonitorListener speakerMonitorListener) {
        this.zooKeeper = zooKeeper;
        this.znode = znode;
        this.speakerMonitorListener = speakerMonitorListener;

        zooKeeper.exists(znode, true, this,null);
    }

    public interface SpeakerMonitorListener {
        void exists(byte data[]);

        void closing(int rc);

    }

    public void process(WatchedEvent watchedEvent) {

    }

    public void processResult(int i, String s, Object o, Stat stat) {

    }
}
