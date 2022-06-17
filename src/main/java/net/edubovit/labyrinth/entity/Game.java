package net.edubovit.labyrinth.entity;

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
