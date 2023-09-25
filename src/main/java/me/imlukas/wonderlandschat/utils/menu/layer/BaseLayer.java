/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 */
package me.imlukas.wonderlandschat.utils.menu.layer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import me.imlukas.wonderlandschat.utils.menu.base.BaseMenu;
import me.imlukas.wonderlandschat.utils.menu.element.MenuElement;
import me.imlukas.wonderlandschat.utils.menu.element.Renderable;
import me.imlukas.wonderlandschat.utils.menu.selection.Selection;
import me.imlukas.wonderlandschat.utils.text.Placeholder;
import org.bukkit.entity.Player;

public class BaseLayer
extends Renderable {
    private final Map<Integer, MenuElement> slots = new HashMap<Integer, MenuElement>();

    public BaseLayer(BaseMenu menu) {
        super(menu);
    }

    @Override
    public void forceUpdate() {
        for (Map.Entry<Integer, MenuElement> entry : this.slots.entrySet()) {
            this.menu.setElement(entry.getKey(), entry.getValue());
        }
    }

    public void applySelection(Selection selection, MenuElement element) {
        for (int slot : selection.getSlots()) {
            this.slots.put(slot, element.copy());
        }
    }

    public void applyRawSelection(Selection selection, MenuElement element) {
        for (int slot : selection.getSlots()) {
            this.slots.put(slot, element);
        }
    }

    public void setItemPlaceholders(Collection<Placeholder<Player>> placeholders) {
        for (MenuElement element : this.slots.values()) {
            element.setItemPlaceholders(placeholders);
        }
    }

    public void clearElements() {
        this.slots.clear();
    }
}

