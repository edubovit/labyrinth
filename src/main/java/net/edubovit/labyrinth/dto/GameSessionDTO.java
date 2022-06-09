package net.edubovit.labyrinth.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record GameSessionDTO(UUID id,
                             String mapUrl,
                             @JsonUnwrapped LabyrinthDTO map,
                             PlayerCoordinates playerCoordinates,
                             int turns,
                             Boolean finish,
                             Boolean successMove) {

    public record PlayerCoordinates(int x, int y, int i, int j) {
    }

    @Override
    public String toString() {
        return "GameSessionDTO{" +
                "id=" + id +
                ", playerCoordinates=" + playerCoordinates +
                ", turns=" + turns +
                ", finish=" + finish +
                ", successMove=" + successMove +
                '}';
    }

}
