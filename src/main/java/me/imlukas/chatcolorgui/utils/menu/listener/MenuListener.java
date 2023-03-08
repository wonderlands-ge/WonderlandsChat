package me.imlukas.chatcolorgui.utils.menu.listener;

import me.imlukas.chatcolorgui.ChatColorPlugin;
import me.imlukas.chatcolorgui.utils.collection.TypedMap;
import me.imlukas.chatcolorgui.utils.menu.base.BaseMenu;
import me.imlukas.chatcolorgui.utils.menu.registry.MenuRegistry;
import me.imlukas.chatcolorgui.utils.menu.registry.meta.HiddenMenuData;
import me.imlukas.chatcolorgui.utils.menu.registry.meta.HiddenMenuTracker;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.InventoryHolder;

import java.util.UUID;
import java.util.function.Consumer;

public class MenuListener implements Listener {

    public static boolean REGISTERED = false;

    private final MenuRegistry registry;

    private MenuListener(MenuRegistry registry) {
        this.registry = registry;
    }

    public static void register(MenuRegistry registry) {
        if (REGISTERED)
            return;

        ChatColorPlugin plugin = registry.getPlugin();
        plugin.getServer().getPluginManager().registerEvents(new MenuListener(registry), plugin);
        REGISTERED = true;
    }

    @EventHandler
    private void onClick(InventoryClickEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();

        if (!(holder instanceof BaseMenu)) {
            return;
        }

        BaseMenu baseMenu = (BaseMenu) holder;

        baseMenu.handleClick(event);
    }

    @EventHandler
    private void onClose(InventoryCloseEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();

        if (!(holder instanceof BaseMenu)) {
            return;
        }

        BaseMenu baseMenu = (BaseMenu) holder;

        baseMenu.handleClose();
    }

    @EventHandler
    private void onChat(AsyncPlayerChatEvent event) {
        /*Component message = event.getMessage()

        if(!(message instanceof TextComponent text))
            return;

         */

        String content = event.getMessage();
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        HiddenMenuTracker tracker = registry.getHiddenMenuTracker();

        HiddenMenuData data = tracker.getHiddenMenu(uuid);

        if (data == null)
            return;

        tracker.removeHiddenMenu(uuid);

        if (content.equalsIgnoreCase("cancel")) {
            event.setMessage("");
            player.sendMessage("Cancelled");
            data.runDisplayTasks();
            return;
        }

        TypedMap<String> meta = data.getMeta();

        if (!meta.containsKey("input-task"))
            return;

        Consumer<String> task = meta.getTyped("input-task");
        task.accept(content);
        data.runDisplayTasks();


        event.setCancelled(true);


    }
}
