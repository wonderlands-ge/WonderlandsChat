package me.imlukas.wonderlandschat.utils.menu.element;

import me.imlukas.wonderlandschat.utils.menu.base.BaseMenu;

public abstract class Renderable {

    protected BaseMenu menu;
    private boolean active = true;

    public Renderable(BaseMenu menu) {
        this.menu = menu;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
        forceUpdate();
    }

    public abstract void forceUpdate();

}
