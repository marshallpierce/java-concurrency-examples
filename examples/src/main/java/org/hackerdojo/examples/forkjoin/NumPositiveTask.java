package org.hackerdojo.examples.forkjoin;

public class NumPositiveTask extends AbstractExampleTask {
    NumPositiveTask(int[] data, int firstIndex, int lastIndex) {
        super(data, firstIndex, lastIndex);
    }

    @Override
    protected Long directlySolve() {
        long numPositive = 0L;
        for (int i = 0; i < this.getLength(); i++) {
            if (this.get(i) > 0) {
                numPositive++;
            }
        }

        return numPositive;
    }

    @Override
    protected Long handleResults(Long leftLong, Long rightLong) {
        return leftLong + rightLong;
    }

    @Override
    protected AbstractExampleTask getSubtask(int start, int end) {
        return new NumPositiveTask(data, start, end);
    }
}
