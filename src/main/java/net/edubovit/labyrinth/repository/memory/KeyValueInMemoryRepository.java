package net.edubovit.labyrinth.repository.memory;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

class KeyValueInMemoryRepository<K, V> {

    protected final Map<K, V> storageMap = new ConcurrentHashMap<>();

    public Optional<V> get(K key) {
        return Optional.ofNullable(storageMap.get(key));
    }

    public void save(K key, V value) {
        storageMap.put(key, value);
    }

    public void delete(K key) {
        storageMap.remove(key);
    }

}
