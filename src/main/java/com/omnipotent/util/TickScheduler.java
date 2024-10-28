package com.omnipotent.util;

import lombok.Getter;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.time.DateTimeException;
import java.time.Duration;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

public class TickScheduler {

    private static final Map<Integer, TickTask> tasks = new ConcurrentHashMap<>();
    private static final Queue<Integer> tasksToCancel = new LinkedList<>();
    private static long ticksElapsed = 0;
    private static final Random rand = new Random();

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) throws Exception {
        if (event.phase == TickEvent.Phase.START) {
            ticksElapsed++;
            for (Map.Entry<Integer, TickTask> task : tasks.entrySet()) {
                TickTask tickTask = task.getValue();
                tickTask.executeTask(ticksElapsed);
                if (tickTask.isDone())
                    tasksToCancel.add(task.getKey());
            }
        }
        while (!tasksToCancel.isEmpty()) {
            Integer task = tasksToCancel.poll();
            tasks.remove(task);
        }
        if (tasks.isEmpty())
            ticksElapsed = 0;
    }

    public static int scheduleWithCondition(Duration interval, Callable<Boolean> task) {
        int id;
        do {
            id = rand.nextInt();
        } while (tasks.containsKey(id));
        tasks.put(id, checkAndConvertToTickTask(interval, task));
        return id;
    }

    public static int scheduleFromHere(Duration timeUnit, Runnable task) {
        int id;
        do {
            id = rand.nextInt();
        } while (tasks.containsKey(id));
        tasks.put(id, checkAndConvertToTickTask(timeUnit, task));
        return id;
    }

    private static TickTask checkAndConvertToTickTask(Duration interval, Object task) {
        if (interval.isNegative() || interval.isZero())
            throw new DateTimeException("Duration is negative or zero");
        long millis = interval.toMillis();
        if (millis < 50)
            throw new DateTimeException("Duration less than 50 milliseconds");
        return new TickTask(Math.round((float) millis / 50), task);
    }

    private static class TickTask {
        private static final int hasNoPredeterminedExecutionsLeft = -100;
        private final int initialDelay;
        private int elapsedTicks;
        private boolean hasCondition;
        private boolean taskWillCancel;
        private long creationTick;
        private int executionsLeft;
        @Getter
        private final int tickInterval;
        @Getter
        private final Object task;

        public TickTask(int tickInterval, int executions, Object task) {
            this.initialDelay = 0;
            this.tickInterval = tickInterval;
            this.executionsLeft = executions;
            this.task = task;
        }

        public TickTask(int tickInterval, int executions) {
            this(tickInterval, executions, null);
            this.creationTick = ticksElapsed;
        }

        public TickTask(int tickInterval, Object task) {
            this(tickInterval, hasNoPredeterminedExecutionsLeft, task);
        }

        public TickTask(int tickInterval) {
            this(tickInterval, hasNoPredeterminedExecutionsLeft);
        }

        public void executeTask(long ticksElapsed) throws Exception {
            if (task instanceof Runnable runnable) {
                if (ticksElapsed - creationTick == tickInterval) {
                    runnable.run();
                    taskWillCancel = true;
                }
            } else if (task instanceof Callable<?> callable && ticksElapsed - creationTick >= tickInterval && ((Callable<Boolean>) callable).call())
                        taskWillCancel = true;
        }

        public boolean isDone() {
            if (hasPredeterminedExecutionsLeft()) {
                if (executionsLeft > 0) {
                    executionsLeft--;
                    return false;
                } else return true;
            } else return taskWillCancel;
        }

        private boolean hasPredeterminedExecutionsLeft() {
            return executionsLeft != hasNoPredeterminedExecutionsLeft;
        }
    }
}
