/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 */
package me.imlukas.wonderlandschat.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.UnaryOperator;
import me.imlukas.wonderlandschat.WonderlandsChatPlugin;
import me.imlukas.wonderlandschat.data.PlayerData;
import me.imlukas.wonderlandschat.data.color.ColorParser;
import me.imlukas.wonderlandschat.storage.PlayerStorage;
import me.imlukas.wonderlandschat.utils.PlayerUtil;
import me.imlukas.wonderlandschat.utils.TextUtil;
import me.imlukas.wonderlandschat.utils.menu.base.BaseMenu;
import me.imlukas.wonderlandschat.utils.menu.base.ConfigurableMenu;
import me.imlukas.wonderlandschat.utils.menu.button.Button;
import me.imlukas.wonderlandschat.utils.menu.configuration.ConfigurationApplicator;
import me.imlukas.wonderlandschat.utils.menu.layer.BaseLayer;
import me.imlukas.wonderlandschat.utils.storage.MessagesFile;
import me.imlukas.wonderlandschat.utils.text.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ChatColorMenu {
    private final WonderlandsChatPlugin plugin;
    private final PlayerStorage playerStorage;
    private final ColorParser colorParser;
    private final MessagesFile messages;

    public ChatColorMenu(WonderlandsChatPlugin plugin) {
        this.plugin = plugin;
        this.playerStorage = plugin.getPlayerStorage();
        this.colorParser = plugin.getColorParser();
        this.messages = plugin.getMessages();
    }

    public void open(Player viewer) {
        ConfigurableMenu baseMenu = (ConfigurableMenu)this.plugin.getMenuRegistry().create("colorlist", viewer);
        ConfigurationApplicator applicator = baseMenu.getApplicator();
        BaseLayer layer = new BaseLayer(baseMenu);
        PlayerData playerData = this.playerStorage.getPlayerData(viewer.getUniqueId());
        applicator.registerButton(layer, "c", ((Player)viewer)::closeInventory);
        applicator.registerButton(layer, "ba", () -> {
            String command = applicator.getConfig().getString("items.ba.command");
            if (command == null || command.isEmpty()) {
                viewer.closeInventory();
                return;
            }
            if (command.startsWith("dm")) {
                viewer.closeInventory();
            }
            viewer.performCommand(command);
        });
        Button resetButton = applicator.registerButton(layer, "r", () -> {
            playerData.reset();
            this.messages.sendMessage((CommandSender)viewer, "reset");
            this.open(viewer);
        });
        resetButton.setPlaceholders(new Placeholder("player", viewer.getName()));
        Button randomButton = applicator.registerButton(layer, "ra");
        randomButton.setLeftClickAction(() -> {
            if (!PlayerUtil.hasPermission(viewer, "*") && !PlayerUtil.hasPermission(viewer, "random")) {
                this.messages.sendMessage((CommandSender)viewer, "no-permission");
                return;
            }
            playerData.setRandomColor(!playerData.isRandomColor());
            if (playerData.isRandomColor()) {
                this.setSelectedConfig(randomButton.getDisplayItem(), "ra", applicator, new UnaryOperator[0]);
            }
            this.messages.sendMessage(viewer, "random-color", new Placeholder[]{new Placeholder("state", playerData.isRandomColor() ? "enabled" : "disabled")});
            this.updateMenu(viewer, baseMenu, layer, playerData, randomButton, applicator);
        });
        if (!PlayerUtil.hasPermission(viewer, "*") && !PlayerUtil.hasPermission(viewer, "random")) {
            this.setNoPermission(randomButton.getDisplayItem(), "ra", applicator, new UnaryOperator[0]);
        }
        for (Map.Entry<String, String> entry : ColorParser.FORMATS.entrySet()) {
            String formatName = TextUtil.uncapitalize(entry.getKey());
            String formatChar = entry.getKey().substring(0, 1).toLowerCase();
            String formatCode = entry.getValue();
            Button formatButton = applicator.registerButton(layer, formatChar);
            formatButton.setClickAction(event -> {
                if (!PlayerUtil.hasPermission(viewer, "*") && !PlayerUtil.hasPermission(viewer, formatName.replace(" ", "-"))) {
                    this.messages.sendMessage((CommandSender)viewer, "no-permission");
                    return;
                }
                playerData.setFormat(formatCode);
                this.messages.sendMessage(viewer, "format-set", new Placeholder[]{new Placeholder("format", TextUtil.colorAndCapitalize((String)entry.getValue() + (String)entry.getKey()))});
                this.updateMenu(viewer, baseMenu, layer, playerData, randomButton, applicator);
            });
            this.updateFormat(viewer, playerData, applicator, formatButton.getDisplayItem(), formatName, formatChar, formatCode);
        }
        this.updateMenu(viewer, baseMenu, layer, playerData, randomButton, applicator);
        baseMenu.addRenderable(layer);
        baseMenu.forceUpdate();
        baseMenu.open();
    }

    private void updateMenu(Player player, BaseMenu baseMenu, BaseLayer layer, PlayerData data, Button randomButton, ConfigurationApplicator applicator) {
        baseMenu.clearElements();
        ItemStack item = applicator.getItem("ra");
        randomButton.setDisplayItem(item.clone());
        for (Map.Entry<String, ItemStack> entry : this.colorParser.getColorsMap().entrySet()) {
            String colorName = entry.getKey();
            String itemDisplayName = this.colorParser.getDisplayName(colorName);
            String colorChar = this.colorParser.getDisplayColor(colorName);
            int slot = this.colorParser.getSlot(colorName);
            ArrayList<Placeholder<Player>> placeholderList = new ArrayList<Placeholder<Player>>();
            placeholderList.add(new Placeholder("color", TextUtil.colorAndCapitalize(colorChar + itemDisplayName)));
            ItemStack colorItem = entry.getValue();
            Button colorButton = new Button(colorItem.clone());
            colorButton.setItemPlaceholders(placeholderList);
            colorButton.setClickAction(event -> {
                if (!PlayerUtil.hasPermission(player, "*") && !PlayerUtil.hasPermission(player, colorName)) {
                    this.messages.sendMessage((CommandSender)player, "no-permission");
                    return;
                }
                data.setRandomColor(false);
                data.setColor(colorChar);
                this.messages.sendMessage(player, "color-set", placeholderList);
                this.updateMenu(player, baseMenu, layer, data, randomButton, applicator);
            });
            this.updateColor(player, data, applicator, colorButton.getDisplayItem(), randomButton.getDisplayItem(), colorName);
            baseMenu.setElement(slot, colorButton);
            baseMenu.forceUpdate();
        }
        for (Map.Entry<String, String> entry : ColorParser.FORMATS.entrySet()) {
            String formatName = entry.getKey().toLowerCase(Locale.ROOT);
            String formatChar = entry.getKey().substring(0, 1).toLowerCase();
            String formatCode = entry.getValue();
            Button formatButton = applicator.registerButton(layer, formatChar);
            formatButton.setClickAction(event -> {
                if (!PlayerUtil.hasPermission(player, "*") && !PlayerUtil.hasPermission(player, formatName.replace(" ", "-"))) {
                    this.messages.sendMessage((CommandSender)player, "no-permission");
                    return;
                }
                data.setFormat(formatCode);
                this.messages.sendMessage(player, "format-set", new Placeholder<>("format", TextUtil.colorAndCapitalize((String)entry.getValue() + (String)entry.getKey())));
                this.updateMenu(player, baseMenu, layer, data, randomButton, applicator);
            });
            this.updateFormat(player, data, applicator, formatButton.getDisplayItem(), formatName, formatChar, formatCode);
        }
        baseMenu.forceUpdate();
    }

    private void updateColor(Player player, PlayerData data, ConfigurationApplicator applicator, ItemStack displayItem, ItemStack randomDisplayItem, String colorName) {
        String displayName = this.colorParser.getDisplayName(colorName);
        String colorChar = this.colorParser.getDisplayColor(colorName);
        if (!PlayerUtil.hasPermission(player, "*") && !PlayerUtil.hasPermission(player, colorName)) {
            this.setNoPermission(displayItem, colorName, s -> s.replace("%color%", displayName));
        }
        if (data.getColor().equalsIgnoreCase(colorChar)) {
            this.setSelected(displayItem, colorName, s -> s.replace("%color%", displayName));
        } else if (data.isRandomColor()) {
            this.setSelectedConfig(randomDisplayItem, "ra", applicator, new UnaryOperator[0]);
        }
    }

    private void updateFormat(Player player, PlayerData data, ConfigurationApplicator applicator, ItemStack formatItem, String formatName, String formatChar, String formatCode) {
        this.setDefault(formatItem, formatChar, applicator, s -> s.replace("%format%", formatName));
        if (data.getFormat().equalsIgnoreCase(formatCode)) {
            this.setSelectedConfig(formatItem, formatChar, applicator, s -> s.replace("%format%", formatName));
        }
        if (!PlayerUtil.hasPermission(player, "*") && !PlayerUtil.hasPermission(player, formatName.replace(" ", "-"))) {
            if (data.getFormat().equalsIgnoreCase(formatCode)) {
                data.setFormat("");
            }
            this.setNoPermission(formatItem, formatChar, applicator, s -> s.replace("%format%", formatName));
        }
    }

    public void setDefault(ItemStack item, String character, ConfigurationApplicator applicator, UnaryOperator<String> ... operators) {
        ItemMeta meta = item.getItemMeta();
        List<String> selected = TextUtil.color(applicator.getConfig().getStringList("items." + character + ".lore"));
        for (UnaryOperator<String> operator : operators) {
            selected.replaceAll(operator);
        }
        meta.setLore(selected);
        item.setItemMeta(meta);
    }

    private void setNoPermission(ItemStack colorButton, String color, UnaryOperator<String> ... operators) {
        List<String> noPerm = this.colorParser.getNoPermLore(color);
        noPerm.replaceAll(TextUtil::color);
        for (UnaryOperator<String> operator : operators) {
            noPerm.replaceAll(operator);
        }
        ItemMeta meta = colorButton.getItemMeta();
        meta.setLore(noPerm);
        colorButton.setItemMeta(meta);
    }

    private void setNoPermission(ItemStack displayItem, String character, ConfigurationApplicator applicator, UnaryOperator<String> ... operators) {
        List<String> noPerm = applicator.getConfig().getStringList("items." + character + ".lore-no-perm");
        noPerm.replaceAll(TextUtil::color);
        for (UnaryOperator<String> operator : operators) {
            noPerm.replaceAll(operator);
        }
        ItemMeta meta = displayItem.getItemMeta();
        meta.setLore(noPerm);
        displayItem.setItemMeta(meta);
    }

    public void setSelected(ItemStack item, String color, UnaryOperator<String> ... operators) {
        List<String> selected = this.colorParser.getSelectedLore(color);
        selected.replaceAll(TextUtil::color);
        for (UnaryOperator<String> operator : operators) {
            selected.replaceAll(operator);
        }
        ItemMeta meta = item.getItemMeta();
        meta.setLore(selected);
        item.setItemMeta(meta);
    }

    public void setSelectedConfig(ItemStack item, String character, ConfigurationApplicator applicator, UnaryOperator<String> ... operators) {
        ItemMeta meta = item.getItemMeta();
        List<String> selected = TextUtil.color(applicator.getConfig().getStringList("items." + character + ".selected"));
        for (UnaryOperator<String> operator : operators) {
            selected.replaceAll(operator);
        }
        meta.setLore(selected);
        item.setItemMeta(meta);
    }
}

