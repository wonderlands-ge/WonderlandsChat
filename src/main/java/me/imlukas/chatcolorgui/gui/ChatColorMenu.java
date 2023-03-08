package me.imlukas.chatcolorgui.gui;

import com.google.common.collect.ImmutableMap;
import me.imlukas.chatcolorgui.ChatColorPlugin;
import me.imlukas.chatcolorgui.data.PlayerData;
import me.imlukas.chatcolorgui.storage.PlayerStorage;
import me.imlukas.chatcolorgui.utils.TextUtil;
import me.imlukas.chatcolorgui.utils.menu.base.ConfigurableMenu;
import me.imlukas.chatcolorgui.utils.menu.button.Button;
import me.imlukas.chatcolorgui.utils.menu.configuration.ConfigurationApplicator;
import me.imlukas.chatcolorgui.utils.menu.layer.BaseLayer;
import me.imlukas.chatcolorgui.utils.storage.MessagesFile;
import me.imlukas.chatcolorgui.utils.text.Placeholder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Dye;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

public class ChatColorMenu {

    // I hate java 8. :)
    private static final Map<String, String> COLORS = ImmutableMap.<String, String>builder()
            .put("red", "&c")
            .put("green", "&2")
            .put("gold", "&6")
            .put("blue", "&9")
            .put("purple", "&5")
            .put("cyan", "&3")
            .put("gray", "&7")
            .put("dark_red", "&4")
            .put("pink", "&d")
            .put("lime", "&a")
            .put("dark_blue", "&1")
            .put("yellow", "&e")
            .put("aqua", "&b")
            .put("magenta", "&d")
            .build();

    private static final Map<String, String> FORMATS = ImmutableMap.<String, String>builder()
            .put("Strikethrough", "&m")
            .put("Bold", "&l")
            .put("No format", "")
            .put("Underline", "&n")
            .put("Italic", "&o")
            .build();
    private final ChatColorPlugin plugin;
    private final PlayerStorage playerStorage;
    private final MessagesFile messages;

    public ChatColorMenu(ChatColorPlugin plugin) {
        this.plugin = plugin;
        this.playerStorage = plugin.getPlayerStorage();
        this.messages = plugin.getMessages();
    }

    public void open(Player viewer) {

        ConfigurableMenu baseMenu = (ConfigurableMenu) plugin.getMenuRegistry().create("colorlist", viewer);
        ConfigurationApplicator applicator = baseMenu.getApplicator();

        int slot = 10;

        BaseLayer layer = new BaseLayer(baseMenu);

        PlayerData playerData = playerStorage.getPlayerData(viewer.getUniqueId());

        applicator.registerButton(layer, "c", viewer::closeInventory);
        applicator.registerButton(layer, "r", () -> {
            playerData.setColor("");
            playerData.setFormat("");
            playerData.setRandomColor(false);
            messages.sendMessage(viewer, "reset");
        });

        applicator.registerButton(layer, "ra", () -> {
            if (!viewer.hasPermission("chatcolor.*")) {
                if (!viewer.hasPermission("chatcolor.random")) {
                    messages.sendMessage(viewer, "no-permission");
                    return;
                }
            }
            playerData.setRandomColor(!playerData.isRandomColor());
            playerData.setColor("");
            messages.sendMessage(viewer, "random-color", new Placeholder<>("state", playerData.isRandomColor() ? "enabled" : "disabled"));
        });
        // register format buttons
        for (Map.Entry<String, String> entry : FORMATS.entrySet()) {

            String formatChar = entry.getKey().substring(0, 1).toLowerCase();

            applicator.registerButton(layer, formatChar, () -> {
                if (!viewer.hasPermission("chatcolor.*")) {
                    if (!viewer.hasPermission("chatcolor." + entry.getKey().toLowerCase())) {
                        messages.sendMessage(viewer, "no-permission");
                        return;
                    }
                }
                playerData.setFormat(entry.getValue());
                messages.sendMessage(viewer, "format-set", new Placeholder<>("format", TextUtil.colorAndCapitalize(entry.getValue()
                        + entry.getKey())));
            });
        }

        String hasPerm = applicator.getConfig().getString("lore-perm");
        String noPerm = applicator.getConfig().getString("lore-no-perm");
        // register color buttons
        for (Map.Entry<String, String> colorEntry : COLORS.entrySet()) {
            String colorName = colorEntry.getKey();
            String colorCode = colorEntry.getValue();

            Button colorButton = applicator.registerButton(layer, colorName, () -> {
                if (!viewer.hasPermission("chatcolor.*")) {
                    if (!viewer.hasPermission("chatcolor." + colorEntry.getKey())) {
                        messages.sendMessage(viewer, "no-permission");
                        return;
                    }
                }

                playerData.setRandomColor(false);

                playerData.setColor(colorEntry.getValue());
                messages.sendMessage(viewer, "color-set", new Placeholder<>("color", TextUtil.colorAndCapitalize(colorCode
                        + colorName.replace("_", " "))));
            });

            ItemMeta meta = colorButton.getDisplayItem().getItemMeta();

            String hasPermCopy = hasPerm;
            hasPermCopy = hasPermCopy.replace("%color%", TextUtil.color(colorCode + colorName.replace("_", " ")));

            if (viewer.hasPermission("chatcolor." + colorName)) {
                meta.setLore(Collections.singletonList(TextUtil.color(hasPermCopy)));
            } else {
                meta.setLore(Collections.singletonList(TextUtil.color(noPerm)));
            }

            colorButton.getDisplayItem().setItemMeta(meta);
            baseMenu.setElement(slot, colorButton);

            // not the best way of handling slots, but here works fine.
            if (slot == 16) {
                slot = 19;
                continue;
            }

            if (slot == 25){
                slot = 30;
                continue;
            }
            slot++;
        }

        baseMenu.addRenderable(layer);
        baseMenu.forceUpdate();
        baseMenu.open();
    }
}
