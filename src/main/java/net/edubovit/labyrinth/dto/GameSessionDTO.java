package net.edubovit.labyrinth.dto;

import java.util.UUID;

public record GameSessionDTO(UUID id, String mapUrl, PlayerCoordinates playerCoordinates,
                             Boolean finish, Boolean successMove) {

    public record PlayerCoordinates(int x, int y) {
    }

}
