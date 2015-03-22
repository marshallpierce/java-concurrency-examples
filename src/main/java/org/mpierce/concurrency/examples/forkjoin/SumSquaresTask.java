package org.mpierce.concurrency.examples.forkjoin;

public class SumSquaresTask extends AbstractExampleTask {
    SumSquaresTask(int[] data, int firstIndex, int lastIndex) {
        super(data, firstIndex, lastIndex);
    }

    @Override
    protected Long directlySolve() {
        long accum = 0L;

        for (int i = 0; i < this.getLength(); i++) {
            accum = accum + ((long) this.get(i)) * ((long) this.get(i));
        }

        return accum;
    }

    @Override
    protected Long handleResults(Long leftLong, Long rightLong) {
        return leftLong + rightLong;
    }

    @Override
    protected AbstractExampleTask getSubtask(int start, int end) {
        return new SumSquaresTask(data, start, end);
    }
}
