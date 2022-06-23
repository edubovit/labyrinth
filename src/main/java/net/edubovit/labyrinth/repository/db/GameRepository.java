package net.edubovit.labyrinth.repository.db;

import net.edubovit.labyrinth.entity.GameBlob;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.UUID;

public interface GameRepository extends CrudRepository<GameBlob, UUID> {

    @Modifying
    @Query("INSERT INTO game (id, game_blob, last_used)" +
            " VALUES (:id, :gameBlob, :lastUsed)" +
            " ON CONFLICT (id) DO UPDATE" +
            " SET game_blob = :gameBlob," +
            " last_used = :lastUsed")
    void insert(UUID id, byte[] gameBlob, LocalDateTime lastUsed);

    @Modifying
    @Query("DELETE FROM game WHERE id = (SELECT game_id FROM \"user\" WHERE username = :username)")
    void deleteByUsername(String username);

}
