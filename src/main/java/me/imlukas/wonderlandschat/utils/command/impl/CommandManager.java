/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Server
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandMap
 *  org.bukkit.command.PluginCommand
 *  org.bukkit.command.TabCompleter
 *  org.bukkit.plugin.Plugin
 */
package me.imlukas.wonderlandschat.utils.command.impl;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import me.imlukas.wonderlandschat.WonderlandsChatPlugin;
import me.imlukas.wonderlandschat.utils.command.BaseCommand;
import me.imlukas.wonderlandschat.utils.command.SimpleCommand;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.Plugin;

public class CommandManager {
    private static CommandMap commandMap;
    private static Constructor<PluginCommand> pluginCommandConstructor;
    private final Set<String> registeredBaseCommands = new HashSet<String>();
    private final Map<String, SimpleCommand> commands = new HashMap<String, SimpleCommand>();
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
            if (c != '*') continue;
            ++wildcardCount;
        }
        for (String alias : command.getAliases()) {
            if (alias.startsWith("*")) {
                throw new IllegalArgumentException("Command alias cannot start with *");
            }
            int aliasWildcardCount = 0;
            for (char c : alias.toCharArray()) {
                if (c != '*') continue;
                ++aliasWildcardCount;
            }
            if (aliasWildcardCount == wildcardCount) continue;
            throw new IllegalArgumentException("Command alias " + alias + " does not have the same amount of wildcards as the command identifier " + command.getIdentifier());
        }
        this.registerCommand(command.getIdentifier(), command);
        for (String alias : command.getAliases()) {
            this.registerCommand(alias, command);
        }
    }

    private void registerCommand(String identifier, SimpleCommand command) {
        this.commands.put(identifier, command);
        String base = this.getBaseCommand(identifier);
        System.out.println(identifier + "'s base command is " + base);
        if (!this.registeredBaseCommands.contains(base)) {
            System.out.println(base + " was not registered as a command, registering..");
            try {
                PluginCommand pluginCommand = pluginCommandConstructor.newInstance(new Object[]{base, this.main});
                BaseCommand baseCommand = new BaseCommand(this.main);
                pluginCommand.setExecutor((CommandExecutor)baseCommand);
                pluginCommand.setTabCompleter((TabCompleter)baseCommand);
                commandMap.register(base, (Command)pluginCommand);
            }
            catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
                e.printStackTrace();
            }
            System.out.println(base + " was registered as a command");
            this.registeredBaseCommands.add(base);
        }
    }

    public SimpleCommand get(String identifier) {
        String[] split = identifier.split("\\.");
        String base = split[0];
        if (split.length == 1) {
            return this.commands.get(base);
        }
        SimpleCommand command = this.commands.get(base);
        if (command != null) {
            return command;
        }
        for (String key : this.commands.keySet()) {
            if (!key.startsWith(base + ".")) continue;
            String[] splitKey = key.split("\\.");
            String[] splitIdentifier = identifier.split("\\.");
            boolean matches = true;
            for (int i = 0; i < splitKey.length; ++i) {
                if (splitKey[i].equals("*") || splitKey[i].equals(splitIdentifier[i])) continue;
                matches = false;
                break;
            }
            if (!matches) continue;
            return this.commands.get(key);
        }
        return null;
    }

    public List<String> tabComplete(String identifier) {
        String[] split = identifier.split("\\.");
        String base = split[0];
        if (split.length == 1) {
            return new ArrayList<String>();
        }
        ArrayList<String> completions = new ArrayList<String>();
        for (String key : this.commands.keySet()) {
            if (!key.startsWith(base + ".")) continue;
            String[] splitKey = key.split("\\.");
            String[] splitIdentifier = identifier.split("\\.");
            boolean matches = true;
            for (int i = 0; i < splitKey.length; ++i) {
                if (splitKey[i].equals("*") || splitKey[i].equals(splitIdentifier[i])) continue;
                matches = false;
                break;
            }
            if (!matches) continue;
            if (splitKey.length == splitIdentifier.length) {
                completions.add(splitKey[splitKey.length - 1]);
                continue;
            }
            if (splitKey.length <= splitIdentifier.length) continue;
            if (splitKey[splitIdentifier.length].equals("*")) {
                completions.addAll((Collection<String>)this.commands.get(key).tabCompleteWildcards().get(splitIdentifier.length));
                continue;
            }
            completions.add(splitKey[splitIdentifier.length]);
        }
        return completions;
    }

    public SimpleCommand get(String name, String ... args) {
        String identifier = String.join((CharSequence)".", name, String.join((CharSequence)".", args));
        return this.get(identifier);
    }

    private String getBaseCommand(String identifier) {
        int index = identifier.indexOf(".");
        return index == -1 ? identifier : identifier.substring(0, index);
    }

    static {
        try {
            Server server = Bukkit.getServer();
            Field commandMapField = server.getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            commandMap = (CommandMap)commandMapField.get((Object)server);
        }
        catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
        try {
            pluginCommandConstructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
            pluginCommandConstructor.setAccessible(true);
        }
        catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
}

