package org.mpierce.concurrency.examples.diningphilosophers.waiter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
class Waiter {

    private final Lock stateChangeLock = new ReentrantLock();

    private final Map<ChopstickAssignment, Condition> conditions = new HashMap<>();

    private final Map<WaiterPhilosopher, ChopstickAssignment> pairs = new HashMap<>();

    void initialize(List<WaiterPhilosopher> philosophers) {

        stateChangeLock.lock();
        try {
            List<ChopstickAssignment> assignmentList = new ArrayList<>();
            for (WaiterPhilosopher philosopher : philosophers) {
                final ChopstickAssignment assignment = new ChopstickAssignment();
                assignmentList.add(assignment);
                pairs.put(philosopher, assignment);
                conditions.put(assignment, stateChangeLock.newCondition());
            }

            for (int i = 0; i < assignmentList.size(); i++) {
                int rightIndex = i + 1;
                if (rightIndex == assignmentList.size()) {
                    rightIndex = 0;
                }

                int leftIndex = i - 1;
                if (leftIndex == -1) {
                    leftIndex = assignmentList.size() - 1;
                }

                assignmentList.get(i).setLeftNeighbor(assignmentList.get(leftIndex));
                assignmentList.get(i).setRightNeighbor(assignmentList.get(rightIndex));
            }
        } finally {
            stateChangeLock.unlock();
        }
    }

    void acquirePair(WaiterPhilosopher philosopher) throws InterruptedException {
        stateChangeLock.lockInterruptibly();

        try {
            final ChopstickAssignment assignment = pairs.get(philosopher);
            final Condition masterCondition = conditions.get(assignment);
            final Lock leftSharedLock = assignment.getLeftNeighbor().getSharedLock();
            final Lock rightSharedLock = assignment.getRightNeighbor().getSharedLock();

            while (true) {
                while (!assignment.getExclusiveLock().tryLock()) {
                    masterCondition.await();
                }

                if (!leftSharedLock.tryLock()) {
                    assignment.getExclusiveLock().unlock();
                    masterCondition.signalAll();

                    continue;
                }

                if (!rightSharedLock.tryLock()) {
                    assignment.getExclusiveLock().unlock();
                    masterCondition.signalAll();

                    leftSharedLock.unlock();
                    conditions.get(assignment.getLeftNeighbor()).signalAll();

                    continue;
                }

                break;
            }
        } finally {
            stateChangeLock.unlock();
        }
    }

    void releasePair(WaiterPhilosopher philosopher) throws InterruptedException {
        stateChangeLock.lockInterruptibly();

        try {
            final ChopstickAssignment assignment = pairs.get(philosopher);
            final Lock leftSharedLock = assignment.getLeftNeighbor().getSharedLock();
            final Lock rightSharedLock = assignment.getRightNeighbor().getSharedLock();

            assignment.getExclusiveLock().unlock();
            leftSharedLock.unlock();
            rightSharedLock.unlock();

            conditions.get(assignment).signalAll();
            conditions.get(assignment.getLeftNeighbor()).signalAll();
            conditions.get(assignment.getRightNeighbor()).signalAll();
        } finally {
            stateChangeLock.unlock();
        }
    }
}
