package net.edubovit.labyrinth.service;

import net.edubovit.labyrinth.domain.Cell;
import net.edubovit.labyrinth.domain.HorizontalWall;
import net.edubovit.labyrinth.domain.VerticalWall;
import net.edubovit.labyrinth.domain.Wall;

import lombok.Getter;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Objects;
import java.util.stream.Stream;

import static net.edubovit.labyrinth.domain.Cell.Visibility.HIDDEN;
import static net.edubovit.labyrinth.domain.Cell.Visibility.REVEALED;
import static net.edubovit.labyrinth.domain.Cell.Visibility.SEEN;
import static net.edubovit.labyrinth.domain.Wall.State.ABSENT;

public class LabyrinthView {

    private static final Color COLOR_ENTER = Color.RED;

    private static final Color COLOR_EXIT = Color.GREEN;

    private static final Color COLOR_HIDDEN = new Color(0xff, 0xf2, 0xcc);

    private static final Color COLOR_REVEALED = new Color(0xbb, 0xbb, 0xbb);

    private static final Color COLOR_SEEN = Color.WHITE;

    private final int fieldWidth;

    private final int fieldHeight;

    @Getter
    private final int cellSize;

    @Getter
    private final int cellBorder;

    @Getter
    private final int outerBorder;

    @Getter
    private final int canvasWidth;

    @Getter
    private final int canvasHeight;

    @Getter
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
        graphicsContext.setColor(COLOR_HIDDEN);
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
        if (cell.getVisibility() == SEEN) {
            graphicsContext.setColor(COLOR_SEEN);
            graphicsContext.fillRect(outerBorder + cellSize * cell.getJ(), outerBorder + cellSize * cell.getI(),
                    cellSize, cellSize);
        } else if (cell.getVisibility() == REVEALED) {
            graphicsContext.setColor(COLOR_REVEALED);
            graphicsContext.fillRect(outerBorder + cellSize * cell.getJ(), outerBorder + cellSize * cell.getI(),
                    cellSize, cellSize);
        }
        var up = cell.getUp().getWall();
        var left = cell.getLeft().getWall();
        var right = cell.getRight().getWall();
        var down = cell.getDown().getWall();
        Stream.of(up, left, right, down)
                .filter(Objects::nonNull)
                .forEach(this::drawWall);
        if (cell.getVisibility() == HIDDEN) {
            graphicsContext.setColor(COLOR_HIDDEN);
            graphicsContext.fillRect(outerBorder + cellSize * cell.getJ(), outerBorder + cellSize * cell.getI(),
                    cellSize, cellSize);
        }
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

    private void drawWall(Wall wall) {
        if (wall.getState() != ABSENT) {
            drawWall(wall, wall.getState().getColor(), 2 * cellBorder);
        }
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
                if (color == ABSENT.getColor() || color == COLOR_ENTER || color == COLOR_EXIT) {
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
                if (color == ABSENT.getColor() || color == COLOR_ENTER || color == COLOR_EXIT) {
                    graphicsContext.drawLine(x, y + 2 * cellBorder, x, y + cellSize - 2 * cellBorder);
                } else {
                    graphicsContext.drawLine(x, y, x, y + cellSize);
                }
            }
        }
    }

}
