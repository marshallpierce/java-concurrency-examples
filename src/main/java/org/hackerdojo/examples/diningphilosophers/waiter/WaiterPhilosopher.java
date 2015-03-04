package org.hackerdojo.examples.diningphilosophers.waiter;

import org.hackerdojo.examples.diningphilosophers.AbstractPhilosopher;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CountDownLatch;

final class WaiterPhilosopher extends AbstractPhilosopher<WaiterChopstick> {

    private final Waiter waiter;

    WaiterPhilosopher(@NotNull CountDownLatch latch, @NotNull WaiterChopstick left, @NotNull WaiterChopstick right,
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
