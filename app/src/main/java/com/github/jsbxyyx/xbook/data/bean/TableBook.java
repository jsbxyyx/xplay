package com.github.jsbxyyx.xbook.data.bean;

import java.util.ArrayList;
import java.util.List;

public class TableBook extends TableStruct {

    public final TableField id = TableField.ofPK("id");
    public final TableField bid = TableField.of("book_id", "TEXT", true);
    public final TableField isbn = TableField.of("isbn", "TEXT", true);
    public final TableField img = TableField.of("img", "TEXT", true);
    public final TableField title = TableField.of("title", "TEXT", true);
    public final TableField publisher = TableField.of("publisher", "TEXT");
    public final TableField authors = TableField.of("authors", "TEXT");
    public final TableField file = TableField.of("file", "TEXT");
    public final TableField language = TableField.of("language", "TEXT");
    public final TableField year = TableField.of("year", "TEXT");
    public final TableField detail_url = TableField.of("detail_url", "TEXT", true);
    public final TableField download_url = TableField.of("download_url", "TEXT", true);
    public final TableField remark = TableField.of("remark", "TEXT");
    public final TableField created = TableField.of("created", "DATETIME", true);
    public final TableField user = TableField.of("user", "TEXT", true);

    public String getTableName() {
        return "book";
    }

    public List<TableField> getAllField(TableField... excludes) {
        List<TableField> list = new ArrayList<>();
        list.add(id);
        list.add(bid);
        list.add(isbn);
        list.add(img);
        list.add(title);
        list.add(publisher);
        list.add(authors);
        list.add(file);
        list.add(language);
        list.add(year);
        list.add(detail_url);
        list.add(download_url);
        list.add(remark);
        list.add(created);
        list.add(user);
        if (excludes != null) {
            for (TableField f : excludes) {
                list.remove(f);
            }
        }
        return list;
    }

}
