package org.mpierce.concurrency.examples.future;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;

final class CompletableFutureMain {
    public static void main(String[] args) throws InterruptedException {

        CountDownLatch latch = new CountDownLatch(1);

        CompletableFuture.supplyAsync(() -> {
            sleep(500);
            return "Slow string";
        }).handle((s, e) -> {
            if (e != null) {
                System.out.println("Kaboom: " + e);
                return "";
            } else {
                return s;
            }
        }).thenAccept((s) -> {
            sleep(500);
            System.out.println("Slow length is: " + s.length());
        }).thenAccept((v) -> latch.countDown());

        latch.await();
    }

    static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted!");
        }
    }
}
