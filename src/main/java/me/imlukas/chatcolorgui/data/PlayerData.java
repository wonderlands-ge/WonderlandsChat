package me.imlukas.chatcolorgui.data;

import me.imlukas.chatcolorgui.utils.TextUtil;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Stores all information about a player and their chat color and format.
 */
public class PlayerData {

    private final UUID uuid;
    private String format, color;
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

    public UUID getUUID() {
        return uuid;
    }

    public String getFormat() {
        return format;
    }

    public String getColor() {
        if (isRandomColor()) {
            return "random";
        }

        return color;
    }

    public String getFormatted() {

        if (isRandomColor()) {
            setColor(TextUtil.getRandomColor());
        }

        return color + format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setRandomColor(boolean randomColor) {
        this.randomColor = randomColor;
    }

    public boolean isRandomColor() {
        return randomColor;
    }


}
