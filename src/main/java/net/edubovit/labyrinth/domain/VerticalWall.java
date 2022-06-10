package net.edubovit.labyrinth.domain;

import lombok.Getter;

@Getter
public final class VerticalWall extends Wall {

    private final Cell left;
    private final Cell right;

    public VerticalWall(Cell left, Cell right, State state) {
        super(state);
        this.left = left;
        this.right = right;
    }

}
