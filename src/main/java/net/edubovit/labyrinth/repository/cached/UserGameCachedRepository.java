package net.edubovit.labyrinth.repository.cached;

import net.edubovit.labyrinth.entity.Game;
import net.edubovit.labyrinth.repository.db.UserRepository;
import net.edubovit.labyrinth.repository.memory.UserGameCache;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static java.util.function.Function.identity;

@Component
@RequiredArgsConstructor
public class UserGameCachedRepository {

    private final UserRepository userRepository;

    private final GameCachedRepository gameCachedRepository;

    private final UserGameCache userGameCache;

    public Optional<Game> getGameByUsername(String username) {
        var cachedGame = userGameCache.get(username);
        if (cachedGame.isPresent()) {
            return cachedGame;
        } else {
            var gameOptional = userRepository.selectGameIdByUsername(username)
                    .map(gameCachedRepository::get)
                    .flatMap(identity());
            gameOptional.ifPresent(game -> userGameCache.save(username, game));
            return gameOptional;
        }
    }

    public void deleteUserGame(String username) {
        userGameCache.get(username)
                .ifPresent(game -> {
                    gameCachedRepository.deleteByGameId(game.getId());
                    userGameCache.delete(username);
                });
    }

}
