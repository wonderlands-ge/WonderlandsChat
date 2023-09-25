/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 */
package me.imlukas.wonderlandschat.utils.menu.registry;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import me.imlukas.wonderlandschat.WonderlandsChatPlugin;
import me.imlukas.wonderlandschat.utils.TextUtil;
import me.imlukas.wonderlandschat.utils.menu.base.BaseMenu;
import me.imlukas.wonderlandschat.utils.menu.base.ConfigurableMenu;
import me.imlukas.wonderlandschat.utils.menu.configuration.ConfigurationApplicator;
import me.imlukas.wonderlandschat.utils.menu.layer.BaseLayer;
import me.imlukas.wonderlandschat.utils.menu.listener.MenuListener;
import me.imlukas.wonderlandschat.utils.menu.registry.meta.HiddenMenuTracker;
import me.imlukas.wonderlandschat.utils.storage.YMLBase;
import org.bukkit.entity.Player;

public class MenuRegistry {
    private final Map<String, Function<Player, BaseMenu>> menuInitializers = new ConcurrentHashMap<String, Function<Player, BaseMenu>>();
    private final WonderlandsChatPlugin plugin;
    private final HiddenMenuTracker hiddenMenuTracker = new HiddenMenuTracker();

    public MenuRegistry(WonderlandsChatPlugin plugin) {
        this.plugin = plugin;
        MenuListener.register(this);
        this.load(new File(plugin.getDataFolder(), "menu"));
    }

    private void load(File folder) {
        folder.mkdir();
        for (File file : folder.listFiles()) {
            if (file.isDirectory()) {
                this.load(file);
                continue;
            }
            if (!file.getName().endsWith(".yml")) continue;
            boolean existsOnSource = this.plugin.getResource(file.getName()) != null;
            YMLBase config = new YMLBase(this.plugin, file, existsOnSource);
            this.registerConfigurable(config);
        }
    }

    public void register(String name, Function<Player, BaseMenu> initializer) {
        this.menuInitializers.put(name, initializer);
    }

    public void registerConfigurable(YMLBase base) {
        String name = base.getFile().getName().replace(".yml", "");
        int rows = base.getConfiguration().getInt("rows", base.getConfiguration().getInt("slots", 54) / 9);
        String title = TextUtil.color(base.getConfiguration().getString("title", name));
        this.register(name, player -> {
            ConfigurationApplicator applicator = new ConfigurationApplicator(base.getConfiguration());
            ConfigurableMenu menu = new ConfigurableMenu(player.getUniqueId(), title, rows, applicator);
            BaseLayer layer = new BaseLayer(menu);
            applicator.applyConfiguration(layer);
            layer.forceUpdate();
            return menu;
        });
    }

    public Function<Player, BaseMenu> getInitializer(String name) {
        return this.menuInitializers.get(name);
    }

    public BaseMenu create(String name, Player player) {
        return this.getInitializer(name).apply(player);
    }

    public void registerPostInitTask(String name, Consumer<BaseMenu> consumer) {
        Function<Player, BaseMenu> initializer = this.getInitializer(name);
        this.register(name, player -> {
            BaseMenu menu = (BaseMenu)initializer.apply((Player)player);
            consumer.accept(menu);
            return menu;
        });
    }

    public List<String> getMenuNames() {
        return new ArrayList<String>(this.menuInitializers.keySet());
    }

    public void reload() {
        this.menuInitializers.clear();
        this.load(new File(this.plugin.getDataFolder(), "menu"));
    }

    public Map<String, Function<Player, BaseMenu>> getMenuInitializers() {
        return this.menuInitializers;
    }

    public WonderlandsChatPlugin getPlugin() {
        return this.plugin;
    }

    public HiddenMenuTracker getHiddenMenuTracker() {
        return this.hiddenMenuTracker;
    }
}

