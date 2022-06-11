package net.edubovit.labyrinth.repository.cached;

import net.edubovit.labyrinth.entity.Game;
import net.edubovit.labyrinth.repository.db.UserRepository;
import net.edubovit.labyrinth.repository.memory.GameCache;
import net.edubovit.labyrinth.repository.memory.UserGameCache;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static java.util.function.Function.identity;

@Component
@RequiredArgsConstructor
public class UserGameCachedRepository {

    private final UserRepository userRepository;

    private final GameCache gameCache;

    private final UserGameCache userGameCache;

    public Optional<Game> getGameByUsername(String username) {
        var cachedGame = userGameCache.get(username);
        if (cachedGame.isPresent()) {
            return cachedGame;
        } else {
            var gameOptional = userRepository.selectGameIdByUsername(username)
                    .map(gameCache::get)
                    .flatMap(identity());
            gameOptional.ifPresent(game -> userGameCache.save(username, game));
            return gameOptional;
        }
    }

    public void flushUser(String username) {
        userGameCache.delete(username);
    }

}
