package net.spike.zookeeper;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;

/**
 * User: cyberroadie
 * Date: 07/11/2011
 */
public class Speaker implements Watcher, Runnable, SpeakerMonitor.SpeakerMonitorListener {

    private String message;

    private int sleepTime;
    private String processName;
    private boolean doTask = false;
    private final Object lock = new Object();

    private String znode;
    private ZooKeeper zooKeeper;
    private String connectionString;
    private SpeakerMonitor speakerMonitor;

    public Speaker(String message, int sleepTime, String znode) throws IOException {
        this.message = message;
        this.sleepTime = sleepTime;
        this.processName = getProcessName();

        this.znode = znode;
        this.zooKeeper = new ZooKeeper(connectionString, 3000, this);


    }

    private static String getProcessName() {
        return ManagementFactory.getRuntimeMXBean().getName();
    }

    public void run() {
        try {
            while (true) {
                if (doTask) {
                    FileWriter fstream = new FileWriter("out.txt");
                    BufferedWriter out = new BufferedWriter(fstream);
                    out.write(message + " " + processName + "\n");
                    out.close();
                    Thread.sleep(sleepTime);
                } else {
                    System.out.println("Locked: " + processName);
                    lock.wait();
                }
            }
        } catch (Exception ex) {
            System.out.println("Something went wrong: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            printUsage();
            System.exit(1);
        }
        Speaker speaker = null;
        try {
            speaker = new Speaker(args[0], Integer.parseInt(args[1]), getProcessName());
        } catch (IOException e) {
            e.printStackTrace();
        }
        new Thread(speaker).start();
    }

    private static void printUsage() {
        System.out.println("program [message] [wait between messages in millisecond]");
    }

    public void exists(byte[] data) {
        if (data == null) {
            if (doTask) doTask = false;
        } else {
            doTask = true;
            lock.notify();
        }
    }

    public void closing(int rc) {
        synchronized (this) {
            doTask = false;
        }
    }

    public void process(WatchedEvent watchedEvent) {
        speakerMonitor.process(watchedEvent);
    }
}
