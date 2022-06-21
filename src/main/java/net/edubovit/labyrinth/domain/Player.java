package net.edubovit.labyrinth.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Collection;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Player implements Serializable {

    @EqualsAndHashCode.Include
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
