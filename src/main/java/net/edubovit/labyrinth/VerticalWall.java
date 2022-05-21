package net.edubovit.labyrinth;

import lombok.Getter;

import static net.edubovit.labyrinth.Wall.State.PLAN;

@Getter
public final class VerticalWall extends Wall {

    private final Cell left;
    private final Cell right;

    public VerticalWall(Cell left, Cell right) {
        this(left, right, PLAN);
    }

    public VerticalWall(Cell left, Cell right, State state) {
        super(state);
        this.left = left;
        this.right = right;
    }

}
