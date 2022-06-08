package net.edubovit.labyrinth.domain;

import net.edubovit.labyrinth.service.LabyrinthProcessor;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class GameSession implements Identifiable<UUID> {

    private UUID id;

    private LabyrinthProcessor processor;

    private String mapUrl;

    private LocalDateTime lastUsed;

}
