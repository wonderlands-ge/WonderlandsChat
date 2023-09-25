/*
 * Decompiled with CFR 0.150.
 */
package me.imlukas.wonderlandschat.utils.command.comparison;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import me.imlukas.wonderlandschat.utils.command.SimpleCommand;

public class ComparisonResultFull {
    private final Map<String, SimpleCommand> commands;
    private final LinkedList<Integer> wildCards;

    public ComparisonResultFull(Map<String, SimpleCommand> commands) {
        this.commands = commands;
        this.wildCards = new LinkedList();
    }

    public SimpleCommand match(String input) {
        String[] inputs = input.split("\\.");
        if (inputs.length == 0) {
            return null;
        }
        for (SimpleCommand command : this.commands.values()) {
            if (inputs.length == 1) {
                if (!command.getIdentifier().equalsIgnoreCase(input) && !this.searchAliases(input, command)) continue;
                return command;
            }
            if (this.findAliasingCommand(inputs[0], command) && this.searchArgs(inputs, command)) {
                return command;
            }
            if (!this.searchIds(inputs, command) || !this.searchArgs(inputs, command)) continue;
            return command;
        }
        return null;
    }

    private boolean searchArgs(String[] inputs, SimpleCommand command) {
        String[] args = command.getIdentifier().split("\\.");
        if (inputs.length > args.length) {
            return false;
        }
        for (int i = 1; i < inputs.length; ++i) {
            if (args[i].equals("*")) {
                this.wildCards.add(i);
                continue;
            }
            if (args[i].equals(inputs[i])) continue;
            return false;
        }
        return true;
    }

    private boolean searchAliases(String input, SimpleCommand command) {
        String[] aliases;
        for (String alias : aliases = command.getAliases()) {
            if (!alias.equals(input)) continue;
            return true;
        }
        return false;
    }

    private boolean searchIds(String[] inputs, SimpleCommand command) {
        String[] ids = command.getIdentifier().split("\\.");
        String id = ids[0];
        return id.equals(inputs[0]);
    }

    private boolean findAliasingCommand(String input, SimpleCommand command) {
        String id = command.getIdentifier().split("\\.")[0];
        SimpleCommand it = this.commands.get(id);
        if (it == null) {
            return false;
        }
        if (it.getIdentifier().equals(id)) {
            for (String alias : it.getAliases()) {
                if (!alias.equals(input)) continue;
                return true;
            }
        }
        return false;
    }

    public List<Integer> getWildCards() {
        return Collections.unmodifiableList(this.wildCards);
    }
}

