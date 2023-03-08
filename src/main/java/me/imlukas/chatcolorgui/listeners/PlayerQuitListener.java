package me.imlukas.chatcolorgui.listeners;

import me.imlukas.chatcolorgui.ChatColorPlugin;
import me.imlukas.chatcolorgui.data.PlayerData;
import me.imlukas.chatcolorgui.data.sql.SQLDatabase;
import me.imlukas.chatcolorgui.data.sql.objects.SQLTable;
import me.imlukas.chatcolorgui.storage.PlayerStorage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerQuitListener implements Listener {

    private final SQLDatabase database;
    private final PlayerStorage playerStorage;

    public PlayerQuitListener(ChatColorPlugin plugin) {
        this.database = plugin.getSqlDatabase();
        this.playerStorage = plugin.getPlayerStorage();
    }


    @EventHandler
    public void onJoin(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        SQLTable colorTable = database.getOrCreateTable("chatcolor");

        PlayerData data = playerStorage.getPlayerData(playerUUID);

        String color = data.getColor();
        String format = data.getFormat();

        Map<String, Object> dataMap = new HashMap<>();

        dataMap.put("player_id", playerUUID.toString());
        dataMap.put("color", color);
        dataMap.put("format", format);

        String selectQuery = "SELECT * FROM `chatcolor` WHERE player_id = '" + playerUUID.toString() + "';";

        colorTable.executeQuery(selectQuery).thenAccept((resultSet -> {
            try {
                if (resultSet.next()) {
                    String updateQuery = "UPDATE `chatcolor` SET color = '" + color + "', format = '" + format + "' WHERE player_id = '" + playerUUID.toString() + "';";
                    colorTable.executeQuery(updateQuery);
                } else {
                    colorTable.insert(dataMap);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }));
        playerStorage.remove(playerUUID);
    }
}
