package com.github.jsbxyyx.xbook.data.bean;

import java.util.Objects;

public class TableField {

    private String name;
    private String type;
    private boolean notNull;
    private boolean primaryKey;
    private boolean autoincrement;
    private String defaultValue;

    public static TableField pk(String name) {
        return of(name, "INTEGER", true, true, true, null);
    }

    public static TableField column(String name, String type, boolean notNull) {
        return column(name, type, notNull, null);
    }

    public static TableField column(String name, String type, boolean notNull, String defaultValue) {
        return of(name, type, notNull, false, false, defaultValue);
    }

    public static TableField of(String name, String type, boolean notNull, boolean primaryKey, boolean autoincrement, String defaultValue) {
        TableField tf = new TableField();
        tf.name = name;
        tf.type = type;
        tf.notNull = notNull;
        tf.primaryKey = primaryKey;
        tf.autoincrement = autoincrement;
        tf.defaultValue = defaultValue;
        return tf;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public boolean isNotNull() {
        return notNull;
    }

    public boolean isPrimaryKey() {
        return primaryKey;
    }

    public boolean isAutoincrement() {
        return autoincrement;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TableField that = (TableField) o;
        return notNull == that.notNull && primaryKey == that.primaryKey && autoincrement == that.autoincrement && Objects.equals(name, that.name) && Objects.equals(type, that.type) && Objects.equals(defaultValue, that.defaultValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type, notNull, primaryKey, autoincrement, defaultValue);
    }

    @Override
    public String toString() {
        return "TableField{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", notNull=" + notNull +
                ", primaryKey=" + primaryKey +
                ", autoincrement=" + autoincrement +
                ", defaultValue='" + defaultValue + '\'' +
                '}';
    }

}
