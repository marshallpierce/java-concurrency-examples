package org.mpierce.concurrency.examples.publication.obj;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Supplier;

/**
 * Demo of unsafe publication leading to possibly seeing a partially-initialized object
 */
@SuppressWarnings("unused")
class Main {

    public static void main(String[] args) throws InterruptedException {
        final ExecutorService ex = Executors.newCachedThreadPool();
        final CompletionService<Void> completionService = new ExecutorCompletionService<Void>(ex);

        repeat(4, SanityChecker::new)
                .forEach((checker) -> {
                    completionService.submit(new Runnable() {
                        @Override
                        public void run() {
                            // cold outer loop that will be OSR JIT'd
                            while (true) {
                                hotLoop();
                            }
                        }

                        // have hot code in a separate function so it can get properly JIT'd
                        void hotLoop() {
                            for (int i = 1; i < 128; i++) {
                                checker.check();
                            }
                        }
                    }, null);

                    completionService.submit(new Runnable() {
                        @Override
                        public void run() {
                            while (true) {
                                hotLoop();
                            }
                        }

                        private void hotLoop() {
                            for (int i = 0; i < 128; i++) {
                                checker.write();
                            }
                        }
                    }, null);
                });

        final Future<Void> future = completionService.take();

        try {
            future.get();
        } catch (ExecutionException e) {
            System.out.println("Worker died with exception: " + e.getCause());
            ex.shutdownNow();
        }

        System.exit(1);
    }

    private static <T> List<T> repeat(int n, Supplier<T> supp) {
        var res = new ArrayList<T>();
        for (int i = 0; i < n; i++) {
            res.add(supp.get());
        }

        return res;
    }
}
