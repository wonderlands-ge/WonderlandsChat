package me.imlukas.chatcolorgui.data.sql.objects;

import lombok.Getter;
import me.imlukas.chatcolorgui.data.sql.data.ColumnData;

@Getter
public class SQLColumn {

    private final SQLTable table;
    private final ColumnData data;

    public SQLColumn(SQLTable table, ColumnData data) {
        this.table = table;
        this.data = data;
    }
}
