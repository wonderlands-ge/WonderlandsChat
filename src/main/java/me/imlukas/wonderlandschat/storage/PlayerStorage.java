/*
 * Decompiled with CFR 0.150.
 */
package me.imlukas.wonderlandschat.storage;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import me.imlukas.wonderlandschat.data.PlayerData;

public class PlayerStorage {
    private final Map<UUID, PlayerData> playerData = new HashMap<UUID, PlayerData>();

    public PlayerData getPlayerData(UUID uuid) {
        return this.playerData.get(uuid);
    }

    public PlayerData getOrCreatePlayerData(UUID uuid) {
        if (this.playerData.containsKey(uuid)) {
            return this.getPlayerData(uuid);
        }
        PlayerData data = new PlayerData(uuid);
        this.add(uuid, data);
        return data;
    }

    public void add(UUID uuid, PlayerData data) {
        this.playerData.put(uuid, data);
    }

    public void remove(UUID uuid) {
        this.playerData.remove(uuid);
    }

    public boolean contains(UUID uuid) {
        return this.playerData.containsKey(uuid);
    }
}

