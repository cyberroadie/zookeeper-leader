package net.spike.zookeeper;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * User: cyberroadie
 * Date: 21/11/2011
 */
public class SpeakerServer {

    final static Logger logger = LoggerFactory.getLogger(SpeakerServer.class);

    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static NodeMonitor monitor;

    private static void printUsage() {
        System.out.println("program [message] [wait between messages in millisecond]");
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            printUsage();
            System.exit(1);
        }

        long delay = Long.parseLong(args[1]);

        Speaker speaker = null;

        try {
            speaker = new Speaker(args[0]);
            monitor = new NodeMonitor();
            monitor.setListener(speaker);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        scheduler.scheduleWithFixedDelay(speaker, 0, delay, TimeUnit.MILLISECONDS);
        logger.info("Speaker server started with fixed time delay of " + delay + " milliseconds.");

    }
}
