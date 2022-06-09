package net.edubovit.labyrinth.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Cell {

    @EqualsAndHashCode.Include
    private final int i;

    @EqualsAndHashCode.Include
    private final int j;

    private Visibility visibility;

    private final Direction<HorizontalWall> up;
    private final Direction<VerticalWall> left;
    private final Direction<VerticalWall> right;
    private final Direction<HorizontalWall> down;

    public Cell(int i, int j) {
        this.i = i;
        this.j = j;
        visibility = Visibility.HIDDEN;
        up = new Direction<>();
        left = new Direction<>();
        right = new Direction<>();
        down = new Direction<>();
    }

}
