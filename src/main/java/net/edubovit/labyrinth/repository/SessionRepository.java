package net.edubovit.labyrinth.repository;

import net.edubovit.labyrinth.domain.GameSession;

import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class SessionRepository extends KeyValueInMemoryRepository<UUID, GameSession> {
}
