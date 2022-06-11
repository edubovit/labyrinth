package net.edubovit.labyrinth.service;

import net.edubovit.labyrinth.entity.Game;
import net.edubovit.labyrinth.dto.LabyrinthDTO;
import net.edubovit.labyrinth.exception.NotFoundException;
import net.edubovit.labyrinth.repository.memory.GameCache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class LabyrinthService {

    private final GameCache gameCache;

    public LabyrinthDTO getLabyrinth(UUID id) {
        log.info("retrieving labyrinth: {}", id.toString());
        return gameCache.get(id)
                .map(Game::getProcessor)
                .map(LabyrinthProcessor::getLabyrinthDTO)
                .orElseThrow(NotFoundException::new);
    }

}
