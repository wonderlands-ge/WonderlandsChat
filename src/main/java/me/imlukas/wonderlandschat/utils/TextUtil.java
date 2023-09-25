/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  me.clip.placeholderapi.PlaceholderAPI
 *  org.bukkit.ChatColor
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.entity.Player
 */
package me.imlukas.wonderlandschat.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import me.clip.placeholderapi.PlaceholderAPI;
import me.imlukas.wonderlandschat.WonderlandsChatPlugin;
import me.imlukas.wonderlandschat.utils.collection.ListUtils;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class TextUtil {
    private final FileConfiguration config;
    private static Pattern pattern;
    private static final List<String> COLORS;

    public TextUtil(WonderlandsChatPlugin main) {
        this.config = main.getConfig();
    }

    public static String getRandomColor() {
        return ListUtils.getRandom(COLORS);
    }

    public static String capitalize(String text) {
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }

    public static String uncapitalize(String text) {
        return text.substring(0, 1).toLowerCase() + text.substring(1);
    }

    public static String capitalizeAll(String text) {
        text = text.replaceAll("_", " ");
        String[] words = text.split(" ");
        StringBuilder builder = new StringBuilder();
        for (String word : words) {
            builder.append(TextUtil.capitalize(word)).append(" ");
        }
        return builder.toString().trim();
    }

    public static String capitalizeAllAndColor(String text) {
        return TextUtil.color(TextUtil.capitalizeAll(text));
    }

    public static String color(String text) {
        return ChatColor.translateAlternateColorCodes((char)'&', (String)text);
    }

    public static String colorAndReplace(Player player, String text) {
        return ChatColor.translateAlternateColorCodes((char)'&', (String)TextUtil.replacePlaceholders(player, text));
    }

    public static String replacePlaceholders(Player player, String text) {
        String papiParsed = PlaceholderAPI.setPlaceholders((Player)player, (String)text);
        if (papiParsed.isEmpty()) {
            return text;
        }
        return papiParsed;
    }

    public static List<String> color(List<String> list) {
        ArrayList<String> colored = new ArrayList<String>();
        for (String s : list) {
            colored.add(TextUtil.color(s));
        }
        return colored;
    }

    public static String colorAndCapitalize(String text) {
        return TextUtil.color(TextUtil.capitalize(text));
    }

    public String getColorConfig(String key) {
        return TextUtil.color(this.config.getString(key));
    }

    static {
        COLORS = Arrays.asList("&0", "&1", "&2", "&3", "&4", "&5", "&6", "&7", "&9", "&a", "&b", "&c", "&d", "&e", "&f");
    }
}

