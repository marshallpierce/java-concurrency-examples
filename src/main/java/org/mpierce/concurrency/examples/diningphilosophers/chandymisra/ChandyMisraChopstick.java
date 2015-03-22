package org.mpierce.concurrency.examples.diningphilosophers.chandymisra;

import net.jcip.annotations.ThreadSafe;
import org.mpierce.concurrency.examples.diningphilosophers.Chopstick;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

@ThreadSafe
final class ChandyMisraChopstick implements Chopstick {

    private final AtomicBoolean clean = new AtomicBoolean(false);

    private final ReentrantLock lock = new ReentrantLock();

    boolean isClean() {
        return this.clean.get();
    }

    public void wash() {
        this.clean.set(true);
    }

    public void use() {
        this.clean.set(false);
    }

    @Override
    public void acquireOwnership() {
        if (!lock.tryLock()) {
            throw new IllegalStateException();
        }
    }

    @Override
    public void releaseOwnership() {
        lock.unlock();
    }
}
