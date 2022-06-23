package net.edubovit.labyrinth.dto;

import net.edubovit.labyrinth.domain.Cell;
import net.edubovit.labyrinth.domain.Player;
import net.edubovit.labyrinth.domain.Visibility;

import java.util.ArrayList;
import java.util.Collection;

import static net.edubovit.labyrinth.domain.Wall.State.FINAL;

public record CellDTO(boolean wallUp,
                      boolean wallDown,
                      boolean wallLeft,
                      boolean wallRight,
                      Visibility visibility,
                      Collection<PlayerDTO> players) {

    public CellDTO(Cell cell, Collection<Player> players, Visibility visibility) {
        this(
                cell.getUp().getWall().getState() == FINAL,
                cell.getDown().getWall().getState() == FINAL,
                cell.getLeft().getWall().getState() == FINAL,
                cell.getRight().getWall().getState() == FINAL,
                visibility,
                players.stream().map(PlayerDTO::new).toList()
        );
    }

}
