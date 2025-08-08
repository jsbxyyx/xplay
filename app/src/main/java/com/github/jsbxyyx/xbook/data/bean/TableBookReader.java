package com.github.jsbxyyx.xbook.data.bean;

import java.util.ArrayList;
import java.util.List;

public class TableBookReader extends TableStruct {

    public final TableField id = TableField.pk("id");
    public final TableField book_id = TableField.column("book_id", "INTEGER", true);
    public final TableField cur = TableField.column("cur", "TEXT", true);
    public final TableField pages = TableField.column("pages", "TEXT", true);
    public final TableField created = TableField.column("created", "DATETIME", true);
    public final TableField user = TableField.column("user", "TEXT", true);
    public final TableField remark = TableField.column("remark", "TEXT", false);
    public final TableField updated = TableField.column("updated", "INTEGER", false);

    @Override
    public String getTableName() {
        return "book_reader";
    }

    @Override
    public TableField getPk() {
        return id;
    }

    @Override
    public List<TableField> getAllField(TableField... excludes) {
        List<TableField> list = new ArrayList<>();
        list.add(id);
        list.add(book_id);
        list.add(cur);
        list.add(pages);
        list.add(created);
        list.add(user);
        list.add(remark);
        list.add(updated);
        if (excludes != null) {
            for (TableField f : excludes) {
                list.remove(f);
            }
        }
        return list;
    }

}
