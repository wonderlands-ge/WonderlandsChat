/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 *  org.bukkit.event.inventory.ClickType
 *  org.bukkit.event.inventory.InventoryClickEvent
 *  org.bukkit.inventory.ItemStack
 */
package me.imlukas.wonderlandschat.utils.menu.button;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Consumer;
import me.imlukas.wonderlandschat.utils.menu.element.MenuElement;
import me.imlukas.wonderlandschat.utils.text.Placeholder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class Button
implements MenuElement {
    private ItemStack displayItem;
    private Consumer<InventoryClickEvent> clickTask;
    private Consumer<InventoryClickEvent> rightClickTask;
    private Consumer<InventoryClickEvent> leftClickTask;
    private Collection<Placeholder<Player>> placeholders;

    public Button(ItemStack displayItem) {
        this.displayItem = displayItem;
    }

    public Button(ItemStack displayItem, Consumer<InventoryClickEvent> clickTask) {
        this.displayItem = displayItem;
        this.clickTask = clickTask;
    }

    public void setClickAction(Consumer<InventoryClickEvent> clickTask) {
        this.clickTask = clickTask;
    }

    public void setRightClickAction(Runnable clickTask) {
        this.rightClickTask = event -> clickTask.run();
    }

    public void setLeftClickAction(Runnable clickTask) {
        this.leftClickTask = event -> clickTask.run();
    }

    @Override
    public void handle(InventoryClickEvent event) {
        ClickType clickType = event.getClick();
        if (this.clickTask != null) {
            this.clickTask.accept(event);
            return;
        }
        if (clickType == ClickType.LEFT && this.leftClickTask != null) {
            this.leftClickTask.accept(event);
            return;
        }
        if (clickType == ClickType.RIGHT && this.rightClickTask != null) {
            this.rightClickTask.accept(event);
            return;
        }
    }

    public void setDisplayItem(ItemStack displayItem) {
        this.displayItem = displayItem;
    }

    public void setPlaceholders(Collection<Placeholder<Player>> placeholders) {
        this.placeholders = placeholders;
    }

    public void setPlaceholders(Placeholder<Player> ... placeholders) {
        this.placeholders = Arrays.asList(placeholders);
    }

    @Override
    public MenuElement copy() {
        return new Button(this.displayItem.clone(), this.clickTask, this.rightClickTask, this.leftClickTask, this.placeholders);
    }

    @Override
    public Collection<Placeholder<Player>> getItemPlaceholders() {
        return this.placeholders;
    }

    @Override
    public MenuElement setItemPlaceholders(Collection<Placeholder<Player>> placeholders) {
        this.placeholders = placeholders;
        return this;
    }

    public void setDisplayMaterial(Material material) {
        this.displayItem.setType(material);
    }

    public void setDisplayMaterial(String material) {
        this.displayItem.setType(Material.valueOf((String)material));
    }

    public void onClick(ClickType clickType, Consumer<InventoryClickEvent> clickTask) {
        this.clickTask = clickTask;
    }

    public Button(ItemStack displayItem, Consumer<InventoryClickEvent> clickTask, Consumer<InventoryClickEvent> rightClickTask, Consumer<InventoryClickEvent> leftClickTask, Collection<Placeholder<Player>> placeholders) {
        this.displayItem = displayItem;
        this.clickTask = clickTask;
        this.rightClickTask = rightClickTask;
        this.leftClickTask = leftClickTask;
        this.placeholders = placeholders;
    }

    @Override
    public ItemStack getDisplayItem() {
        return this.displayItem;
    }

    public Consumer<InventoryClickEvent> getClickTask() {
        return this.clickTask;
    }

    public Consumer<InventoryClickEvent> getRightClickTask() {
        return this.rightClickTask;
    }

    public Consumer<InventoryClickEvent> getLeftClickTask() {
        return this.leftClickTask;
    }

    public Collection<Placeholder<Player>> getPlaceholders() {
        return this.placeholders;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Button)) {
            return false;
        }
        Button other = (Button)o;
        if (!other.canEqual(this)) {
            return false;
        }
        ItemStack this$displayItem = this.getDisplayItem();
        ItemStack other$displayItem = other.getDisplayItem();
        if (this$displayItem == null ? other$displayItem != null : !this$displayItem.equals((Object)other$displayItem)) {
            return false;
        }
        Consumer<InventoryClickEvent> this$clickTask = this.getClickTask();
        Consumer<InventoryClickEvent> other$clickTask = other.getClickTask();
        if (this$clickTask == null ? other$clickTask != null : !this$clickTask.equals(other$clickTask)) {
            return false;
        }
        Consumer<InventoryClickEvent> this$rightClickTask = this.getRightClickTask();
        Consumer<InventoryClickEvent> other$rightClickTask = other.getRightClickTask();
        if (this$rightClickTask == null ? other$rightClickTask != null : !this$rightClickTask.equals(other$rightClickTask)) {
            return false;
        }
        Consumer<InventoryClickEvent> this$leftClickTask = this.getLeftClickTask();
        Consumer<InventoryClickEvent> other$leftClickTask = other.getLeftClickTask();
        if (this$leftClickTask == null ? other$leftClickTask != null : !this$leftClickTask.equals(other$leftClickTask)) {
            return false;
        }
        Collection<Placeholder<Player>> this$placeholders = this.getPlaceholders();
        Collection<Placeholder<Player>> other$placeholders = other.getPlaceholders();
        return !(this$placeholders == null ? other$placeholders != null : !((Object)this$placeholders).equals(other$placeholders));
    }

    protected boolean canEqual(Object other) {
        return other instanceof Button;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        ItemStack $displayItem = this.getDisplayItem();
        result = result * 59 + ($displayItem == null ? 43 : $displayItem.hashCode());
        Consumer<InventoryClickEvent> $clickTask = this.getClickTask();
        result = result * 59 + ($clickTask == null ? 43 : $clickTask.hashCode());
        Consumer<InventoryClickEvent> $rightClickTask = this.getRightClickTask();
        result = result * 59 + ($rightClickTask == null ? 43 : $rightClickTask.hashCode());
        Consumer<InventoryClickEvent> $leftClickTask = this.getLeftClickTask();
        result = result * 59 + ($leftClickTask == null ? 43 : $leftClickTask.hashCode());
        Collection<Placeholder<Player>> $placeholders = this.getPlaceholders();
        result = result * 59 + ($placeholders == null ? 43 : ((Object)$placeholders).hashCode());
        return result;
    }
}

