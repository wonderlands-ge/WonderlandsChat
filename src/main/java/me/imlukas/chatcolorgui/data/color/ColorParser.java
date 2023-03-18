package me.imlukas.chatcolorgui.data.color;

import me.imlukas.chatcolorgui.utils.item.ItemBuilder;
import me.imlukas.chatcolorgui.utils.storage.YMLBase;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Array;
import java.util.*;

public class ColorParser extends YMLBase {

    private final Map<String, ItemStack> colors = new HashMap<>();
    private final FileConfiguration config;

    public ColorParser(JavaPlugin plugin){
        super(plugin, "colors.yml");
        this.config = getConfiguration();
    }

    public void parse() {
        for (String key : config.getKeys(false)) {
            ItemStack item = ItemBuilder.fromSection(config.getConfigurationSection(key));
            colors.put(key, item);
        }
    }

    public ItemStack getItem(String color) {
        return colors.get(color);
    }

    public Set<String> getColors() {
       return colors.keySet();
    }

    public List<ItemStack> getItems() {
       return (List<ItemStack>) colors.values();
    }

    public Map<String, ItemStack> getColorsMap() {
       return colors;
    }

    public String getDisplayName(String color) {
        return config.getString(color + ".name");
    }

    public String getDisplayColor(String color) {
        return config.getString(color + ".color");
    }

    public List<String> getNoPermLore(String color) {
        return config.getStringList(color + ".lore-no-perm");
    }

    public List<String> getSelectedLore (String color) {
        return config.getStringList(color + ".selected");
    }

    public int getSlot(String color) {
        return config.getInt(color + ".slot");
    }

}
