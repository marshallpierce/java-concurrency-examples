package org.mpierce.concurrency.examples.condition;

import javax.annotation.concurrent.ThreadSafe;
import javax.annotation.Nonnull;

@ThreadSafe
public interface Handoff<T> {
    void put(@Nonnull T obj) throws InterruptedException;

    @Nonnull
    T take() throws InterruptedException;
}
