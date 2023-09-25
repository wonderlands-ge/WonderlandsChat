/*
 * Decompiled with CFR 0.150.
 */
package me.imlukas.wonderlandschat.utils.concurrent;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import me.imlukas.wonderlandschat.utils.collection.TypedMap;

public class ConcurrentHelper {
    public static CompletableFuture<TypedMap<String>> getMap(Map<String, CompletableFuture<?>> futures) {
        TypedMap map = new TypedMap();
        for (Map.Entry<String, CompletableFuture<?>> entry : futures.entrySet()) {
            String key = entry.getKey();
            CompletableFuture<?> future = entry.getValue();
            future.thenAccept(value -> map.put(key, value));
        }
        return CompletableFuture.allOf(futures.values().toArray(new CompletableFuture[0])).thenApply(v -> map);
    }
}

