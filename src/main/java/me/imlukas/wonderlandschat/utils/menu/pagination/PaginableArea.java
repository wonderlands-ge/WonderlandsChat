package me.imlukas.wonderlandschat.utils.menu.pagination;

import me.imlukas.wonderlandschat.utils.menu.base.BaseMenu;
import me.imlukas.wonderlandschat.utils.menu.button.Button;
import me.imlukas.wonderlandschat.utils.menu.element.MenuElement;
import me.imlukas.wonderlandschat.utils.menu.selection.Selection;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class PaginableArea {

    private final List<Integer> slots;

    private MenuElement emptyElement = new Button(new ItemStack(Material.AIR));
    private final List<MenuElement> elements = new ArrayList<>();


    public PaginableArea(Selection selection) {
        this.slots = selection.getSlots();
    }

    public PaginableArea(Selection selection, MenuElement emptyElement) {
        this(selection);
        this.emptyElement = emptyElement;
    }

    public void setEmptyElement(
            MenuElement emptyElement) {
        this.emptyElement = emptyElement;
    }

    public void addElement(MenuElement element) {
        elements.add(element);
    }

    public void forceUpdate(BaseMenu menu, int page) {
        int startIdx = (page - 1) * slots.size();
        int endIdx = startIdx + slots.size();

        for (int index = startIdx; index < endIdx; index++) {
            int slot = slots.get(index - startIdx);

            if (index >= elements.size())
                menu.setElement(slot, emptyElement);
            else
                menu.setElement(slot, elements.get(index));
        }
    }

    public int getPageCount() {
        return (int) Math.ceil((double) elements.size() / slots.size());
    }

}
