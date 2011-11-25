package net.spike.zookeeper;

import org.apache.zookeeper.KeeperException;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;

/**
 * User: cyberroadie
 * Date: 24/11/2011
 */
public class NodeMonitorTest {

    NodeMonitor classToTest;

    @Before
    public void setup() throws IOException, InterruptedException, KeeperException {
        classToTest = new NodeMonitor();
    }

    @Test
    public void testParseSequenceNumber() throws Exception {
        assertEquals(154, classToTest.parseSequenceNumber("/ELECTION/pid-70878.0000000154"));
    }

    @Test
    public void testGetLowestNumber() throws IOException, InterruptedException, KeeperException {
        classToTest.setSequenceNumber(15);
        List<String> children = new ArrayList<String>();
        children.add("/ELECTION/pid-233.000003");
        children.add("/ELECTION/pid-163.000015");
        children.add("/ELECTION/pid-12333.000333");
        children.add("/ELECTION/pid-003.000033");
        long expected = 3;
        long result = classToTest.getLowestNumber(children);
        assertEquals(expected, result);
    }

}
