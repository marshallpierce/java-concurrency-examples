package org.mpierce.concurrency.examples.forkjoin;

class SumTask extends AbstractExampleTask {

    public SumTask(int[] data, int firstIndex, int lastIndex) {
        super(data, firstIndex, lastIndex);
    }

    @Override
    protected Long handleResults(Long leftLong, Long rightLong) {
        return leftLong + rightLong;
    }

    @Override
    protected SumTask getSubtask(int start, int end) {
        return new SumTask(this.data, start, end);
    }

    @Override
    protected Long directlySolve() {
        long accum = 0L;

        for (int i = 0; i < this.getLength(); i++) {
            accum = accum + (long) this.get(i);
        }

        return accum;
    }


}
