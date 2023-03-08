package me.imlukas.chatcolorgui.listeners;

import me.imlukas.chatcolorgui.ChatColorPlugin;
import me.imlukas.chatcolorgui.data.PlayerData;
import me.imlukas.chatcolorgui.storage.PlayerStorage;
import me.imlukas.chatcolorgui.utils.TextUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class SendMessageListener implements Listener {

    private final PlayerStorage playerStorage;

    public SendMessageListener(ChatColorPlugin plugin) {
        this.playerStorage = plugin.getPlayerStorage();
    }

    @EventHandler
    public void onMessage(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        PlayerData data = playerStorage.getPlayerData(player.getUniqueId());

        if (data == null) {
            return;
        }

        event.setMessage(TextUtil.color(data.getFormatted() + event.getMessage()));
    }
}
