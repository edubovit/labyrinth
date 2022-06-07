package net.edubovit.labyrinth.domain;

import lombok.Getter;

import static net.edubovit.labyrinth.domain.Wall.State.PLAN;

@Getter
public final class HorizontalWall extends Wall {

    private final Cell up;
    private final Cell down;

    public HorizontalWall(Cell up, Cell down) {
        this(up, down, PLAN);
    }

    public HorizontalWall(Cell up, Cell down, State state) {
        super(state);
        this.up = up;
        this.down = down;
    }

}
