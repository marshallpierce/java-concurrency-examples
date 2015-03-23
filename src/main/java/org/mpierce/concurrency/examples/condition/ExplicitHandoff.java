package org.mpierce.concurrency.examples.condition;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@ThreadSafe
public final class ExplicitHandoff<T> implements Handoff<T> {

    private final Lock lock = new ReentrantLock();

    private final Condition notEmptyCondition = lock.newCondition();
    private final Condition emptyCondition = lock.newCondition();

    @Nullable
    @GuardedBy("lock")
    private T obj;

    @Override
    public void put(@Nonnull T obj) throws InterruptedException {
        lock.lock();
        try {
            while (!isEmpty()) {
                emptyCondition.await();
            }

            this.obj = obj;

            notEmptyCondition.signal();
        } finally {
            lock.unlock();
        }
    }

    private boolean isEmpty() {
        return this.obj == null;
    }

    @Nonnull
    @Override
    public T take() throws InterruptedException {
        lock.lock();
        try {
            while (isEmpty()) {
                notEmptyCondition.await();
            }

            T obj = this.obj;
            this.obj = null;

            emptyCondition.signal();

            return obj;
        } finally {
            lock.unlock();
        }
    }
}
