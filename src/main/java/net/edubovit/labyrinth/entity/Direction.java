package net.edubovit.labyrinth.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class Direction<ORIENTATION extends Wall> implements Serializable {

    private Cell cell;
    private ORIENTATION wall;

}
