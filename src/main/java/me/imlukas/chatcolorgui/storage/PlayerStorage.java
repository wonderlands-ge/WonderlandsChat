package me.imlukas.chatcolorgui.storage;

import me.imlukas.chatcolorgui.data.PlayerData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Stores all the player data.
 */
public class PlayerStorage{

    private final Map<UUID, PlayerData> playerData = new HashMap<>();

    public PlayerData getPlayerData(UUID uuid) {
        return playerData.get(uuid);
    }

    public PlayerData getOrCreatePlayerData(UUID uuid) {
        if (playerData.containsKey(uuid)) {
            return getPlayerData(uuid);
        } else {
            PlayerData data = new PlayerData(uuid);
            add(uuid, data);
            return data;
        }
    }

    public void add(UUID uuid, PlayerData data) {
        playerData.put(uuid, data);
    }

    public void remove(UUID uuid) {
        playerData.remove(uuid);
    }

    public boolean contains(UUID uuid) {
        return playerData.containsKey(uuid);
    }
}
