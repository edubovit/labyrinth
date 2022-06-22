package net.edubovit.labyrinth.domain;

import net.edubovit.labyrinth.dto.CellChangeDTO;
import net.edubovit.labyrinth.dto.LabyrinthDTO;
import net.edubovit.labyrinth.dto.GameChangedEvent;
import net.edubovit.labyrinth.exception.Exceptions;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serial;
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
import static net.edubovit.labyrinth.domain.Visibility.REVEALED;
import static net.edubovit.labyrinth.domain.Visibility.SEEN;
import static net.edubovit.labyrinth.domain.Wall.State.FINAL;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

@Slf4j
public class Game implements Serializable {

    @Getter
    private final UUID id;

    private final int width;

    private final int height;

    private final Labyrinth labyrinth;

    private final List<Player> players = new ArrayList<>();

    @Getter
    private LocalDateTime lastUsed;

    public Game(int width, int height, long seed) {
        id = UUID.randomUUID();
        this.width = width;
        this.height = height;
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
        return new LabyrinthDTO(labyrinth, players);
    }

    public GameChangedEvent join(String username) {
        lastUsed = LocalDateTime.now();
        var player = new Player(username);
        player.setPosition(labyrinth.getCell(width - 1, height - 1));
        var seenTiles = seenTiles(player.getPosition());
        player.setSeenTiles(seenTiles);
        player.getSeenTiles().forEach(cell -> cell.setVisibility(SEEN));
        players.add(player);
        return new GameChangedEvent(username, 0, finish(username),
                seenTiles.stream()
                        .map(tile -> new CellChangeDTO(tile, playersOnTile(tile)))
                        .toList()
        );
    }

    public GameChangedEvent leave(String username) {
        var playerOptional = playerByUsernameOptional(username);
        if (playerOptional.isPresent()) {
            lastUsed = LocalDateTime.now();
            var player = playerOptional.get();
            players.remove(player);
            return new GameChangedEvent(username, player.getTurns(), false,
                    player.getSeenTiles()
                            .stream()
                            .map(tile -> new CellChangeDTO(tile, playersOnTile(tile)))
                            .toList()
            );
        } else {
            return new GameChangedEvent(username, 0, false, emptyList());
        }
    }

    public int playersCount() {
        return players.size();
    }

    public Stream<String> playerNames() {
        return players.stream().map(Player::getUsername);
    }

    public boolean finish(String username) {
        var player = playerByUsername(username);
        return player.getPosition().getI() == 0 && player.getPosition().getJ() == 0;
    }

    public int turns(String username) {
        return playerByUsername(username).getTurns();
    }

    public GameChangedEvent moveUp(String username) {
        var player = playerByUsername(username);
        return move(player, player.getPosition().getUp());
    }

    public GameChangedEvent moveDown(String username) {
        var player = playerByUsername(username);
        return move(player, player.getPosition().getDown());
    }

    public GameChangedEvent moveLeft(String username) {
        var player = playerByUsername(username);
        return move(player, player.getPosition().getLeft());
    }

    public GameChangedEvent moveRight(String username) {
        var player = playerByUsername(username);
        return move(player, player.getPosition().getRight());
    }

    private GameChangedEvent move(Player player, Direction<?> direction) {
        if (direction.getWall().getState() == FINAL) {
            return new GameChangedEvent(player.getUsername(), player.getTurns(), finish(player.getUsername()), emptyList());
        }
        lastUsed = LocalDateTime.now();
        var prevPosition = player.getPosition();
        var nextPosition = direction.getCell();
        var prevSeenTiles = player.getSeenTiles();
        var nextSeenTiles = seenTiles(nextPosition);
        prevSeenTiles.forEach(cell -> cell.setVisibility(REVEALED));
        nextSeenTiles.forEach(cell -> cell.setVisibility(SEEN));
        player.setPosition(nextPosition);
        player.setSeenTiles(nextSeenTiles);
        player.setTurns(player.getTurns() + 1);
        return new GameChangedEvent(player.getUsername(), player.getTurns(), finish(player.getUsername()),
                changedTiles(prevPosition, nextPosition, prevSeenTiles, nextSeenTiles));
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

    private Collection<CellChangeDTO> changedTiles(Cell prevPosition, Cell nextPosition,
                                                   Collection<Cell> prevSeenTiles, Collection<Cell> nextSeenTiles) {
        var changedTiles = new ArrayList<>(prevSeenTiles);
        nextSeenTiles.forEach(tile -> {
            if (!changedTiles.remove(tile)) {
                changedTiles.add(tile);
            }
        });
        if (!changedTiles.contains(prevPosition)) {
            changedTiles.add(prevPosition);
        }
        if (!changedTiles.contains(nextPosition)) {
            changedTiles.add(nextPosition);
        }
        return changedTiles.stream()
                .map(tile -> new CellChangeDTO(tile, playersOnTile(tile)))
                .toList();
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

    @Serial
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        players.forEach(player -> player.postDeserialize(labyrinth.getMatrix()));
    }

}
