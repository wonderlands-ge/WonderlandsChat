/*
 * Decompiled with CFR 0.150.
 */
package me.imlukas.wonderlandschat.data.sql.objects;

import me.imlukas.wonderlandschat.data.sql.data.ColumnData;
import me.imlukas.wonderlandschat.data.sql.objects.SQLTable;

public class SQLColumn {
    private final SQLTable table;
    private final ColumnData data;

    public SQLColumn(SQLTable table, ColumnData data) {
        this.table = table;
        this.data = data;
    }

    public SQLTable getTable() {
        return this.table;
    }

    public ColumnData getData() {
        return this.data;
    }
}

