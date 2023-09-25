/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package me.imlukas.wonderlandschat.command;

import me.imlukas.wonderlandschat.utils.command.SimpleCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CloseInventory
implements SimpleCommand {
    @Override
    public String getIdentifier() {
        return "closeinventory.*";
    }

    @Override
    public void execute(CommandSender sender, String ... args) {
        if (args[0].isEmpty()) {
            Player player = (Player)sender;
            player.closeInventory();
            return;
        }
        Player target = Bukkit.getPlayer((String)args[0]);
        if (target == null) {
            sender.sendMessage("\u00c2\u00a7cPlayer not found!");
            return;
        }
        target.closeInventory();
    }
}

