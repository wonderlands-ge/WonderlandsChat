/*
 * Decompiled with CFR 0.150.
 */
package me.imlukas.wonderlandschat.data.sql.data;

import me.imlukas.wonderlandschat.data.sql.constants.ColumnType;

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

    public String getName() {
        return this.name;
    }

    public ColumnType getType() {
        return this.type;
    }

    public Object getData() {
        return this.data;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ColumnData)) {
            return false;
        }
        ColumnData other = (ColumnData)o;
        if (!other.canEqual(this)) {
            return false;
        }
        String this$name = this.getName();
        String other$name = other.getName();
        if (this$name == null ? other$name != null : !this$name.equals(other$name)) {
            return false;
        }
        ColumnType this$type = this.getType();
        ColumnType other$type = other.getType();
        if (this$type == null ? other$type != null : !((Object)((Object)this$type)).equals((Object)other$type)) {
            return false;
        }
        Object this$data = this.getData();
        Object other$data = other.getData();
        return !(this$data == null ? other$data != null : !this$data.equals(other$data));
    }

    protected boolean canEqual(Object other) {
        return other instanceof ColumnData;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $name = this.getName();
        result = result * 59 + ($name == null ? 43 : $name.hashCode());
        ColumnType $type = this.getType();
        result = result * 59 + ($type == null ? 43 : ((Object)((Object)$type)).hashCode());
        Object $data = this.getData();
        result = result * 59 + ($data == null ? 43 : $data.hashCode());
        return result;
    }

    public String toString() {
        return "ColumnData(name=" + this.getName() + ", type=" + (Object)((Object)this.getType()) + ", data=" + this.getData() + ")";
    }
}

