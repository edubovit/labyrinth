package net.edubovit.labyrinth.domain;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class Direction<ORIENTATION extends Wall> implements Serializable {

    private transient Cell cell;
    private ORIENTATION wall;

}
