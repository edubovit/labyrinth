package net.edubovit.labyrinth.service;

import net.edubovit.labyrinth.config.properties.ApplicationProperties;
import net.edubovit.labyrinth.domain.Game;
import net.edubovit.labyrinth.dto.CreateGameRequestDTO;
import net.edubovit.labyrinth.dto.GameDTO;
import net.edubovit.labyrinth.event.PlayerLeftEvent;
import net.edubovit.labyrinth.event.TilesChangedEvent;
import net.edubovit.labyrinth.exception.NotFoundException;
import net.edubovit.labyrinth.repository.cached.GameCachedRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.BiFunction;

@Service
@RequiredArgsConstructor
@Slf4j
public class GameService {

    private final EventService eventService;

    private final SessionUtilService sessionUtilService;

    private final GameCachedRepository gameCachedRepository;

    private final ApplicationProperties properties;

    @Transactional
    public GameDTO create(CreateGameRequestDTO request) {
        String username = sessionUtilService.getUsername();
        log.info("creating new game for {}: {}", username, request);
        gameCachedRepository.getByUsername(username)
                .ifPresent(prevGame -> {
                    eventService.sendGameEvent(prevGame.getId(), prevGame.leave(username));
                    eventService.sendGameEvent(prevGame.getId(), new PlayerLeftEvent(username));
                    gameCachedRepository.deleteGameIfAbandoned(prevGame);
                });
        var game = new Game(request.width(), request.height(), request.seed());
        game.generate();
        game.join(username);
        gameCachedRepository.join(username, game);
        var response = new GameDTO(game, username);
        log.info("game created: {}", response);
        return response;
    }

    @Transactional
    public GameDTO join(String hostUsername) {
        String username = sessionUtilService.getUsername();
        log.info("{} joins game of {}", username, hostUsername);
        var game = gameCachedRepository.getByUsername(hostUsername)
                        .orElseThrow(NotFoundException::new);
        gameCachedRepository.getByUsername(username)
                .ifPresent(prevGame -> {
                    eventService.sendGameEvent(prevGame.getId(), prevGame.leave(username));
                    eventService.sendGameEvent(prevGame.getId(), new PlayerLeftEvent(username));
                    gameCachedRepository.deleteGameIfAbandoned(prevGame);
                });
        eventService.sendGameEvent(game.getId(), game.join(username));
        gameCachedRepository.join(username, game);
        var response = new GameDTO(game, username);
        log.info("{} successfully joined game: {}", username, response);
        return response;
    }

    @Transactional(readOnly = true)
    public GameDTO getCurrent() {
        String username = sessionUtilService.getUsername();
        log.info("reading game for user {}", username);
        var game = gameCachedRepository.getByUsername(username)
                .orElseThrow(NotFoundException::new);
        var response =  new GameDTO(game, username);
        log.info("retrieved game: {}", response);
        return response;
    }

    @Transactional
    public void move(MovementDirection direction) {
        move(sessionUtilService.getUsername(), direction);
    }

    @Transactional
    public TilesChangedEvent move(String username, MovementDirection direction) {
        log.info("moving {} {}", username, direction.toString().toLowerCase());
        var game = gameCachedRepository.getByUsername(username)
                .orElseThrow(NotFoundException::new);
        var response = direction.action.apply(game, username);
        if (game.turns(username) % properties.getGameFlushPeriod() == 0) {
            gameCachedRepository.persist(game);
        }
        log.info("movement result: game {}, {}", game.getId(), response);
        eventService.sendGameEvent(game.getId(), response);
        return response;
    }

    @RequiredArgsConstructor
    public enum MovementDirection {
        UP(Game::moveUp),
        DOWN(Game::moveDown),
        LEFT(Game::moveLeft),
        RIGHT(Game::moveRight),
        SOLUTION(Game::moveBySolution);

        final BiFunction<Game, String, TilesChangedEvent> action;
    }

}
