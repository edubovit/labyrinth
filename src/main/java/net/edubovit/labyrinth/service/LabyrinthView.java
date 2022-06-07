package net.edubovit.labyrinth.service;

import net.edubovit.labyrinth.domain.Cell;
import net.edubovit.labyrinth.domain.HorizontalWall;
import net.edubovit.labyrinth.domain.VerticalWall;
import net.edubovit.labyrinth.domain.Wall;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Objects;
import java.util.stream.Stream;

public class LabyrinthView {

    private static final Color COLOR_ENTER = Color.RED;

    private static final Color COLOR_EXIT = Color.GREEN;

    private final int fieldWidth;

    private final int fieldHeight;

    private final int cellSize;

    private final int cellBorder;

    private final int outerBorder;

    private final int canvasWidth;

    private final int canvasHeight;

    private final BufferedImage canvas;

    private final Graphics2D graphicsContext;

    public LabyrinthView(int fieldWidth, int fieldHeight, int cellSize, int cellBorder, int outerBorder) {
        this.fieldWidth = fieldWidth;
        this.fieldHeight = fieldHeight;
        this.cellSize = cellSize;
        this.cellBorder = cellBorder;
        this.outerBorder = outerBorder;

        canvasWidth = 2 * outerBorder + cellSize * fieldWidth;
        canvasHeight = 2 * outerBorder + cellSize * fieldHeight;
        canvas = new BufferedImage(canvasWidth, canvasHeight, BufferedImage.TYPE_INT_RGB);
        graphicsContext = canvas.createGraphics();
        graphicsContext.fillRect(0, 0, canvasWidth, canvasHeight);
    }

    public void drawOuterBorders(Wall enter, Wall exit) {
        graphicsContext.setColor(Color.BLACK);
        graphicsContext.setStroke(new BasicStroke(outerBorder));
        graphicsContext.drawRect(outerBorder / 2, outerBorder / 2,
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

    public BufferedImage snapshot() {
        return canvas;
    }

    public void drawPlayer(Cell cell) {
        putMark(cell, new Color(0xff, 0x77, 0x0));
    }

    public void removePlayer(Cell cell) {
        putMark(cell, Color.WHITE);
    }

    private void putMark(Cell cell, Color color) {
        graphicsContext.setColor(color);
        graphicsContext.fillRect(
                (int) (outerBorder + cellSize * (0.25 + cell.getJ())),
                (int) (outerBorder + cellSize * (0.25 + cell.getI())),
                (int) (cellSize * 0.5),
                (int) (cellSize * 0.5));
    }

    private void drawWall(Wall wall, Color color) {
        drawWall(wall, color, 2 * cellBorder);
    }

    private void drawWall(Wall wall, Color color, int width) {
        graphicsContext.setColor(color);
        graphicsContext.setStroke(new BasicStroke(width));
        switch (wall) {
            case HorizontalWall horizontalWall -> {
                int x, y;
                if (horizontalWall.getUp() == null) {
                    x = outerBorder + cellSize * horizontalWall.getDown().getJ();
                    y = outerBorder;
                } else {
                    x = outerBorder + cellSize * horizontalWall.getUp().getJ();
                    y = outerBorder + cellSize * (1 + horizontalWall.getUp().getI());
                }
                if (color == Wall.State.ABSENT.getColor() || color == COLOR_ENTER || color == COLOR_EXIT) {
                    graphicsContext.drawLine(x + 2 * cellBorder, y, x + cellSize - 2 * cellBorder, y);
                } else {
                    graphicsContext.drawLine(x, y, x + cellSize, y);
                }
            }
            case VerticalWall verticalWall -> {
                int x, y;
                if (verticalWall.getLeft() == null) {
                    x = outerBorder;
                    y = outerBorder + cellSize * verticalWall.getRight().getI();
                } else {
                    x = outerBorder + cellSize * (1 + verticalWall.getLeft().getJ());
                    y = outerBorder + cellSize * verticalWall.getLeft().getI();
                }
                if (color == Wall.State.ABSENT.getColor() || color == COLOR_ENTER || color == COLOR_EXIT) {
                    graphicsContext.drawLine(x, y + 2 * cellBorder, x, y + cellSize - 2 * cellBorder);
                } else {
                    graphicsContext.drawLine(x, y, x, y + cellSize);
                }
            }
        }
    }

}
