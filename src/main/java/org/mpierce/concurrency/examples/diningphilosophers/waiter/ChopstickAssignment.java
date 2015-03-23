package org.mpierce.concurrency.examples.diningphilosophers.waiter;

import javax.annotation.concurrent.ThreadSafe;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@ThreadSafe
final class ChopstickAssignment {

    private ChopstickAssignment leftNeighbor;
    private ChopstickAssignment rightNeighbor;

    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public Lock getSharedLock() {
        return lock.readLock();
    }

    public Lock getExclusiveLock() {
        return lock.writeLock();
    }

    public ChopstickAssignment getLeftNeighbor() {
        if (this.leftNeighbor == null) {
            throw new IllegalStateException();
        }
        return this.leftNeighbor;
    }

    public ChopstickAssignment getRightNeighbor() {
        if (this.rightNeighbor == null) {
            throw new IllegalStateException();
        }
        return this.rightNeighbor;
    }

    public void setLeftNeighbor(ChopstickAssignment leftNeighbor) {
        if (this.leftNeighbor != null) {
            throw new IllegalStateException();
        }
        this.leftNeighbor = leftNeighbor;
    }

    public void setRightNeighbor(ChopstickAssignment rightNeighbor) {
        if (this.rightNeighbor != null) {
            throw new IllegalStateException();
        }
        this.rightNeighbor = rightNeighbor;
    }
}
