/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.HashBasedTable
 *  com.google.common.collect.Table
 *  com.mojang.authlib.GameProfile
 *  com.mojang.authlib.properties.Property
 *  com.mojang.authlib.properties.PropertyMap
 *  org.apache.commons.lang.Validate
 *  org.bukkit.Material
 *  org.bukkit.configuration.ConfigurationSection
 *  org.bukkit.enchantments.Enchantment
 *  org.bukkit.inventory.ItemFlag
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 *  org.bukkit.inventory.meta.SkullMeta
 */
package me.imlukas.wonderlandschat.utils.item;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import me.imlukas.wonderlandschat.utils.TextUtil;
import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class ItemBuilder {
    private static final Table<String, Method, String> configurableValues = HashBasedTable.create();
    private final Material material;
    private final List<String> lore = new ArrayList<String>();
    private final Map<Enchantment, Integer> enchantments = new HashMap<Enchantment, Integer>();
    private int amount = 1;
    private String name = "";
    private List<ItemFlag> itemFlags = new ArrayList<ItemFlag>();
    private short data = (short)-1;
    private boolean glowing;
    private String skullName = null;
    private String skullHash = null;

    public ItemBuilder(Material material) {
        this.material = material;
    }

    public static ItemStack fromSection(ConfigurationSection section) {
        ItemBuilder builder = new ItemBuilder(Material.valueOf((String)section.getString(section.contains("material") ? "material" : "type")));
        configurableValues.cellSet().forEach(cell -> {
            String id = (String)cell.getRowKey();
            Method method = (Method)cell.getColumnKey();
            String mName = (String)cell.getValue();
            if (section.contains(id)) {
                try {
                    method.invoke(builder, section.getClass().getMethod(mName, String.class).invoke((Object)section, id));
                }
                catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
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
            SkullMeta skullMeta = (SkullMeta)meta;
            if (skullMeta.hasOwner()) {
                builder.skull(skullMeta.getOwner());
            } else {
                builder.skullHash(ItemBuilder.getSkullHash(original));
            }
        }
        return builder;
    }

    private static String getSkullHash(ItemStack item) {
        try {
            Iterator<Property> iterator;
            Field profileField = item.getItemMeta().getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            GameProfile profile = (GameProfile)profileField.get(item.getItemMeta());
            PropertyMap propertyMap = profile.getProperties();
            if (propertyMap.containsKey("textures") && (iterator = propertyMap.get("textures").iterator()).hasNext()) {
                Property property = iterator.next();
                return property.getValue();
            }
        }
        catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ItemBuilder glowing(boolean glowing) {
        this.glowing = glowing;
        this.itemFlags.add(ItemFlag.HIDE_ENCHANTS);
        return this;
    }

    public ItemBuilder amount(int amount) {
        this.amount = amount;
        return this;
    }

    public ItemBuilder data(int num) {
        this.data = (short)num;
        return this;
    }

    public ItemBuilder lore(List<String> lore) {
        for (String s : lore) {
            this.lore.add(TextUtil.color(s));
        }
        return this;
    }

    public ItemBuilder lore(String ... lore) {
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
            Enchantment enchant = Enchantment.getByName((String)name);
            this.enchantments.put(enchant, level);
        }
        return this;
    }

    public ItemBuilder flags(ItemFlag ... flags) {
        this.itemFlags.addAll(Arrays.asList(flags));
        return this;
    }

    public ItemBuilder flags(List<String> flags) {
        ItemFlag[] array = new ItemFlag[flags.size()];
        for (int index = 0; index < array.length; ++index) {
            array[index] = ItemFlag.valueOf((String)flags.get(index));
        }
        return this.flags(array);
    }

    public ItemBuilder skull(String name) {
        Validate.isTrue((this.material.name().contains("SKULL") || this.material.name().contains("HEAD") ? 1 : 0) != 0, (String)"Attempt to set skull data on non skull item");
        this.skullName = name;
        this.data(3);
        return this;
    }

    public ItemBuilder skullHash(String hash) {
        Validate.isTrue((this.material.name().contains("SKULL") || this.material.name().contains("HEAD") ? 1 : 0) != 0, (String)"Attempt to set skull data on non skull item");
        this.skullHash = hash;
        this.data(3);
        return this;
    }

    public ItemBuilder clone() {
        ItemBuilder newBuilder = new ItemBuilder(this.material);
        newBuilder.data = this.data;
        newBuilder.lore.clear();
        newBuilder.lore.addAll(this.lore);
        newBuilder.name = this.name;
        newBuilder.skullName = this.skullName;
        newBuilder.skullHash = this.skullHash;
        newBuilder.itemFlags = this.itemFlags;
        return newBuilder;
    }

    public ItemStack build() {
        if (this.amount > 64) {
            this.amount = 64;
        }
        ItemStack item = new ItemStack(this.material, this.amount);
        if (this.data != -1) {
            item.setDurability(this.data);
        }
        ItemMeta meta = item.getItemMeta();
        if (this.name != null && !this.name.isEmpty()) {
            meta.setDisplayName(this.name);
        }
        if (!this.lore.isEmpty()) {
            meta.setLore(this.lore);
        }
        if (this.itemFlags != null) {
            meta.addItemFlags(this.itemFlags.toArray(new ItemFlag[0]));
        }
        if (this.skullName != null) {
            ((SkullMeta)meta).setOwner(this.skullName);
        }
        if (this.glowing && this.enchantments.isEmpty()) {
            meta.addEnchant(Enchantment.LUCK, 123, true);
        }
        if (!this.enchantments.isEmpty()) {
            for (Map.Entry<Enchantment, Integer> entry : this.enchantments.entrySet()) {
                meta.addEnchant(entry.getKey(), entry.getValue().intValue(), true);
            }
        }
        if (this.skullHash != null) {
            GameProfile gameProfile = new GameProfile(UUID.randomUUID(), "");
            PropertyMap propertyMap = gameProfile.getProperties();
            propertyMap.put("textures", new Property("textures", this.skullHash));
            SkullMeta skullMeta = (SkullMeta)meta;
            try {
                Field profileField = skullMeta.getClass().getDeclaredField("profile");
                profileField.setAccessible(true);
                profileField.set(skullMeta, gameProfile);
                profileField.setAccessible(false);
            }
            catch (IllegalAccessException | NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
        item.setItemMeta(meta);
        return item;
    }

    static {
        try {
            configurableValues.put("data", ItemBuilder.class.getMethod("data", Integer.TYPE), "getInt");
            configurableValues.put("name", ItemBuilder.class.getMethod("name", String.class), "getString");
            configurableValues.put("amount", ItemBuilder.class.getMethod("amount", Integer.TYPE), "getInt");
            configurableValues.put("lore", ItemBuilder.class.getMethod("lore", List.class), "getStringList");
            configurableValues.put("glow", ItemBuilder.class.getMethod("glowing", Boolean.TYPE), "getBoolean");
            configurableValues.put("enchants", ItemBuilder.class.getMethod("enchants", ConfigurationSection.class), "getConfigurationSection");
            configurableValues.put("flags", ItemBuilder.class.getMethod("flags", List.class), "getStringList");
            configurableValues.put("skull-hash", ItemBuilder.class.getMethod("skullHash", String.class), "getString");
            configurableValues.put("skull", ItemBuilder.class.getMethod("skull", String.class), "getString");
        }
        catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
}

