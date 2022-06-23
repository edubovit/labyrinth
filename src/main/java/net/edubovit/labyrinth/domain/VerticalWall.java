package net.edubovit.labyrinth.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter(AccessLevel.PACKAGE)
public final class VerticalWall extends Wall {

    private transient Cell left;
    private transient Cell right;

    public VerticalWall() {
    }

    public VerticalWall(State state) {
        super(state);
    }

}
