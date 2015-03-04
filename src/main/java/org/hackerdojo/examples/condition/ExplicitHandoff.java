package org.hackerdojo.examples.condition;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    public void put(@NotNull T obj) throws InterruptedException {
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

    @NotNull
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
