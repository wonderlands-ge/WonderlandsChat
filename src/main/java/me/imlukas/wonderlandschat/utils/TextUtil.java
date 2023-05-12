package me.imlukas.wonderlandschat.utils;

import me.clip.placeholderapi.PlaceholderAPI;
import me.imlukas.wonderlandschat.WonderlandsChatPlugin;
import me.imlukas.wonderlandschat.utils.collection.ListUtils;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class TextUtil {

    private final FileConfiguration config;
    private static final List<String> COLORS = Arrays.asList("&3", "&e", "&a", "&d", "&7", "&6", "&5", "&9", "&c");


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

    public static String colorAndReplace(Player player, String text) {
        return ChatColor.translateAlternateColorCodes('&', replacePlaceholders(player, text));
    }

    public static String replacePlaceholders(Player player, String text) {
        String papiParsed = PlaceholderAPI.setPlaceholders(player, text);

        if (papiParsed.isEmpty()) {
            return text;
        }

        return papiParsed;
    }

    public static List<String> color(List<String> list) {
        List<String> colored = new ArrayList<>();
        for (String s : list) {
            colored.add(color(s));
        }
        return colored;
    }


    public static String colorAndCapitalize(String text) {
        return color(capitalize(text));
    }

    public String getColorConfig(String key) {
        return color(config.getString(key));
    }
}
