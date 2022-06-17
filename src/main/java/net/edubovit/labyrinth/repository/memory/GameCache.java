package net.edubovit.labyrinth.repository.memory;

import net.edubovit.labyrinth.domain.Game;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class GameCache extends KeyValueInMemoryRepository<UUID, Game> {

}
