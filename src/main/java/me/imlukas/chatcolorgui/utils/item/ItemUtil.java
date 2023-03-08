package me.imlukas.chatcolorgui.utils.item;

import me.imlukas.chatcolorgui.utils.text.Placeholder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public final class ItemUtil {

    private ItemUtil() {

    }

    public static void give(Player player, ItemStack item) {
        PlayerInventory inv = player.getInventory();

        for (Map.Entry<Integer, ItemStack> entry : inv.addItem(item).entrySet()) {
            ItemStack copy = entry.getValue().clone();
            item.setAmount(entry.getKey());
            player.getWorld().dropItemNaturally(player.getLocation(), copy);
        }
    }

    public static <T> void replacePlaceholder(ItemStack item, T replacementObject,
                                              Collection<Placeholder<T>> placeholderCollection) {
        if (item == null || item.getItemMeta() == null) {
            return;
        }

        if (placeholderCollection == null || placeholderCollection.isEmpty()) {
            return;
        }

        Placeholder<T>[] placeholders = new Placeholder[placeholderCollection.size()];

        int index = 0;
        for (Placeholder<T> placeholder : placeholderCollection) {
            if (placeholder == null) {
                continue;
            }

            placeholders[index++] = placeholder;
        }

        // shrink array to fit
        if (index != placeholders.length) {
            Placeholder<T>[] newPlaceholders = new Placeholder[index];
            System.arraycopy(placeholders, 0, newPlaceholders, 0, index);
            placeholders = newPlaceholders;
        }

        replacePlaceholder(item, replacementObject, placeholders);
    }

    @SafeVarargs
    public static synchronized <T> void replacePlaceholder(ItemStack item, T replacementObject,
                                                           Placeholder<T>... placeholder) {
        if (item == null) {
            return;
        }

        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            return;
        }

        if (meta.hasDisplayName()) {
            String displayName = meta.getDisplayName();

            for (Placeholder<T> placeholder1 : placeholder) {
                displayName = placeholder1.replace(displayName, replacementObject);
            }

            meta.setDisplayName(displayName);
        }

        if (meta.hasLore()) {
            List<String> lore = meta.getLore();

            for (int index = 0; index < lore.size(); index++) {
                String line = lore.get(index);

                for (Placeholder<T> placeholder1 : placeholder) {
                    line = placeholder1.replace(line, replacementObject);
                }

                lore.set(index, line);
            }

            meta.setLore(lore);
        }
        item.setItemMeta(meta);
    }
}
