package net.edubovit.labyrinth.entity;

import net.edubovit.labyrinth.util.ReflectionUtils;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serial;
import java.io.Serializable;

import static net.edubovit.labyrinth.entity.Visibility.REVEALED;
import static net.edubovit.labyrinth.entity.Visibility.SEEN;
import static net.edubovit.labyrinth.entity.Wall.State.FINAL;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Cell implements Serializable {

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

    byte tyByteState() {
        byte result = 0;
        if (up.getWall().getState() == FINAL) result |= 1 << 0;
        if (down.getWall().getState() == FINAL) result |= 1 << 1;
        if (left.getWall().getState() == FINAL) result |= 1 << 2;
        if (right.getWall().getState() == FINAL) result |= 1 << 3;
        result |= switch (visibility) {
            case HIDDEN -> 0;
            case REVEALED -> 1 << 4;
            case SEEN -> 1 << 5;
        };
        return result;
    }

    void loadByteState(byte state) {
        if ((state & 1 << 0) != 0) up.getWall().setState(FINAL);
        if ((state & 1 << 1) != 0) down.getWall().setState(FINAL);
        if ((state & 1 << 2) != 0) left.getWall().setState(FINAL);
        if ((state & 1 << 3) != 0) right.getWall().setState(FINAL);
        if ((state & 1 << 4) != 0) visibility = REVEALED;
        else if ((state & 1 << 5) != 0) visibility = SEEN;
    }

    @Serial
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeInt(i);
        out.writeInt(j);
    }

    @Serial
    private void readObject(ObjectInputStream in) throws IOException, NoSuchFieldException, IllegalAccessException {
        ReflectionUtils.setInt("i", this, in.readInt());
        ReflectionUtils.setInt("j", this, in.readInt());
    }

}
