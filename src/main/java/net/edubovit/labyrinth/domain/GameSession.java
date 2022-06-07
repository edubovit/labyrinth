package net.edubovit.labyrinth.domain;

import net.edubovit.labyrinth.service.LabyrinthProcessor;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class GameSession {

    private UUID id;

    private LabyrinthProcessor labyrinth;

    private Cell playerPosition;

}
