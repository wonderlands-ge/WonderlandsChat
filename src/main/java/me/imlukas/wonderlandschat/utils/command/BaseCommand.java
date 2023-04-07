package me.imlukas.wonderlandschat.utils.command;

import me.imlukas.wonderlandschat.WonderlandsChatPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.Collections;
import java.util.List;

public class BaseCommand implements CommandExecutor, TabCompleter {

    private final WonderlandsChatPlugin main;

    public BaseCommand(WonderlandsChatPlugin main) {
        this.main = main;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String name, String[] args)
            throws IllegalArgumentException {
        if (args.length == 0) {
            return Collections.emptyList();
        }

        String identifier = String.join(".", name, String.join(".", args)).replace(" ", ".");
        return main.getCommandManager().tabComplete(identifier);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String name, String[] args) {
        String identifier = String.join(".", name, String.join(".", args));

        // remove trailing dots, this is not tab completion
        while (identifier.endsWith(".")) {
            identifier = identifier.substring(0, identifier.length() - 1);
        }

        SimpleCommand command = main.getCommandManager().get(identifier);

        if (command == null) {
            main.getMessages().sendMessage(sender, "command.invalid-args");
            return true;
        }

        String permission = command.getPermission();

        if (!command.canExecute(sender)) {
            main.getMessages().sendMessage(sender, "command.cannot-use",
                    (str) -> str.replace("%permission%", permission).replace("%command%", name));
            return true;
        }

        if (command.hasPermission() && !sender.hasPermission(command.getPermission())) {
            main.getMessages().sendMessage(sender, "command.no-permission",
                    (str) -> str.replace("%permission%", permission).replace("%command%", name));
            return true;
        }

        List<Integer> wildcards = command.getWildcards();

        String[] commandArgs = new String[wildcards.size()];

        for (int index = 0; index < wildcards.size(); index++) {
            int argsIndex = wildcards.get(index) - 1;

            String text;

            if (argsIndex < args.length) {
                text = args[argsIndex];
            } else {
                text = "";
            }

            commandArgs[index] = text;
        }

        command.execute(sender, commandArgs);
        return true;
    }


}