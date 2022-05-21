package net.edubovit.labyrinth;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.util.Objects;
import java.util.stream.Stream;

public class LabyrinthView {

    private static final Color COLOR_ENTER = Color.RED;

    private static final Color COLOR_EXIT = Color.LAWNGREEN;

    private final int fieldWidth;

    private final int fieldHeight;

    private final double cellSize;

    private final double cellBorder;

    private final double outerBorder;

    private final double canvasWidth;

    private final double canvasHeight;

    private final Canvas canvas;

    private final GraphicsContext graphicsContext;

    public LabyrinthView(int fieldWidth, int fieldHeight, int cellSize, int cellBorder, int outerBorder) {
        this.fieldWidth = fieldWidth;
        this.fieldHeight = fieldHeight;
        this.cellSize = cellSize;
        this.cellBorder = cellBorder;
        this.outerBorder = outerBorder;

        canvasWidth = 2 * outerBorder + cellSize * fieldWidth;
        canvasHeight = 2 * outerBorder + cellSize * fieldHeight;
        canvas = new Canvas(canvasWidth, canvasHeight);
        graphicsContext = canvas.getGraphicsContext2D();
    }

    public Pane getPane() {
        return new Pane(canvas);
    }

    public void drawOuterBorders(Wall enter, Wall exit) {
        graphicsContext.setStroke(Color.BLACK);
        graphicsContext.setLineWidth(outerBorder);
        graphicsContext.strokeRect(0 + outerBorder / 2, 0 + outerBorder / 2,
                outerBorder + cellSize * fieldWidth, outerBorder + cellSize * fieldHeight);
        drawWall(enter, COLOR_ENTER, 6 * cellBorder);
        drawWall(exit, COLOR_EXIT, 6 * cellBorder);
    }

    public void drawCell(Cell cell) {
        var up = cell.getUp().getWall();
        var left = cell.getLeft().getWall();
        var right = cell.getRight().getWall();
        var down = cell.getDown().getWall();
        Stream.of(up, left, right, down)
                .filter(Objects::nonNull)
                .forEach(wall -> drawWall(wall, wall.getState().getColor()));
    }

    public void markCell(Cell cell) {
        graphicsContext.setFill(Color.color(1, 1, 0, 0.75));
        graphicsContext.fillOval(cellBorder + cellSize * (0.3 + cell.getJ()), cellBorder + cellSize * (0.3 + cell.getI()),
                cellSize * 0.4, cellSize * 0.4);
    }

    private void drawWall(Wall wall, Color color) {
        drawWall(wall, color, 2 * cellBorder);
    }

    private void drawWall(Wall wall, Color color, double width) {
        graphicsContext.setStroke(color);
        graphicsContext.setLineWidth(width);
        switch (wall) {
            case HorizontalWall horizontalWall -> {
                double x, y;
                if (horizontalWall.getUp() == null) {
                    x = outerBorder + cellSize * horizontalWall.getDown().getJ();
                    y = outerBorder;
                } else {
                    x = outerBorder + cellSize * horizontalWall.getUp().getJ();
                    y = outerBorder + cellSize * (1 + horizontalWall.getUp().getI());
                }
                if (color == Wall.State.ABSENT.getColor() || color == COLOR_ENTER || color == COLOR_EXIT) {
                    graphicsContext.strokeLine(x + 2 * cellBorder, y, x + cellSize - 2 * cellBorder, y);
                } else {
                    graphicsContext.strokeLine(x, y, x + cellSize, y);
                }
            }
            case VerticalWall verticalWall -> {
                double x, y;
                if (verticalWall.getLeft() == null) {
                    x = outerBorder;
                    y = outerBorder + cellSize * verticalWall.getRight().getI();
                } else {
                    x = outerBorder + cellSize * (1 + verticalWall.getLeft().getJ());
                    y = outerBorder + cellSize * verticalWall.getLeft().getI();
                }
                if (color == Wall.State.ABSENT.getColor() || color == COLOR_ENTER || color == COLOR_EXIT) {
                    graphicsContext.strokeLine(x, y + 2 * cellBorder, x, y + cellSize - 2 * cellBorder);
                } else {
                    graphicsContext.strokeLine(x, y, x, y + cellSize);
                }
            }
        }
    }

}
