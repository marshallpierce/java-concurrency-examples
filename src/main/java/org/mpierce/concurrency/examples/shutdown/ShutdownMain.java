package org.mpierce.concurrency.examples.shutdown;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ShutdownMain {
    public static void main(String[] args) throws InterruptedException {
        rawThread();
        executor();
    }

    private static void executor() throws InterruptedException {
        ExecutorService executorService = Executors.newCachedThreadPool();
        Future<?> future = executorService.submit(new Sleeper());

        Thread.sleep(50);

        future.cancel(true);
        executorService.shutdown();
    }

    private static void rawThread() throws InterruptedException {
        Thread thread = new Thread(new Sleeper());
        thread.start();

        Thread.sleep(50);

        thread.interrupt();
    }

    private static class Sleeper implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    System.out.println("Exiting loop in " + Thread.currentThread().getName());
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
    }
}
