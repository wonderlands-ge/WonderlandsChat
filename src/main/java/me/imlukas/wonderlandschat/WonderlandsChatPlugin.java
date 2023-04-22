package me.imlukas.wonderlandschat;

import lombok.Getter;
import me.imlukas.wonderlandschat.command.*;
import me.imlukas.wonderlandschat.data.color.ColorParser;
import me.imlukas.wonderlandschat.data.groups.GroupParser;
import me.imlukas.wonderlandschat.data.sql.SQLDatabase;
import me.imlukas.wonderlandschat.data.sql.constants.ColumnType;
import me.imlukas.wonderlandschat.data.sql.data.ColumnData;
import me.imlukas.wonderlandschat.data.sql.objects.SQLTable;
import me.imlukas.wonderlandschat.listeners.*;
import me.imlukas.wonderlandschat.storage.PlayerStorage;
import me.imlukas.wonderlandschat.utils.command.impl.CommandManager;
import me.imlukas.wonderlandschat.utils.menu.registry.MenuRegistry;
import me.imlukas.wonderlandschat.utils.storage.MessagesFile;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.CompletableFuture;

@Getter
public final class WonderlandsChatPlugin extends JavaPlugin {

    private Economy economy;
    private Permission perms;
    private Chat chat;

    private ColorParser colorParser;
    private GroupParser groupParser;
    private MessagesFile messages;
    private CommandManager commandManager;
    private MenuRegistry menuRegistry;

    private PlayerStorage playerStorage;
    private SQLDatabase sqlDatabase;

    private InventoryCloseListener inventoryCloseListener;

    public static boolean CHAT_ENABLED;

    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();
        setupEconomy();

        if (!setupChat()) {
            System.out.println("[WonderlandsChat]  &cCould not find Vault and/or a Vault compatible permissions plugin!\"");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        if (!setupPermissions()) {
            System.out.println("[WonderlandsChat] &cCould not find Vault and/or a Vault compatible permissions plugin!\"");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        saveResource("menu/colorlist.yml", false);

        CHAT_ENABLED = getConfig().getBoolean("chat.enabled");

        colorParser = new ColorParser(this);
        groupParser = new GroupParser(this);

        sqlDatabase = new SQLDatabase(getConfig().getConfigurationSection("mysql"));

        messages = new MessagesFile(this);
        commandManager = new CommandManager(this);
        menuRegistry = new MenuRegistry(this);

        playerStorage = new PlayerStorage();

        commandManager.register(new ChatColorCommand(this));
        commandManager.register(new ReloadCommand(this));
        commandManager.register(new ChatToggleCommand(this));
        commandManager.register(new ChatColorResetCommand(this));
        commandManager.register(new CloseInventory());

        registerListener(new PlayerJoinListener(this));
        registerListener(new SendMessageListener(this));
        registerListener(new InventoryCloseListener(this));

        initSQLTables();
    }


    @Override
    public void onDisable() {
        economy = null;
        chat = null;
        perms = null;

        colorParser = null;
        groupParser = null;
        sqlDatabase = null;
        messages = null;
        commandManager = null;
        menuRegistry = null;
        playerStorage = null;
        inventoryCloseListener = null;

        HandlerList.unregisterAll(this);
    }

    private boolean setupChat() {
        RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
        chat = rsp.getProvider();
        return chat != null;
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }


    private void setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return;
        }
        economy = rsp.getProvider();
    }


    private void initSQLTables() {
        SQLTable chatColorTable = sqlDatabase.getOrCreateTable("chatcolor");

        CompletableFuture.allOf(
                chatColorTable.addColumn(new ColumnData("player_id", ColumnType.VARCHAR, 36)),
                chatColorTable.addColumn(new ColumnData("format", ColumnType.VARCHAR, 2)),
                chatColorTable.addColumn(new ColumnData("color", ColumnType.VARCHAR, 6))
        );
    }

    private void registerListener(Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, this);
    }
}
