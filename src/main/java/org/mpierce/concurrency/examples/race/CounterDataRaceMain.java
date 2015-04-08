package org.mpierce.concurrency.examples.race;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Demo of two threads updating the same memory without synchronization.
 */
final class CounterDataRaceMain {

    private static final int[] counter = new int[]{0};

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newCachedThreadPool();

        int numThreads = 2;
        CountDownLatch latch = new CountDownLatch(numThreads);

        List<Future<?>> futures = new ArrayList<>();

        for (int i = 0; i < numThreads; i++) {
            futures.add(executorService.submit(() -> {
                waitUntilReady(latch);

                for (int j = 0; j < 1_000_000; j++) {
                    // data race here
                    counter[0]++;
                }
            }));
        }

        for (Future<?> future : futures) {
            future.get();
        }

        executorService.shutdown();

        System.out.println(counter[0]);
    }

    private static void waitUntilReady(CountDownLatch latch) {
        latch.countDown();
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException("Not gonna happen", e);
        }
    }
}
