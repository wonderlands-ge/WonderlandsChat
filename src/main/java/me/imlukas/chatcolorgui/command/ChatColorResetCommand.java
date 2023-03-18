package me.imlukas.chatcolorgui.command;

import me.imlukas.chatcolorgui.ChatColorPlugin;
import me.imlukas.chatcolorgui.data.PlayerData;
import me.imlukas.chatcolorgui.storage.PlayerStorage;
import me.imlukas.chatcolorgui.utils.command.SimpleCommand;
import me.imlukas.chatcolorgui.utils.storage.MessagesFile;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChatColorResetCommand implements SimpleCommand {

    private final MessagesFile messages;
    private final PlayerStorage playerStorage;

    public ChatColorResetCommand(ChatColorPlugin plugin) {
        this.messages = plugin.getMessages();
        this.playerStorage = plugin.getPlayerStorage();
    }

    @Override
    public String[] getAliases() {
        return new String[]{"cc.reset.*"};
    }

    @Override
    public String getIdentifier() {
        return "chatcolor.reset.*";
    }

    @Override
    public void execute(CommandSender sender, String... args) {

        if (args[0].isEmpty()) {
            messages.sendMessage(sender,"sepcify-player");
            return;
        }

        Player player = Bukkit.getPlayer(args[0]);

        if (player == null) {
            messages.sendMessage(sender, "player-not-found");
            return;
        }

        PlayerData data = playerStorage.getPlayerData(player.getUniqueId());

        if (data == null) {
            System.out.println("Player's data is null, contact developer.");
            return;
        }

        data.reset();
        messages.sendMessage(sender, "reset");
    }
}
