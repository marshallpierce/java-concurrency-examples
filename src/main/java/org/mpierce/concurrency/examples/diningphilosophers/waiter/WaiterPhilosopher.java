package org.mpierce.concurrency.examples.diningphilosophers.waiter;

import org.mpierce.concurrency.examples.diningphilosophers.AbstractPhilosopher;
import javax.annotation.Nonnull;

import java.util.concurrent.CountDownLatch;

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

        this.waiter.releasePair(this);
    }

}
