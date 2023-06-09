package me.imlukas.wonderlandschat.utils.item;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import me.imlukas.wonderlandschat.utils.TextUtil;
import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class ItemBuilder {

    private static final Table<String, Method, String> configurableValues = HashBasedTable.create();

    static {
        try {
            configurableValues.put("data", ItemBuilder.class.getMethod("data", int.class),
                    "getInt");
            configurableValues.put("name", ItemBuilder.class.getMethod("name", String.class),
                    "getString");
            configurableValues.put("amount", ItemBuilder.class.getMethod("amount", int.class),
                    "getInt");
            configurableValues.put("lore", ItemBuilder.class.getMethod("lore", List.class),
                    "getStringList");
            configurableValues.put("glow", ItemBuilder.class.getMethod("glowing", boolean.class),
                    "getBoolean");
            configurableValues.put("enchants",
                    ItemBuilder.class.getMethod("enchants", ConfigurationSection.class),
                    "getConfigurationSection");
            configurableValues.put("flags", ItemBuilder.class.getMethod("flags", List.class),
                    "getStringList");
            configurableValues.put("skull-hash",
                    ItemBuilder.class.getMethod("skullHash", String.class),
                    "getString");
            configurableValues.put("skull", ItemBuilder.class.getMethod("skull", String.class),
                    "getString");
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    private final Material material;
    private final List<String> lore = new ArrayList<>();
    private final Map<Enchantment, Integer> enchantments = new HashMap<>();
    private int amount = 1;
    private String name = "";
    private List<ItemFlag> itemFlags = new ArrayList<>();
    private short data = -1;
    private boolean glowing;

    private String skullName = null;
    private String skullHash = null;

    public ItemBuilder(Material material) {
        this.material = material;
    }

    public static ItemStack fromSection(ConfigurationSection section) {
        ItemBuilder builder = new ItemBuilder(
                Material.valueOf(
                        section.getString(section.contains("material") ? "material" : "type")));

        configurableValues.cellSet().forEach(cell -> {
            String id = cell.getRowKey();
            Method method = cell.getColumnKey();
            String mName = cell.getValue();

            if (section.contains(id)) {
                try {
                    method.invoke(builder,
                            section.getClass().getMethod(mName, String.class).invoke(section, id));
                } catch (IllegalAccessException | InvocationTargetException |
                         NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }
        });

        return builder.build();
    }

    public static ItemBuilder fromItem(ItemStack original) {
        Material material = original.getType();
        ItemBuilder builder = new ItemBuilder(material);

        ItemMeta meta = original.getItemMeta();

        builder.amount(original.getAmount());

        if (meta.hasDisplayName()) {
            builder.name(meta.getDisplayName());
        }

        if (meta.hasLore()) {
            builder.lore(meta.getLore());
        }

        if (meta.hasEnchants()) {
            builder.enchantments.putAll(meta.getEnchants());
        }

        builder.itemFlags.addAll(meta.getItemFlags());

        if (meta instanceof SkullMeta) {
            SkullMeta skullMeta = (SkullMeta) meta;

            if (skullMeta.hasOwner()) {
                builder.skull(skullMeta.getOwner());
            } else {
                builder.skullHash(getSkullHash(original));
            }
        }

        return builder;
    }

    private static String getSkullHash(ItemStack item) {
        try {
            Field profileField = item.getItemMeta().getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            GameProfile profile = (GameProfile) profileField.get(item.getItemMeta());
            PropertyMap propertyMap = profile.getProperties();
            if (propertyMap.containsKey("textures")) {
                for (Property property : propertyMap.get("textures")) {
                    return property.getValue();
                }
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ItemBuilder glowing(boolean glowing) {
        this.glowing = glowing;
        itemFlags.add(ItemFlag.HIDE_ENCHANTS);
        return this;
    }

    public ItemBuilder amount(int amount) {
        this.amount = amount;
        return this;
    }

    public ItemBuilder data(int num) {
        this.data = (short) num;
        return this;
    }
    public ItemBuilder lore(List<String> lore) {
        for (String s : lore) {
            this.lore.add(TextUtil.color(s));
        }
        return this;
    }

    public ItemBuilder lore(String... lore) {
        for (String s : lore) {
            this.lore.add(TextUtil.color(s));
        }
        return this;
    }

    public ItemBuilder name(String name) {
        this.name = TextUtil.color(name);
        return this;
    }

    public ItemBuilder enchants(ConfigurationSection section) {
        for (String str : section.getKeys(false)) {
            String name = section.getString(str + ".enchant-type");
            int level = section.getInt(str + ".level");

            Enchantment enchant = Enchantment.getByName(name);

            enchantments.put(enchant, level);
        }
        return this;
    }

    public ItemBuilder flags(ItemFlag... flags) {
        this.itemFlags.addAll(Arrays.asList(flags));
        return this;
    }

    public ItemBuilder flags(List<String> flags) {
        ItemFlag[] array = new ItemFlag[flags.size()];
        for (int index = 0; index < array.length; index++) {
            array[index] = ItemFlag.valueOf(flags.get(index));
        }

        return flags(array);
    }

    public ItemBuilder skull(String name) {
        Validate.isTrue(material.name().contains("SKULL") || material.name().contains("HEAD"),
                "Attempt to set skull data on non skull item");
        this.skullName = name;
        data(3);
        return this;
    }

    public ItemBuilder skullHash(String hash) {
        Validate.isTrue(material.name().contains("SKULL") || material.name().contains("HEAD"),
                "Attempt to set skull data on non skull item");
        this.skullHash = hash;
        data(3);
        return this;
    }

    public ItemBuilder clone() {
        ItemBuilder newBuilder = new ItemBuilder(material);
        newBuilder.data = data;
        newBuilder.lore.clear();
        newBuilder.lore.addAll(lore);
        newBuilder.name = name;
        newBuilder.skullName = skullName;
        newBuilder.skullHash = skullHash;
        newBuilder.itemFlags = itemFlags;
        return newBuilder;

    }

    public ItemStack build() {
        if (amount > 64) {
            amount = 64;
        }

        ItemStack item = new ItemStack(material, amount);

        if (data != -1) {
            item.setDurability(data);
        }

        ItemMeta meta = item.getItemMeta();

        if (name != null && !name.isEmpty()) {
            meta.setDisplayName(name);
        }

        if (!lore.isEmpty()) {
            meta.setLore(lore);
        }

        if (itemFlags != null) {
            meta.addItemFlags(itemFlags.toArray(new ItemFlag[]{}));
        }

        if (skullName != null) {
            ((SkullMeta) meta).setOwner(skullName);
        }

        if (glowing && enchantments.isEmpty()) {
            meta.addEnchant(Enchantment.LUCK, 123, true);
        }

        if (!enchantments.isEmpty()) {
            for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
                meta.addEnchant(entry.getKey(), entry.getValue(), true);
            }
        }

        if (skullHash != null) {
            GameProfile gameProfile = new GameProfile(new UUID(skullHash.hashCode(), skullHash.hashCode()), null);
            PropertyMap propertyMap = gameProfile.getProperties();
            propertyMap.put("textures", new Property("Value", skullHash));

            SkullMeta skullMeta = (SkullMeta) meta;

            try {
                Field profileField = skullMeta.getClass().getDeclaredField("profile");
                profileField.setAccessible(true);
                profileField.set(skullMeta, gameProfile);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        item.setItemMeta(meta);
        return item;
    }
}
