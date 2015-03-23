package org.mpierce.concurrency.examples.publication.obj;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
class SanityCheckerWithOneFinal implements SanityChecker {

    @SuppressWarnings({"FieldMayBeFinal"})
    private int value;

    private final int finalValue;

    SanityCheckerWithOneFinal(int value) {
        this.value = value;
        this.finalValue = value;
    }

    public void check() {
        //noinspection ConstantConditions
        int v = this.value;
        int v2 = this.finalValue;
        if (v != v2) {
            throw new IllegalStateException("saw " + v + " and " + v2);
        }
    }
}
