package me.imlukas.wonderlandschat.listeners;

import me.imlukas.wonderlandschat.WonderlandsChatPlugin;
import me.imlukas.wonderlandschat.data.PlayerData;
import me.imlukas.wonderlandschat.data.sql.SQLDatabase;
import me.imlukas.wonderlandschat.data.sql.objects.SQLTable;
import me.imlukas.wonderlandschat.storage.PlayerStorage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.SQLException;
import java.util.UUID;

public class PlayerJoinListener implements Listener {

    private final SQLDatabase database;
    private final PlayerStorage playerStorage;

    public PlayerJoinListener(WonderlandsChatPlugin plugin) {
        this.database = plugin.getSqlDatabase();
        this.playerStorage = plugin.getPlayerStorage();
        reload();
    }

    public void reload() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            addPlayer(player);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        addPlayer(player);
    }

    public void addPlayer(Player player) {
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
        })).thenRun(() -> playerStorage.add(playerUUID, data));
    }

}
