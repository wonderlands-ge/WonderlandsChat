package me.imlukas.wonderlandschat.command;

import me.imlukas.wonderlandschat.WonderlandsChatPlugin;
import me.imlukas.wonderlandschat.utils.command.SimpleCommand;
import org.bukkit.command.CommandSender;

public class ReloadCommand implements SimpleCommand {

    private final WonderlandsChatPlugin plugin;

    public ReloadCommand(WonderlandsChatPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getIdentifier() {
        return "chat.reload";
    }

    @Override
    public String getPermission() {
        return "wonderlandschat.reload";
    }

    @Override
    public void execute(CommandSender sender, String... args) {
        plugin.onDisable();
        plugin.onEnable();

        plugin.getMessages().sendMessage(sender, "admin.reload");
    }
}
