package net.edubovit.labyrinth.repository.cached;

import net.edubovit.labyrinth.domain.Game;
import net.edubovit.labyrinth.entity.GameBlob;
import net.edubovit.labyrinth.repository.db.GameRepository;
import net.edubovit.labyrinth.repository.db.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.function.Function.identity;

@Component
@RequiredArgsConstructor
@Slf4j
public class GameCachedRepository {

    private final GameRepository gameRepository;

    private final UserRepository userRepository;

    private final Map<String, Game> gamesByUsername = new ConcurrentHashMap<>();

    public Optional<Game> getByUsername(String username) {
        var game = gamesByUsername.get(username);
        if (game == null) {
            var gameOptional = userRepository.selectGameIdByUsername(username)
                    .map(gameRepository::findById)
                    .flatMap(identity())
                    .map(this::deserialize);
            if (gameOptional.isPresent()) {
                game = gameOptional.get();
                populateCaches(game);
            }
        }
        return Optional.ofNullable(game);
    }

    public void persist(Game game) {
        var gameBlob = serialize(game);
        gameRepository.insert(gameBlob.getId(), gameBlob.getGameBlob(), gameBlob.getLastUsed());
    }

    public void join(String username, Game game) {
        gamesByUsername.put(username, game);
        userRepository.updateGameForUser(game.getId(), username);
    }

    public void deleteGameIfAbandoned(Game game) {
        if (game.playersCount() == 0) {
            gameRepository.deleteById(game.getId());
        }
    }

    private void populateCaches(Game game) {
        game.playerNames().forEach(username -> gamesByUsername.put(username, game));
    }

    @SneakyThrows
    private GameBlob serialize(Game game) {
        log.info("serializing game {}", game.getId());
        long time = System.nanoTime();
        var byteArrayOutputStream = new ByteArrayOutputStream();
        var objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(game);
        var gameBlob = byteArrayOutputStream.toByteArray();
        log.info("successful serialization of game {}, result: {} bytes, took {}us", game.getId().toString(), gameBlob.length, (System.nanoTime() - time) / 1000);
        return GameBlob.builder()
                .id(game.getId())
                .gameBlob(gameBlob)
                .lastUsed(game.getLastUsed())
                .build();
    }

    @SneakyThrows
    private Game deserialize(GameBlob gameBlob) {
        try {
            log.info("deserializing game {}", gameBlob.getId());
            long time = System.nanoTime();
            var objectInputStream = new ObjectInputStream(new ByteArrayInputStream(gameBlob.getGameBlob()));
            var game = (Game) objectInputStream.readObject();
            log.info("successful deserialization of game {}, took {}us", game.getId(), (System.nanoTime() - time) / 1000);
            return game;
        } catch (ObjectStreamException e) {
            log.warn("failed to deserialize game {}: {}", gameBlob.getId(), e.toString());
            return null;
        }
    }


}
