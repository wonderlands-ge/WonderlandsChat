/*
 * Decompiled with CFR 0.150.
 */
package me.imlukas.wonderlandschat.data.sql.objects;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import me.imlukas.wonderlandschat.data.sql.SQLDatabase;
import me.imlukas.wonderlandschat.data.sql.data.ColumnData;
import me.imlukas.wonderlandschat.data.sql.objects.SQLColumn;

public class SQLTable {
    private final String name;
    private final SQLDatabase provider;
    private final Map<String, SQLColumn> columns = new HashMap<String, SQLColumn>();

    public SQLTable(String name, SQLDatabase provider) {
        this.name = name;
        this.provider = provider;
        this.createTable();
    }

    public CompletableFuture<Void> createTable() {
        return this.provider.getConnection().thenAccept(connection -> {
            try {
                connection.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS " + this.name + " (id INT NOT NULL AUTO_INCREMENT, PRIMARY KEY (id))");
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public CompletableFuture<Void> addColumn(ColumnData data) {
        this.columns.put(this.name, new SQLColumn(this, data));
        return this.provider.getConnection().thenAccept(connection -> {
            try {
                if (connection.getMetaData().getColumns(null, null, this.name, data.getName()).next()) {
                    return;
                }
                Object value = data.getData();
                String valueString = value == null ? "" : "(" + value + ")";
                connection.createStatement().executeUpdate("ALTER TABLE " + this.name + " ADD COLUMN " + data.getName() + " " + data.getType().name() + valueString);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public CompletableFuture<Void> removeColumn(String columnName) {
        this.columns.remove(columnName);
        return this.provider.getConnection().thenAccept(connection -> {
            try {
                connection.createStatement().executeUpdate("ALTER TABLE " + this.name + " DROP COLUMN " + columnName);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public CompletableFuture<Void> insert(Map<String, Object> data) {
        return this.provider.getConnection().thenAccept(connection -> {
            try {
                StringBuilder builder = new StringBuilder();
                for (Object key : data.keySet()) {
                    builder.append((String)key).append(", ");
                }
                String columns = builder.substring(0, builder.length() - 2);
                builder = new StringBuilder();
                for (Object value : data.values()) {
                    if (value instanceof String) {
                        value = "'" + value + "'";
                    }
                    builder.append(value).append(", ");
                }
                String values = builder.substring(0, builder.length() - 2);
                connection.createStatement().executeUpdate("INSERT INTO " + this.name + " (" + columns + ") VALUES (" + values + ")");
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public CompletableFuture<ResultSet> executeQuery(String query, Object ... args) {
        return this.provider.getConnection().thenApply(connection -> {
            try {
                PreparedStatement statement = connection.prepareStatement(query);
                for (int index = 0; index < args.length; ++index) {
                    Object ar = args[index];
                    if (ar instanceof String) {
                        statement.setString(index + 1, (String)ar);
                        continue;
                    }
                    statement.setObject(index + 1, args[index]);
                }
                if (query.contains("SELECT") || query.contains("select") || query.contains("Select")) {
                    return statement.executeQuery();
                }
                statement.executeUpdate();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        });
    }

    public SQLColumn getColumn(String columnName) {
        return this.columns.get(columnName);
    }
}

