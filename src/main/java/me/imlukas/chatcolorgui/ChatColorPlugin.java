package me.imlukas.chatcolorgui;

import lombok.Getter;
import me.imlukas.chatcolorgui.command.ChatColorCommand;
import me.imlukas.chatcolorgui.command.ChatColorResetCommand;
import me.imlukas.chatcolorgui.data.color.ColorParser;
import me.imlukas.chatcolorgui.data.sql.SQLDatabase;
import me.imlukas.chatcolorgui.data.sql.constants.ColumnType;
import me.imlukas.chatcolorgui.data.sql.data.ColumnData;
import me.imlukas.chatcolorgui.data.sql.objects.SQLTable;
import me.imlukas.chatcolorgui.listeners.PlayerJoinListener;
import me.imlukas.chatcolorgui.listeners.PlayerQuitListener;
import me.imlukas.chatcolorgui.listeners.SendMessageListener;
import me.imlukas.chatcolorgui.storage.PlayerStorage;
import me.imlukas.chatcolorgui.utils.command.impl.CommandManager;
import me.imlukas.chatcolorgui.utils.menu.registry.MenuRegistry;
import me.imlukas.chatcolorgui.utils.storage.MessagesFile;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.CompletableFuture;

@Getter
public final class ChatColorPlugin extends JavaPlugin {

    private ColorParser colorParser;
    private MessagesFile messages;
    private CommandManager commandManager;
    private MenuRegistry menuRegistry;

    private PlayerStorage playerStorage;
    private SQLDatabase sqlDatabase;

    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();
        saveResource("menu/colorlist.yml", false);

        colorParser = new ColorParser(this);
        colorParser.parse();

        sqlDatabase = new SQLDatabase(getConfig().getConfigurationSection("mysql"));

        messages = new MessagesFile(this);
        commandManager = new CommandManager(this);
        menuRegistry = new MenuRegistry(this);

        playerStorage = new PlayerStorage();

        commandManager.register(new ChatColorCommand(this));
        commandManager.register(new ChatColorResetCommand(this));

        registerListener(new PlayerJoinListener(this));
        registerListener(new PlayerQuitListener(this));
        registerListener(new SendMessageListener(this));

        initSQLTables();
    }


    @Override
    public void onDisable() {
        // Plugin shutdown logic
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
