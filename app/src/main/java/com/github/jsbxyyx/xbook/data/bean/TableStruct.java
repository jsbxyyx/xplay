package com.github.jsbxyyx.xbook.data.bean;

import java.util.ArrayList;
import java.util.List;

public abstract class TableStruct {

    public abstract String getTableName();

    public abstract TableField getPk();

    public abstract List<TableField> getAllField(TableField... excludes);

    public String getAllFieldString(TableField... excludes) {
        List<TableField> all = getAllField(excludes);
        StringBuilder sql = new StringBuilder();
        for (int i = 0, len = all.size(); i < len; i++) {
            TableField f = all.get(i);
            if (i > 0) {
                sql.append(", ");
            }
            sql.append(f.getName());
        }
        return sql.toString();
    }

    public String insert() {
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO ");
        sql.append(getTableName());
        sql.append(" (");
        sql.append(getAllFieldString());
        sql.append(")");
        sql.append(" VALUES (");
        List<TableField> all = getAllField();
        for (int i = 0, len = all.size(); i < len; i++) {
            if (i > 0) {
                sql.append(", ");
            }
            sql.append("?");
        }
        sql.append(")");
        return sql.toString();
    }

    public String selectAll(TableField... where) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");
        sql.append(getAllFieldString());
        sql.append(" FROM ");
        sql.append(getTableName());
        if (where != null && where.length > 0) {
            sql.append(" WHERE ");
            for (int i = 0, len = where.length; i < len; i++) {
                TableField f = where[i];
                if (i > 0) {
                    sql.append(" AND ");
                }
                sql.append(f.getName()).append("=").append("?");
            }
        }
        return sql.toString();
    }

    public String delete(TableField... where) {
        StringBuilder sql = new StringBuilder();
        sql.append("DELETE FROM ").append(getTableName());
        if (where != null && where.length > 0) {
            sql.append(" WHERE ");
            for (int i = 0, len = where.length; i < len; i++) {
                TableField f = where[i];
                if (i > 0) {
                    sql.append(" AND ");
                }
                sql.append(f.getName()).append("=").append("?");
            }
        }
        return sql.toString();
    }

    public String update(TableField[] update, TableField... where) {
        StringBuilder sql = new StringBuilder();
        sql.append("UPDATE ").append(getTableName());
        sql.append(" SET ");
        for (int i = 0, len = update.length; i < len; i++) {
            TableField f = update[i];
            if (i > 0) {
                sql.append(", ");
            }
            sql.append(f.getName()).append("=").append("?");
        }
        if (where != null && where.length > 0) {
            sql.append(" WHERE ");
            for (int i = 0, len = where.length; i < len; i++) {
                TableField f = where[i];
                if (i > 0) {
                    sql.append(", ");
                }
                sql.append(f.getName()).append("=").append("?");
            }
        }
        return sql.toString();
    }

    public String create(boolean temporary, boolean notExists, String suffix) {
        return create(temporary, notExists, suffix, new ArrayList<>());
    }

    public String create(boolean temporary, boolean notExists, String suffix, List<TableField> excludes) {
        StringBuilder sql = new StringBuilder();
        sql.append("CREATE ");
        if (temporary) {
            sql.append("TEMPORARY ");
        }
        sql.append("TABLE ");
        if (notExists) {
            sql.append("IF NOT EXISTS ");
        }
        sql.append(getTableName());
        sql.append(suffix);
        sql.append(" (");
        sql.append("\n");

        List<TableField> all = getAllField(excludes.toArray(new TableField[0]));
        for (int i = 0, size = all.size(); i < size; i++) {
            TableField f = all.get(i);
            if (i > 0) {
                sql.append(",\n");
            }
            sql.append("  ");
            sql.append(f.getName());
            sql.append(" ");
            sql.append(f.getType());
            if (f.isNotNull()) {
                sql.append(" NOT NULL");
            }
            if (f.isPrimaryKey()) {
                sql.append(" PRIMARY KEY");
            }
            if (f.isAutoincrement()) {
                sql.append(" AUTOINCREMENT");
            }
            if (f.getDefaultValue() != null) {
                sql.append(" DEFAULT '").append(f.getDefaultValue()).append("'");
            }
        }
        sql.append("\n");
        sql.append(")");
        return sql.toString();
    }



}
