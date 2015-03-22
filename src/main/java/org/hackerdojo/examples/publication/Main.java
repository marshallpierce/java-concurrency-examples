package org.hackerdojo.examples.publication;

import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;

class Main {

    @SuppressWarnings({"StaticNonFinalField", "PublicField", "StaticVariableMayNotBeInitialized"})
    public static SanityChecker checker;

    public static void main(String[] args) throws InterruptedException {

        final ExecutorService ex = Executors.newCachedThreadPool();
        final CompletionService<Void> completionService = new ExecutorCompletionService<Void>(ex);

        ThreadLocalRandom r = ThreadLocalRandom.current();

        for (int i = 0; i < 8; i++) {
            completionService.submit(() -> {
                while (true) {
                    checker = new SanityCheckerWithNoFinal(r.nextInt());
                }
            }, null);

            completionService.submit(() -> {
                while (true) {
                    if (Main.checker == null) {
                        continue;
                    }

                    Main.checker.check();
                }
            }, null);
        }

        final Future<Void> future = completionService.take();

        try {
            future.get();
        } catch (ExecutionException e) {
            System.out.println("Worker died with exception: " + e);
            ex.shutdownNow();
        }

        System.exit(1);
    }
}
