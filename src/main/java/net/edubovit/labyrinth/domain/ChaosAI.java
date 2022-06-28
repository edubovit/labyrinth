package net.edubovit.labyrinth.domain;

import net.edubovit.labyrinth.service.GameService;

import org.springframework.scheduling.TaskScheduler;

import java.util.concurrent.ThreadLocalRandom;

import static net.edubovit.labyrinth.service.GameService.MovementDirection.DOWN;
import static net.edubovit.labyrinth.service.GameService.MovementDirection.LEFT;
import static net.edubovit.labyrinth.service.GameService.MovementDirection.RIGHT;
import static net.edubovit.labyrinth.service.GameService.MovementDirection.UP;

public class ChaosAI extends AI {

    private final GameService gameService;

    private final String username;

    public ChaosAI(TaskScheduler taskScheduler, int delayMillis, GameService gameService, String username) {
        super(taskScheduler, delayMillis);
        this.gameService = gameService;
        this.username = username;
    }

    @Override
    protected void step() {
        float roll = ThreadLocalRandom.current().nextFloat();
        if (roll < 0.25) {
            gameService.move(username, UP);
        } else if (roll < 0.5) {
            gameService.move(username, LEFT);
        } else if (roll < 0.75) {
            gameService.move(username, RIGHT);
        } else {
            gameService.move(username, DOWN);
        }
    }

    @Override
    protected boolean isActive() {
        return true;
    }

}
