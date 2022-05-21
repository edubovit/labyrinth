package net.edubovit.labyrinth;

import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

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
        ABSENT(Color.WHITE), PLAN(Color.DARKGRAY), FINAL(Color.BLACK);

        private final Color color;
    }

}
