package com.github.jsbxyyx.xbook.data.bean;

import java.util.ArrayList;
import java.util.List;

public class TableBookReader extends TableStruct {

    public final TableField id = TableField.of("id", "INTEGER", true, true, true, null);
    public final TableField book_id = TableField.of("book_id", "INTEGER", true);
    public final TableField cur = TableField.of("cur", "TEXT", true);
    public final TableField pages = TableField.of("pages", "TEXT", true);
    public final TableField created = TableField.of("created", "DATETIME", true);
    public final TableField user = TableField.of("user", "TEXT", true);
    public final TableField remark = TableField.of("remark", "TEXT");

    public String getTableName() {
        return "book_reader";
    }

    public List<TableField> getAllField(TableField... excludes) {
        List<TableField> list = new ArrayList<>();
        list.add(id);
        list.add(book_id);
        list.add(cur);
        list.add(pages);
        list.add(created);
        list.add(user);
        list.add(remark);
        if (excludes != null) {
            for (TableField f : excludes) {
                list.remove(f);
            }
        }
        return list;
    }

}
