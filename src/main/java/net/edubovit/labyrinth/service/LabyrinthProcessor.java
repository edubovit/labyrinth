package net.edubovit.labyrinth.service;

import net.edubovit.labyrinth.domain.Direction;
import net.edubovit.labyrinth.domain.Labyrinth;
import net.edubovit.labyrinth.domain.Player;
import net.edubovit.labyrinth.domain.Wall;

import java.awt.image.BufferedImage;

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
        view.drawOuterBorders(labyrinth.getEnter(), labyrinth.getExit());
        view.drawPlayer(player.getPosition());
    }

    public BufferedImage printMap() {
        return view.snapshot();
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

    private boolean move(Direction<?> direction) {
        if (direction.getWall().getState() == Wall.State.FINAL) {
            return false;
        } else {
            view.removePlayer(player.getPosition());
            player.setPosition(direction.getCell());
            view.drawPlayer(player.getPosition());
            return true;
        }
    }

    private boolean digOne() {
        var chosenWay = labyrinth.chooseRandomWay();
        if (chosenWay == null) {
            return false;
        } else {
            labyrinth.digTunnel(chosenWay, view::drawCell);
            return true;
        }
    }

}
