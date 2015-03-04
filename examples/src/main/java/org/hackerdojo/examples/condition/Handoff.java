package org.hackerdojo.examples.condition;

import net.jcip.annotations.ThreadSafe;
import org.jetbrains.annotations.NotNull;

@ThreadSafe
public interface Handoff<T> {
    void put(@NotNull T obj) throws InterruptedException;

    @NotNull
    T take() throws InterruptedException;
}
