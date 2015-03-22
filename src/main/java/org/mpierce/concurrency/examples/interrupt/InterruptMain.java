package org.mpierce.concurrency.examples.interrupt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class InterruptMain {

    private static final Logger logger = LoggerFactory.getLogger(InterruptMain.class);

    public static void main(String[] args) {

        ExecutorService ex = Executors.newCachedThreadPool();

        final Thread mainThread = Thread.currentThread();

        logger.info("Submitting");
        Future<?> future = ex.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    logger.info("Starting to sleep");
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    logger.info("Interrupted");
                    Thread.currentThread().interrupt();
                    return;
                }
                logger.info("Done sleeping");
            }
        });

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        future.cancel(true);

        try {
            if (!future.isCancelled()) {
                logger.info("Waiting");
                future.get();
                logger.info("Done waiting");
                System.out.println("Main thread wasn't interrupted");
            }
        } catch (InterruptedException e) {
            logger.info("Interrupted");
            System.out.println("Interrupted as expected");
        } catch (ExecutionException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


        ex.shutdown();
    }

}
