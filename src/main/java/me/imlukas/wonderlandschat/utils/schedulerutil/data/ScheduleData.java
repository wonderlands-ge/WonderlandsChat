/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.plugin.java.JavaPlugin
 */
package me.imlukas.wonderlandschat.utils.schedulerutil.data;

import org.bukkit.plugin.java.JavaPlugin;

public class ScheduleData {
    private boolean sync;
    private long ticks;
    private Runnable runnable;
    private boolean repeating;
    private long cancelIn = -1L;
    private JavaPlugin plugin;

    public boolean isSync() {
        return this.sync;
    }

    public long getTicks() {
        return this.ticks;
    }

    public Runnable getRunnable() {
        return this.runnable;
    }

    public boolean isRepeating() {
        return this.repeating;
    }

    public long getCancelIn() {
        return this.cancelIn;
    }

    public JavaPlugin getPlugin() {
        return this.plugin;
    }

    public void setSync(boolean sync) {
        this.sync = sync;
    }

    public void setTicks(long ticks) {
        this.ticks = ticks;
    }

    public void setRunnable(Runnable runnable) {
        this.runnable = runnable;
    }

    public void setRepeating(boolean repeating) {
        this.repeating = repeating;
    }

    public void setCancelIn(long cancelIn) {
        this.cancelIn = cancelIn;
    }

    public void setPlugin(JavaPlugin plugin) {
        this.plugin = plugin;
    }
}

