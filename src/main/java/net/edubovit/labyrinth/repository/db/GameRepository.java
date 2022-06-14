package net.edubovit.labyrinth.repository.db;

import net.edubovit.labyrinth.entity.GameBlob;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface GameRepository extends CrudRepository<GameBlob, UUID> {

    @Modifying
    @Query("INSERT INTO game (id, game_blob) VALUES (:id, :gameBlob) ON CONFLICT (id) DO UPDATE SET game_blob = :gameBlob")
    void insert(UUID id, byte[] gameBlob);

    @Modifying
    @Query("DELETE FROM game WHERE id = (SELECT game_id FROM \"user\" WHERE username = :username)")
    void deleteByUsername(String username);

}
