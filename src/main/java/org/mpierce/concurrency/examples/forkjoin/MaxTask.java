package org.mpierce.concurrency.examples.forkjoin;

public class MaxTask extends AbstractExampleTask {
    MaxTask(int[] data, int firstIndex, int lastIndex) {
        super(data, firstIndex, lastIndex);
    }

    @Override
    protected Long directlySolve() {
        int max = Integer.MIN_VALUE;

        for (int i = 0; i < this.getLength(); i++) {
            if (this.get(i) > max) {
                max = this.get(i);
            }
        }

        return (long) max;
    }

    @Override
    protected Long handleResults(Long leftLong, Long rightLong) {
        return leftLong > rightLong ? leftLong : rightLong;
    }

    @Override
    protected AbstractExampleTask getSubtask(int start, int end) {
        return new MaxTask(data, start, end);
    }

}
