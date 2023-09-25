/*
 * Decompiled with CFR 0.150.
 */
package me.imlukas.wonderlandschat.utils.command.comparison;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.Collectors;
import me.imlukas.wonderlandschat.utils.command.SimpleCommand;
import me.imlukas.wonderlandschat.utils.command.comparison.SmallestStringComparator;

public class ComparisonResult {
    private final Map<String, SimpleCommand> commands;
    private final LinkedList<Integer> wildCards;

    public ComparisonResult(Map<String, SimpleCommand> commands) {
        this.commands = commands;
        this.wildCards = new LinkedList();
    }

    String[] match(String input) {
        boolean isNextCommand;
        TreeSet<String> result = new TreeSet<String>(new SmallestStringComparator());
        String[] inputs = input.split("\\.");
        boolean bl = isNextCommand = input.charAt(input.length() - 1) == '.';
        if (inputs.length == 0) {
            return new String[0];
        }
        for (SimpleCommand command : this.commands.values()) {
            if (this.findAliasingCommand(inputs[0], command) && this.searchArgs(inputs, command, isNextCommand)) {
                result.add(command.getIdentifier());
                continue;
            }
            if (!this.searchIds(inputs, command) || !this.searchArgs(inputs, command, isNextCommand)) continue;
            result.add(command.getIdentifier());
        }
        return result.toArray(new String[0]);
    }

    private boolean searchArgs(String[] inputs, SimpleCommand command, boolean isNextCommand) {
        int next;
        String[] args = command.getIdentifier().split("\\.");
        int n = next = isNextCommand ? 1 : 0;
        if (inputs.length > args.length) {
            return false;
        }
        for (int i = 1; i < inputs.length - 1 + next; ++i) {
            if (args[i].equals("*")) {
                this.wildCards.add(i);
                continue;
            }
            if (args[i].equals(inputs[i])) continue;
            return false;
        }
        if (!isNextCommand) {
            return args[inputs.length - 1].startsWith(inputs[inputs.length - 1]) || args[inputs.length - 1].equals("*");
        }
        return true;
    }

    private boolean searchIds(String[] inputs, SimpleCommand command) {
        String[] ids = command.getIdentifier().split("\\.");
        String id = ids[0];
        if (inputs.length == 1 && ids.length == 1) {
            return id.startsWith(inputs[0]);
        }
        return id.equals(inputs[0]);
    }

    public List<Integer> getWildCards() {
        return Collections.unmodifiableList(this.wildCards);
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

    public List<String> tabComplete(String identifier) {
        String[] results = this.match(identifier);
        String[] inputs = identifier.split("\\.");
        boolean isNextCommand = identifier.charAt(identifier.length() - 1) == '.';
        int nextCommand = isNextCommand ? 1 : 0;
        ArrayList<String> toReturn = new ArrayList<String>();
        for (String str : results) {
            if (this.commands.get(str) == null) {
                System.out.println("Command:" + str + " not found");
                continue;
            }
            if (str.equals(identifier)) {
                toReturn.add("");
                continue;
            }
            String[] inputs2 = str.split("\\.");
            if (isNextCommand && inputs.length == inputs2.length) continue;
            if (!isNextCommand && this.commands.containsKey(inputs2[inputs.length - 1]) && this.findAliasingCommand(inputs[inputs.length - 1], this.commands.get(inputs2[inputs.length - 1]))) {
                toReturn.add("");
                continue;
            }
            if (inputs2[inputs.length - 1 + nextCommand].equals("*")) {
                int wildcardN = 0;
                for (int i = 1; i < inputs.length + nextCommand; ++i) {
                    if (!inputs2[i].equals("*")) continue;
                    ++wildcardN;
                }
                if (!this.commands.get(str).tabCompleteWildcards().containsKey(wildcardN)) continue;
                List<String> wildcards = this.commands.get(str).tabCompleteWildcards().get(wildcardN);
                if (!isNextCommand) {
                    wildcards = wildcards.stream().filter(card -> card.startsWith(inputs[inputs.length - 1])).collect(Collectors.toList());
                }
                toReturn.addAll(wildcards);
                continue;
            }
            if (isNextCommand) {
                toReturn.add(inputs2[inputs.length]);
                continue;
            }
            if (inputs2[inputs.length - 1].equals(inputs[inputs.length - 1])) continue;
            toReturn.add(inputs2[inputs.length - 1]);
        }
        return toReturn.stream().distinct().collect(Collectors.toList());
    }
}

