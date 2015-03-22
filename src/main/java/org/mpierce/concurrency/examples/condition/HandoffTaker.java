package org.mpierce.concurrency.examples.condition;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class HandoffTaker implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(HandoffTaker.class);

    private final Handoff<Object> handoff;

    public HandoffTaker(Handoff<Object> handoff) {
        this.handoff = handoff;
    }

    @Override
    public void run() {
        logger.info("Getting object...");
        Object o;
        try {
            o = this.handoff.take();
        } catch (InterruptedException e) {
            logger.warn("Interrupted");
            return;
        }

        logger.info("Got an object " + o);
    }
}
