package net.edubovit.labyrinth.repository;

import net.edubovit.labyrinth.config.Defaults;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@Slf4j
public class ImageRepository extends KeyValueInMemoryRepository<UUID, byte[]> {

    private final StoredImage[] lastStored = new StoredImage[Defaults.STORE_LAST_IMAGES];

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
        log.info("It's time to images cleanup! Currently images stored: {}", storageMap.size());
        storageMap.clear();
        for (var storedImage : lastStored) {
            storageMap.put(storedImage.id, storedImage.bytes);
        }
        log.info("Images survived: {}", storageMap.size());
    }

    private record StoredImage(UUID id, byte[] bytes) {
    }

}
