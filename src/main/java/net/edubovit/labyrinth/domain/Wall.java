package net.edubovit.labyrinth.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.awt.Color;

@Getter
@Setter
public abstract sealed class Wall permits HorizontalWall, VerticalWall {

    private State state;

    protected Wall(State state) {
        this.state = state;
    }

    @Getter
    @RequiredArgsConstructor
    public enum State {
        ABSENT(Color.WHITE), PLAN(Color.GRAY), FINAL(Color.BLACK);

        private final Color color;
    }

}
