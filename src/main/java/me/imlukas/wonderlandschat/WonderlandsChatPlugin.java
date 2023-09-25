/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.milkbowl.vault.chat.Chat
 *  net.milkbowl.vault.economy.Economy
 *  net.milkbowl.vault.permission.Permission
 *  org.bukkit.Bukkit
 *  org.bukkit.event.HandlerList
 *  org.bukkit.event.Listener
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.RegisteredServiceProvider
 *  org.bukkit.plugin.java.JavaPlugin
 */
package me.imlukas.wonderlandschat;

import java.util.concurrent.CompletableFuture;
import me.imlukas.wonderlandschat.command.ChatColorCommand;
import me.imlukas.wonderlandschat.command.ChatColorResetCommand;
import me.imlukas.wonderlandschat.command.ChatToggleCommand;
import me.imlukas.wonderlandschat.command.CloseInventory;
import me.imlukas.wonderlandschat.command.ReloadCommand;
import me.imlukas.wonderlandschat.data.color.ColorParser;
import me.imlukas.wonderlandschat.data.groups.GroupParser;
import me.imlukas.wonderlandschat.data.sql.SQLDatabase;
import me.imlukas.wonderlandschat.data.sql.constants.ColumnType;
import me.imlukas.wonderlandschat.data.sql.data.ColumnData;
import me.imlukas.wonderlandschat.data.sql.objects.SQLTable;
import me.imlukas.wonderlandschat.listeners.InventoryCloseListener;
import me.imlukas.wonderlandschat.listeners.PlayerJoinListener;
import me.imlukas.wonderlandschat.listeners.SendMessageListener;
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
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class WonderlandsChatPlugin
extends JavaPlugin {
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

    public void onEnable() {
        this.saveDefaultConfig();
        this.setupEconomy();
        if (!this.setupChat()) {
            System.out.println("[WonderlandsChat]  &cCould not find Vault and/or a Vault compatible permissions plugin!\"");
            Bukkit.getPluginManager().disablePlugin((Plugin)this);
            return;
        }
        if (!this.setupPermissions()) {
            System.out.println("[WonderlandsChat] &cCould not find Vault and/or a Vault compatible permissions plugin!\"");
            Bukkit.getPluginManager().disablePlugin((Plugin)this);
            return;
        }
        this.saveResource("menu/colorlist.yml", false);
        CHAT_ENABLED = this.getConfig().getBoolean("chat.enabled");
        this.colorParser = new ColorParser(this);
        this.groupParser = new GroupParser(this);
        this.sqlDatabase = new SQLDatabase(this.getConfig().getConfigurationSection("mysql"));
        this.messages = new MessagesFile(this);
        this.commandManager = new CommandManager(this);
        this.menuRegistry = new MenuRegistry(this);
        this.playerStorage = new PlayerStorage();
        this.commandManager.register(new ChatColorCommand(this));
        this.commandManager.register(new ReloadCommand(this));
        this.commandManager.register(new ChatToggleCommand(this));
        this.commandManager.register(new ChatColorResetCommand(this));
        this.commandManager.register(new CloseInventory());
        this.registerListener(new PlayerJoinListener(this));
        this.registerListener(new SendMessageListener(this));
        this.registerListener(new InventoryCloseListener(this));
        this.initSQLTables();
    }

    public void onDisable() {
        this.economy = null;
        this.chat = null;
        this.perms = null;
        this.colorParser = null;
        this.groupParser = null;
        this.sqlDatabase = null;
        this.messages = null;
        this.commandManager = null;
        this.menuRegistry = null;
        this.playerStorage = null;
        this.inventoryCloseListener = null;
        HandlerList.unregisterAll((Plugin)this);
    }

    private boolean setupChat() {
        RegisteredServiceProvider rsp = this.getServer().getServicesManager().getRegistration(Chat.class);
        this.chat = (Chat)rsp.getProvider();
        return this.chat != null;
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider rsp = this.getServer().getServicesManager().getRegistration(Permission.class);
        this.perms = (Permission)rsp.getProvider();
        return this.perms != null;
    }

    private void setupEconomy() {
        if (this.getServer().getPluginManager().getPlugin("Vault") == null) {
            return;
        }
        RegisteredServiceProvider rsp = this.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return;
        }
        this.economy = (Economy)rsp.getProvider();
    }

    private void initSQLTables() {
        SQLTable chatColorTable = this.sqlDatabase.getOrCreateTable("chatcolor");
        CompletableFuture.allOf(chatColorTable.addColumn(new ColumnData("player_id", ColumnType.VARCHAR, 36)), chatColorTable.addColumn(new ColumnData("format", ColumnType.VARCHAR, 2)), chatColorTable.addColumn(new ColumnData("color", ColumnType.VARCHAR, 6)));
    }

    private void registerListener(Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, (Plugin)this);
    }

    public Economy getEconomy() {
        return this.economy;
    }

    public Permission getPerms() {
        return this.perms;
    }

    public Chat getChat() {
        return this.chat;
    }

    public ColorParser getColorParser() {
        return this.colorParser;
    }

    public GroupParser getGroupParser() {
        return this.groupParser;
    }

    public MessagesFile getMessages() {
        return this.messages;
    }

    public CommandManager getCommandManager() {
        return this.commandManager;
    }

    public MenuRegistry getMenuRegistry() {
        return this.menuRegistry;
    }

    public PlayerStorage getPlayerStorage() {
        return this.playerStorage;
    }

    public SQLDatabase getSqlDatabase() {
        return this.sqlDatabase;
    }

    public InventoryCloseListener getInventoryCloseListener() {
        return this.inventoryCloseListener;
    }
}

