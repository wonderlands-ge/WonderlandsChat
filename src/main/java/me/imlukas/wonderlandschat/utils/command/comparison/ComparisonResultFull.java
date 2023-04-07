package me.imlukas.wonderlandschat.utils.command.comparison;


import me.imlukas.wonderlandschat.utils.command.SimpleCommand;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ComparisonResultFull {

    private final Map<String, SimpleCommand> commands;
    private final LinkedList<Integer> wildCards;

    public ComparisonResultFull(Map<String, SimpleCommand> commands) {
        this.commands = commands;
        this.wildCards = new LinkedList<>();
    }

    public SimpleCommand match(String input) {
        String[] inputs = input.split("\\.");

        //in case input is empty
        if (inputs.length == 0) {
            return null;
        }

        //case alias.arg
        //first take out the alias
        for (SimpleCommand command : commands.values()) {
            if (inputs.length == 1) {
                if (command.getIdentifier().equalsIgnoreCase(input) || searchAliases(input,
                        command)) {
                    return command;
                }
            }
            //case for alias.command
            //if it's an alias of another command
            else {
                if (findAliasingCommand(inputs[0], command)) {
                    if (searchArgs(inputs, command)) {
                        return command;
                    }
                }

                //look for identifiers
                if (searchIds(inputs, command)) {

                    if (searchArgs(inputs, command)) {
                        return command;

                    }
                }
            }
        }
        return null;
    }

    private boolean searchArgs(String[] inputs, SimpleCommand command) {
        String[] args = command.getIdentifier().split("\\.");

        if (inputs.length > args.length) {
            return false;
        }

        for (int i = 1; i < inputs.length; i++) {
            if (args[i].equals("*")) {
                wildCards.add(i);
                continue;
            }
            if (!args[i].equals(inputs[i])) {
                return false;
            }
        }
        return true;
    }

    private boolean searchAliases(String input, SimpleCommand command) {
        String[] aliases = command.getAliases();
        for (String alias : aliases) {
            if (alias.equals(input)) {
                return true;
            }
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

        SimpleCommand it = commands.get(id);

        if (it == null) {
            return false;
        }

        if (it.getIdentifier().equals(id)) {
            for (String alias : it.getAliases()) {
                if (alias.equals(input)) {
                    return true;
                }
            }
        }
        return false;

    }

    public List<Integer> getWildCards() {
        return Collections.unmodifiableList(wildCards);
    }


}