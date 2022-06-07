package net.edubovit.labyrinth.service;

import net.edubovit.labyrinth.domain.GameSession;
import net.edubovit.labyrinth.domain.Labyrinth;
import net.edubovit.labyrinth.dto.CreateGameRequestDTO;
import net.edubovit.labyrinth.dto.GameSessionDTO;
import net.edubovit.labyrinth.repository.SessionRepository;

import javafx.embed.swing.SwingFXUtils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.io.File;

@Service
@RequiredArgsConstructor
public class LabyrinthApiService {

    private final SessionRepository sessionRepository;

    @SneakyThrows
    public GameSessionDTO create(CreateGameRequestDTO request) {
        var labyrinth = new Labyrinth(request.width(), request.height(), request.seed());
        var labyrinthView = new LabyrinthView(request.width(), request.height(), request.cellSize(), request.cellBorder(), request.outerBorder());
        var labyrinthProcessor = new LabyrinthProcessor(labyrinth, labyrinthView);
        labyrinthProcessor.generate();
        var writableImage = labyrinthProcessor.printMap();
        var bufferedImage = SwingFXUtils.fromFXImage(writableImage, null);
        ImageIO.write(bufferedImage, "png", new File("test.png"));
        var session = GameSession.builder()
                .labyrinth(labyrinthProcessor)
                .playerPosition(labyrinth.getCell(request.height() - 1, request.width() - 1))
                .build();
        sessionRepository.save(session);
        return new GameSessionDTO(session.getId(), null);
    }

}
