package net.spike.zookeeper.simple;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.UUID;

import static org.apache.zookeeper.ZooDefs.Ids.OPEN_ACL_UNSAFE;

/**
 * User: cyberroadie
 * Date: 09/11/2011
 */
public class SimpleZKClient implements Watcher, Runnable {

    private String znode;
    private ZooKeeper zooKeeper;
    private String connectionString;
    private static String ROOT = "/simplezkclient";
    private static UUID ID = UUID.randomUUID();

    public SimpleZKClient(String znode, String connectionString) throws IOException, InterruptedException, KeeperException {
        this.znode = znode;
        this.zooKeeper = new ZooKeeper(connectionString, 3000, this);
        Stat stat = zooKeeper.exists(ROOT, false);
        if(stat == null) {
            zooKeeper.create(ROOT, new byte[0], OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
    }

    public static void main(String[] args) {
        try {
            new Thread(new SimpleZKClient(getZNodeName(), ":2181")).start();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }

    public static String getZNodeName() {
        return ROOT + "/" + ID;
    }

    public void run() {
        try {
            Stat s = zooKeeper.exists(getZNodeName(), false);
            if(s == null) {
                zooKeeper.create(getZNodeName() , new byte[0], OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
            }
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        while(true) {
            System.out.println("running");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void process(WatchedEvent watchedEvent) {
        System.out.println("process");
    }
}
