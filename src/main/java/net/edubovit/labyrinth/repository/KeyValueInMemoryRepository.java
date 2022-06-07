package net.edubovit.labyrinth.repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

class KeyValueInMemoryRepository<K, V> {

    private final Map<K, V> storageMap = new HashMap<>();

    public Optional<V> get(K key) {
        return Optional.ofNullable(storageMap.get(key));
    }

    public void save(K key, V value) {
        storageMap.put(key, value);
    }

}
