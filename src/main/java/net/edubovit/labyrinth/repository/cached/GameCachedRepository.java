package net.edubovit.labyrinth.repository.cached;

import net.edubovit.labyrinth.domain.Game;
import net.edubovit.labyrinth.entity.GameBlob;
import net.edubovit.labyrinth.repository.db.GameRepository;
import net.edubovit.labyrinth.repository.memory.GameCache;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class GameCachedRepository {

    private final GameRepository gameRepository;

    private final GameCache gameCache;

    public Optional<Game> get(UUID id) {
        var gameCached = gameCache.get(id);
        if (gameCached.isPresent()) {
            return gameCached;
        } else {
            return gameRepository.findById(id)
                    .map(this::deserialize);
        }
    }

    public void flush(Game game) {
        var serialized = serialize(game);
        gameRepository.insert(serialized.getId(), serialized.getGameBlob());
    }

    public void deleteByGameId(UUID gameId) {
        gameCache.delete(gameId);
        gameRepository.deleteById(gameId);
    }

    @SneakyThrows
    private GameBlob serialize(Game game) {
        var byteArrayOutputStream = new ByteArrayOutputStream();
        var objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(game);
        var gameBlob = byteArrayOutputStream.toByteArray();
        log.info("successful serialization of game {}, result: {} bytes", game.getId().toString(), gameBlob.length);
        return GameBlob.builder()
                .id(game.getId())
                .gameBlob(gameBlob)
                .build();
    }

    @SneakyThrows
    private Game deserialize(GameBlob gameBlob) {
        try {
            var objectInputStream = new ObjectInputStream(new ByteArrayInputStream(gameBlob.getGameBlob()));
            return (Game) objectInputStream.readObject();
        } catch (InvalidClassException e) {
            return null;
        }
    }

}
