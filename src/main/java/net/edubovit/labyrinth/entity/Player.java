package net.edubovit.labyrinth.entity;

import lombok.Data;

import java.util.Collection;

@Data
public class Player {

    private Cell position;

    private Collection<Cell> seenTiles;

}
