package me.imlukas.wonderlandschat.utils.menu.button;

import me.imlukas.wonderlandschat.utils.text.Placeholder;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.function.Consumer;

public class RemovableItem extends Button {

    public RemovableItem(ItemStack displayItem,
                         Consumer<InventoryClickEvent> clickTask,
                         Consumer<InventoryClickEvent> rightClickTask,
                         Consumer<InventoryClickEvent> leftClickTask,
                         Collection<Placeholder<Player>> placeholders) {
        super(displayItem, clickTask, rightClickTask, leftClickTask, placeholders);
    }

    public RemovableItem(ItemStack displayItem) {
        super(displayItem);
    }

    public RemovableItem(ItemStack displayItem,
                         Consumer<InventoryClickEvent> clickTask) {
        super(displayItem, clickTask);
    }

    @Override
    public void handle(InventoryClickEvent event) {
        event.setCancelled(false);
    }
}
