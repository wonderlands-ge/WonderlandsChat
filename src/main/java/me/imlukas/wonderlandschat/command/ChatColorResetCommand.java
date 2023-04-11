package me.imlukas.wonderlandschat.command;

import me.imlukas.wonderlandschat.WonderlandsChatPlugin;
import me.imlukas.wonderlandschat.data.PlayerData;
import me.imlukas.wonderlandschat.utils.command.SimpleCommand;
import me.imlukas.wonderlandschat.utils.storage.MessagesFile;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChatColorResetCommand implements SimpleCommand {

    private final WonderlandsChatPlugin plugin;
    private final MessagesFile messages;

    public ChatColorResetCommand(WonderlandsChatPlugin plugin) {
        this.plugin = plugin;
        this.messages = plugin.getMessages();
    }

    @Override
    public String getIdentifier() {
        return "chat.reset.*";
    }

    @Override
    public String getPermission() {
        return "wonderlandschat.reset";
    }

    @Override
    public void execute(CommandSender sender, String... args) {
        if (args[0].isEmpty()) {
            messages.sendMessage(sender, "specify-player");
        }

        Player player = Bukkit.getPlayer(args[0]);

        if (player == null) {
            messages.sendMessage(sender, "player-not-found", (message) -> message.replace("%player%", args[0]));
        }

        PlayerData data = plugin.getPlayerStorage().getPlayerData(player.getUniqueId());

        data.reset();

        plugin.getMessages().sendMessage(player, "reset-player", (message) -> message.replace("%player%", player.getName()));
    }
}
