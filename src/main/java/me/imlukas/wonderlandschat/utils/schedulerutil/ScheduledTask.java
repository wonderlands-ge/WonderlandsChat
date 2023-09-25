/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.java.JavaPlugin
 *  org.bukkit.scheduler.BukkitScheduler
 */
package me.imlukas.wonderlandschat.utils.schedulerutil;

import java.util.ArrayList;
import java.util.List;
import me.imlukas.wonderlandschat.utils.schedulerutil.data.ScheduleData;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class ScheduledTask {
    private final ScheduleData data;
    private final JavaPlugin plugin;
    private boolean running = false;
    private int id = 0;
    private final List<Runnable> cancelTasks = new ArrayList<Runnable>();

    public ScheduledTask(JavaPlugin plugin, ScheduleData data) {
        this.plugin = plugin;
        this.data = data;
    }

    public ScheduledTask start() {
        if (this.running) {
            return this;
        }
        BukkitScheduler scheduler = Bukkit.getScheduler();
        this.id = this.data.isSync() ? (this.data.isRepeating() ? scheduler.scheduleSyncRepeatingTask((Plugin)this.plugin, this.data.getRunnable(), this.data.getTicks(), this.data.getTicks()) : scheduler.scheduleSyncDelayedTask((Plugin)this.plugin, this.data.getRunnable(), this.data.getTicks())) : (this.data.isRepeating() ? scheduler.runTaskTimerAsynchronously((Plugin)this.plugin, this.data.getRunnable(), this.data.getTicks(), this.data.getTicks()).getTaskId() : scheduler.runTaskLaterAsynchronously((Plugin)this.plugin, this.data.getRunnable(), this.data.getTicks()).getTaskId());
        if (this.data.getCancelIn() != -1L) {
            scheduler.scheduleSyncDelayedTask((Plugin)this.plugin, this::cancel, this.data.getCancelIn());
        }
        this.running = true;
        return this;
    }

    public ScheduledTask onCancel(Runnable runnable) {
        this.cancelTasks.add(runnable);
        return this;
    }

    public void forceCancel() {
        this.cancelTasks.clear();
        this.cancel();
    }

    public void cancel() {
        if (!this.running) {
            return;
        }
        this.running = false;
        Bukkit.getScheduler().cancelTask(this.id);
        this.cancelTasks.forEach(Runnable::run);
    }

    public int getId() {
        return this.id;
    }
}

