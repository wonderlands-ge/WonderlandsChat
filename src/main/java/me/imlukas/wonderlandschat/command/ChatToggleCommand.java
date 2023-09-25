/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package me.imlukas.wonderlandschat.command;

import me.imlukas.wonderlandschat.WonderlandsChatPlugin;
import me.imlukas.wonderlandschat.utils.command.SimpleCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChatToggleCommand
implements SimpleCommand {
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
    public void execute(CommandSender sender, String ... args) {
        Player player = (Player)sender;
        WonderlandsChatPlugin.CHAT_ENABLED = !WonderlandsChatPlugin.CHAT_ENABLED;
        this.plugin.getConfig().set("chat.enabled", (Object)WonderlandsChatPlugin.CHAT_ENABLED);
        this.plugin.saveConfig();
        System.out.println(WonderlandsChatPlugin.CHAT_ENABLED);
        String state = WonderlandsChatPlugin.CHAT_ENABLED ? "enabled" : "disabled";
        this.plugin.getMessages().sendMessage((CommandSender)player, "chat.toggle", message -> message.replace("%state%", state));
    }
}

