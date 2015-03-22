package org.mpierce.concurrency.examples.diningphilosophers;

import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public interface Chopstick {
    void acquireOwnership() throws InterruptedException;

    void releaseOwnership();
}
