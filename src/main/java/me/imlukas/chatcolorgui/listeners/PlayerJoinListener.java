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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class PlayerJoinListener implements Listener {

    private final SQLDatabase database;
    private final PlayerStorage playerStorage;

    public PlayerJoinListener(ChatColorPlugin plugin) {
        this.database = plugin.getSqlDatabase();
        this.playerStorage = plugin.getPlayerStorage();
    }


    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        SQLTable colorTable = database.getOrCreateTable("chatcolor");

        String query = "SELECT * FROM `chatcolor` WHERE player_id = '" + playerUUID.toString() + "';";

        PlayerData data = new PlayerData(playerUUID);

        colorTable.executeQuery(query).thenAccept((resultSet -> {
            try {
                if (resultSet.next()) {
                    String color = resultSet.getString("color");
                    String format = resultSet.getString("format");

                    if (color != null) {
                        if (color.equals("random")) {
                            data.setRandomColor(true);
                        } else {
                            data.setColor(color);
                        }
                    }

                    if (format != null) {
                        data.setFormat(format);
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }));

        playerStorage.add(playerUUID, data);
    }

}
