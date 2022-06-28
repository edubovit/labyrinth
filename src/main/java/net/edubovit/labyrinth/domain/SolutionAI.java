package net.edubovit.labyrinth.domain;

import net.edubovit.labyrinth.service.GameService;

import org.springframework.scheduling.TaskScheduler;

import static net.edubovit.labyrinth.service.GameService.MovementDirection.SOLUTION;

public class SolutionAI extends AI {

    private final GameService gameService;

    private final String username;

    private boolean finished = false;

    public SolutionAI(TaskScheduler taskScheduler, int delayMillis, GameService gameService, String username) {
        super(taskScheduler, delayMillis);
        this.gameService = gameService;
        this.username = username;
    }

    @Override
    protected void step() {
        finished = gameService.move(username, SOLUTION).finish();
    }

    @Override
    protected boolean isActive() {
        return !finished;
    }

}
