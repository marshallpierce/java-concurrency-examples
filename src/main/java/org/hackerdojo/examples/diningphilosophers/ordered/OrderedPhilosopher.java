package org.hackerdojo.examples.diningphilosophers.ordered;

import org.hackerdojo.examples.diningphilosophers.AbstractPhilosopher;
import org.hackerdojo.examples.diningphilosophers.Chopstick;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CountDownLatch;

class OrderedPhilosopher extends AbstractPhilosopher<OrderedChopstick> {
    protected OrderedPhilosopher(@NotNull CountDownLatch latch, @NotNull OrderedChopstick left,
                                 @NotNull OrderedChopstick right,
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
