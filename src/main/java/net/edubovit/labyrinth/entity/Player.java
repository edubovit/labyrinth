package net.edubovit.labyrinth.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Collection;

@Data
public class Player implements Serializable {

    private Cell position;

    private Collection<Cell> seenTiles;

}
