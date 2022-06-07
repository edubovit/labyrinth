package net.edubovit.labyrinth.dto;

import java.util.UUID;

public record GameSessionDTO(UUID id, String mapUrl, Boolean finish, Boolean successMove) {
}
