package me.imlukas.wonderlandschat.listeners;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import me.imlukas.wonderlandschat.WonderlandsChatPlugin;
import me.imlukas.wonderlandschat.data.PlayerData;
import me.imlukas.wonderlandschat.data.sql.SQLDatabase;
import me.imlukas.wonderlandschat.data.sql.objects.SQLTable;
import me.imlukas.wonderlandschat.storage.PlayerStorage;
import me.imlukas.wonderlandschat.utils.menu.base.BaseMenu;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InventoryPacketListener {

    private final WonderlandsChatPlugin plugin;
    private final PlayerStorage playerStorage;
    private final SQLDatabase database;

    public InventoryPacketListener(WonderlandsChatPlugin plugin) {
        this.plugin = plugin;
        this.playerStorage = plugin.getPlayerStorage();
        this.database = plugin.getSqlDatabase();
    }

    /**
     * Saves player data on inventory close to avoid sync issues
     */
    public void listen() {
        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        protocolManager.addPacketListener(new PacketAdapter(plugin, PacketType.Play.Client.CLOSE_WINDOW) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                Player player = event.getPlayer();
                Inventory playerInv = player.getOpenInventory().getTopInventory();

                event.setCancelled(true);

                if (!(playerInv.getHolder() instanceof BaseMenu)) {
                    return;
                }

                UUID playerId = player.getUniqueId();
                SQLTable colorTable = database.getOrCreateTable("chatcolor");

                PlayerData data = playerStorage.getPlayerData(playerId);

                String color = data.getColor();
                String format = data.getFormat();

                Map<String, Object> dataMap = new HashMap<>();

                dataMap.put("player_id", playerId.toString());
                dataMap.put("color", color);
                dataMap.put("format", format);

                String selectQuery = "SELECT * FROM `chatcolor` WHERE player_id = '" + playerId + "';";

                colorTable.executeQuery(selectQuery).thenAccept((resultSet -> {
                    try {
                        if (resultSet.next()) {
                            String updateQuery = "UPDATE `chatcolor` SET color = '" + color + "', format = '" + format +
                                    "' WHERE player_id = '" + playerId + "';";
                            colorTable.executeQuery(updateQuery);
                        } else {
                            colorTable.insert(dataMap);
                        }
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                })).thenRun(player::closeInventory);
            }
            @Override
            public void onPacketSending(PacketEvent event) {
            }
        });
    }
}