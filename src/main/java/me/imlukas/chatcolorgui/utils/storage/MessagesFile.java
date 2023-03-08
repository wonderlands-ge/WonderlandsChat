package me.imlukas.chatcolorgui.utils.storage;

import lombok.Getter;
import me.imlukas.chatcolorgui.ChatColorPlugin;
import me.imlukas.chatcolorgui.utils.text.Placeholder;
import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.util.Collection;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessagesFile extends YMLBase {

    private final Pattern pattern;
    @Getter
    private final String prefix;
    @Getter
    private boolean usePrefixConfig;
    private String msg;

    public MessagesFile(ChatColorPlugin plugin) {
        super(plugin, new File(plugin.getDataFolder(), "messages.yml"), true);
        pattern = Pattern.compile("#[a-fA-F0-9]{6}");
        prefix = StringEscapeUtils.unescapeJava(getConfiguration().getString("messages.prefix"));
        usePrefixConfig = getConfiguration().getBoolean("messages.use-prefix");

    }

    public String setColor(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    private String setMessage(String name) {
        return setMessage(name, (s) -> s);
    }

    private String setMessage(String name, Function<String, String> action) {
        if (!getConfiguration().contains("messages." + name))
            return "";
        msg = getMessage(name);
        if (usePrefixConfig) {
            msg = prefix + " " + getMessage(name);
        } else {
            msg = getMessage(name).replace("%prefix%", prefix);
        }
        msg = action.apply(msg);
        return setColor(msg);
    }

    public void sendStringMessage(CommandSender player, String msg) {
        player.sendMessage(setColor(msg));
    }

    public void sendMessage(CommandSender sender, String name) {
        sendMessage(sender, name, (s) -> s);
    }


    @SafeVarargs
    public final <T extends CommandSender> void sendMessage(T sender, String name, Placeholder<T>... placeholders) {
        sendMessage(sender, name, (text) -> {
            for (Placeholder<T> placeholder : placeholders) {
                text = placeholder.replace(text, sender);
            }

            return text;
        });
    }

    public final <T extends CommandSender> void sendMessage(T sender, String name, Collection<Placeholder<T>> placeholders) {
        sendMessage(sender, name, (text) -> {
            for (Placeholder<T> placeholder : placeholders) {
                text = placeholder.replace(text, sender);
            }

            return text;
        });
    }


    public void sendMessage(CommandSender sender, String name, Function<String, String> action) {
        if (getConfiguration().isList("messages." + name)) {
            for (String str : getConfiguration().getStringList("messages." + name)) {
                msg = StringEscapeUtils.unescapeJava(str.replace("%prefix%", prefix));
                msg = action.apply(msg);
                sender.sendMessage(setColor(msg));
            }
            return;
        }

        msg = setMessage(name, action);
        sender.sendMessage(msg);
    }

    public String getMessage(String name) {
        return getConfiguration().getString("messages." + name);
    }


    public boolean togglePrefix() {
        boolean isEnabled = usePrefixConfig;
        if (isEnabled) {
            getConfiguration().set("messages.use-prefix", false);
        } else {
            getConfiguration().set("messages.use-prefix", true);
        }
        save();
        usePrefixConfig = !isEnabled;
        return !isEnabled;
    }
}

