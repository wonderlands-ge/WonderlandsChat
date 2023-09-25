/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.command.TabCompleter
 */
package me.imlukas.wonderlandschat.utils.command;

import java.util.Collections;
import java.util.List;
import me.imlukas.wonderlandschat.WonderlandsChatPlugin;
import me.imlukas.wonderlandschat.utils.command.SimpleCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class BaseCommand
implements CommandExecutor,
TabCompleter {
    private final WonderlandsChatPlugin main;

    public BaseCommand(WonderlandsChatPlugin main) {
        this.main = main;
    }

    public List<String> onTabComplete(CommandSender sender, Command cmd, String name, String[] args) throws IllegalArgumentException {
        if (args.length == 0) {
            return Collections.emptyList();
        }
        String identifier = String.join((CharSequence)".", name, String.join((CharSequence)".", args)).replace(" ", ".");
        return this.main.getCommandManager().tabComplete(identifier);
    }

    public boolean onCommand(CommandSender sender, Command cmd, String name, String[] args) {
        String identifier = String.join((CharSequence)".", name, String.join((CharSequence)".", args));
        while (identifier.endsWith(".")) {
            identifier = identifier.substring(0, identifier.length() - 1);
        }
        SimpleCommand command = this.main.getCommandManager().get(identifier);
        if (command == null) {
            this.main.getMessages().sendMessage(sender, "command.invalid-args");
            return true;
        }
        String permission = command.getPermission();
        if (!command.canExecute(sender)) {
            this.main.getMessages().sendMessage(sender, "command.cannot-use", str -> str.replace("%permission%", permission).replace("%command%", name));
            return true;
        }
        if (command.hasPermission() && !sender.hasPermission(command.getPermission())) {
            this.main.getMessages().sendMessage(sender, "command.no-permission", str -> str.replace("%permission%", permission).replace("%command%", name));
            return true;
        }
        List<Integer> wildcards = command.getWildcards();
        String[] commandArgs = new String[wildcards.size()];
        for (int index = 0; index < wildcards.size(); ++index) {
            int argsIndex = wildcards.get(index) - 1;
            String text = argsIndex < args.length ? args[argsIndex] : "";
            commandArgs[index] = text;
        }
        command.execute(sender, commandArgs);
        return true;
    }
}

