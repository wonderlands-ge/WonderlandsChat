package me.imlukas.wonderlandschat.command;

import me.imlukas.wonderlandschat.WonderlandsChatPlugin;
import me.imlukas.wonderlandschat.utils.command.SimpleCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static me.imlukas.wonderlandschat.WonderlandsChatPlugin.CHAT_ENABLED;

public class ChatToggleCommand implements SimpleCommand {
    private final WonderlandsChatPlugin plugin;

    public ChatToggleCommand(WonderlandsChatPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getIdentifier() {
        return "chat.toggle";
    }

    @Override
    public String getPermission() {
        return "wonderlandschat.toggle";
    }

    @Override
    public void execute(CommandSender sender, String... args) {
        Player player = (Player) sender;
        CHAT_ENABLED = !CHAT_ENABLED;
        plugin.getConfig().set("chat.enabled", CHAT_ENABLED);
        plugin.saveConfig();

        System.out.println(CHAT_ENABLED);

        String state = CHAT_ENABLED ? "enabled" : "disabled";
        plugin.getMessages().sendMessage(player, "chat.toggle", (message) -> message.replace("%state%", state));
    }
}
