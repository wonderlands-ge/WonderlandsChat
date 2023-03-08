package me.imlukas.chatcolorgui.command;

import me.imlukas.chatcolorgui.ChatColorPlugin;
import me.imlukas.chatcolorgui.gui.ChatColorMenu;
import me.imlukas.chatcolorgui.utils.command.SimpleCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChatColorCommand implements SimpleCommand {

    private final ChatColorPlugin plugin;

    public ChatColorCommand(ChatColorPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String[] getAliases() {
        return new String[]{"cc"};
    }

    @Override
    public String getIdentifier() {
        return "chatcolor";
    }

    @Override
    public void execute(CommandSender sender, String... args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("You must be a player to use this command!");
            return;
        }

        new ChatColorMenu(plugin).open((Player) sender);
    }
}
