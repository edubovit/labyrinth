package net.edubovit.labyrinth.domain;

import net.edubovit.labyrinth.domain.LabyrinthProcessor;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class Game implements Serializable {

    private UUID id;

    private LabyrinthProcessor processor;

    private LocalDateTime lastUsed;

}
