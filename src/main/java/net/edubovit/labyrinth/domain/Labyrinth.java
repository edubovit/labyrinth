package net.edubovit.labyrinth.domain;

import lombok.Getter;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static net.edubovit.labyrinth.domain.Wall.State.ABSENT;
import static net.edubovit.labyrinth.domain.Wall.State.FINAL;
import static net.edubovit.labyrinth.domain.Wall.State.PLAN;

public class Labyrinth implements Serializable {

    @Getter
    private final Cell[][] matrix;

    @Getter
    private final int width;

    @Getter
    private final int height;

    private final transient Random random;

    private final transient List<Way> availableDigDirections;

    public Labyrinth(int width, int height, long seed) {
        this.width = width;
        this.height = height;
        matrix = new Cell[height][width];
        initializeField();
        random = new Random(seed);
        availableDigDirections = new ArrayList<>();
        availableDigDirections.add(new Way(matrix[height - 1][width - 1], DigDirection.UP));
    }

    public Cell getCell(int x, int y) {
        return matrix[y][x];
    }

    public void generateWalls() {
        while (digTunnel());
    }

    private void initializeField() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                matrix[i][j] = new Cell(i, j);
            }
        }
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                var cell = matrix[i][j];
                if (i == 0) {
                    cell.getUp().setWall(new HorizontalWall(FINAL));
                } else {
                    cell.getUp().setCell(matrix[i - 1][j]);
                    cell.getUp().setWall(cell.getUp().getCell().getDown().getWall());
                }
                if (i == height - 1) {
                    cell.getDown().setWall(new HorizontalWall(FINAL));
                } else {
                    cell.getDown().setCell(matrix[i + 1][j]);
                    cell.getDown().setWall(new HorizontalWall());
                }
                if (j == 0) {
                    cell.getLeft().setWall(new VerticalWall(FINAL));
                } else {
                    cell.getLeft().setCell(matrix[i][j - 1]);
                    cell.getLeft().setWall(cell.getLeft().getCell().getRight().getWall());
                }
                if (j == width - 1) {
                    cell.getRight().setWall(new VerticalWall(FINAL));
                } else {
                    cell.getRight().setCell(matrix[i][j + 1]);
                    cell.getRight().setWall(new VerticalWall());
                }
            }
        }
        resolveMatrixLinks();
    }

    private void resolveMatrixLinks() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                var cell = matrix[i][j];
                var up = i > 0 ? matrix[i - 1][j] : null;
                var down = i < height - 1 ? matrix[i + 1][j] : null;
                var left = j > 0 ? matrix[i][j - 1] : null;
                var right = j < width - 1 ? matrix[i][j + 1] : null;
                cell.getUp().setCell(up);
                cell.getUp().getWall().setUp(up);
                cell.getUp().getWall().setDown(cell);
                cell.getDown().setCell(down);
                cell.getDown().getWall().setDown(down);
                cell.getDown().getWall().setUp(cell);
                cell.getLeft().setCell(left);
                cell.getLeft().getWall().setLeft(left);
                cell.getLeft().getWall().setRight(cell);
                cell.getRight().setCell(right);
                cell.getRight().getWall().setRight(right);
                cell.getRight().getWall().setLeft(cell);
            }
        }
    }

    private boolean digTunnel() {
        var chosenWay = chooseRandomWay();
        if (chosenWay == null) {
            return false;
        } else {
            var cell = chosenWay.next;
            var direction = chosenWay.to;
            do {
                dig(cell, direction);
                direction = chooseRandomDirection(direction);
                cell = direction.getCell(cell);
            } while (cell != null);
            return true;
        }
    }

    private Way chooseRandomWay() {
        if (availableDigDirections.isEmpty()) {
            return null;
        }
        return availableDigDirections.get(random.nextInt(availableDigDirections.size()));
    }

    private void dig(Cell cell, DigDirection to) {
        availableDigDirections.remove(new Way(cell, to));
        if (to == DigDirection.UP) {
            if (cell.getDown().getWall().getState() != FINAL) {
                cell.getDown().getWall().setState(ABSENT);
            }
        } else {
            var wall = cell.getDown().getWall();
            if (wall.getState() == ABSENT) {
                wall.setState(PLAN);
                availableDigDirections.add(new Way(cell.getDown().getCell(), DigDirection.DOWN));
            } else if (wall.getState() == PLAN) {
                wall.setState(FINAL);
                availableDigDirections.remove(new Way(cell, DigDirection.UP));
            }
        }
        if (to == DigDirection.DOWN) {
            if (cell.getUp().getWall().getState() != FINAL) {
                cell.getUp().getWall().setState(ABSENT);
            }
        } else {
            var wall = cell.getUp().getWall();
            if (wall.getState() == ABSENT) {
                wall.setState(PLAN);
                availableDigDirections.add(new Way(cell.getUp().getCell(), DigDirection.UP));
            } else if (wall.getState() == PLAN) {
                wall.setState(FINAL);
                availableDigDirections.remove(new Way(cell, DigDirection.DOWN));
            }
        }
        if (to == DigDirection.LEFT) {
            if (cell.getRight().getWall().getState() != FINAL) {
                cell.getRight().getWall().setState(ABSENT);
            }
        } else {
            var wall = cell.getRight().getWall();
            if (wall.getState() == ABSENT) {
                wall.setState(PLAN);
                availableDigDirections.add(new Way(cell.getRight().getCell(), DigDirection.RIGHT));
            } else if (wall.getState() == PLAN) {
                wall.setState(FINAL);
                availableDigDirections.remove(new Way(cell, DigDirection.LEFT));
            }
        }
        if (to == DigDirection.RIGHT) {
            if (cell.getLeft().getWall().getState() != FINAL) {
                cell.getLeft().getWall().setState(ABSENT);
            }
        } else {
            var wall = cell.getLeft().getWall();
            if (wall.getState() == ABSENT) {
                wall.setState(PLAN);
                availableDigDirections.add(new Way(cell.getLeft().getCell(), DigDirection.LEFT));
            } else if (wall.getState() == PLAN) {
                wall.setState(FINAL);
                availableDigDirections.remove(new Way(cell, DigDirection.RIGHT));
            }
        }
    }

    private DigDirection chooseRandomDirection(DigDirection previousDirection) {
        return previousDirection.nextPossibleDirections()[random.nextInt(3)];
    }

    @Serial
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        resolveMatrixLinks();
    }

    private record Way(Cell next, DigDirection to) {
    }

    private enum DigDirection {
        UP, LEFT, RIGHT, DOWN;

        private static final DigDirection[] nextPossibleDirectionsUp = new DigDirection[] { LEFT, UP, RIGHT };
        private static final DigDirection[] nextPossibleDirectionsLeft = new DigDirection[] { DOWN, LEFT, UP };
        private static final DigDirection[] nextPossibleDirectionsRight = new DigDirection[] { UP, RIGHT, DOWN };
        private static final DigDirection[] nextPossibleDirectionsDown = new DigDirection[] { RIGHT, DOWN, LEFT };

        private DigDirection[] nextPossibleDirections() {
            return switch (this) {
                case UP -> nextPossibleDirectionsUp;
                case LEFT -> nextPossibleDirectionsLeft;
                case RIGHT -> nextPossibleDirectionsRight;
                case DOWN -> nextPossibleDirectionsDown;
            };
        }

        private Direction<?> getDirection(Cell from) {
            return switch (this) {
                case UP -> from.getUp();
                case LEFT -> from.getLeft();
                case RIGHT -> from.getRight();
                case DOWN -> from.getDown();
            };
        }

        private Cell getCell(Cell from) {
            var direction = getDirection(from);
            if (direction.getWall().getState() == ABSENT || direction.getWall().getState() == PLAN) {
                return direction.getCell();
            } else {
                return null;
            }
        }
    }

}
