package net.edubovit.labyrinth.service;

import net.edubovit.labyrinth.dto.CreateGameRequestDTO;
import net.edubovit.labyrinth.dto.GameDTO;
import net.edubovit.labyrinth.entity.Game;
import net.edubovit.labyrinth.exception.NotFoundException;
import net.edubovit.labyrinth.repository.cached.UserGameCachedRepository;
import net.edubovit.labyrinth.repository.db.UserRepository;
import net.edubovit.labyrinth.repository.memory.GameCache;
import net.edubovit.labyrinth.util.SessionUtils;

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

    private final GameCache gameCache;

    private final UserRepository userRepository;

    private final UserGameCachedRepository userGameCachedRepository;

    @Transactional
    public GameDTO create(CreateGameRequestDTO request) {
        log.info("creating game: {}", request.toString());
        var processor = new LabyrinthProcessor(request.width(), request.height(), request.seed());
        processor.generate();
        var game = Game.builder()
                .id(UUID.randomUUID())
                .processor(processor)
                .lastUsed(LocalDateTime.now())
                .build();
        var username = SessionUtils.getUsername();
        userGameCachedRepository.flushUser(username);
        gameCache.save(game.getId(), game);
        userRepository.updateGameForUser(game.getId(), username);
        var response = new GameDTO(
                game.getId(),
                processor.getLabyrinthDTO(),
                processor.playerCoordinates(),
                game.getTurns(),
                processor.finish(),
                null);
        log.info("game created: {}", response.toString());
        return response;
    }

    @Transactional(readOnly = true)
    public GameDTO getCurrent() {
        var username = SessionUtils.getUsername();
        log.info("reading game for user {}", username);
        var game = userGameCachedRepository.getGameByUsername(username)
                .orElseThrow(NotFoundException::new);
        var response =  new GameDTO(
                game.getId(),
                game.getProcessor().getLabyrinthDTO(),
                game.getProcessor().playerCoordinates(),
                game.getTurns(),
                null,
                null);
        log.info("retrieved game: {}", response.toString());
        return response;
    }

    @Transactional
    public GameDTO moveUp() {
        return move(MovementDirection.UP);
    }

    @Transactional
    public GameDTO moveDown() {
        return move(MovementDirection.DOWN);
    }

    @Transactional
    public GameDTO moveLeft() {
        return move(MovementDirection.LEFT);
    }

    @Transactional
    public GameDTO moveRight() {
        return move(MovementDirection.RIGHT);
    }

    private GameDTO move(MovementDirection direction) {
        var username = SessionUtils.getUsername();
        log.info("moving {} {}", username, direction.toString().toLowerCase());
        var game = userGameCachedRepository.getGameByUsername(username)
                .orElseThrow(NotFoundException::new);
        game.setTurns(game.getTurns() + 1);
        game.setLastUsed(LocalDateTime.now());
        var processor = game.getProcessor();
        boolean successMove = direction.action.apply(processor);
        var response = new GameDTO(game.getId(), processor.getLabyrinthDTO(), processor.playerCoordinates(),
                game.getTurns(), processor.finish(), successMove);
        log.info("movement result: {}", response);
        return response;
    }

    @RequiredArgsConstructor
    private enum MovementDirection {
        UP(LabyrinthProcessor::moveUp),
        DOWN(LabyrinthProcessor::moveDown),
        LEFT(LabyrinthProcessor::moveLeft),
        RIGHT(LabyrinthProcessor::moveRight);

        final Function<LabyrinthProcessor, Boolean> action;
    }

}
