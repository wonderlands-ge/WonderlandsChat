/*
 * Decompiled with CFR 0.150.
 */
package me.imlukas.wonderlandschat.utils.menu.element;

import me.imlukas.wonderlandschat.utils.menu.base.BaseMenu;

public abstract class Renderable {
    protected BaseMenu menu;
    private boolean active = true;

    public Renderable(BaseMenu menu) {
        this.menu = menu;
    }

    public boolean isActive() {
        return this.active;
    }

    public void setActive(boolean active) {
        this.active = active;
        this.forceUpdate();
    }

    public abstract void forceUpdate();
}

