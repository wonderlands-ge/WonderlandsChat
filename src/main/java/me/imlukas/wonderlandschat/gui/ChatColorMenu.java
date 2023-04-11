package me.imlukas.wonderlandschat.gui;

import com.google.common.collect.ImmutableMap;
import me.imlukas.wonderlandschat.WonderlandsChatPlugin;
import me.imlukas.wonderlandschat.data.PlayerData;
import me.imlukas.wonderlandschat.data.color.ColorParser;
import me.imlukas.wonderlandschat.storage.PlayerStorage;
import me.imlukas.wonderlandschat.utils.TextUtil;
import me.imlukas.wonderlandschat.utils.menu.base.BaseMenu;
import me.imlukas.wonderlandschat.utils.menu.base.ConfigurableMenu;
import me.imlukas.wonderlandschat.utils.menu.button.Button;
import me.imlukas.wonderlandschat.utils.menu.configuration.ConfigurationApplicator;
import me.imlukas.wonderlandschat.utils.menu.layer.BaseLayer;
import me.imlukas.wonderlandschat.utils.storage.MessagesFile;
import me.imlukas.wonderlandschat.utils.text.Placeholder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.UnaryOperator;

import static me.imlukas.wonderlandschat.data.color.ColorParser.FORMATS;
import static me.imlukas.wonderlandschat.utils.PlayerUtil.hasPermission;

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
        ConfigurableMenu baseMenu = (ConfigurableMenu) plugin.getMenuRegistry().create("colorlist", viewer);
        ConfigurationApplicator applicator = baseMenu.getApplicator();
        BaseLayer layer = new BaseLayer(baseMenu);

        PlayerData playerData = playerStorage.getPlayerData(viewer.getUniqueId());

        applicator.registerButton(layer, "c", viewer::closeInventory);
        applicator.registerButton(layer, "ba", () -> {
            String command = applicator.getConfig().getString("items.ba.command");

            if (command == null || command.isEmpty()) {
                viewer.closeInventory();
                return;
            }

            viewer.performCommand(command);
        });

        Button resetButton = applicator.registerButton(layer, "r", () -> {
            playerData.reset();
            messages.sendMessage(viewer, "reset");
            open(viewer);
        });

        resetButton.setPlaceholders(new Placeholder<>("player", viewer.getName()));

        Button randomButton = applicator.registerButton(layer, "ra");

        randomButton.setLeftClickAction(() -> {
            if (!hasPermission(viewer, "*")) {
                if (!hasPermission(viewer, "random")) {
                    messages.sendMessage(viewer, "no-permission");
                    return;
                }
            }

            playerData.setRandomColor(!playerData.isRandomColor());

            if (playerData.isRandomColor()) {
                setSelectedConfig(randomButton.getDisplayItem(), "ra", applicator);
            }

            messages.sendMessage(viewer, "random-color", new Placeholder<>("state", playerData.isRandomColor() ? "enabled" : "disabled"));
            updateMenu(viewer, baseMenu, layer, playerData, randomButton, applicator);
        });


        if (!hasPermission(viewer, "*")) {
            if (!hasPermission(viewer, "random")) {
                setNoPermission(randomButton.getDisplayItem(), "ra", applicator);
            }
        }

        for (Map.Entry<String, String> entry : FORMATS.entrySet()) {
            String formatName = TextUtil.uncapitalize(entry.getKey());
            String formatChar = entry.getKey().substring(0, 1).toLowerCase();
            String formatCode = entry.getValue();

            Button formatButton = applicator.registerButton(layer, formatChar);

            formatButton.setClickAction((event) -> {
                if (!hasPermission(viewer, "*")) {
                    if (!hasPermission(viewer, formatName.replace(" ", "-"))) {
                        messages.sendMessage(viewer, "no-permission");
                        return;
                    }
                }

                playerData.setFormat(formatCode);

                messages.sendMessage(viewer, "format-set", new Placeholder<>("format", TextUtil.colorAndCapitalize(entry.getValue()
                        + entry.getKey())));
                updateMenu(viewer, baseMenu, layer, playerData, randomButton, applicator);
            });

            updateFormat(viewer, playerData, applicator, formatButton.getDisplayItem(), formatName, formatChar, formatCode);
        }

        updateMenu(viewer, baseMenu, layer, playerData, randomButton, applicator);

        baseMenu.addRenderable(layer);
        baseMenu.forceUpdate();
        baseMenu.open();
    }

    private void updateMenu(Player player, BaseMenu baseMenu, BaseLayer layer, PlayerData data, Button randomButton, ConfigurationApplicator applicator) {
        baseMenu.clearElements();
        ItemStack item = applicator.getItem("ra");
        randomButton.setDisplayItem(item.clone());
        for (Map.Entry<String, ItemStack> colorEntry : colorParser.getColorsMap().entrySet()) {

            String colorName = colorEntry.getKey();
            String itemDisplayName = colorParser.getDisplayName(colorName);
            String colorChar = colorParser.getDisplayColor(colorName);
            int slot = colorParser.getSlot(colorName);

            List<Placeholder<Player>> placeholderList = new ArrayList<>();
            placeholderList.add(new Placeholder<>("color", TextUtil.colorAndCapitalize(colorChar
                    + itemDisplayName)));

            ItemStack colorItem = colorEntry.getValue();
            Button colorButton = new Button(colorItem.clone());
            colorButton.setItemPlaceholders(placeholderList);

            colorButton.setClickAction((event) -> {
                if (!hasPermission(player, "*")) {
                    if (!hasPermission(player, colorName)) {
                        messages.sendMessage(player, "no-permission");
                        return;
                    }
                }

                data.setRandomColor(false);
                data.setColor(colorChar);

                messages.sendMessage(player, "color-set", placeholderList);
                updateMenu(player, baseMenu, layer, data, randomButton, applicator);
            });

            updateColor(player, data, applicator, colorButton.getDisplayItem(), randomButton.getDisplayItem(), colorName);

            baseMenu.setElement(slot, colorButton);
            baseMenu.forceUpdate();
        }

        for (Map.Entry<String, String> entry : FORMATS.entrySet()) {
            String formatName = entry.getKey().toLowerCase(Locale.ROOT);
            String formatChar = entry.getKey().substring(0, 1).toLowerCase();
            String formatCode = entry.getValue();

            Button formatButton = applicator.registerButton(layer, formatChar);

            formatButton.setClickAction((event) -> {
                if (!hasPermission(player, "*")) {
                    if (!hasPermission(player, formatName.replace(" ", "-"))) {
                        messages.sendMessage(player, "no-permission");
                        return;
                    }
                }

                data.setFormat(formatCode);

                messages.sendMessage(player, "format-set", new Placeholder<>("format", TextUtil.colorAndCapitalize(entry.getValue()
                        + entry.getKey())));
                updateMenu(player, baseMenu, layer, data, randomButton, applicator);
            });

            updateFormat(player, data, applicator, formatButton.getDisplayItem(), formatName, formatChar, formatCode);
        }

        baseMenu.forceUpdate();
    }

    private void updateColor(Player player, PlayerData data, ConfigurationApplicator applicator, ItemStack displayItem, ItemStack randomDisplayItem, String colorName) {
        String displayName = colorParser.getDisplayName(colorName);
        String colorChar = colorParser.getDisplayColor(colorName);
        if (!hasPermission(player, "*")) {
            if (!hasPermission(player, colorName)) {
                setNoPermission(displayItem, colorName, s -> s.replace("%color%", displayName));
            }
        }

        if (data.getColor().equalsIgnoreCase(colorChar)) {
            setSelected(displayItem, colorName, s -> s.replace("%color%", displayName));
        } else if (data.isRandomColor()) {
            setSelectedConfig(randomDisplayItem, "ra", applicator);
        }
    }

    private void updateFormat(Player player, PlayerData data, ConfigurationApplicator applicator, ItemStack formatItem, String formatName, String formatChar, String formatCode) {
        setDefault(formatItem, formatChar, applicator, s -> s.replace("%format%", formatName));

        if (data.getFormat().equalsIgnoreCase(formatCode)) {
            setSelectedConfig(formatItem, formatChar, applicator, s -> s.replace("%format%", formatName));
        }

        if (!hasPermission(player, "*")) {
            if (!hasPermission(player, formatName.replace(" ", "-"))) {
                if (data.getFormat().equalsIgnoreCase(formatCode)) {
                    data.setFormat("");
                }
                setNoPermission(formatItem, formatChar, applicator, s -> s.replace("%format%", formatName));
            }
        }
    }

    public void setDefault(ItemStack item, String character, ConfigurationApplicator applicator, UnaryOperator<String>... operators) {
        ItemMeta meta = item.getItemMeta();

        List<String> selected = TextUtil.color(applicator.getConfig().getStringList("items." + character + ".lore"));

        for (UnaryOperator<String> operator : operators) {
            selected.replaceAll(operator);
        }

        meta.setLore(selected);
        item.setItemMeta(meta);
    }

    private void setNoPermission(ItemStack colorButton, String color, UnaryOperator<String>... operators) {
        List<String> noPerm = colorParser.getNoPermLore(color);

        noPerm.replaceAll(TextUtil::color);
        for (UnaryOperator<String> operator : operators) {
            noPerm.replaceAll(operator);
        }

        ItemMeta meta = colorButton.getItemMeta();
        meta.setLore(noPerm);
        colorButton.setItemMeta(meta);
    }

    private void setNoPermission(ItemStack displayItem, String character, ConfigurationApplicator applicator, UnaryOperator<String>... operators) {
        List<String> noPerm = applicator.getConfig().getStringList("items." + character + ".lore-no-perm");

        noPerm.replaceAll(TextUtil::color);
        for (UnaryOperator<String> operator : operators) {
            noPerm.replaceAll(operator);
        }

        ItemMeta meta = displayItem.getItemMeta();
        meta.setLore(noPerm);
        displayItem.setItemMeta(meta);
    }

    public void setSelected(ItemStack item, String color, UnaryOperator<String>... operators) {
        List<String> selected = colorParser.getSelectedLore(color);
        selected.replaceAll(TextUtil::color);
        for (UnaryOperator<String> operator : operators) {
            selected.replaceAll(operator);
        }

        ItemMeta meta = item.getItemMeta();

        meta.setLore(selected);
        item.setItemMeta(meta);
    }

    public void setSelectedConfig(ItemStack item, String character, ConfigurationApplicator applicator, UnaryOperator<String>... operators) {
        ItemMeta meta = item.getItemMeta();

        List<String> selected = TextUtil.color(applicator.getConfig().getStringList("items." + character + ".selected"));

        for (UnaryOperator<String> operator : operators) {
            selected.replaceAll(operator);
        }

        meta.setLore(selected);
        item.setItemMeta(meta);
    }
}
