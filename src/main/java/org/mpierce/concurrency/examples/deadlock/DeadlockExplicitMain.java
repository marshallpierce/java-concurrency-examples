package org.mpierce.concurrency.examples.deadlock;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * In Java 6+, deadlock detection will work on Lock objects (and other "ownable synchronizers").
 */
final class DeadlockExplicitMain {

    public static void main(String[] args) {
        Lock lock1 = new ReentrantLock();
        Lock lock2 = new ReentrantLock();

        ExecutorService ex = Executors.newCachedThreadPool();

        CountDownLatch latch = new CountDownLatch(2);

        ex.submit(() -> {
            latch.countDown();
            waitForLatch(latch);
            while (true) {
                lock1.lock();
                try {
                    lock2.lock();
                    try {
                        System.out.println("I got 'em!");
                    } finally {
                        lock2.unlock();
                    }
                } finally {
                    lock1.unlock();
                }
            }
        });

        ex.submit(() -> {
            latch.countDown();
            waitForLatch(latch);
            while (true) {
                // acquire locks in inverse order
                lock2.lock();
                try {
                    lock1.lock();
                    try {
                        System.out.println("I got 'em!");
                    } finally {
                        lock1.unlock();
                    }
                } finally {
                    lock2.unlock();
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
