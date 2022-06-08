package net.edubovit.labyrinth.service;

import net.edubovit.labyrinth.domain.GameSession;
import net.edubovit.labyrinth.dto.CreateGameRequestDTO;
import net.edubovit.labyrinth.dto.GameSessionDTO;
import net.edubovit.labyrinth.exception.NotFoundException;
import net.edubovit.labyrinth.repository.ImageRepository;
import net.edubovit.labyrinth.repository.SessionRepository;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class LabyrinthApiService {

    private final SessionRepository sessionRepository;

    private final ImageRepository imageRepository;

    public GameSessionDTO create(CreateGameRequestDTO request) {
        var processor = new LabyrinthProcessor(request.width(), request.height(), request.seed(), request.cellSize(), request.cellBorder(), request.outerBorder());
        processor.generate();
        var session = GameSession.builder()
                .id(UUID.randomUUID())
                .processor(processor)
                .lastUsed(LocalDateTime.now())
                .build();
        sessionRepository.save(session.getId(), session);
        String mapUrl = "/image/" + saveImage(processor.printMap());
        session.setMapUrl(mapUrl);
        return new GameSessionDTO(session.getId(), mapUrl, processor.finish(), null);
    }

    public GameSessionDTO getSession(UUID id) {
        return sessionRepository.get(id)
                .map(session -> new GameSessionDTO(session.getId(), session.getMapUrl(), null, null))
                .orElseThrow(NotFoundException::new);
    }

    public GameSessionDTO moveUp(UUID sessionId) {
        return move(sessionId, LabyrinthProcessor::moveUp);
    }

    public GameSessionDTO moveDown(UUID sessionId) {
        return move(sessionId, LabyrinthProcessor::moveDown);
    }

    public GameSessionDTO moveLeft(UUID sessionId) {
        return move(sessionId, LabyrinthProcessor::moveLeft);
    }

    public GameSessionDTO moveRight(UUID sessionId) {
        return move(sessionId, LabyrinthProcessor::moveRight);
    }

    private GameSessionDTO move(UUID sessionId, Function<LabyrinthProcessor, Boolean> action) {
        var session = sessionRepository.get(sessionId)
                .orElseThrow(NotFoundException::new);
        session.setLastUsed(LocalDateTime.now());
        var processor = session.getProcessor();
        boolean successMove = action.apply(processor);
        if (processor.finish()) {
            sessionRepository.delete(sessionId);
        }
        String mapUrl = "/image/" + saveImage(processor.printMap());
        session.setMapUrl(mapUrl);
        return new GameSessionDTO(sessionId, mapUrl, processor.finish(), successMove);
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
