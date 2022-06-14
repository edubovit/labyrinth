package net.edubovit.labyrinth.repository.db;

import net.edubovit.labyrinth.entity.User;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends CrudRepository<User, UUID> {

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    @Query("SELECT game_id FROM \"user\" WHERE username = :username")
    Optional<UUID> selectGameIdByUsername(String username);

    @Modifying
    @Query("UPDATE \"user\" SET game_id = :gameId WHERE username = :username")
    void updateGameForUser(UUID gameId, String username);

}
