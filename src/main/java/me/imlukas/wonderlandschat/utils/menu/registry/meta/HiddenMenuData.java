/*
 * Decompiled with CFR 0.150.
 */
package me.imlukas.wonderlandschat.utils.menu.registry.meta;

import java.util.ArrayList;
import java.util.List;
import me.imlukas.wonderlandschat.utils.collection.TypedMap;
import me.imlukas.wonderlandschat.utils.menu.base.BaseMenu;

public class HiddenMenuData {
    private final BaseMenu menu;
    private final TypedMap<String> meta = new TypedMap();
    private final List<Runnable> displayTasks = new ArrayList<Runnable>();

    public HiddenMenuData(BaseMenu menu) {
        this.menu = menu;
    }

    public void addDisplayTask(Runnable task) {
        this.displayTasks.add(task);
    }

    public void runDisplayTasks() {
        this.displayTasks.forEach(Runnable::run);
    }

    public BaseMenu getMenu() {
        return this.menu;
    }

    public TypedMap<String> getMeta() {
        return this.meta;
    }

    public List<Runnable> getDisplayTasks() {
        return this.displayTasks;
    }
}

