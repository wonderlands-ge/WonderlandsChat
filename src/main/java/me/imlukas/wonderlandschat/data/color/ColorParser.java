/*
 * Decompiled with CFR 0.150.
 *
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.plugin.java.JavaPlugin
 */
package me.imlukas.wonderlandschat.data.color;

import com.google.common.collect.ImmutableMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import me.imlukas.wonderlandschat.utils.item.ItemBuilder;
import me.imlukas.wonderlandschat.utils.storage.YMLBase;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class ColorParser
        extends YMLBase {
    public static final Map<String, String> FORMATS = ImmutableMap.of(
            "Strikethrough", "&m",
            "Bold", "&l"
            , "No format", ""
            , "Underline", "&n",
            "Italic", "&o");
    private final Map<String, ItemStack> colors = new HashMap<String, ItemStack>();
    private final FileConfiguration config = this.getConfiguration();

    public ColorParser(JavaPlugin plugin) {
        super(plugin, "colors.yml");
        this.parse();
    }

    public void parse() {
        for (String key : this.config.getKeys(false)) {
            ItemStack item = ItemBuilder.fromSection(this.config.getConfigurationSection(key));
            this.colors.put(key, item);
        }
    }

    public ItemStack getItem(String color) {
        return this.colors.get(color);
    }

    public Set<String> getColors() {
        return this.colors.keySet();
    }

    public List<ItemStack> getItems() {
        return (List) this.colors.values();
    }

    public Map<String, ItemStack> getColorsMap() {
        return this.colors;
    }

    public String getFormatByCode(String code) {
        for (Map.Entry<String, String> entry : FORMATS.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (!code.equals(value)) continue;
            return key;
        }
        return null;
    }

    public String getColorNameByCode(String code) {
        for (String key : this.config.getKeys(false)) {
            if (!this.config.getString(key + ".color").equals(code)) continue;
            return key;
        }
        return null;
    }

    public String getDisplayName(String color) {
        return this.config.getString(color + ".name");
    }

    public String getDisplayColor(String color) {
        return this.config.getString(color + ".color");
    }

    public List<String> getNoPermLore(String color) {
        return this.config.getStringList(color + ".lore-no-perm");
    }

    public List<String> getSelectedLore(String color) {
        return this.config.getStringList(color + ".selected");
    }

    public int getSlot(String color) {
        return this.config.getInt(color + ".slot");
    }
}

