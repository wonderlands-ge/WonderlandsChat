package me.imlukas.chatcolorgui.utils.menu.base;

import me.imlukas.chatcolorgui.utils.menu.configuration.ConfigurationApplicator;
import me.imlukas.chatcolorgui.utils.menu.element.MenuElement;
import me.imlukas.chatcolorgui.utils.menu.mask.PatternMask;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class ConfigurableMenu extends BaseMenu {

    private final ConfigurationApplicator applicator;

    public ConfigurableMenu(UUID playerId, String title,
                            int rows, ConfigurationApplicator applicator) {
        super(playerId, title, rows);
        this.applicator = applicator;
    }
    public ConfigurationApplicator getApplicator() {
        return applicator;
    }

    public ItemStack getItem(String key) {
        return getApplicator().getItem(key);
    }

    public MenuElement getDecorationItem(String key) {
        return getApplicator().getDecorationItem(key);
    }

    public PatternMask getMask() {
        return getApplicator().getMask();
    }

    public FileConfiguration getConfig() {
        return getApplicator().getConfig();
    }


}
