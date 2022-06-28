package net.edubovit.labyrinth.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.TaskScheduler;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@RequiredArgsConstructor
public abstract class AI implements Runnable {

    private final TaskScheduler taskScheduler;

    private final int delayMillis;

    private boolean disabled = false;

    @Override
    public void run() {
        if (!disabled) {
            step();
            if (isActive()) {
                taskScheduler.schedule(this, Instant.now().plus(delayMillis, ChronoUnit.MILLIS));
            }
        }
    }

    public void start() {
        if (!disabled && isActive()) {
            taskScheduler.schedule(this, Instant.now());
        }
    }

    public void shutdown() {
        disabled = true;
    }

    protected abstract void step();

    protected abstract boolean isActive();

}
