package org.mpierce.concurrency.examples.diningphilosophers.chandymisra;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@ThreadSafe
class ChandyMisraPhilosopher implements Runnable {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @SuppressWarnings({"UnsecureRandomNumberGeneration"})
    private final Random random = new Random();

    private static final int MAX_SLEEP = 100;
    private static final int MIN_SLEEP = 10;

    @GuardedBy("lock")
    private final ChopstickHolder left = new ChopstickHolder();
    @GuardedBy("lock")
    private final ChopstickHolder right = new ChopstickHolder();

    private final int maxEats;

    private final CountDownLatch latch;

    /**
     * Guards mutable field access in parent
     */
    private final Lock lock = new ReentrantLock();

    private final Condition condition = lock.newCondition();

    @GuardedBy("lock")
    private ChandyMisraPhilosopher leftNeighbor;
    @GuardedBy("lock")
    private ChandyMisraPhilosopher rightNeighbor;

    @GuardedBy("lock")
    private AwaitingChopstickState leftState = new AwaitingChopstickState();
    @GuardedBy("lock")
    private AwaitingChopstickState rightState = new AwaitingChopstickState();


    protected ChandyMisraPhilosopher(@Nonnull CountDownLatch latch, @Nonnull ChandyMisraChopstick left,
                                     int maxEats) {
        this.latch = latch;
        this.left.set(left);
        this.maxEats = maxEats;
    }


    @Override
    public void run() {
        try {
            this.latch.countDown();
            this.latch.await();

            int mealsEaten = 0;
            while (mealsEaten < this.maxEats) {
                randomSleep();
                eat(mealsEaten);
                mealsEaten++;
            }
            logger.info("Eaten enough");
        } catch (InterruptedException ignored) {
            logger.warn("Interrupted, exiting");
        }
    }

    /**
     * @throws InterruptedException if interrupted while sleeping
     */

    protected void randomSleep() throws InterruptedException {
        int sleep = random.nextInt(MAX_SLEEP - MIN_SLEEP + 1) + MIN_SLEEP;

        Thread.sleep(sleep);
    }

    public void setLeftNeighbor(ChandyMisraPhilosopher leftNeighbor) {
        this.leftNeighbor = leftNeighbor;
    }

    public void setRightNeighbor(ChandyMisraPhilosopher rightNeighbor) {
        this.rightNeighbor = rightNeighbor;
    }

    private void eat(int mealsEaten) throws InterruptedException {
        lock.lockInterruptibly();
        try {

            while (this.left.get() == null || this.right.get() == null) {
                logger.trace("Top of eat loop");
                if (this.left.get() == null) {
                    logger.trace("Left is null");
                    this.leftNeighbor.requestRightChopstick(this.lock, this.condition, this.left);
                    continue;
                }

                if (this.right.get() == null) {
                    logger.trace("Right is null");
                    this.rightNeighbor.requestLeftChopstick(this.lock, this.condition, this.right);
                    continue;
                }

                logger.trace("Awaiting");
                this.condition.await();
                logger.trace("Returned from await");
            }

            this.left.get().acquireOwnership();
            this.right.get().acquireOwnership();

            logger.info("eating meal #" + mealsEaten);
            randomSleep();

            this.left.get().use();
            this.right.get().use();

            this.left.get().releaseOwnership();
            this.right.get().releaseOwnership();

            this.publishChopstickIfRequested(this.left, this.leftState);
            this.publishChopstickIfRequested(this.right, this.rightState);
        } finally {
            lock.unlock();
        }

    }

    void requestLeftChopstick(Lock lock, Condition condition, ChopstickHolder otherHolder) throws InterruptedException {
        logger.trace("Requesting left");
        registerChopstickRequest(this.leftState, this.left, lock, condition, otherHolder);
    }

    void requestRightChopstick(Lock lock, Condition condition, ChopstickHolder otherHolder)
            throws InterruptedException {
        logger.trace("Requesting right");
        registerChopstickRequest(this.rightState, this.right, lock, condition, otherHolder);
    }

    @SuppressWarnings({"AssignmentToNull"})
    private void registerChopstickRequest(AwaitingChopstickState requestState,
                                          ChopstickHolder ourHolder, Lock lock, Condition condition,
                                          ChopstickHolder otherHolder) throws InterruptedException {

        this.lock.lockInterruptibly();
        try {
            requestState.set(lock, condition, otherHolder);

            this.publishChopstickIfRequested(ourHolder, requestState);
        } finally {
            this.lock.unlock();
        }
    }

    /**
     * Publishes contents of ourHolder if it's dirty.
     *
     * Only called when lock is held.
     *
     * @param ourHolder    the holder to pull the chopstick from
     * @param requestState the state about who is waiting for it
     * @throws InterruptedException if thread is interrupted
     */
    private void publishChopstickIfRequested(ChopstickHolder ourHolder, AwaitingChopstickState requestState)
            throws InterruptedException {
        ChandyMisraChopstick currentChopstick = ourHolder.get();
        if (requestState.isClear()) {
            logger.trace("Not publishing, no request state");
            return;
        }
        if (currentChopstick.isClean()) {
            logger.trace("Not publishing, chopstick is clean");
            return;
        }

        logger.trace("Publishing");
        // we no longer have this chopstick
        ourHolder.set(null);

        // it's clean when it goes to another philosopher
        currentChopstick.wash();

        // inform anyone waiting on that condition
        requestState.getLock().lockInterruptibly();
        try {
            requestState.getHolder().set(currentChopstick);
            requestState.getCondition().signal();
        } finally {
            requestState.getLock().unlock();
        }

        requestState.clear();

    }

    private static class ChopstickHolder {
        @Nullable
        private ChandyMisraChopstick chopstick;

        public ChandyMisraChopstick get() {
            return chopstick;
        }

        public void set(@Nullable ChandyMisraChopstick chopstick) {
            this.chopstick = chopstick;
        }
    }

    private static class AwaitingChopstickState {
        @Nullable
        private Lock lock;
        @Nullable
        private Condition condition;
        @Nullable
        private ChopstickHolder holder;

        public Lock getLock() {
            return lock;
        }

        public boolean isClear() {
            return this.lock == null;
        }

        public void clear() {
            this.lock = null;
            this.condition = null;
            this.holder = null;
        }

        public void set(@Nonnull Lock lock, @Nonnull Condition condition, @Nonnull ChopstickHolder holder) {
            this.lock = lock;
            this.condition = condition;
            this.holder = holder;
        }

        public Condition getCondition() {
            return condition;
        }

        public ChopstickHolder getHolder() {
            return holder;
        }
    }
}
