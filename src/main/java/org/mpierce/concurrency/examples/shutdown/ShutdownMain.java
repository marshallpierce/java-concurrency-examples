package org.mpierce.concurrency.examples.shutdown;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ShutdownMain {
    public static void main(String[] args) throws InterruptedException {
        // start a plain thread
        Thread thread = new Thread(new Sleeper());
        thread.start();

        // use an executor
        ExecutorService executorService = Executors.newCachedThreadPool();
        Future<?> future = executorService.submit(new Sleeper());

        Thread.sleep(50);

        // interrupt the two tasks
        thread.interrupt();

        future.cancel(true);
        executorService.shutdown();
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
