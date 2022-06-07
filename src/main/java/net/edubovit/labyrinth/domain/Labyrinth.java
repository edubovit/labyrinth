package net.edubovit.labyrinth.domain;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

import static net.edubovit.labyrinth.domain.Wall.State.ABSENT;
import static net.edubovit.labyrinth.domain.Wall.State.FINAL;
import static net.edubovit.labyrinth.domain.Wall.State.PLAN;

public class Labyrinth {

    private final Cell[][] matrix;

    @Getter
    private final HorizontalWall enter;

    @Getter
    private final HorizontalWall exit;

    private final Random random;

    private final List<Way> availableDigDirections;

    public Labyrinth(int width, int height, long seed) {
        matrix = new Cell[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                matrix[i][j] = new Cell(i, j);
            }
        }
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                var cell = matrix[i][j];
                if (i == 0) {
                    cell.getUp().setWall(new HorizontalWall(null, cell, FINAL));
                } else {
                    var up = matrix[i - 1][j];
                    cell.getUp().setCell(up);
                    cell.getUp().setWall(cell.getUp().getCell().getDown().getWall());
                }
                if (i == height - 1) {
                    cell.getDown().setWall(new HorizontalWall(cell, null, FINAL));
                } else {
                    var down = matrix[i + 1][j];
                    cell.getDown().setCell(down);
                    cell.getDown().setWall(new HorizontalWall(cell, down, ABSENT));
                }
                if (j == 0) {
                    cell.getLeft().setWall(new VerticalWall(null, cell, FINAL));
                } else {
                    var left = matrix[i][j - 1];
                    cell.getLeft().setCell(left);
                    cell.getLeft().setWall(cell.getLeft().getCell().getRight().getWall());
                }
                if (j == width - 1) {
                    cell.getRight().setWall(new VerticalWall(cell, null, FINAL));
                } else {
                    var right = matrix[i][j + 1];
                    cell.getRight().setCell(right);
                    cell.getRight().setWall(new VerticalWall(cell, right, ABSENT));
                }
            }
        }
        enter = matrix[height - 1][width - 1].getDown().getWall();
        exit = matrix[0][0].getUp().getWall();
        random = new Random(seed);
        availableDigDirections = new ArrayList<>();
        availableDigDirections.add(new Way(matrix[height - 1][width - 1], DigDirection.UP));
    }

    public void digTunnel(Way way, Consumer<Cell> cellDiggedListener) {
        var cell = way.next;
        var direction = way.to;
        do {
            dig(cell, direction);
            cellDiggedListener.accept(cell);
            direction = chooseRandomDirection(direction);
            cell = direction.getCell(cell);
        } while (cell != null);
    }

    public Way chooseRandomWay() {
        if (availableDigDirections.isEmpty()) {
            return null;
        }
        return availableDigDirections.get(random.nextInt(availableDigDirections.size()));
    }

    public Cell getCell(int x, int y) {
        return matrix[y][x];
    }

    private void dig(Cell cell, DigDirection to) {
        availableDigDirections.remove(new Way(cell, to));
        if (to == DigDirection.UP) {
            cell.getDown().getWall().setState(ABSENT);
        } else {
            var wall = cell.getDown().getWall();
            if (wall.getState() == ABSENT) {
                wall.setState(PLAN);
                availableDigDirections.add(new Way(cell.getDown().getCell(), DigDirection.DOWN));
            } else if (wall.getState() == PLAN /*&& to != DigDirection.DOWN*/) {
                wall.setState(FINAL);
                availableDigDirections.remove(new Way(cell, DigDirection.UP));
            }
        }
        if (to == DigDirection.DOWN) {
            cell.getUp().getWall().setState(ABSENT);
        } else {
            var wall = cell.getUp().getWall();
            if (wall.getState() == ABSENT) {
                wall.setState(PLAN);
                availableDigDirections.add(new Way(cell.getUp().getCell(), DigDirection.UP));
            } else if (wall.getState() == PLAN /*&& to != DigDirection.UP*/) {
                wall.setState(FINAL);
                availableDigDirections.remove(new Way(cell, DigDirection.DOWN));
            }
        }
        if (to == DigDirection.LEFT) {
            cell.getRight().getWall().setState(ABSENT);
        } else {
            var wall = cell.getRight().getWall();
            if (wall.getState() == ABSENT) {
                wall.setState(PLAN);
                availableDigDirections.add(new Way(cell.getRight().getCell(), DigDirection.RIGHT));
            } else if (wall.getState() == PLAN /*&& to != DigDirection.RIGHT*/) {
                wall.setState(FINAL);
                availableDigDirections.remove(new Way(cell, DigDirection.LEFT));
            }
        }
        if (to == DigDirection.RIGHT) {
            cell.getLeft().getWall().setState(ABSENT);
        } else {
            var wall = cell.getLeft().getWall();
            if (wall.getState() == ABSENT) {
                wall.setState(PLAN);
                availableDigDirections.add(new Way(cell.getLeft().getCell(), DigDirection.LEFT));
            } else if (wall.getState() == PLAN /*&& to != DigDirection.LEFT*/) {
                wall.setState(FINAL);
                availableDigDirections.remove(new Way(cell, DigDirection.RIGHT));
            }
        }
    }

    private DigDirection chooseRandomDirection(DigDirection previousDirection) {
        return previousDirection.nextPossibleDirections()[random.nextInt(3)];
    }

    public record Way(Cell next, DigDirection to) {
    }

    public enum DigDirection {
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
