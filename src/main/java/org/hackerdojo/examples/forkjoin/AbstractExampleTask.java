package org.hackerdojo.examples.forkjoin;

import java.util.concurrent.RecursiveTask;

abstract class AbstractExampleTask extends RecursiveTask<Long> {

    final static int THRESHOLD = 100000;

    final int[] data;

    private final int firstIndex;

    private final int lastIndex;

    AbstractExampleTask(int[] data, int firstIndex, int lastIndex) {
        this.data = data;
        this.firstIndex = firstIndex;
        this.lastIndex = lastIndex;
    }

    @Override
    protected Long compute() {
        if (this.getLength() < THRESHOLD) {
//            System.out
//                    .println("Task [" + this.firstIndex + ", " + this.lastIndex + "] directly solving");
            return directlySolve();
        }

//        System.out
//                .println("Task [" + this.firstIndex + ", " + this.lastIndex + "] left subtask [" +
//                        this.getLeftHalfStart() + ", " + this.getLeftHalfEnd() + "], right subtask [" +
//                        this.getRightHalfStart() + ", " + this.getRightHalfEnd() + "]");

        AbstractExampleTask left = getSubtask(this.getLeftHalfStart(), this.getLeftHalfEnd());
        left.fork();
        AbstractExampleTask right = getSubtask(this.getRightHalfStart(), this.getRightHalfEnd());
        return handleResults(left.join(), right.compute());
    }

    protected abstract Long directlySolve();

    protected abstract Long handleResults(Long leftLong, Long rightLong);

    protected abstract AbstractExampleTask getSubtask(int start, int end);


    /**
     * @return the length of the slice we're looking at
     */
    int getLength() {
        return this.lastIndex - this.firstIndex + 1;
    }

    int get(int index) {
        return this.data[this.firstIndex + index];
    }

    int getLeftHalfStart() {
        return this.firstIndex;
    }

    int getLeftHalfEnd() {
        if (this.getLength() % 2 == 0) {
            return this.getLeftHalfStart() + this.getLength() / 2 - 1;
        }

        // left is the "big" half
        return this.getLeftHalfStart() + this.getLength() / 2;
    }

    int getRightHalfStart() {
        return this.getLeftHalfEnd() + 1;
    }

    int getRightHalfEnd() {
        return this.lastIndex;
    }
}
