package net.edubovit.labyrinth.repository.memory;

import net.edubovit.labyrinth.entity.Game;

import org.springframework.stereotype.Component;

@Component
public class UserGameCache extends KeyValueInMemoryRepository<String, Game> {

}
