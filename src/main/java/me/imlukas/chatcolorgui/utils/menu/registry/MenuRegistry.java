package me.imlukas.chatcolorgui.utils.menu.registry;

import lombok.Getter;
import me.imlukas.chatcolorgui.ChatColorPlugin;
import me.imlukas.chatcolorgui.utils.TextUtil;
import me.imlukas.chatcolorgui.utils.menu.base.BaseMenu;
import me.imlukas.chatcolorgui.utils.menu.base.ConfigurableMenu;
import me.imlukas.chatcolorgui.utils.menu.configuration.ConfigurationApplicator;
import me.imlukas.chatcolorgui.utils.menu.layer.BaseLayer;
import me.imlukas.chatcolorgui.utils.menu.listener.MenuListener;
import me.imlukas.chatcolorgui.utils.menu.registry.meta.HiddenMenuTracker;
import me.imlukas.chatcolorgui.utils.storage.YMLBase;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;

@Getter
public class MenuRegistry {

    private final Map<String, Function<Player, BaseMenu>> menuInitializers = new ConcurrentHashMap<>();
    private final ChatColorPlugin plugin;
    private final HiddenMenuTracker hiddenMenuTracker = new HiddenMenuTracker();

    public MenuRegistry(ChatColorPlugin plugin) {
        this.plugin = plugin;

        MenuListener.register(this);
        load(new File(plugin.getDataFolder(), "menu"));
    }

    private void load(File folder) {
        folder.mkdir();

        for (File file : folder.listFiles()) {
            if (file.isDirectory()) {
                load(file);
                continue;
            }

            if (!file.getName().endsWith(".yml")) {
                continue;
            }

            boolean existsOnSource = plugin.getResource(file.getName()) != null;
            YMLBase config = new YMLBase(plugin, file, existsOnSource);

            registerConfigurable(config);
        }
    }

    public void register(String name, Function<Player, BaseMenu> initializer) {
        menuInitializers.put(name, initializer);
    }

    public void registerConfigurable(YMLBase base) {
        String name = base.getFile().getName().replace(".yml", "");
        int rows = base.getConfiguration().getInt("rows", base.getConfiguration().getInt("slots", 54) / 9);
        String title = TextUtil.color(base.getConfiguration().getString("title", name));

        register(name, player -> {
            ConfigurationApplicator applicator = new ConfigurationApplicator(base.getConfiguration());

            BaseMenu menu = new ConfigurableMenu(player.getUniqueId(), title, rows, applicator);
            BaseLayer layer = new BaseLayer(menu);

            applicator.applyConfiguration(layer);
            layer.forceUpdate();

            return menu;
        });
    }

    public Function<Player, BaseMenu> getInitializer(String name) {
        return menuInitializers.get(name);
    }

    public BaseMenu create(String name, Player player) {
        return getInitializer(name).apply(player);
    }

    public void registerPostInitTask(String name, Consumer<BaseMenu> consumer) {
        Function<Player, BaseMenu> initializer = getInitializer(name);

        register(name, player -> {
            BaseMenu menu = initializer.apply(player);

            consumer.accept(menu);
            return menu;
        });
    }

    public List<String> getMenuNames() {
        return new ArrayList<>(menuInitializers.keySet());
    }

    public void reload() {
        menuInitializers.clear();
        load(new File(plugin.getDataFolder(), "menu"));
    }

}
