package net.edubovit.labyrinth.service;

import net.edubovit.labyrinth.config.properties.ApplicationProperties;
import net.edubovit.labyrinth.domain.Game;
import net.edubovit.labyrinth.domain.LabyrinthProcessor;
import net.edubovit.labyrinth.dto.CreateGameRequestDTO;
import net.edubovit.labyrinth.dto.GameDTO;
import net.edubovit.labyrinth.dto.MovementResultDTO;
import net.edubovit.labyrinth.exception.NotFoundException;
import net.edubovit.labyrinth.repository.cached.GameCachedRepository;
import net.edubovit.labyrinth.repository.cached.UserGameCachedRepository;
import net.edubovit.labyrinth.repository.db.UserRepository;
import net.edubovit.labyrinth.repository.memory.GameCache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.function.BiFunction;

@Service
@RequiredArgsConstructor
@Slf4j
public class GameService {

    private final EventService eventService;

    private final SessionUtilService sessionUtilService;

    private final GameCachedRepository gameCachedRepository;

    private final GameCache gameCache;

    private final UserRepository userRepository;

    private final UserGameCachedRepository userGameCachedRepository;

    private final ApplicationProperties properties;

    @Transactional
    public GameDTO create(CreateGameRequestDTO request) {
        log.info("creating game: {}", request);
        String username = sessionUtilService.getUsername();
        var processor = new LabyrinthProcessor(request.width(), request.height(), request.seed());
        processor.generate();
        processor.join(username);
        var game = Game.builder()
                .id(UUID.randomUUID())
                .processor(processor)
                .lastUsed(LocalDateTime.now())
                .build();
        userGameCachedRepository.deleteUserGame(username);
        gameCache.save(game.getId(), game);
        userRepository.updateGameForUser(game.getId(), username);
        var response = new GameDTO(
                game.getId(),
                processor.buildLabyrinthDTO(),
                processor.playerCoordinates(username),
                processor.turns(username),
                processor.finish(username));
        log.info("game created: {}", response);
        return response;
    }

    @Transactional
    public GameDTO join(String hostUsername) {
        String username = sessionUtilService.getUsername();
        log.info("{} joins game of {}", username, hostUsername);
        var game = userGameCachedRepository.getGameByUsername(hostUsername)
                        .orElseThrow(NotFoundException::new);
        var processor = game.getProcessor();
        eventService.tilesChanged(game.getId(), processor.join(username));
        userGameCachedRepository.deleteUserGame(username);
        userRepository.updateGameForUser(game.getId(), username);
        var response = new GameDTO(
                game.getId(),
                processor.buildLabyrinthDTO(),
                processor.playerCoordinates(username),
                processor.turns(username),
                processor.finish(username));
        log.info("{} successfully joined game: {}", username, response);
        return response;
    }

    @Transactional(readOnly = true)
    public GameDTO getCurrent() {
        String username = sessionUtilService.getUsername();
        log.info("reading game for user {}", username);
        var game = userGameCachedRepository.getGameByUsername(username)
                .orElseThrow(NotFoundException::new);
        var processor = game.getProcessor();
        var response =  new GameDTO(
                game.getId(),
                processor.buildLabyrinthDTO(),
                processor.playerCoordinates(username),
                processor.turns(username),
                processor.finish(username));
        log.info("retrieved game: {}", response);
        return response;
    }

    @Transactional
    public void moveUp() {
        move(MovementDirection.UP);
    }

    @Transactional
    public void moveDown() {
        move(MovementDirection.DOWN);
    }

    @Transactional
    public void moveLeft() {
        move(MovementDirection.LEFT);
    }

    @Transactional
    public void moveRight() {
        move(MovementDirection.RIGHT);
    }

    private void move(MovementDirection direction) {
        String username = sessionUtilService.getUsername();
        log.info("moving {} {}", username, direction.toString().toLowerCase());
        var game = userGameCachedRepository.getGameByUsername(username)
                .orElseThrow(NotFoundException::new);
        game.setLastUsed(LocalDateTime.now());
        var processor = game.getProcessor();
        if (processor.turns(username) % properties.getGameFlushPeriod() == 0) {
            gameCachedRepository.flush(game);
        }
        var response = direction.action.apply(processor, username);
        log.info("movement result: game {}, {}", game.getId(), response);
        eventService.tilesChanged(game.getId(), response);
    }

    @RequiredArgsConstructor
    private enum MovementDirection {
        UP(LabyrinthProcessor::moveUp),
        DOWN(LabyrinthProcessor::moveDown),
        LEFT(LabyrinthProcessor::moveLeft),
        RIGHT(LabyrinthProcessor::moveRight);

        final BiFunction<LabyrinthProcessor, String, MovementResultDTO> action;
    }

}
