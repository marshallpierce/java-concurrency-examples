package org.hackerdojo.examples.condition;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class HandoffMain {

    private static final Logger logger = LoggerFactory.getLogger(HandoffMain.class);


    public static void main(String[] args) throws InterruptedException {
        Handoff<Object> handoff;
        handoff = new IntrinsicHandoff<Object>();
//        handoff = new ExplicitHandoff<Object>();
//        handoff = new AqsHandoff<Object>();

        ExecutorService executorService = Executors.newCachedThreadPool();
        CompletionService<Void> cs = new ExecutorCompletionService<Void>(executorService);
        int numTakers = 3;
        for (int i = 0; i < numTakers; i++) {
            cs.submit(new HandoffTaker(handoff), null);
        }

        int numGivers = numTakers;
        for (int i = 0; i < numGivers; i++) {
            cs.submit(new HandoffGiver(handoff), null);
        }

        for (int i = 0; i < numGivers + numTakers; i++) {
            Future<Void> future = cs.take();
            try {
                future.get();
            } catch (ExecutionException e) {
                logger.warn("Task failed with " + e.getCause());
            }
        }

        executorService.shutdown();
    }

}
