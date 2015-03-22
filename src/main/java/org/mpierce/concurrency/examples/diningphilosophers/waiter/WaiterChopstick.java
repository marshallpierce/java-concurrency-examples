package org.mpierce.concurrency.examples.diningphilosophers.waiter;

import net.jcip.annotations.ThreadSafe;
import org.mpierce.concurrency.examples.diningphilosophers.Chopstick;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@ThreadSafe
public class WaiterChopstick implements Chopstick {

    private final Lock lock = new ReentrantLock();

    @Override
    public void acquireOwnership() throws InterruptedException {
        if (!lock.tryLock()) {
            throw new IllegalStateException();
        }
    }

    @Override
    public void releaseOwnership() {
        lock.unlock();
    }
}
