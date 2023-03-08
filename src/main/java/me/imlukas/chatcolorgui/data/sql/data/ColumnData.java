package me.imlukas.chatcolorgui.data.sql.data;

import lombok.Data;
import me.imlukas.chatcolorgui.data.sql.constants.ColumnType;

@Data
public class ColumnData {

    private final String name;
    private final ColumnType type;
    private final Object data;

    public ColumnData(String name, ColumnType type) {
        this.name = name;
        this.type = type;
        this.data = null;
    }

    public ColumnData(String name, ColumnType type, Object data) {
        this.name = name;
        this.type = type;
        this.data = data;
    }
}
