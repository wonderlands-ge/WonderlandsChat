/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 */
package me.imlukas.wonderlandschat.data;

import java.util.UUID;
import me.imlukas.wonderlandschat.utils.TextUtil;
import org.bukkit.entity.Player;

public class PlayerData {
    private final UUID uuid;
    private String format;
    private String color;
    private boolean randomColor;

    public PlayerData(UUID uuid) {
        this.uuid = uuid;
        this.color = "";
        this.format = "";
    }

    public PlayerData(Player player) {
        this.uuid = player.getUniqueId();
        this.color = "";
        this.format = "";
    }

    public PlayerData(Player player, String format, String color) {
        this.uuid = player.getUniqueId();
        this.format = format;
        this.color = color;
    }

    public void reset() {
        this.format = "";
        this.color = "";
        this.randomColor = false;
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public String getFormat() {
        return this.format;
    }

    public String getColor() {
        if (this.isRandomColor()) {
            return "random";
        }
        return this.color;
    }

    public String getFormatted() {
        if (this.isRandomColor()) {
            this.setColor(TextUtil.getRandomColor());
        }
        return this.color + this.format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setRandomColor(boolean randomColor) {
        this.randomColor = randomColor;
        if (randomColor) {
            this.color = "";
        }
    }

    public boolean isRandomColor() {
        return this.randomColor;
    }
}

