package org.mpierce.concurrency.examples.diningphilosophers.waiter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

class WaiterMain {

    private static final Logger logger = LoggerFactory.getLogger(WaiterMain.class);

    public static void main(String[] args) throws InterruptedException {
        int numPhilosophers = 50;
        int numEats = 50;

        CountDownLatch startLatch = new CountDownLatch(numPhilosophers);

        List<WaiterChopstick> chopsticks = new ArrayList<WaiterChopstick>();
        for (int i = 0; i < numPhilosophers; i++) {
            chopsticks.add(new WaiterChopstick());
        }

        List<WaiterPhilosopher> philosophers = new ArrayList<WaiterPhilosopher>();
        // handle last philosopher by hand

        Waiter waiter = new Waiter();

        for (int i = 0; i < numPhilosophers; i++) {
            int rightChopstickIndex = i + 1;
            if (rightChopstickIndex == numPhilosophers) {
                rightChopstickIndex = 0;
            }
            philosophers.add(new WaiterPhilosopher(startLatch, chopsticks.get(i), chopsticks.get(rightChopstickIndex),
                    numEats,
                    waiter));

        }

        waiter.initialize(philosophers);

        ExecutorService ex = Executors.newCachedThreadPool();
        CompletionService<Void> cs = new ExecutorCompletionService<Void>(ex);

        for (Runnable philosopher : philosophers) {
            cs.submit(philosopher, null);
        }

        for (int i = 0; i < numPhilosophers; i++) {
            final Future<Void> future = cs.take();
            try {
                future.get();
            } catch (ExecutionException e) {
                logger.warn("Philosopher failed: ", e.getCause());
                ex.shutdownNow();
                break;
            }
        }

        ex.shutdown();
    }
}
