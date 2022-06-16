package net.edubovit.labyrinth.service;

import net.edubovit.labyrinth.config.properties.ApplicationProperties;
import net.edubovit.labyrinth.dto.CreateGameRequestDTO;
import net.edubovit.labyrinth.dto.GameDTO;
import net.edubovit.labyrinth.dto.MovementResultDTO;
import net.edubovit.labyrinth.entity.Game;
import net.edubovit.labyrinth.entity.LabyrinthProcessor;
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
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Slf4j
public class GameService {

    private final SessionUtilService sessionUtilService;

    private final GameCachedRepository gameCachedRepository;

    private final GameCache gameCache;

    private final UserRepository userRepository;

    private final UserGameCachedRepository userGameCachedRepository;

    private final ApplicationProperties properties;

    @Transactional
    public GameDTO create(CreateGameRequestDTO request) {
        log.info("creating game: {}", request);
        var processor = new LabyrinthProcessor(request.width(), request.height(), request.seed());
        processor.generate();
        var game = Game.builder()
                .id(UUID.randomUUID())
                .processor(processor)
                .lastUsed(LocalDateTime.now())
                .build();
        var username = sessionUtilService.getUsername();
        userGameCachedRepository.deleteUserGame(username);
        gameCache.save(game.getId(), game);
        userRepository.updateGameForUser(game.getId(), username);
        var response = new GameDTO(
                game.getId(),
                processor.buildLabyrinthDTO(),
                processor.playerCoordinates(),
                processor.turns(),
                processor.finish());
        log.info("game created: {}", response);
        return response;
    }

    @Transactional(readOnly = true)
    public GameDTO getCurrent() {
        var username = sessionUtilService.getUsername();
        log.info("reading game for user {}", username);
        var game = userGameCachedRepository.getGameByUsername(username)
                .orElseThrow(NotFoundException::new);
        var processor = game.getProcessor();
        var response =  new GameDTO(
                game.getId(),
                processor.buildLabyrinthDTO(),
                processor.playerCoordinates(),
                processor.turns(),
                null);
        log.info("retrieved game: {}", response);
        return response;
    }

    @Transactional
    public MovementResultDTO moveUp() {
        return move(MovementDirection.UP);
    }

    @Transactional
    public MovementResultDTO moveDown() {
        return move(MovementDirection.DOWN);
    }

    @Transactional
    public MovementResultDTO moveLeft() {
        return move(MovementDirection.LEFT);
    }

    @Transactional
    public MovementResultDTO moveRight() {
        return move(MovementDirection.RIGHT);
    }

    private MovementResultDTO move(MovementDirection direction) {
        var username = sessionUtilService.getUsername();
        log.info("moving {} {}", username, direction.toString().toLowerCase());
        var game = userGameCachedRepository.getGameByUsername(username)
                .orElseThrow(NotFoundException::new);
        game.setLastUsed(LocalDateTime.now());
        var processor = game.getProcessor();
        if (processor.turns() % properties.getGameFlushPeriod() == 0) {
            gameCachedRepository.flush(game);
        }
        var response = direction.action.apply(processor);
        log.info("game {} movement result: {}", game.getId(), response);
        return response;
    }

    @RequiredArgsConstructor
    private enum MovementDirection {
        UP(LabyrinthProcessor::moveUp),
        DOWN(LabyrinthProcessor::moveDown),
        LEFT(LabyrinthProcessor::moveLeft),
        RIGHT(LabyrinthProcessor::moveRight);

        final Function<LabyrinthProcessor, MovementResultDTO> action;
    }

}
