/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.command.CommandSender
 */
package me.imlukas.wonderlandschat.utils.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.command.CommandSender;

public interface SimpleCommand {
    public String getIdentifier();

    default public String[] getAliases() {
        return new String[0];
    }

    default public String getPermission() {
        return "";
    }

    default public boolean canExecute(CommandSender sender) {
        return true;
    }

    default public boolean hasPermission() {
        return !this.getPermission().isEmpty();
    }

    default public Map<Integer, List<String>> tabCompleteWildcards() {
        return new HashMap<Integer, List<String>>(0);
    }

    public void execute(CommandSender var1, String ... var2);

    default public List<Integer> getWildcards() {
        String identifier = this.getIdentifier();
        ArrayList<Integer> wildcards = new ArrayList<Integer>();
        String[] split = identifier.split("\\.");
        for (int index = 0; index < split.length; ++index) {
            if (!split[index].equals("*")) continue;
            wildcards.add(index);
        }
        return wildcards;
    }
}

