/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 */
package me.imlukas.wonderlandschat.utils;

import org.bukkit.entity.Player;

public class PlayerUtil {
    private static String PERMISSION_PREFIX = "chatcolor.";

    public static boolean hasPermission(Player player, String permission) {
        return player.hasPermission(PERMISSION_PREFIX + permission);
    }
}

