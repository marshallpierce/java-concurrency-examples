package org.mpierce.concurrency.examples.publication.obj;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
class SanityChecker {

    DataHolder holder;

    int counter = 1;

    public void check() {
        DataHolder h = this.holder;
        if (h == null) {
            return;
        }
        int v = h.value;
        int v2 = h.otherValue;
        // if they're not equal, half-initialized.
        // if either is 0, they're uninitialized.
        if (v != v2 || v == 0) {
            throw new IllegalStateException("saw " + v + " and " + v2);
        }
    }

    public void write() {
        holder = new DataHolder(counter++);
    }

    private static class DataHolder {
        // make sure each holder's values will be on their own cache line
        private final Object pad_0 = new Object();
        private final Object pad_1 = new Object();
        private final Object pad_2 = new Object();
        private final Object pad_3 = new Object();
        private final Object pad_4 = new Object();
        private final Object pad_5 = new Object();
        private final Object pad_6 = new Object();
        private final Object pad_7 = new Object();
        private final Object pad_8 = new Object();
        private final Object pad_9 = new Object();
        private final Object pad_10 = new Object();
        private final Object pad_11 = new Object();
        private final Object pad_12 = new Object();
        private final Object pad_13 = new Object();
        private final Object pad_14 = new Object();
        private int value;
        private int otherValue;

        DataHolder(int val) {
            this.value = val;
            this.otherValue = val;
        }
    }
}
