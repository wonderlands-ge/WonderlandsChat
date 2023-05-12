package me.imlukas.wonderlandschat.command;

import me.imlukas.wonderlandschat.utils.command.SimpleCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CloseInventory implements SimpleCommand {
    @Override
    public String getIdentifier() {
        return "closeinventory.*";
    }

    @Override
    public void execute(CommandSender sender, String... args) {
        if (args[0].isEmpty()) {
            Player player = (Player) sender;
            player.closeInventory();
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage("§cPlayer not found!");
            return;
        }

        target.closeInventory();
    }
}
