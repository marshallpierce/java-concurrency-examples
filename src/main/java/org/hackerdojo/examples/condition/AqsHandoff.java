package org.hackerdojo.examples.condition;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.locks.AbstractQueuedSynchronizer;

public class AqsHandoff<T> implements Handoff<T> {

    private final ToggleSync emptySync;

    private final ToggleSync nonEmptySync;

    public AqsHandoff() {
        this.emptySync = new ToggleSync();
        this.nonEmptySync = new ToggleSync();

        // nonEmptySync should block
        this.nonEmptySync.acquire(0);
    }


    @Nullable
    private T obj;

    @Override
    public void put(@NotNull T obj) throws InterruptedException {

        this.emptySync.acquire(0);
        try {
            this.obj = obj;
        } finally {
            this.nonEmptySync.release(0);
        }
    }

    @NotNull
    @Override
    public T take() throws InterruptedException {
        this.nonEmptySync.acquire(0);
        try {
            T obj = this.obj;
            this.obj = null;
            return obj;
        } finally {
            this.emptySync.release(0);
        }
    }

    /**
     * Synchronizer state: 0 = nonempty, 1 = empty
     */
    private final static class ToggleSync extends AbstractQueuedSynchronizer {

        private static final int INACTIVE = 0;
        private static final int ACTIVE = 1;

        @Override
        protected boolean tryAcquire(int arg) {
            if (this.getState() == INACTIVE) {
                if (this.compareAndSetState(INACTIVE, ACTIVE)) {
                    return true;
                }
            }

            return false;
        }

        @Override
        protected boolean tryRelease(int arg) {
            if (this.getState() == ACTIVE) {
                if (this.compareAndSetState(ACTIVE, INACTIVE)) {
                    return true;
                }
            }

            return false;
        }

        @Override
        protected boolean isHeldExclusively() {
            return this.getState() == ACTIVE;
        }
    }
}
