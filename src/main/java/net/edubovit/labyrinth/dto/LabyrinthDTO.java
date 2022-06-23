package net.edubovit.labyrinth.dto;

import net.edubovit.labyrinth.domain.Labyrinth;
import net.edubovit.labyrinth.domain.Player;
import net.edubovit.labyrinth.domain.VisibilityMatrix;

import java.util.Collection;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.groupingBy;

public record LabyrinthDTO(CellDTO[][] map) {

    public LabyrinthDTO(Labyrinth labyrinth, Collection<Player> players, VisibilityMatrix visibilityMatrix) {
        this(new CellDTO[labyrinth.getHeight()][labyrinth.getWidth()]);
        var playersByCell = players.stream().collect(groupingBy(Player::getPosition));
        var matrix = labyrinth.getMatrix();
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                var cell = matrix[i][j];
                map[i][j] = new CellDTO(
                        cell,
                        playersByCell.getOrDefault(cell, emptyList()),
                        visibilityMatrix.getVisibility(cell));
            }
        }
    }

}
