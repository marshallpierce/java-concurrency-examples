package org.hackerdojo.examples.condition;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class HandoffGiver implements Runnable  {

    private static final Logger logger = LoggerFactory.getLogger(HandoffGiver.class);

    private final Handoff<Object> handoff;

    public HandoffGiver(Handoff<Object> handoff) {
        this.handoff = handoff;
    }

    @Override
    public void run() {
        Object o = new Object();
        logger.info("Giving object " + o);

        try {
            this.handoff.put(o);
        } catch (InterruptedException e) {
            logger.warn("Interrupted");
            return;
        }

        logger.info("Gave object " + o);
    }
}
