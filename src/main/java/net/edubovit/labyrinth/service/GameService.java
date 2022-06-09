package net.edubovit.labyrinth.service;

import net.edubovit.labyrinth.domain.GameSession;
import net.edubovit.labyrinth.dto.CreateGameRequestDTO;
import net.edubovit.labyrinth.dto.GameSessionDTO;
import net.edubovit.labyrinth.exception.NotFoundException;
import net.edubovit.labyrinth.repository.ImageRepository;
import net.edubovit.labyrinth.repository.SessionRepository;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Slf4j
public class GameService {

    private final SessionRepository sessionRepository;

    private final ImageRepository imageRepository;

    public GameSessionDTO create(CreateGameRequestDTO request) {
        log.info("creating game: {}", request.toString());
        var processor = new LabyrinthProcessor(request.width(), request.height(), request.seed(),
                request.cellSize(), request.cellBorder(), request.outerBorder());
        processor.generate();
        var session = GameSession.builder()
                .id(UUID.randomUUID())
                .processor(processor)
                .lastUsed(LocalDateTime.now())
                .build();
        sessionRepository.save(session.getId(), session);
        String mapUrl = "/image/" + saveImage(processor.printMap());
        session.setMapUrl(mapUrl);
        var response = new GameSessionDTO(
                session.getId(),
                mapUrl,
                processor.getLabyrinthDTO(),
                processor.playerCoordinates(),
                session.getTurns(),
                processor.finish(),
                null);
        log.info("game created: {}", response.toString());
        return response;
    }

    public GameSessionDTO getSession(UUID id) {
        log.info("reading session: {}", id.toString());
        var response = sessionRepository.get(id)
                .map(session -> new GameSessionDTO(
                        session.getId(),
                        session.getMapUrl(),
                        session.getProcessor().getLabyrinthDTO(),
                        session.getProcessor().playerCoordinates(),
                        session.getTurns(),
                        null,
                        null))
                .orElseThrow(NotFoundException::new);
        log.info("retrieved session: {}", response.toString());
        return response;
    }

    public GameSessionDTO moveUp(UUID sessionId) {
        log.info("moving up: {}", sessionId.toString());
        return move(sessionId, LabyrinthProcessor::moveUp);
    }

    public GameSessionDTO moveDown(UUID sessionId) {
        log.info("moving down: {}", sessionId.toString());
        return move(sessionId, LabyrinthProcessor::moveDown);
    }

    public GameSessionDTO moveLeft(UUID sessionId) {
        log.info("moving left: {}", sessionId.toString());
        return move(sessionId, LabyrinthProcessor::moveLeft);
    }

    public GameSessionDTO moveRight(UUID sessionId) {
        log.info("moving right: {}", sessionId.toString());
        return move(sessionId, LabyrinthProcessor::moveRight);
    }

    private GameSessionDTO move(UUID sessionId, Function<LabyrinthProcessor, Boolean> action) {
        var session = sessionRepository.get(sessionId)
                .orElseThrow(NotFoundException::new);
        session.setTurns(session.getTurns() + 1);
        session.setLastUsed(LocalDateTime.now());
        var processor = session.getProcessor();
        boolean successMove = action.apply(processor);
        if (processor.finish()) {
            sessionRepository.delete(sessionId);
        }
        String mapUrl = "/image/" + saveImage(processor.printMap());
        session.setMapUrl(mapUrl);
        var response = new GameSessionDTO(sessionId, mapUrl, processor.getLabyrinthDTO(), processor.playerCoordinates(),
                session.getTurns(), processor.finish(), successMove);
        log.info("movement result: {}", response);
        return response;
    }

    @SneakyThrows
    private UUID saveImage(BufferedImage image) {
        var imageId = UUID.randomUUID();
        var imageBytes = new ByteArrayOutputStream();
        ImageIO.write(image, "png", imageBytes);
        imageRepository.save(imageId, imageBytes.toByteArray());
        return imageId;
    }

}
