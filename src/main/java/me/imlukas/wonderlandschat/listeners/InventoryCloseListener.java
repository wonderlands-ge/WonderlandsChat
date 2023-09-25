/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.inventory.InventoryCloseEvent
 *  org.bukkit.inventory.Inventory
 */
package me.imlukas.wonderlandschat.listeners;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;
import me.imlukas.wonderlandschat.WonderlandsChatPlugin;
import me.imlukas.wonderlandschat.data.PlayerData;
import me.imlukas.wonderlandschat.data.sql.SQLDatabase;
import me.imlukas.wonderlandschat.data.sql.objects.SQLTable;
import me.imlukas.wonderlandschat.storage.PlayerStorage;
import me.imlukas.wonderlandschat.utils.menu.base.BaseMenu;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

public class InventoryCloseListener
implements Listener {
    private final PlayerStorage playerStorage;
    private final SQLDatabase database;

    public InventoryCloseListener(WonderlandsChatPlugin plugin) {
        this.playerStorage = plugin.getPlayerStorage();
        this.database = plugin.getSqlDatabase();
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player)event.getPlayer();
        Inventory playerInv = player.getOpenInventory().getTopInventory();
        if (!(playerInv.getHolder() instanceof BaseMenu)) {
            return;
        }
        UUID playerId = player.getUniqueId();
        SQLTable colorTable = this.database.getOrCreateTable("chatcolor");
        PlayerData data = this.playerStorage.getPlayerData(playerId);
        String color = data.getColor();
        String format = data.getFormat();
        HashMap<String, Object> dataMap = new HashMap<>();
        dataMap.put("player_id", playerId.toString());
        dataMap.put("color", color);
        dataMap.put("format", format);
        String selectQuery = "SELECT * FROM `chatcolor` WHERE player_id = '" + playerId + "';";
        colorTable.executeQuery(selectQuery, new Object[0]).thenAccept(resultSet -> {
            try {
                if (resultSet.next()) {
                    String updateQuery = "UPDATE `chatcolor` SET color = '" + color + "', format = '" + format + "' WHERE player_id = '" + playerId + "';";
                    colorTable.executeQuery(updateQuery);
                } else {
                    colorTable.insert(dataMap);
                }
            }
            catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }
}

