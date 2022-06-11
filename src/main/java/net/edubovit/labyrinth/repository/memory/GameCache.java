package net.edubovit.labyrinth.repository.memory;

import net.edubovit.labyrinth.config.Defaults;
import net.edubovit.labyrinth.entity.Game;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

import static net.edubovit.labyrinth.config.Defaults.SESSION_STORAGE_TIME;
import static net.edubovit.labyrinth.config.Defaults.STALE_SESSION_STORAGE_TIME;
import static net.edubovit.labyrinth.config.Defaults.STALE_SESSION_TURNS;

@Component
@Slf4j
public class GameCache extends KeyValueInMemoryRepository<UUID, Game> {

    private int cleanupCounter = 0;

    @Override
    public void save(UUID key, Game value) {
        super.save(key, value);
        cleanupCounter = (cleanupCounter + 1) % Defaults.SESSIONS_CLEANUP_THRESHOLD;
        if (cleanupCounter == 0 && storageMap.size() >= Defaults.SESSIONS_CLEANUP_THRESHOLD) {
            cleanup();
        }
    }

    private void cleanup() {
        log.info("It's time to session cleanup! Currently sessions stored: {}", storageMap.size());
        var survivors = storageMap.entrySet()
                .stream()
                .filter(entry -> entry.getValue().getLastUsed().plus(SESSION_STORAGE_TIME).isAfter(LocalDateTime.now())
                        || entry.getValue().getLastUsed().plus(STALE_SESSION_STORAGE_TIME).isAfter(LocalDateTime.now())
                        && entry.getValue().getTurns() < STALE_SESSION_TURNS)
                .map(entry -> new StoredSession(entry.getKey(), entry.getValue()))
                .toList();
        storageMap.clear();
        survivors.forEach(storedSession -> storageMap.put(storedSession.id, storedSession.session));
        log.info("Sessions survived: {}", storageMap.size());
    }

    private record StoredSession(UUID id, Game session) {
    }

}
