package me.imlukas.wonderlandschat.data.color;

import com.google.common.collect.ImmutableMap;
import me.imlukas.wonderlandschat.utils.item.ItemBuilder;
import me.imlukas.wonderlandschat.utils.storage.YMLBase;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class ColorParser extends YMLBase {
    public static final Map<String, String> FORMATS = ImmutableMap.<String, String>builder()
            .put("Strikethrough", "&m")
            .put("Bold", "&l")
            .put("No format", "")
            .put("Underline", "&n")
            .put("Italic", "&o")
            .build();
    private final Map<String, ItemStack> colors = new HashMap<>();
    private final FileConfiguration config;

    public ColorParser(JavaPlugin plugin){
        super(plugin, "colors.yml");
        this.config = getConfiguration();
        parse();
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

    public String getFormatByCode(String code) {
        for (Map.Entry<String, String> entry : FORMATS.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            if (code.equals(value)) {
                return key;
            }
        }
        return null;
    }
    public String getColorNameByCode(String code) {
        for (String key : config.getKeys(false)) {
            if (config.getString(key + ".color").equals(code)) {
                return key;
            }
        }
        return null;
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
