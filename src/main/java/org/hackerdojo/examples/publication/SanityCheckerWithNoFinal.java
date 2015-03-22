package org.hackerdojo.examples.publication;

import net.jcip.annotations.NotThreadSafe;

@NotThreadSafe
class SanityCheckerWithNoFinal implements SanityChecker {

    @SuppressWarnings({"FieldMayBeFinal"})
    private int value;

    private int otherValue;

    SanityCheckerWithNoFinal(int value) {
        this.value = value;
        this.otherValue = value;
    }

    public void check() {
        //noinspection ConstantConditions
        int v = this.value;
        int v2 = this.otherValue;
        if (v != v2) {
            throw new IllegalStateException("saw " + v + " and " + v2);
        }
    }

}
