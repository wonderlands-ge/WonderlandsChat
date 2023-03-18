package me.imlukas.chatcolorgui.gui;

import com.google.common.collect.ImmutableMap;
import me.imlukas.chatcolorgui.ChatColorPlugin;
import me.imlukas.chatcolorgui.data.PlayerData;
import me.imlukas.chatcolorgui.data.color.ColorParser;
import me.imlukas.chatcolorgui.storage.PlayerStorage;
import me.imlukas.chatcolorgui.utils.TextUtil;
import me.imlukas.chatcolorgui.utils.menu.base.ConfigurableMenu;
import me.imlukas.chatcolorgui.utils.menu.button.Button;
import me.imlukas.chatcolorgui.utils.menu.configuration.ConfigurationApplicator;
import me.imlukas.chatcolorgui.utils.menu.layer.BaseLayer;
import me.imlukas.chatcolorgui.utils.storage.MessagesFile;
import me.imlukas.chatcolorgui.utils.text.Placeholder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Dye;

import java.util.*;

public class ChatColorMenu {

    private static final Map<String, String> FORMATS = ImmutableMap.<String, String>builder()
            .put("Strikethrough", "&m")
            .put("Bold", "&l")
            .put("No format", "")
            .put("Underline", "&n")
            .put("Italic", "&o")
            .build();
    private final ChatColorPlugin plugin;
    private final PlayerStorage playerStorage;
    private final ColorParser colorParser;
    private final MessagesFile messages;

    public ChatColorMenu(ChatColorPlugin plugin) {
        this.plugin = plugin;
        this.playerStorage = plugin.getPlayerStorage();
        this.colorParser = plugin.getColorParser();
        this.messages = plugin.getMessages();
    }

    public void open(Player viewer) {
        ConfigurableMenu baseMenu = (ConfigurableMenu) plugin.getMenuRegistry().create("colorlist", viewer);
        ConfigurationApplicator applicator = baseMenu.getApplicator();

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

            String formatName = TextUtil.uncapitalize(entry.getKey());
            String formatChar = entry.getKey().substring(0, 1).toLowerCase();
            String formatCode = entry.getValue();

            Button formatButton = applicator.registerButton(layer, formatChar);

            formatButton.setClickAction((event) -> {
                if (!viewer.hasPermission("chatcolor.*")) {
                    if (!viewer.hasPermission("chatcolor." + formatName)) {
                        messages.sendMessage(viewer, "no-permission");
                        return;
                    }
                }

                setSelectedFormat(formatButton.getDisplayItem(), formatName, applicator);

                playerData.setFormat(formatCode);
                messages.sendMessage(viewer, "format-set", new Placeholder<>("format", TextUtil.colorAndCapitalize(entry.getValue()
                        + entry.getKey())));
                open(viewer);
            });


            if (!formatName.equalsIgnoreCase("no format")) {
                if (!viewer.hasPermission("chatcolor." + formatName)) {

                    if (playerData.getFormat().equalsIgnoreCase(formatCode)) {
                        playerData.setFormat("");
                    }

                    ItemMeta meta = formatButton.getDisplayItem().getItemMeta();
                    String noPerm = applicator.getConfig().getString("items." +  formatChar + ".lore-no-perm");
                    noPerm = noPerm.replace("%format%", formatName);
                    meta.setLore(Collections.singletonList(TextUtil.color(noPerm)));
                    formatButton.getDisplayItem().setItemMeta(meta);
                }
            }

            if (playerData.getFormat().equalsIgnoreCase(formatCode)) {
                setSelectedFormat(formatButton.getDisplayItem(), formatChar, applicator);
            }

            baseMenu.forceUpdate();
        }

        for (Map.Entry<String, ItemStack> colorEntry : colorParser.getColorsMap().entrySet()) {
            String colorName = colorEntry.getKey();
            String displayName = colorParser.getDisplayName(colorName);
            String color = colorParser.getDisplayColor(colorName);
            int slot = colorParser.getSlot(colorName);

            List<Placeholder<Player>> placeholderList = new ArrayList<>();
            placeholderList.add(new Placeholder<>("color", TextUtil.colorAndCapitalize(color
                    + displayName)));

            ItemStack colorItem = colorEntry.getValue();
            Button colorButton = new Button(colorItem.clone());

            colorButton.setClickAction((event) -> {
                if (!viewer.hasPermission("chatcolor.*")) {
                    if (!viewer.hasPermission("chatcolor." + colorName)) {
                        messages.sendMessage(viewer, "no-permission");
                        return;
                    }
                }

                setSelected(colorButton.getDisplayItem(), colorName);

                playerData.setRandomColor(false);
                playerData.setColor(color);
                messages.sendMessage(viewer, "color-set", placeholderList);
                open(viewer);
            });

            colorButton.setItemPlaceholders(placeholderList);


            if (!viewer.hasPermission("chatcolor." + colorName)) {

                if (playerData.getColor().equalsIgnoreCase(color)) {
                    playerData.setColor("");
                }

                List<String> noPerm = colorParser.getNoPermLore(colorName);
                noPerm.replaceAll(TextUtil::color);
                noPerm.replaceAll(s -> s.replace("%color%", displayName));

                ItemMeta meta = colorButton.getDisplayItem().getItemMeta();
                meta.setLore(noPerm);
                colorButton.getDisplayItem().setItemMeta(meta);
            }

            if (playerData.getColor().equalsIgnoreCase(color)) {
                setSelected(colorButton.getDisplayItem(), colorName);
            }

            baseMenu.setElement(slot, colorButton);
        }

        baseMenu.addRenderable(layer);
        baseMenu.forceUpdate();
        baseMenu.open();
    }


    public void setSelected(ItemStack item, String color) {
        ItemMeta meta = item.getItemMeta();

        List<String> selected = TextUtil.color(colorParser.getSelectedLore(color));
        meta.setLore(selected);
        item.setItemMeta(meta);
    }

    public void setSelectedFormat(ItemStack item, String format, ConfigurationApplicator applicator) {
        ItemMeta meta = item.getItemMeta();

        List<String> selected = TextUtil.color(applicator.getConfig().getStringList("items." + format + ".selected"));
        meta.setLore(selected);
        item.setItemMeta(meta);
    }
}
