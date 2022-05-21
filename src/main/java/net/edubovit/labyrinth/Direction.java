package net.edubovit.labyrinth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Direction<ORIENTATION extends Wall> {

    private Cell cell;
    private ORIENTATION wall;

}
