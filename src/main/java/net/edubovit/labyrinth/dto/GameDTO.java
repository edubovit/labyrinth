package net.edubovit.labyrinth.dto;

import net.edubovit.labyrinth.domain.Game;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record GameDTO(UUID id,
                      @JsonUnwrapped LabyrinthDTO map,
                      int turns,
                      boolean finish) {

    public GameDTO(Game game, String owner) {
        this(game.getId(), game.buildLabyrinthDTO(), game.turns(owner), game.finish(owner));
    }

    @Override
    public String toString() {
        return "GameDTO{" +
                "id=" + id +
                ", turns=" + turns +
                ", finish=" + finish +
                '}';
    }

}
