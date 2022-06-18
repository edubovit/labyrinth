package net.edubovit.labyrinth.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.Collection;

@Data
public class Player implements Serializable {

    private final String username;

    private Cell position;

    private Collection<Cell> seenTiles;

    private int turns;

    void postDeserialize(Cell[][] matrix) {
        position = matrix[position.getI()][position.getJ()];
        seenTiles = seenTiles.stream()
                .map(tile -> matrix[tile.getI()][tile.getJ()])
                .toList();
    }

}
