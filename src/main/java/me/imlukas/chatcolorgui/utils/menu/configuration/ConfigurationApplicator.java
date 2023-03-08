package me.imlukas.chatcolorgui.utils.menu.configuration;

import me.imlukas.chatcolorgui.utils.item.ItemBuilder;
import me.imlukas.chatcolorgui.utils.menu.button.Button;
import me.imlukas.chatcolorgui.utils.menu.button.DecorationItem;
import me.imlukas.chatcolorgui.utils.menu.element.MenuElement;
import me.imlukas.chatcolorgui.utils.menu.layer.BaseLayer;
import me.imlukas.chatcolorgui.utils.menu.mask.PatternMask;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class ConfigurationApplicator {

    private final Map<String, ItemStack> items = new ConcurrentHashMap<>();
    private final PatternMask mask;

    private final FileConfiguration config;

    public ConfigurationApplicator(FileConfiguration config) {
        this.config = config;

        String menuTitle = config.getString("title");

        ConfigurationSection items = config.getConfigurationSection("items");

        for (String key : items.getKeys(false)) {
            this.items.put(key, ItemBuilder.fromSection(items.getConfigurationSection(key)));
        }

        List<String> maskLayout = config.getStringList("layout");
        mask = PatternMask.of(maskLayout);
    }

    public ItemStack getItem(String key) {
        return items.get(key);
    }

    public MenuElement getDecorationItem(String key) {
        return new DecorationItem(getItem(key));
    }

    public Button registerButton(BaseLayer layer, String key) {
        ItemStack item = getItem(key);

        if (item == null)
            throw new IllegalArgumentException("No item with key " + key + " found! (items: " + items.keySet() + ")");

        Button button = new Button(item);
        layer.applyRawSelection(mask.selection(key), button);

        return button;
    }

    public Button registerButton(BaseLayer layer, String key, Consumer<InventoryClickEvent> defaultHandler) {
        Button button = new Button(getItem(key), defaultHandler);
        layer.applyRawSelection(mask.selection(key), button);

        return button;
    }

    public Button registerButton(BaseLayer layer, String key, Runnable clickHandler) {
        Button button = new Button(getItem(key), (event) -> clickHandler.run());
        layer.applyRawSelection(mask.selection(key), button);

        return button;
    }

    public PatternMask getMask() {
        return mask;
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public void applyConfiguration(BaseLayer layer) {
        for (Map.Entry<String, ItemStack> entry : items.entrySet()) {
            String itemId = entry.getKey();
            ItemStack item = entry.getValue();

            MenuElement element = new DecorationItem(item);

            layer.applySelection(mask.selection(itemId), element);
        }
    }
}
