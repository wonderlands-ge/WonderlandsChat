/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang.StringEscapeUtils
 *  org.bukkit.ChatColor
 *  org.bukkit.command.CommandSender
 */
package me.imlukas.wonderlandschat.utils.storage;

import java.io.File;
import java.util.Collection;
import java.util.function.Function;
import java.util.regex.Pattern;
import me.imlukas.wonderlandschat.WonderlandsChatPlugin;
import me.imlukas.wonderlandschat.utils.storage.YMLBase;
import me.imlukas.wonderlandschat.utils.text.Placeholder;
import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class MessagesFile
extends YMLBase {
    private final Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
    private final String prefix = StringEscapeUtils.unescapeJava((String)this.getConfiguration().getString("messages.prefix"));
    private boolean usePrefixConfig = this.getConfiguration().getBoolean("messages.use-prefix");
    private String msg;

    public MessagesFile(WonderlandsChatPlugin plugin) {
        super(plugin, new File(plugin.getDataFolder(), "messages.yml"), true);
    }

    public String setColor(String message) {
        return ChatColor.translateAlternateColorCodes((char)'&', (String)message);
    }

    private String setMessage(String name) {
        return this.setMessage(name, s -> s);
    }

    private String setMessage(String name, Function<String, String> action) {
        if (!this.getConfiguration().contains("messages." + name)) {
            return "";
        }
        this.msg = this.getMessage(name);
        this.msg = this.usePrefixConfig ? this.prefix + " " + this.getMessage(name) : this.getMessage(name).replace("%prefix%", this.prefix);
        this.msg = action.apply(this.msg);
        return this.setColor(this.msg);
    }

    public void sendStringMessage(CommandSender player, String msg) {
        player.sendMessage(this.setColor(msg));
    }

    public void sendMessage(CommandSender sender, String name) {
        this.sendMessage(sender, name, (String s) -> s);
    }

    @SafeVarargs
    public final <T extends CommandSender> void sendMessage(T sender, String name, Placeholder<T> ... placeholders) {
        this.sendMessage(sender, name, (String text) -> {
            for (Placeholder placeholder : placeholders) {
                text = placeholder.replace((String)text, sender);
            }
            return text;
        });
    }

    public final <T extends CommandSender> void sendMessage(T sender, String name, Collection<Placeholder<T>> placeholders) {
        this.sendMessage(sender, name, (String text) -> {
            for (Placeholder placeholder : placeholders) {
                text = placeholder.replace((String)text, sender);
            }
            return text;
        });
    }

    public void sendMessage(CommandSender sender, String name, Function<String, String> action) {
        if (this.getConfiguration().isList("messages." + name)) {
            for (String str : this.getConfiguration().getStringList("messages." + name)) {
                this.msg = StringEscapeUtils.unescapeJava((String)str.replace("%prefix%", this.prefix));
                this.msg = action.apply(this.msg);
                sender.sendMessage(this.setColor(this.msg));
            }
            return;
        }
        this.msg = this.setMessage(name, action);
        sender.sendMessage(this.msg);
    }

    public String getMessage(String name) {
        return this.getConfiguration().getString("messages." + name);
    }

    public boolean togglePrefix() {
        boolean isEnabled = this.usePrefixConfig;
        if (isEnabled) {
            this.getConfiguration().set("messages.use-prefix", (Object)false);
        } else {
            this.getConfiguration().set("messages.use-prefix", (Object)true);
        }
        this.save();
        this.usePrefixConfig = !isEnabled;
        return !isEnabled;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public boolean isUsePrefixConfig() {
        return this.usePrefixConfig;
    }
}

