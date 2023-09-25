/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.inventory.ClickType
 *  org.bukkit.event.inventory.InventoryClickEvent
 *  org.bukkit.event.player.AsyncPlayerChatEvent
 *  org.bukkit.inventory.InventoryHolder
 *  org.bukkit.inventory.PlayerInventory
 *  org.bukkit.plugin.Plugin
 */
package me.imlukas.wonderlandschat.utils.menu.listener;

import java.util.UUID;
import java.util.function.Consumer;
import me.imlukas.wonderlandschat.WonderlandsChatPlugin;
import me.imlukas.wonderlandschat.utils.collection.TypedMap;
import me.imlukas.wonderlandschat.utils.menu.base.BaseMenu;
import me.imlukas.wonderlandschat.utils.menu.registry.MenuRegistry;
import me.imlukas.wonderlandschat.utils.menu.registry.meta.HiddenMenuData;
import me.imlukas.wonderlandschat.utils.menu.registry.meta.HiddenMenuTracker;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;

public class MenuListener
implements Listener {
    public static boolean REGISTERED = false;
    private final MenuRegistry registry;

    private MenuListener(MenuRegistry registry) {
        this.registry = registry;
    }

    public static void register(MenuRegistry registry) {
        if (REGISTERED) {
            return;
        }
        WonderlandsChatPlugin plugin = registry.getPlugin();
        plugin.getServer().getPluginManager().registerEvents((Listener)new MenuListener(registry), (Plugin)plugin);
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    private void onClick(InventoryClickEvent event) {
        if (event.getInventory() instanceof PlayerInventory) {
            return;
        }
        InventoryHolder holder = event.getInventory().getHolder();
        if (!(holder instanceof BaseMenu)) {
            return;
        }
        int slot = event.getSlot();
        if (slot > 0 && slot < 9 || event.getClick() == ClickType.NUMBER_KEY) {
            Bukkit.getServer().getConsoleSender().sendMessage("closeinventory " + event.getWhoClicked().getName());
        }
        BaseMenu baseMenu = (BaseMenu)holder;
        baseMenu.handleClick(event);
        event.setCancelled(true);
    }

    @EventHandler
    private void onChat(AsyncPlayerChatEvent event) {
        String content = event.getMessage();
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        HiddenMenuTracker tracker = this.registry.getHiddenMenuTracker();
        HiddenMenuData data = tracker.getHiddenMenu(uuid);
        if (data == null) {
            return;
        }
        tracker.removeHiddenMenu(uuid);
        if (content.equalsIgnoreCase("cancel")) {
            event.setMessage("");
            player.sendMessage("Cancelled");
            data.runDisplayTasks();
            return;
        }
        TypedMap<String> meta = data.getMeta();
        if (!meta.containsKey("input-task")) {
            return;
        }
        Consumer task = (Consumer)meta.getTyped("input-task");
        task.accept(content);
        data.runDisplayTasks();
        event.setCancelled(true);
    }
}

