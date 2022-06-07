package net.edubovit.labyrinth.repository;

import net.edubovit.labyrinth.domain.GameSession;

import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
public class SessionRepository {

    private final Map<UUID, GameSession> sessionsById = new HashMap<>();

    public Optional<GameSession> get(UUID id) {
        return Optional.ofNullable(sessionsById.get(id));
    }

    public void save(GameSession session) {
        if (session.getId() == null) {
            session.setId(UUID.randomUUID());
        }
        sessionsById.put(session.getId(), session);
    }

}
