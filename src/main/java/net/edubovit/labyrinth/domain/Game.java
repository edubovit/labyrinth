package net.edubovit.labyrinth.domain;

import net.edubovit.labyrinth.dto.CellChangeDTO;
import net.edubovit.labyrinth.event.TilesChangedEvent;
import net.edubovit.labyrinth.dto.LabyrinthDTO;
import net.edubovit.labyrinth.exception.Exceptions;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Stream;

import static net.edubovit.labyrinth.config.Defaults.VIEW_DISTANCE;
import static net.edubovit.labyrinth.domain.Wall.State.FINAL;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

@Slf4j
public class Game implements Serializable {

    @Getter
    private final UUID id;

    private final Labyrinth labyrinth;

    private final List<Player> players = new ArrayList<>();

    private final VisibilityMatrix visibilityMatrix;

    @Getter
    private LocalDateTime lastUsed;

    public Game(int width, int height, long seed) {
        id = UUID.randomUUID();
        visibilityMatrix = new VisibilityMatrix(width, height);
        lastUsed = LocalDateTime.now();

        long time = System.nanoTime();
        labyrinth = new Labyrinth(width, height, seed);
        log.info("labyrinth initializing took {}us", (System.nanoTime() - time) / 1000);
    }

    public void generate() {
        long time = System.nanoTime();
        labyrinth.generateWalls();
        log.info("labyrinth generation took {}us", (System.nanoTime() - time) / 1000);
    }

    public LabyrinthDTO buildLabyrinthDTO() {
        return new LabyrinthDTO(labyrinth, players, visibilityMatrix);
    }

    public TilesChangedEvent join(String username) {
        lastUsed = LocalDateTime.now();
        var player = new Player(username);
        player.setPosition(labyrinth.getCell(labyrinth.getWidth() - 1, labyrinth.getHeight() - 1));
        var seenTiles = seenTiles(player.getPosition());
        visibilityMatrix.setPlayerVision(player, seenTiles);
        players.add(player);
        return new TilesChangedEvent(username, 0, finish(username), buildCellChangesDTO(seenTiles));
    }

    public TilesChangedEvent leave(String username) {
        var playerOptional = playerByUsernameOptional(username);
        if (playerOptional.isPresent()) {
            lastUsed = LocalDateTime.now();
            var player = playerOptional.get();
            players.remove(player);
            var seenTiles = List.copyOf(visibilityMatrix.getPlayerVision(player));
            visibilityMatrix.setPlayerVision(player, emptyList());
            var changedTiles = buildCellChangesDTO(seenTiles);
            return new TilesChangedEvent(username, player.getTurns(), false, changedTiles);
        } else {
            return new TilesChangedEvent(username, 0, false, emptyList());
        }
    }

    public int playersCount() {
        return players.size();
    }

    public Stream<String> playerNames() {
        return players.stream().map(Player::getUsername);
    }

    public boolean finish(String username) {
        return isFinished(playerByUsername(username));
    }

    public int turns(String username) {
        return playerByUsername(username).getTurns();
    }

    public TilesChangedEvent moveUp(String username) {
        var player = playerByUsername(username);
        return move(player, player.getPosition().getUp());
    }

    public TilesChangedEvent moveDown(String username) {
        var player = playerByUsername(username);
        return move(player, player.getPosition().getDown());
    }

    public TilesChangedEvent moveLeft(String username) {
        var player = playerByUsername(username);
        return move(player, player.getPosition().getLeft());
    }

    public TilesChangedEvent moveRight(String username) {
        var player = playerByUsername(username);
        return move(player, player.getPosition().getRight());
    }

    private TilesChangedEvent move(Player player, Direction<?> direction) {
        if (direction.getWall().getState() == FINAL) {
            return new TilesChangedEvent(player.getUsername(), player.getTurns(), finish(player.getUsername()), emptyList());
        }
        lastUsed = LocalDateTime.now();
        var prevSeenTiles = List.copyOf(visibilityMatrix.getPlayerVision(player));
        player.setPosition(direction.getCell());
        var nextSeenTiles = seenTiles(player.getPosition());
        visibilityMatrix.setPlayerVision(player, nextSeenTiles);
        var changedTiles = Stream.of(prevSeenTiles, nextSeenTiles)
                .flatMap(Collection::stream)
                .distinct()
                .map(this::cellToCellChangeDTO)
                .toList();
        player.setTurns(player.getTurns() + 1);
        return new TilesChangedEvent(player.getUsername(), player.getTurns(), isFinished(player), changedTiles);
    }

    private Collection<Cell> seenTiles(Cell position) {
        return Stream.of(singletonList(position),
                        viewDirection(position, Cell::getUp, cell -> Stream.of(cell.getLeft(), cell.getRight())),
                        viewDirection(position, Cell::getDown, cell -> Stream.of(cell.getLeft(), cell.getRight())),
                        viewDirection(position, Cell::getLeft, cell -> Stream.of(cell.getUp(), cell.getDown())),
                        viewDirection(position, Cell::getRight, cell -> Stream.of(cell.getUp(), cell.getDown())))
                .flatMap(Collection::stream)
                .toList();
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

    private boolean isFinished(Player player) {
        return player.getPosition().getI() == 0 && player.getPosition().getJ() == 0;
    }

    private Collection<CellChangeDTO> buildCellChangesDTO(Collection<Cell> cells) {
        return cells.stream()
                .map(this::cellToCellChangeDTO)
                .toList();
    }

    private CellChangeDTO cellToCellChangeDTO(Cell cell) {
        return new CellChangeDTO(cell, playersOnTile(cell), visibilityMatrix.getVisibility(cell));
    }

    private Player playerByUsername(String username) {
        return playerByUsernameOptional(username)
                .orElseThrow(Exceptions.usernameNotFoundException(username));
    }

    private Optional<Player> playerByUsernameOptional(String username) {
        return players.stream()
                .filter(p -> p.getUsername().equals(username))
                .findAny();
    }

    private Collection<Player> playersOnTile(Cell cell) {
        return players.stream()
                .filter(p -> p.getPosition() == cell)
                .toList();
    }

}
