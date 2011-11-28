package net.spike.zookeeper;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.apache.zookeeper.CreateMode.PERSISTENT;

/**
 * User: cyberroadie
 * Date: 24/11/2011
 */
@RunWith(JMock.class)
public class NodeMonitorTest {

    Mockery context = new JUnit4Mockery() {{
        // Concrete class zookeeper needs to be mocked
        setImposteriser(ClassImposteriser.INSTANCE);
    }};

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

    /**
     * NodeMonitor.createRootIfNotExists() should catch NodeExists. In the case
     * of multiple clients, this is a possibility.
     * @throws InterruptedException
     * @throws KeeperException
     */
    @Test
    public void testCreateRootIfNotExistsWithNodeExistsException() throws InterruptedException, KeeperException {
        final ZooKeeper zookeeper = context.mock(ZooKeeper.class);
        classToTest = new NodeMonitor();
        classToTest.setZooKeeper(zookeeper);

        // expectations
        context.checking(new Expectations() {{
            oneOf(zookeeper).exists("/ELECTION", false);
            will(returnValue(null));
            oneOf(zookeeper).create("/ELECTION", new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, PERSISTENT);
            will(throwException(new KeeperException.NodeExistsException()));
        }});

        classToTest.createRootIfNotExists();

    }

}
