package net.edubovit.labyrinth.repository;

import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class ImageRepository extends KeyValueInMemoryRepository<UUID, byte[]> {
}
