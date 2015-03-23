package org.mpierce.concurrency.examples.diningphilosophers.waiter;

import java.util.concurrent.CountDownLatch;
import javax.annotation.Nonnull;
import org.mpierce.concurrency.examples.diningphilosophers.AbstractPhilosopher;

final class WaiterPhilosopher extends AbstractPhilosopher<WaiterChopstick> {

    private final Waiter waiter;

    WaiterPhilosopher(@Nonnull CountDownLatch latch, @Nonnull WaiterChopstick left, @Nonnull WaiterChopstick right,
        int numEats,
        Waiter waiter) {
        super(latch, left, right, numEats);
        this.waiter = waiter;
    }

    protected void eat(int mealsEaten) throws InterruptedException {
        this.waiter.acquirePair(this);

        try {
            this.left.acquireOwnership();
            try {
                this.right.acquireOwnership();
                try {
                    logger.info("eating meal #" + mealsEaten);
                    randomSleep();
                } finally {
                    this.right.releaseOwnership();
                }
            } finally {
                this.left.releaseOwnership();
            }
        } finally {
            this.waiter.releasePair(this);
        }

    }
}
