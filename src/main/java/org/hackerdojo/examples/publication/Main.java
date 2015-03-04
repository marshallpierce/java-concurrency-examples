package org.hackerdojo.examples.publication;

import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

class Main {

    @SuppressWarnings({"StaticNonFinalField", "PublicField", "StaticVariableMayNotBeInitialized"})
    public static SanityChecker checker;

    public static void main(String[] args) throws InterruptedException {

        final ExecutorService ex = Executors.newCachedThreadPool();
        final CompletionService<Void> completionService = new ExecutorCompletionService<Void>(ex);

        for (int i = 0; i < 100; i++) {
            completionService.submit(new FieldWriter(), null);
            completionService.submit(new FieldWatcher(), null);
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
