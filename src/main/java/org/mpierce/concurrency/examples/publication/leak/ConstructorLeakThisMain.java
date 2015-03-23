package org.mpierce.concurrency.examples.publication.leak;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.annotation.concurrent.Immutable;

final class ConstructorLeakThisMain {

    static Holder holder;

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService ex = Executors.newCachedThreadPool();

        Future<?> f1 = ex.submit(() -> {
            while (true) {
                if (holder == null) {
                    continue;
                }

                if (!holder.check()) {
                    return;
                }
            }
        });

        Future<?> f2 = ex.submit(() -> {
            while (true) {
                new Holder();

                if (Thread.interrupted()) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        });

        f1.get();

        f2.cancel(true);
        ex.shutdown();
    }

    @Immutable
    static class Holder {
        final int x;
        final int y;

        public Holder() {
            holder = this;
            x = 1;
            y = 2;
        }

        boolean check() {
            int localX = this.x;
            int localY = this.y;
            if (localX != 1 || localY != 2) {
                System.out.println("x " + localX + " y " + localY);
                if (localX != 0 || localY != 0) {
                    System.out.println("Half-initialized");
                } else {
                    System.out.println("Not at all initialized");
                }
                return false;
            }
            return true;
        }
    }
}
