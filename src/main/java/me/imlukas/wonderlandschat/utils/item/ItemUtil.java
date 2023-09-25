/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  com.mojang.authlib.properties.Property
 *  com.mojang.authlib.properties.PropertyMap
 *  org.bukkit.enchantments.Enchantment
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemFlag
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.PlayerInventory
 *  org.bukkit.inventory.meta.ItemMeta
 *  org.bukkit.inventory.meta.SkullMeta
 */
package me.imlukas.wonderlandschat.utils.item;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import me.imlukas.wonderlandschat.utils.text.Placeholder;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public final class ItemUtil {
    private ItemUtil() {
    }

    public static void give(Player player, ItemStack item) {
        PlayerInventory inv = player.getInventory();
        for (Map.Entry entry : inv.addItem(new ItemStack[]{item}).entrySet()) {
            ItemStack copy = ((ItemStack)entry.getValue()).clone();
            item.setAmount(((Integer)entry.getKey()).intValue());
            player.getWorld().dropItemNaturally(player.getLocation(), copy);
        }
    }

    public static <T> void replacePlaceholder(ItemStack item, T replacementObject, Collection<Placeholder<T>> placeholderCollection) {
        if (item == null || item.getItemMeta() == null) {
            return;
        }
        if (placeholderCollection == null || placeholderCollection.isEmpty()) {
            return;
        }
        Placeholder[] placeholders = new Placeholder[placeholderCollection.size()];
        int index = 0;
        for (Placeholder<T> placeholder : placeholderCollection) {
            if (placeholder == null) continue;
            placeholders[index++] = placeholder;
        }
        if (index != placeholders.length) {
            Placeholder[] newPlaceholders = new Placeholder[index];
            System.arraycopy(placeholders, 0, newPlaceholders, 0, index);
            placeholders = newPlaceholders;
        }
        ItemUtil.replacePlaceholder(item, replacementObject, placeholders);
    }

    @SafeVarargs
    public static synchronized <T> void replacePlaceholder(ItemStack item, T replacementObject, Placeholder<T> ... placeholder) {
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
            List lore = meta.getLore();
            for (int index = 0; index < lore.size(); ++index) {
                String line = (String)lore.get(index);
                Placeholder<T>[] arrplaceholder = placeholder;
                int placeholder1 = arrplaceholder.length;
                for (int i = 0; i < placeholder1; ++i) {
                    Placeholder<T> placeholder12 = arrplaceholder[i];
                    line = placeholder12.replace(line, replacementObject);
                }
                lore.set(index, line);
            }
            meta.setLore(lore);
        }
        if (meta instanceof SkullMeta) {
            SkullMeta skullMeta = (SkullMeta)meta;
            boolean replaceProfile = true;
            if (skullMeta.hasOwner()) {
                String owner = skullMeta.getOwner();
                for (Placeholder<T> placeholder12 : placeholder) {
                    String oldOwner = owner;
                    if ((owner = placeholder12.replace(owner, replacementObject)) == null || owner.equals(oldOwner)) continue;
                    skullMeta.setOwner(owner);
                    replaceProfile = false;
                }
            }
            if (replaceProfile) {
                try {
                    Field profileField = skullMeta.getClass().getDeclaredField("profile");
                    profileField.setAccessible(true);
                    GameProfile profile = (GameProfile)profileField.get((Object)skullMeta);
                    profileField.setAccessible(false);
                    if (profile != null) {
                        PropertyMap propertyMap = profile.getProperties();
                        HashSet<Property> properties = new HashSet<>(propertyMap.get("textures"));
                        propertyMap.removeAll("properties");
                        Iterator<Property> iterator = properties.iterator();
                        if (iterator.hasNext()) {
                            Property property = iterator.next();
                            String value = property.getValue();
                            String signature = property.getSignature();
                            for (Placeholder<T> placeholder1 : placeholder) {
                                value = placeholder1.replace(value, replacementObject);
                                signature = placeholder1.replace(signature, replacementObject);
                            }
                            propertyMap.put("textures", new Property("textures", value, null));
                        }
                    }
                }
                catch (IllegalAccessException | NoSuchFieldException e) {
                    e.printStackTrace();
                }
            }
        }
        item.setItemMeta(meta);
    }

    public static ItemStack setGlowing(ItemStack displayItem, boolean glowing) {
        if (!glowing) {
            displayItem.removeEnchantment(Enchantment.LUCK);
            return displayItem;
        }
        ItemMeta meta = displayItem.getItemMeta();
        meta.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ENCHANTS});
        meta.addEnchant(Enchantment.LUCK, 123, true);
        displayItem.setItemMeta(meta);
        return displayItem;
    }
}

