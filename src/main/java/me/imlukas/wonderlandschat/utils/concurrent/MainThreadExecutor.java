/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.java.JavaPlugin
 */
package me.imlukas.wonderlandschat.utils.concurrent;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class MainThreadExecutor
implements Executor {
    public static final MainThreadExecutor MAIN_THREAD_EXECUTOR = new MainThreadExecutor();
    private static JavaPlugin plugin;

    private MainThreadExecutor() {
    }

    public static void init(JavaPlugin main) {
        plugin = main;
    }

    @Override
    public void execute(Runnable runnable) {
        if (Bukkit.isPrimaryThread()) {
            runnable.run();
            return;
        }
        CountDownLatch latch = new CountDownLatch(1);
        Runnable task = () -> {
            runnable.run();
            latch.countDown();
        };
        Bukkit.getScheduler().runTask((Plugin)plugin, task);
        try {
            latch.await();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

