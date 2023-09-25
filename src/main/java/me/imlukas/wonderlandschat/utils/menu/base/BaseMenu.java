/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.Player
 *  org.bukkit.event.inventory.InventoryClickEvent
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.InventoryHolder
 *  org.bukkit.inventory.ItemStack
 */
package me.imlukas.wonderlandschat.utils.menu.base;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import me.imlukas.wonderlandschat.utils.concurrent.MainThreadExecutor;
import me.imlukas.wonderlandschat.utils.item.ItemUtil;
import me.imlukas.wonderlandschat.utils.menu.element.MenuElement;
import me.imlukas.wonderlandschat.utils.menu.element.Renderable;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class BaseMenu
implements InventoryHolder {
    private final Inventory inventory;
    private final UUID destinationPlayerId;
    private final List<Renderable> renderables = new ArrayList<Renderable>();
    private final Map<Integer, MenuElement> elements = new HashMap<Integer, MenuElement>();
    private Runnable closeTask;
    private boolean allowRemoveItems = false;

    public BaseMenu(UUID playerId, String title, int rows) {
        this.inventory = Bukkit.createInventory((InventoryHolder)this, (int)(rows * 9), (String)title);
        this.destinationPlayerId = playerId;
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    public void clearElements() {
        this.elements.clear();
        this.forceUpdate();
    }

    public void open() {
        if (!Bukkit.isPrimaryThread()) {
            MainThreadExecutor.MAIN_THREAD_EXECUTOR.execute(this::open);
            return;
        }
        Player player = this.getPlayer();
        if (player == null) {
            return;
        }
        player.openInventory(this.inventory);
    }

    public Player getPlayer() {
        return Bukkit.getPlayer((UUID)this.destinationPlayerId);
    }

    public void addRenderable(Renderable ... renderable) {
        this.renderables.addAll(Arrays.asList(renderable));
    }

    public void forceUpdate() {
        Player player = this.getPlayer();
        if (player == null) {
            return;
        }
        for (Renderable renderable : this.renderables) {
            if (!renderable.isActive()) continue;
            renderable.forceUpdate();
        }
        for (Map.Entry entry : this.elements.entrySet()) {
            int slot = (Integer)entry.getKey();
            MenuElement element = (MenuElement)entry.getValue();
            ItemStack item = element.getDisplayItem().clone();
            ItemUtil.replacePlaceholder(item, player, element.getItemPlaceholders());
            this.inventory.setItem(slot, item);
        }
    }

    public void setElement(int slot, MenuElement element) {
        this.elements.put(slot, element);
    }

    public void setAllowRemoveItems(boolean allowRemoveItems) {
        this.allowRemoveItems = allowRemoveItems;
    }

    public void onClose(Runnable task) {
        this.closeTask = task;
    }

    public boolean isAllowRemoveItems() {
        return this.allowRemoveItems;
    }

    public void handleClick(InventoryClickEvent event) {
        int slot = event.getRawSlot();
        if (slot < 0 || slot >= this.inventory.getSize()) {
            if (event.isShiftClick()) {
                event.setCancelled(true);
            }
            return;
        }
        MenuElement element = this.elements.get(slot);
        if (element == null) {
            return;
        }
        element.handle(event);
        if (!this.allowRemoveItems) {
            event.setCancelled(true);
        }
    }

    public void handleClose() {
        if (this.closeTask != null) {
            this.closeTask.run();
        }
    }
}

