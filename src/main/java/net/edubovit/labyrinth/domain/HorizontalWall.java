package net.edubovit.labyrinth.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter(AccessLevel.PACKAGE)
public final class HorizontalWall extends Wall {

    private transient Cell up;
    private transient Cell down;

    public HorizontalWall() {
    }

    public HorizontalWall(State state) {
        super(state);
    }

}
