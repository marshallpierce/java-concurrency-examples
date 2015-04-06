package org.mpierce.concurrency.examples.deadlock;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

final class DeadlockMain {

    public static void main(String[] args) {
        Object lock1 = new Object();
        Object lock2 = new Object();

        ExecutorService ex = Executors.newCachedThreadPool();

        CountDownLatch latch = new CountDownLatch(2);

        ex.submit(() -> {
            latch.countDown();
            waitForLatch(latch);
            while (true) {
                synchronized (lock1) {
                    synchronized (lock2) {
                        System.out.println("I got 'em!");
                    }
                }
            }
        });

        ex.submit(() -> {
            latch.countDown();
            waitForLatch(latch);
            while (true) {
                // acquire locks in inverse order
                synchronized (lock2) {
                    synchronized (lock1) {
                        System.out.println("No, I did!");
                    }
                }
            }
        });
    }

    private static void waitForLatch(CountDownLatch latch) {
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException("I'm totally cheating here", e);
        }
    }
}
