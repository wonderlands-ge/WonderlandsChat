/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 */
package me.imlukas.wonderlandschat.utils.menu.registry.meta;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import me.imlukas.wonderlandschat.utils.menu.base.BaseMenu;
import me.imlukas.wonderlandschat.utils.menu.registry.meta.HiddenMenuData;
import org.bukkit.entity.Player;

public class HiddenMenuTracker {
    private final Map<UUID, HiddenMenuData> hiddenMenus = new ConcurrentHashMap<UUID, HiddenMenuData>();

    public void addHiddenMenu(UUID uuid, HiddenMenuData data) {
        this.hiddenMenus.put(uuid, data);
    }

    public HiddenMenuData getHiddenMenu(UUID uuid) {
        return this.hiddenMenus.get(uuid);
    }

    public void removeHiddenMenu(UUID uuid) {
        this.hiddenMenus.remove(uuid);
    }

    public boolean hasHiddenMenu(UUID uuid) {
        return this.hiddenMenus.containsKey(uuid);
    }

    public void clear() {
        this.hiddenMenus.clear();
    }

    public void holdForInput(BaseMenu menu, Consumer<String> action, boolean reopenMenu) {
        Player player = menu.getPlayer();
        HiddenMenuData data = new HiddenMenuData(menu);
        data.getMeta().put("input-task", action);
        if (reopenMenu) {
            data.addDisplayTask(menu::open);
        }
        this.addHiddenMenu(player.getUniqueId(), data);
        player.closeInventory();
    }

    public void holdForInput(BaseMenu menu, Consumer<String> action) {
        this.holdForInput(menu, action, true);
    }
}

