/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.configuration.ConfigurationSection
 */
package me.imlukas.wonderlandschat.data.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import me.imlukas.wonderlandschat.data.sql.objects.SQLTable;
import org.bukkit.configuration.ConfigurationSection;

public class SQLDatabase {
    private Connection connection;
    private final String host;
    private final String database;
    private final String username;
    private final String password;
    private final int port;
    private final Map<String, SQLTable> tables = new HashMap<String, SQLTable>();

    public SQLDatabase(String host, int port, String database, String username, String password) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
    }

    public SQLDatabase(ConfigurationSection section) {
        this.host = section.getString("host");
        this.port = section.getInt("port");
        this.database = section.getString("database");
        this.username = section.getString("username");
        this.password = section.getString("password");
        this.connection = this.createConnection().join();
    }

    public SQLTable getOrCreateTable(String name) {
        if (this.tables.containsKey(name)) {
            return this.tables.get(name);
        }
        SQLTable table = new SQLTable(name, this);
        this.tables.put(name, table);
        return table;
    }

    public CompletableFuture<Connection> getConnection() {
        if (this.connection == null) {
            this.createConnection().thenAccept(connection -> {
                this.connection = connection;
            });
        }
        return this.validateConnection(this.connection).thenCompose(valid -> {
            if (valid.booleanValue()) {
                return CompletableFuture.completedFuture(this.connection);
            }
            return this.createConnection();
        });
    }

    private CompletableFuture<Boolean> validateConnection(Connection connection) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return connection.isValid(1);
            }
            catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        });
    }

    private CompletableFuture<Connection> createConnection() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection connection = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database, this.username, this.password);
                System.out.println("Connected to MySQL server. // " + connection);
                return connection;
            }
            catch (Exception e) {
                System.out.println("Failed to connect to MySQL server.");
                e.printStackTrace();
                return null;
            }
        });
    }
}

