package me.imlukas.chatcolorgui.utils.item;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import me.imlukas.chatcolorgui.utils.TextUtil;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

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
            configurableValues.put("unbreakable",
                    ItemBuilder.class.getMethod("unbreakable", boolean.class), "getBoolean");
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
    private boolean unbreakable;

    private int modelData = -1;

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

    public ItemBuilder modelData(int modelData) {
        this.modelData = modelData;
        return this;
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

    public ItemBuilder unbreakable(boolean unbreakable) {
        this.unbreakable = unbreakable;
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

        meta.spigot().setUnbreakable(unbreakable);

        if (!enchantments.isEmpty()) {
            for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
                meta.addEnchant(entry.getKey(), entry.getValue(), true);
            }
        }

        item.setItemMeta(meta);
        return item;
    }

}