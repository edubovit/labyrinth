package net.edubovit.labyrinth.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record GameSessionDTO(UUID id, String mapUrl, LabyrinthDTO map, PlayerCoordinates playerCoordinates,
                             int turns, Boolean finish, Boolean successMove) {

    public record PlayerCoordinates(int x, int y, int i, int j) {
    }

}
