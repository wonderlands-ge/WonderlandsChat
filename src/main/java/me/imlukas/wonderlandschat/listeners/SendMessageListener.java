/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.milkbowl.vault.permission.Permission
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.AsyncPlayerChatEvent
 */
package me.imlukas.wonderlandschat.listeners;

import java.util.LinkedList;
import java.util.List;
import me.imlukas.wonderlandschat.WonderlandsChatPlugin;
import me.imlukas.wonderlandschat.data.PlayerData;
import me.imlukas.wonderlandschat.data.color.ColorParser;
import me.imlukas.wonderlandschat.data.groups.GroupParser;
import me.imlukas.wonderlandschat.storage.PlayerStorage;
import me.imlukas.wonderlandschat.utils.PlayerUtil;
import me.imlukas.wonderlandschat.utils.TextUtil;
import me.imlukas.wonderlandschat.utils.text.Placeholder;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class SendMessageListener
implements Listener {
    private final PlayerStorage playerStorage;
    private final GroupParser groupParser;
    private final ColorParser colorParser;
    private final Permission perms;

    public SendMessageListener(WonderlandsChatPlugin plugin) {
        this.playerStorage = plugin.getPlayerStorage();
        this.groupParser = plugin.getGroupParser();
        this.colorParser = plugin.getColorParser();
        this.perms = plugin.getPerms();
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onMessage(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();
        message = message.replaceAll("&\\w|&", "");
        PlayerData data = this.playerStorage.getPlayerData(player.getUniqueId());
        if (data == null) {
            return;
        }
        String group = this.perms.getPrimaryGroup(player);

        String format = TextUtil.colorAndReplace(player, this.groupParser.getFormat(group));
        message = player.isOp() || PlayerUtil.hasPermission(player, "placeholders") ? TextUtil.colorAndReplace(player, data.getFormatted() + message) : TextUtil.color(data.getFormatted() + message);
        LinkedList<Placeholder<Player>> placeholderList = new LinkedList<Placeholder<Player>>();
        placeholderList.add(new Placeholder("player", player.getName()));
        placeholderList.add(new Placeholder("player_display", player.getDisplayName()));
        placeholderList.add(new Placeholder("message", message));
        placeholderList.add(new Placeholder("group", group));
        if (WonderlandsChatPlugin.CHAT_ENABLED) {
            this.setFormat(event, format, placeholderList);
            return;
        }
        event.setMessage(message);
    }

    public void setFormat(AsyncPlayerChatEvent event, String format, List<Placeholder<Player>> placeholderList) {
        Player player = event.getPlayer();
        for (Placeholder<Player> placeholder : placeholderList) {
            format = placeholder.replace(format, player);
        }
        format = format.replaceAll("%", "");
        event.setFormat(TextUtil.color(format));
    }
}

