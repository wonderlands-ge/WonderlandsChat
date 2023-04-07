package me.imlukas.wonderlandschat.utils.command.impl;

import me.imlukas.wonderlandschat.WonderlandsChatPlugin;
import me.imlukas.wonderlandschat.utils.command.BaseCommand;
import me.imlukas.wonderlandschat.utils.command.SimpleCommand;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class CommandManager {

    private static CommandMap commandMap;
    private static Constructor<PluginCommand> pluginCommandConstructor;

    static {
        try {
            Server server = Bukkit.getServer();
            Field commandMapField = server.getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);

            commandMap = (CommandMap) commandMapField.get(server);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        try {
            pluginCommandConstructor = PluginCommand.class.getDeclaredConstructor(String.class,
                    Plugin.class);
            pluginCommandConstructor.setAccessible(true);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    private final Set<String> registeredBaseCommands = new HashSet<>();
    private final Map<String, SimpleCommand> commands = new HashMap<>();
    private final WonderlandsChatPlugin main;

    public CommandManager(WonderlandsChatPlugin main) {
        this.main = main;
    }

    public void register(SimpleCommand command) {
        if (command.getIdentifier() == null) {
            System.err.println("Command " + command.getClass().getSimpleName() + " has no identifier!");
            return;
        }

        System.out.println("Registered command " + command.getClass().getSimpleName());

        if (command.getIdentifier().startsWith("*")) {
            throw new IllegalArgumentException("Command identifier cannot start with *");
        }

        int wildcardCount = 0;

        for (char c : command.getIdentifier().toCharArray()) {
            if (c == '*') {
                wildcardCount++;
            }
        }

        for(String alias : command.getAliases()) {
            if (alias.startsWith("*")) {
                throw new IllegalArgumentException("Command alias cannot start with *");
            }

            int aliasWildcardCount = 0;

            for (char c : alias.toCharArray()) {
                if (c == '*') {
                    aliasWildcardCount++;
                }
            }

            if (aliasWildcardCount != wildcardCount) {
                throw new IllegalArgumentException("Command alias " + alias + " does not have the same amount of wildcards as the command identifier " + command.getIdentifier());
            }
        }

        registerCommand(command.getIdentifier(), command);

        for (String alias : command.getAliases()) {
            registerCommand(alias, command);
        }
    }

    private void registerCommand(String identifier, SimpleCommand command) {
        commands.put(identifier, command);

        String base = getBaseCommand(identifier);

        System.out.println(identifier + "'s base command is " + base);

        if (!registeredBaseCommands.contains(base)) {
            System.out.println(base + " was not registered as a command, registering..");

            try {
                PluginCommand pluginCommand = pluginCommandConstructor.newInstance(base, main);

                BaseCommand baseCommand = new BaseCommand(main);

                pluginCommand.setExecutor(baseCommand);
                pluginCommand.setTabCompleter(baseCommand);

                commandMap.register(base, pluginCommand);

            } catch (InstantiationException | IllegalAccessException |
                     InvocationTargetException e) {
                e.printStackTrace();
            }

            System.out.println(base + " was registered as a command");
            registeredBaseCommands.add(base);
        }
    }

    public SimpleCommand get(String identifier) {
        String[] split = identifier.split("\\.");
        String base = split[0];

        if (split.length == 1) {
            return commands.get(base);
        }

        SimpleCommand command = commands.get(base);

        if(command != null) {
            return command;
        }

        for (String key : commands.keySet()) {
            if (key.startsWith(base + ".")) {
                String[] splitKey = key.split("\\.");
                String[] splitIdentifier = identifier.split("\\.");

                boolean matches = true;

                for (int i = 0; i < splitKey.length; i++) {
                    if (splitKey[i].equals("*")) {
                        continue;
                    }

                    if (splitKey[i].equals(splitIdentifier[i])) {
                        continue;
                    }

                    matches = false;
                    break;
                }

                if (matches) {
                    return commands.get(key);
                }
            }
        }

        return null;
    }

    public List<String> tabComplete(String identifier) {
        /*
        ComparisonResult result = new ComparisonResult(commands);
        return result.tabComplete(identifier);

         */

        // An identifier can, for example, be "test" or "test.subcommand" or "test.subcommand.*"
        // A * is a wildcard, which means it can accept any value.
        // A . is a separator, which means it separates an argument.

        // The identifier parameter is, for example, "test.subcommand.whatever"
        // If the identifier ends with a ., it means that the user is trying to tab complete the next argument.
        // If the tab-complete is for a wildcard, it will return the values in the tabCompleteWildcard method,
        // which is a Map<Integer, List<String>>. The integer is the index of the wildcard (starting at 1), and the list is the values

        // All the logic is done here, there is no tabComplete or tabCompleteWildcard method in the SimpleCommand class.

        // For the input "test.s", and the commands "test.sub" and "test.subcommand", it should return ["sub", "subcommand"]
        // For the input "test.subcommand.", and the commands "test.subcommand.*" and "test.subcommand.whatever", it should return [<tabCompleteWildcards>, "whatever"]

        String[] split = identifier.split("\\.");
        String base = split[0];

        if (split.length == 1) {
            return new ArrayList<>();
        }

        List<String> completions = new ArrayList<>();

        for (String key : commands.keySet()) {
            if (key.startsWith(base + ".")) {
                String[] splitKey = key.split("\\.");
                String[] splitIdentifier = identifier.split("\\.");

                boolean matches = true;

                for (int i = 0; i < splitKey.length; i++) {
                    if (splitKey[i].equals("*")) {
                        continue;
                    }

                    if (splitKey[i].equals(splitIdentifier[i])) {
                        continue;
                    }

                    matches = false;
                    break;
                }

                if (matches) {
                    if (splitKey.length == splitIdentifier.length) {
                        completions.add(splitKey[splitKey.length - 1]);
                    } else if (splitKey.length > splitIdentifier.length) {
                        if (splitKey[splitIdentifier.length].equals("*")) {
                            completions.addAll(commands.get(key).tabCompleteWildcards().get(splitIdentifier.length));
                        } else {
                            completions.add(splitKey[splitIdentifier.length]);
                        }
                    }
                }
            }
        }

        return completions;
    }

    public SimpleCommand get(String name, String... args) {
        String identifier = String.join(".", name, String.join(".", args));

        return get(identifier);
    }

    private String getBaseCommand(String identifier) {
        int index = identifier.indexOf(".");
        return index == -1 ? identifier : identifier.substring(0, index);
    }

}
