package net.edubovit.labyrinth.dto;

import net.edubovit.labyrinth.domain.Labyrinth;
import net.edubovit.labyrinth.domain.Player;

import java.util.Collection;

public record LabyrinthDTO(CellDTO[][] map) {

    public LabyrinthDTO(Labyrinth labyrinth, Collection<Player> players) {
        this(new CellDTO[labyrinth.getHeight()][labyrinth.getWidth()]);
        var matrix = labyrinth.getMatrix();
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                map[i][j] = new CellDTO(matrix[i][j]);
            }
        }
        players.forEach(player -> map[player.getPosition().getI()][player.getPosition().getJ()]
                .players()
                .add(new PlayerDTO(player)));
    }

}
