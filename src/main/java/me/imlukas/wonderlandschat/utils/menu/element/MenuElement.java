/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 *  org.bukkit.event.inventory.InventoryClickEvent
 *  org.bukkit.inventory.ItemStack
 */
package me.imlukas.wonderlandschat.utils.menu.element;

import java.util.Collection;
import java.util.Collections;
import me.imlukas.wonderlandschat.utils.text.Placeholder;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public interface MenuElement {
    public ItemStack getDisplayItem();

    public void handle(InventoryClickEvent var1);

    public MenuElement copy();

    default public Collection<Placeholder<Player>> getItemPlaceholders() {
        return Collections.emptyList();
    }

    default public MenuElement setItemPlaceholders(Collection<Placeholder<Player>> placeholders) {
        return this;
    }
}

