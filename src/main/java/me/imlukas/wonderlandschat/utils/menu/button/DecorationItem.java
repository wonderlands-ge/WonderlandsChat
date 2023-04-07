package me.imlukas.wonderlandschat.utils.menu.button;

import me.imlukas.wonderlandschat.utils.text.Placeholder;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.function.Consumer;

public class DecorationItem extends Button {

    public DecorationItem(ItemStack displayItem,
                          Consumer<InventoryClickEvent> clickTask,
                          Consumer<InventoryClickEvent> rightClickTask,
                          Consumer<InventoryClickEvent> leftClickTask,
                          Collection<Placeholder<Player>> placeholders) {
        super(displayItem, clickTask, rightClickTask, leftClickTask, placeholders);
    }

    public DecorationItem(ItemStack displayItem) {
        super(displayItem);
    }

    public DecorationItem(ItemStack displayItem,
                          Consumer<InventoryClickEvent> clickTask) {
        super(displayItem, clickTask);
    }

    @Override
    public void handle(InventoryClickEvent event) {
        event.setCancelled(true);
    }
}
