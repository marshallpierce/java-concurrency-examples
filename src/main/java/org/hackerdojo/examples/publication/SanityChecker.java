package org.hackerdojo.examples.publication;

import net.jcip.annotations.NotThreadSafe;

@NotThreadSafe
class SanityChecker {

    @SuppressWarnings({"FieldMayBeFinal"})
    private int value;

    private final int finalValue;

    SanityChecker(int value) {
        this.value = value;
        this.finalValue = value;
    }

    public void check() {
        //noinspection ConstantConditions
        if (this.value != this.finalValue) {
            throw new IllegalStateException();
        }
    }
}
