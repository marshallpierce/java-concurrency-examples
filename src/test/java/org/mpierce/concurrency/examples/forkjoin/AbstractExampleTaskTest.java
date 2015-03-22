package org.mpierce.concurrency.examples.forkjoin;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AbstractExampleTaskTest {

    @Test
    public void testGetLeftHalfStart() {
        assertEquals(5, new StubTask(new int[100], 5, 11).getLeftHalfStart());
    }


    @Test
    public void testGetLeftHalfEndEven() {
        assertEquals(7, new StubTask(new int[100], 5, 10).getLeftHalfEnd());
    }


    @Test
    public void testGetLeftHalfEndOdd() {
        assertEquals(8, new StubTask(new int[100], 5, 11).getLeftHalfEnd());
    }


    @Test
    public void testGetRightHalfEnd() {
        assertEquals(11, new StubTask(new int[100], 5, 11).getRightHalfEnd());
    }


    @Test
    public void testGetRightHalfStart() {
        assertEquals(9, new StubTask(new int[100], 5, 11).getRightHalfStart());
    }


    private static final class StubTask extends AbstractExampleTask {
        StubTask(int[] data, int firstIndex, int lastIndex) {
            super(data, firstIndex, lastIndex);
        }

        @Override
        protected Long directlySolve() {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        protected Long handleResults(Long leftLong, Long rightLong) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        protected AbstractExampleTask getSubtask(int start, int end) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

    }
}
