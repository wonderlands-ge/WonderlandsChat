package me.imlukas.wonderlandschat.utils.concurrent;

import me.imlukas.wonderlandschat.utils.collection.TypedMap;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ConcurrentHelper {

    public static CompletableFuture<TypedMap<String>> getMap(Map<String, CompletableFuture<?>> futures) {
        TypedMap<String> map = new TypedMap<>();

        for (Map.Entry<String, CompletableFuture<?>> entry : futures.entrySet()) {
            String key = entry.getKey();
            CompletableFuture<?> future = entry.getValue();

            future.thenAccept(value -> map.put(key, value));
        }

        return CompletableFuture.allOf(futures.values().toArray(new CompletableFuture[0])).thenApply(v -> map);
    }

}
