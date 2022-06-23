package net.edubovit.labyrinth.dto;

import net.edubovit.labyrinth.domain.Cell;
import net.edubovit.labyrinth.domain.Player;
import net.edubovit.labyrinth.domain.Visibility;

import java.util.Collection;

import static java.util.stream.Collectors.joining;

public record CellChangeDTO(int i, int j, CellDTO newState) {

    public CellChangeDTO(Cell cell, Collection<Player> players, Visibility visibility) {
        this(cell.getI(), cell.getJ(), new CellDTO(cell, players, visibility));
    }

    @Override
    public String toString() {
        return "{i=%d,j=%d,%s,[%s]}".formatted(i, j, newState.visibility(),
                newState.players()
                        .stream()
                        .map(PlayerDTO::username)
                        .collect(joining(",")));
    }

}
