package org.mpierce.concurrency.examples.race;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

final class CounterRaceMain {

    private static int[] counter = new int[]{0};

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newCachedThreadPool();

        CountDownLatch latch = new CountDownLatch(2);

        List<Future<?>> futures = new ArrayList<>();

        for (int i = 0; i < 2; i++) {
            futures.add(executorService.submit(() -> {
                waitUntilReady(latch);

                for (int j = 0; j < 1000000; j++) {
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
