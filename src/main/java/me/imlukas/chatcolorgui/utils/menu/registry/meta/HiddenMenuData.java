package me.imlukas.chatcolorgui.utils.menu.registry.meta;

import lombok.Getter;
import me.imlukas.chatcolorgui.utils.collection.TypedMap;
import me.imlukas.chatcolorgui.utils.menu.base.BaseMenu;

import java.util.ArrayList;
import java.util.List;

@Getter
public class HiddenMenuData {

    private final BaseMenu menu;
    private final TypedMap<String> meta = new TypedMap<>();

    private final List<Runnable> displayTasks = new ArrayList<>();

    public HiddenMenuData(BaseMenu menu) {
        this.menu = menu;
    }

    public void addDisplayTask(Runnable task) {
        displayTasks.add(task);
    }

    public void runDisplayTasks() {
        displayTasks.forEach(Runnable::run);
    }

}
