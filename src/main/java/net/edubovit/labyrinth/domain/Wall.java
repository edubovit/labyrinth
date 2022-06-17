package net.edubovit.labyrinth.domain;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public abstract sealed class Wall implements Serializable permits HorizontalWall, VerticalWall {

    private State state;

    protected Wall(State state) {
        this.state = state;
    }

    public enum State { ABSENT, PLAN, FINAL }

}
