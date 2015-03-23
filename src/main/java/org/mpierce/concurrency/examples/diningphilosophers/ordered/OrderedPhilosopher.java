package org.mpierce.concurrency.examples.diningphilosophers.ordered;

import org.mpierce.concurrency.examples.diningphilosophers.AbstractPhilosopher;
import org.mpierce.concurrency.examples.diningphilosophers.Chopstick;
import javax.annotation.Nonnull;

import java.util.concurrent.CountDownLatch;

class OrderedPhilosopher extends AbstractPhilosopher<OrderedChopstick> {
    protected OrderedPhilosopher(@Nonnull CountDownLatch latch, @Nonnull OrderedChopstick left,
                                 @Nonnull OrderedChopstick right,
                                 int maxEats) {
        super(latch, left, right, maxEats);
    }

    @Override
    protected void eat(int mealsEaten) throws InterruptedException {
        Chopstick first;
        Chopstick second;
        if (this.left.isBefore(this.right)) {
            first = this.left;
            second = this.right;
        } else {
            first = this.right;
            second = this.left;
        }


        first.acquireOwnership();
        second.acquireOwnership();

        logger.info("eating meal #" + mealsEaten);
        randomSleep();

        second.releaseOwnership();
        first.releaseOwnership();
    }

}
