package net.edubovit.labyrinth.entity;

import net.edubovit.labyrinth.dto.CellChangeDTO;
import net.edubovit.labyrinth.dto.Coordinates;
import net.edubovit.labyrinth.dto.LabyrinthDTO;
import net.edubovit.labyrinth.dto.MovementResultDTO;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Stream;

import static net.edubovit.labyrinth.config.Defaults.VIEW_DISTANCE;
import static net.edubovit.labyrinth.entity.Visibility.REVEALED;
import static net.edubovit.labyrinth.entity.Visibility.SEEN;
import static net.edubovit.labyrinth.entity.Wall.State.FINAL;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

public class LabyrinthProcessor implements Serializable {

    private final Labyrinth labyrinth;

    private final Player player;

    private final int width;

    private final int height;

    public LabyrinthProcessor(int width, int height, long seed) {
        this.width = width;
        this.height = height;
        labyrinth = new Labyrinth(width, height, seed);
        player = new Player();
    }

    public void generate() {
        labyrinth.generateWalls();
        player.setPosition(labyrinth.getCell(width - 1, height - 1));
        player.setSeenTiles(seenTiles());
        player.getSeenTiles().forEach(cell -> cell.setVisibility(SEEN));
    }

    public MovementResultDTO moveUp() {
        return move(player.getPosition().getUp());
    }

    public MovementResultDTO moveDown() {
        return move(player.getPosition().getDown());
    }

    public MovementResultDTO moveLeft() {
        return move(player.getPosition().getLeft());
    }

    public MovementResultDTO moveRight() {
        return move(player.getPosition().getRight());
    }

    public boolean finish() {
        return player.getPosition().getI() == 0 && player.getPosition().getJ() == 0;
    }

    public Coordinates playerCoordinates() {
        return new Coordinates(player.getPosition().getI(), player.getPosition().getJ());
    }

    public int turns() {
        return player.getTurns();
    }

    public LabyrinthDTO buildLabyrinthDTO() {
        return new LabyrinthDTO(labyrinth, singletonList(player));
    }

    private MovementResultDTO move(Direction<?> direction) {
        if (direction.getWall().getState() == FINAL) {
            return new MovementResultDTO(emptyList(), playerCoordinates(), player.getTurns(), finish());
        } else {
            player.setTurns(player.getTurns() + 1);
            var prevSeenTiles = player.getSeenTiles();
            player.getSeenTiles().forEach(cell -> cell.setVisibility(REVEALED));
            player.setPosition(direction.getCell());
            var newSeenTiles = seenTiles();
            player.setSeenTiles(newSeenTiles);
            player.getSeenTiles().forEach(cell -> cell.setVisibility(SEEN));
            return new MovementResultDTO(
                    Stream.of(prevSeenTiles, newSeenTiles)
                            .flatMap(Collection::stream)
                            .distinct()
                            .map(CellChangeDTO::new)
                            .toList(),
                    playerCoordinates(),
                    player.getTurns(),
                    finish());
        }
    }

    private Collection<Cell> seenTiles() {
        var result = new ArrayList<Cell>();
        var position = player.getPosition();
        result.add(position);
        result.addAll(viewDirection(position, Cell::getUp, cell -> Stream.of(cell.getLeft(), cell.getRight())));
        result.addAll(viewDirection(position, Cell::getDown, cell -> Stream.of(cell.getLeft(), cell.getRight())));
        result.addAll(viewDirection(position, Cell::getLeft, cell -> Stream.of(cell.getUp(), cell.getDown())));
        result.addAll(viewDirection(position, Cell::getRight, cell -> Stream.of(cell.getUp(), cell.getDown())));
        return result;
    }

    private Collection<Cell> viewDirection(Cell start, Function<Cell, Direction<?>> next, Function<Cell, Stream<Direction<?>>> adjacent) {
        var result = new ArrayList<Cell>();
        var cell = start;
        for (int i = 0; i < VIEW_DISTANCE; i++) {
            var direction = next.apply(cell);
            if (direction.getWall().getState() == FINAL) {
                break;
            }
            cell = direction.getCell();
            result.add(cell);
            adjacent.apply(cell)
                    .filter(dir -> dir.getWall().getState() != FINAL)
                    .map(Direction::getCell)
                    .forEach(result::add);
        }
        return result;
    }

    @Serial
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        player.postDeserialize(labyrinth.getMatrix());
    }

}
