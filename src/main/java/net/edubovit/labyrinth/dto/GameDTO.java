package net.edubovit.labyrinth.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record GameDTO(UUID id,
                      @JsonUnwrapped LabyrinthDTO map,
                      Coordinates playerCoordinates,
                      int turns,
                      boolean finish) {

    @Override
    public String toString() {
        return "GameDTO{" +
                "id=" + id +
                ", playerCoordinates=" + playerCoordinates +
                ", turns=" + turns +
                ", finish=" + finish +
                '}';
    }

}
