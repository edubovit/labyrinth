package net.edubovit.labyrinth.service;

import net.edubovit.labyrinth.domain.Cell;
import net.edubovit.labyrinth.domain.Direction;
import net.edubovit.labyrinth.domain.Labyrinth;
import net.edubovit.labyrinth.domain.Player;
import net.edubovit.labyrinth.dto.GameSessionDTO;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Stream;

import static net.edubovit.labyrinth.config.Defaults.VIEW_DISTANCE;
import static net.edubovit.labyrinth.domain.Cell.Visibility.REVEALED;
import static net.edubovit.labyrinth.domain.Cell.Visibility.SEEN;
import static net.edubovit.labyrinth.domain.Wall.State.FINAL;

public class LabyrinthProcessor {

    private final Labyrinth labyrinth;

    private final LabyrinthView view;

    private final Player player;

    private final int width;

    private final int height;

    public LabyrinthProcessor(int width, int height, long seed, int cellSize, int cellBorder, int outerBorder) {
        this.width = width;
        this.height = height;
        labyrinth = new Labyrinth(width, height, seed);
        view = new LabyrinthView(width, height, cellSize, cellBorder, outerBorder);
        player = new Player();
    }

    public void generate() {
        while (digOne());
        player.setPosition(labyrinth.getCell(width - 1, height - 1));
        player.setSeenTiles(seenTiles());
        player.getSeenTiles().forEach(cell -> {
            cell.setVisibility(SEEN);
            view.drawCell(cell);
        });
        view.drawOuterBorders(labyrinth.getEnter(), labyrinth.getExit());
        view.drawPlayer(player.getPosition());
    }

    public BufferedImage printMap() {
        return view.getCanvas();
    }

    public boolean moveUp() {
        return move(player.getPosition().getUp());
    }

    public boolean moveDown() {
        return move(player.getPosition().getDown());
    }

    public boolean moveLeft() {
        return move(player.getPosition().getLeft());
    }

    public boolean moveRight() {
        return move(player.getPosition().getRight());
    }

    public boolean finish() {
        return player.getPosition().getUp().getWall() == labyrinth.getExit();
    }

    public GameSessionDTO.PlayerCoordinates playerCoordinates() {
        int cellSize = view.getCellSize();
        int i = player.getPosition().getI();
        int j = player.getPosition().getJ();
        int outerBorder = view.getOuterBorder();
        return new GameSessionDTO.PlayerCoordinates(
                outerBorder + j * cellSize + cellSize / 2,
                outerBorder + i * cellSize + cellSize / 2);
    }

    private boolean move(Direction<?> direction) {
        if (direction.getWall().getState() == FINAL) {
            return false;
        } else {
            player.getSeenTiles().forEach(cell -> {
                cell.setVisibility(REVEALED);
                view.drawCell(cell);
            });
//            view.removePlayer(player.getPosition());
            player.setPosition(direction.getCell());
            player.setSeenTiles(seenTiles());
            player.getSeenTiles().forEach(cell -> {
                cell.setVisibility(SEEN);
                view.drawCell(cell);
            });
            view.drawPlayer(player.getPosition());
            return true;
        }
    }

    private boolean digOne() {
        var chosenWay = labyrinth.chooseRandomWay();
        if (chosenWay == null) {
            return false;
        } else {
            labyrinth.digTunnel(chosenWay, cell -> {});
            return true;
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

}
