package me.imlukas.wonderlandschat.utils.menu.base;


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

import java.util.*;

public class BaseMenu implements InventoryHolder {

    private final Inventory inventory;
    private final UUID destinationPlayerId;

    private final List<Renderable> renderables = new ArrayList<>();
    private final Map<Integer, MenuElement> elements = new HashMap<>();

    private Runnable closeTask;

    private boolean allowRemoveItems = false;


    public BaseMenu(UUID playerId, String title, int rows) {
        this.inventory = Bukkit.createInventory(this, rows * 9, title);
        this.destinationPlayerId = playerId;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public void clearElements() {
        elements.clear();
        forceUpdate();
    }

    public void open() {
        if (!Bukkit.isPrimaryThread()) {
            MainThreadExecutor.MAIN_THREAD_EXECUTOR.execute(this::open);
            return;
        }

        Player player = getPlayer();

        if (player == null) {
            return;
        }

        player.openInventory(inventory);
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(destinationPlayerId);
    }

    public void addRenderable(Renderable... renderable) {
        renderables.addAll(Arrays.asList(renderable));
    }

    public void forceUpdate() {
        Player player = getPlayer();

        if (player == null) {
            return;
        }

        for (Renderable renderable : renderables) {
            if (renderable.isActive()) {
                renderable.forceUpdate();
            }
        }

        for (Map.Entry<Integer, MenuElement> entry : elements.entrySet()) {
            int slot = entry.getKey();
            MenuElement element = entry.getValue();

            ItemStack item = element.getDisplayItem().clone();

            ItemUtil.replacePlaceholder(item, player, element.getItemPlaceholders());
            inventory.setItem(slot, item);
        }
    }

    public void setElement(int slot, MenuElement element) {
        elements.put(slot, element);
    }

    public void setAllowRemoveItems(boolean allowRemoveItems) {
        this.allowRemoveItems = allowRemoveItems;
    }

    public void onClose(Runnable task) {
        this.closeTask = task;
    }

    public boolean isAllowRemoveItems() {
        return allowRemoveItems;
    }

    public void handleClick(InventoryClickEvent event) {
        int slot = event.getRawSlot();

        if (slot < 0 || slot >= inventory.getSize()) {
            if (event.isShiftClick()) {
                event.setCancelled(true);
            }
            return;
        }

        MenuElement element = elements.get(slot);

        if (element == null) {
            return;
        }

        element.handle(event);

        if (!allowRemoveItems)
            event.setCancelled(true);
    }

    public void handleClose() {
        if (closeTask != null)
            closeTask.run();
    }
}
