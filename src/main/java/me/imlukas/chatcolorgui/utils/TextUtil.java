package me.imlukas.chatcolorgui.utils;

import me.imlukas.chatcolorgui.ChatColorPlugin;
import me.imlukas.chatcolorgui.utils.collection.ListUtils;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class TextUtil {

    private final FileConfiguration config;
    private static Pattern pattern;
    private static final List<String> COLORS = Arrays.asList("&0", "&1", "&2", "&3", "&4", "&5", "&6", "&7", "&9", "&a", "&b", "&c", "&d", "&e", "&f");


    public TextUtil(ChatColorPlugin main) {
        this.config = main.getConfig();
    }

    public static String getRandomColor() {
        return ListUtils.getRandom(COLORS);
    }

    public static String capitalize(String text) {
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }

    public static String capitalizeAll(String text) {
        text = text.replaceAll("_", " ");
        String[] words = text.split(" ");
        StringBuilder builder = new StringBuilder();
        for (String word : words) {
            builder.append(capitalize(word)).append(" ");
        }
        return builder.toString().trim();
    }

    public static String capitalizeAllAndColor(String text) {
        return color(capitalizeAll(text));
    }

    public static String color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }


    public static String colorAndCapitalize(String text) {
        return color(capitalize(text));
    }

    public String getColorConfig(String key) {
        return color(config.getString(key));
    }
}
