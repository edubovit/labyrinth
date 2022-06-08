package net.edubovit.labyrinth.repository;

import net.edubovit.labyrinth.config.Defaults;

import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class ImageRepository extends KeyValueInMemoryRepository<UUID, byte[]> {

    private StoredImage[] lastStored = new StoredImage[Defaults.STORE_LAST_IMAGES];

    private int lastStoredIdx = 0;

    @Override
    public void save(UUID key, byte[] value) {
        super.save(key, value);
        lastStored[lastStoredIdx = (lastStoredIdx + 1) % Defaults.STORE_LAST_IMAGES] = new StoredImage(key, value);
        if (storageMap.size() >= Defaults.IMAGES_CLEANUP_THRESHOLD) {
            cleanup();
        }
    }

    private void cleanup() {
        storageMap.clear();
        for (var storedImage : lastStored) {
            storageMap.put(storedImage.id, storedImage.bytes);
        }
    }

    private record StoredImage(UUID id, byte[] bytes) {
    }

}
