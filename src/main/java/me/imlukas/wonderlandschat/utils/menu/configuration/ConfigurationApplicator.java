/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.configuration.ConfigurationSection
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.event.inventory.InventoryClickEvent
 *  org.bukkit.inventory.ItemStack
 */
package me.imlukas.wonderlandschat.utils.menu.configuration;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import me.imlukas.wonderlandschat.utils.item.ItemBuilder;
import me.imlukas.wonderlandschat.utils.menu.button.Button;
import me.imlukas.wonderlandschat.utils.menu.button.DecorationItem;
import me.imlukas.wonderlandschat.utils.menu.element.MenuElement;
import me.imlukas.wonderlandschat.utils.menu.layer.BaseLayer;
import me.imlukas.wonderlandschat.utils.menu.mask.PatternMask;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class ConfigurationApplicator {
    private final Map<String, ItemStack> items = new ConcurrentHashMap<String, ItemStack>();
    private final PatternMask mask;
    private final FileConfiguration config;

    public ConfigurationApplicator(FileConfiguration config) {
        this.config = config;
        String menuTitle = config.getString("title");
        ConfigurationSection items = config.getConfigurationSection("items");
        for (String key : items.getKeys(false)) {
            this.items.put(key, ItemBuilder.fromSection(items.getConfigurationSection(key)));
        }
        List maskLayout = config.getStringList("layout");
        this.mask = PatternMask.of(maskLayout);
    }

    public ItemStack getItem(String key) {
        return this.items.get(key);
    }

    public MenuElement getDecorationItem(String key) {
        return new DecorationItem(this.getItem(key));
    }

    public Button registerButton(BaseLayer layer, String key) {
        ItemStack item = this.getItem(key);
        if (item == null) {
            throw new IllegalArgumentException("No item with key " + key + " found! (items: " + this.items.keySet() + ")");
        }
        Button button = new Button(item);
        layer.applyRawSelection(this.mask.selection(key), button);
        return button;
    }

    public Button registerButton(BaseLayer layer, String key, Consumer<InventoryClickEvent> defaultHandler) {
        Button button = new Button(this.getItem(key), defaultHandler);
        layer.applyRawSelection(this.mask.selection(key), button);
        return button;
    }

    public Button registerButton(BaseLayer layer, String key, Runnable clickHandler) {
        Button button = new Button(this.getItem(key), event -> clickHandler.run());
        layer.applyRawSelection(this.mask.selection(key), button);
        return button;
    }

    public PatternMask getMask() {
        return this.mask;
    }

    public FileConfiguration getConfig() {
        return this.config;
    }

    public void applyConfiguration(BaseLayer layer) {
        for (Map.Entry<String, ItemStack> entry : this.items.entrySet()) {
            String itemId = entry.getKey();
            ItemStack item = entry.getValue();
            DecorationItem element = new DecorationItem(item);
            layer.applySelection(this.mask.selection(itemId), element);
        }
    }
}

