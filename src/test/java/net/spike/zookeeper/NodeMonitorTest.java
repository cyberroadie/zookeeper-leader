package net.spike.zookeeper;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * User: cyberroadie
 * Date: 24/11/2011
 */
public class NodeMonitorTest {

    NodeMonitor classToTest;

    @Test
    public void testParseSequenceNumber() throws Exception {
        classToTest = new NodeMonitor();
        assertEquals(154, classToTest.parseSequenceNumber("/ELECTION/pid-70878.0000000154"));
    }
}
