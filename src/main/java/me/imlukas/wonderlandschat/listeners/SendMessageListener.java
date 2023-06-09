package me.imlukas.wonderlandschat.listeners;

import me.imlukas.wonderlandschat.WonderlandsChatPlugin;
import me.imlukas.wonderlandschat.data.PlayerData;
import me.imlukas.wonderlandschat.data.color.ColorParser;
import me.imlukas.wonderlandschat.data.groups.GroupParser;
import me.imlukas.wonderlandschat.storage.PlayerStorage;
import me.imlukas.wonderlandschat.utils.TextUtil;
import me.imlukas.wonderlandschat.utils.storage.MessagesFile;
import me.imlukas.wonderlandschat.utils.text.Placeholder;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static me.imlukas.wonderlandschat.WonderlandsChatPlugin.CHAT_ENABLED;
import static me.imlukas.wonderlandschat.data.color.ColorParser.FORMATS;
import static me.imlukas.wonderlandschat.utils.PlayerUtil.hasPermission;

public class SendMessageListener implements Listener {

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

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMessage(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();
        message = message.replaceAll("&\\w|&", "");

        PlayerData data = playerStorage.getPlayerData(player.getUniqueId());

        if (data == null) {
            return;
        }

        String group = perms.getPrimaryGroup(player);
        String format = TextUtil.colorAndReplace(player, groupParser.getFormat(group));

        if (player.isOp() || hasPermission(player, "placeholders")) {
            message = TextUtil.colorAndReplace(player, data.getFormatted() + message);
        } else {
            message = TextUtil.color(data.getFormatted() + message);
        }

        LinkedList<Placeholder<Player>> placeholderList = new LinkedList<>();
        placeholderList.add(new Placeholder<>("player", player.getName()));
        placeholderList.add(new Placeholder<>("player_display", player.getDisplayName()));
        placeholderList.add(new Placeholder<>("message", message));
        placeholderList.add(new Placeholder<>("group", group));

        if (CHAT_ENABLED) {
            setFormat(event, format, placeholderList);
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
