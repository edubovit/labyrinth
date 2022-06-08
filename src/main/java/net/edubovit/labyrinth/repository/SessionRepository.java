package net.edubovit.labyrinth.repository;

import net.edubovit.labyrinth.config.Defaults;
import net.edubovit.labyrinth.domain.GameSession;

import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public class SessionRepository extends KeyValueInMemoryRepository<UUID, GameSession> {

    private int cleanupCounter = 0;

    @Override
    public void save(UUID key, GameSession value) {
        super.save(key, value);
        cleanupCounter = (cleanupCounter + 1) % Defaults.SESSIONS_CLEANUP_THRESHOLD;
        if (cleanupCounter == 0 && storageMap.size() >= Defaults.SESSIONS_CLEANUP_THRESHOLD) {
            cleanup();
        }
    }

    private void cleanup() {
        var survivors = storageMap.entrySet()
                .stream()
                .filter(entry -> entry.getValue().getLastUsed().plus(Defaults.SESSION_STORAGE_TIME).isAfter(LocalDateTime.now()))
                .map(entry -> new StoredSession(entry.getKey(), entry.getValue()))
                .toList();
        storageMap.clear();
        survivors.forEach(storedSession -> storageMap.put(storedSession.id, storedSession.session));
    }

    private record StoredSession(UUID id, GameSession session) {
    }

}
