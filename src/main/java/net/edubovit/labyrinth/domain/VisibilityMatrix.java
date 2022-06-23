package net.edubovit.labyrinth.domain;

import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static net.edubovit.labyrinth.domain.Visibility.HIDDEN;
import static net.edubovit.labyrinth.domain.Visibility.REVEALED;
import static net.edubovit.labyrinth.domain.Visibility.SEEN;
import static java.util.Collections.emptyList;

@Slf4j
public class VisibilityMatrix implements Serializable {

    private final Collection<Player>[][] playersByCell;

    private final List<PlayerAndCells> cellsByPlayer = new ArrayList<>();

    VisibilityMatrix(int width, int height) {
        playersByCell = new Collection[height][width];
    }

    public Collection<Player> whoSee(Cell cell) {
        var players = playersByCell[cell.getI()][cell.getJ()];
        return Objects.requireNonNullElse(players, emptyList());
    }

    public Collection<Cell> getPlayerVision(Player player) {
        return cellsByPlayer(player).orElse(emptyList());
    }

    public Visibility getVisibility(Cell cell) {
        var players = playersByCell[cell.getI()][cell.getJ()];
        if (players == null) {
            return HIDDEN;
        } else if (players.isEmpty()) {
            return REVEALED;
        } else {
            return SEEN;
        }
    }

    public void setPlayerVision(Player player, Collection<Cell> cells) {
        var playerVision = cellsByPlayer(player).orElseGet(() -> {
            var playerAndCells = new PlayerAndCells(player);
            cellsByPlayer.add(playerAndCells);
            return playerAndCells.cells;
        });
        playerVision.forEach(cell -> unsee(cell, player));
        playerVision.clear();
        playerVision.addAll(cells);
        playerVision.forEach(cell -> see(cell, player));
    }

    private void see(Cell cell, Player player) {
        var playersSeeCell = playersByCell[cell.getI()][cell.getJ()];
        if (playersSeeCell == null) {
            playersSeeCell = new ArrayList<>(2);
            playersByCell[cell.getI()][cell.getJ()] = playersSeeCell;
        }
        playersSeeCell.add(player);
    }

    private void unsee(Cell cell, Player player) {
        var playersSeeCell = playersByCell[cell.getI()][cell.getJ()];
        if (playersSeeCell == null) {
            log.warn("player {} tried to unsee cell (i={},j={}) but it wasn't revealed",
                    player.getUsername(), cell.getI(), cell.getJ());
        } else {
            playersSeeCell.remove(player);
        }
    }

    private Optional<Collection<Cell>> cellsByPlayer(Player player) {
        for (var playerAndCells : cellsByPlayer) {
            if (playerAndCells.player == player) {
                return Optional.of(playerAndCells.cells);
            }
        }
        return Optional.empty();
    }

    private record PlayerAndCells(Player player, Collection<Cell> cells) implements Serializable {
        PlayerAndCells(Player player) {
            this(player, new ArrayList<>());
        }
    }

}
