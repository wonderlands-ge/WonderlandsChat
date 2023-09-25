/*
 * Decompiled with CFR 0.150.
 */
package me.imlukas.wonderlandschat.utils.collection;

import java.util.concurrent.ConcurrentHashMap;

public class TypedMap<K>
extends ConcurrentHashMap<K, Object> {
    public <T> T getTyped(K key) {
        return (T)this.get(key);
    }
}

