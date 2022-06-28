package net.edubovit.labyrinth.service;

import net.edubovit.labyrinth.config.properties.ApplicationProperties;
import net.edubovit.labyrinth.domain.AI;
import net.edubovit.labyrinth.domain.ChaosAI;
import net.edubovit.labyrinth.domain.SolutionAI;
import net.edubovit.labyrinth.exception.NotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommandService {

    private final GameService gameService;

    private final ApplicationProperties properties;

    private final TaskScheduler taskScheduler;

    private final SessionUtilService sessionUtilService;

    private final Map<String, AI> aiByUsername = new HashMap<>();

    public void chaos(Integer delayMillis) {
        String username = sessionUtilService.getUsername();
        if (!properties.isCheatsEnabled()) {
            log.warn("{} tried to cheat via ChaosAI!", username);
            return;
        }
        int delayMillisCalculated = delayMillis == null ? 50 : delayMillis;
        log.warn("{} is cheating! Running ChaosAI with delay {}ms", username, delayMillisCalculated);
        var ai = new ChaosAI(taskScheduler, delayMillisCalculated, gameService, username);
        ai.start();
        aiByUsername.put(username, ai);
    }

    public void solve(Integer delayMillis) {
        String username = sessionUtilService.getUsername();
        if (!properties.isCheatsEnabled()) {
            log.warn("{} tried to cheat via SolutionAI!", username);
            return;
        }
        int delayMillisCalculated = delayMillis == null ? 50 : delayMillis;
        log.warn("{} is cheating! Running SolutionAI with delay {}ms", username, delayMillisCalculated);
        var ai = new SolutionAI(taskScheduler, delayMillisCalculated, gameService, username);
        ai.start();
        aiByUsername.put(username, ai);
    }

    public void stop() {
        String username = sessionUtilService.getUsername();
        if (!properties.isCheatsEnabled()) {
            log.warn("{} tried to shutdown AI but the cheating is disabled", username);
            return;
        }
        log.warn("disabling AI for {}", username);
        var ai = aiByUsername.get(username);
        if (ai == null) {
            throw new NotFoundException();
        }
        ai.shutdown();
    }

}
