package org.mpierce.concurrency.examples.condition;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@ThreadSafe
public final class IntrinsicHandoff<T> implements Handoff<T> {

    /**
     * Null when there isn't an object ready to be taken
     */
    @Nullable
    @GuardedBy("this")
    private T obj = null;

    @Override
    public synchronized void put(@Nonnull T obj) throws InterruptedException {
        // wait until someone has taken the current object, if there is one
        while (hasObject()) {
            this.wait();
        }

        this.obj = obj;
        this.notifyAll();
    }

    private boolean hasObject() {
        return this.obj != null;
    }

    @Override
    @Nonnull
    public synchronized T take() throws InterruptedException {
        // wait until something is there
        while (!hasObject()) {
            this.wait();
        }

        T obj = this.obj;
        this.obj = null;

        this.notifyAll();
        return obj;
    }

}
