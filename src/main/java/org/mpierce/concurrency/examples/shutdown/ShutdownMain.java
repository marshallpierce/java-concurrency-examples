package org.mpierce.concurrency.examples.shutdown;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ShutdownMain {
    public static void main(String[] args) {
//        rawThread();
        executor();
    }

    private static void executor() {
        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.submit(new Sleeper());
    }

    private static void rawThread() {
        Thread thread = new Thread(new Sleeper());
    }

    private static class Sleeper implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
    }
}
