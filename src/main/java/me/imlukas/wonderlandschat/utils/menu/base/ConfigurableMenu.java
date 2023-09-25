/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.inventory.ItemStack
 */
package me.imlukas.wonderlandschat.utils.menu.base;

import java.util.UUID;
import me.imlukas.wonderlandschat.utils.menu.base.BaseMenu;
import me.imlukas.wonderlandschat.utils.menu.configuration.ConfigurationApplicator;
import me.imlukas.wonderlandschat.utils.menu.element.MenuElement;
import me.imlukas.wonderlandschat.utils.menu.mask.PatternMask;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

public class ConfigurableMenu
extends BaseMenu {
    private final ConfigurationApplicator applicator;

    public ConfigurableMenu(UUID playerId, String title, int rows, ConfigurationApplicator applicator) {
        super(playerId, title, rows);
        this.applicator = applicator;
    }

    public ConfigurationApplicator getApplicator() {
        return this.applicator;
    }

    public ItemStack getItem(String key) {
        return this.getApplicator().getItem(key);
    }

    public MenuElement getDecorationItem(String key) {
        return this.getApplicator().getDecorationItem(key);
    }

    public PatternMask getMask() {
        return this.getApplicator().getMask();
    }

    public FileConfiguration getConfig() {
        return this.getApplicator().getConfig();
    }
}

