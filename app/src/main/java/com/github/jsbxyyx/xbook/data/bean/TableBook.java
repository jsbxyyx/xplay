package com.github.jsbxyyx.xbook.data.bean;

import java.util.ArrayList;
import java.util.List;

public class TableBook extends TableStruct {

    public final TableField id = TableField.pk("id");
    public final TableField bid = TableField.column("book_id", "TEXT", true);
    public final TableField isbn = TableField.column("isbn", "TEXT", true);
    public final TableField img = TableField.column("img", "TEXT", true);
    public final TableField title = TableField.column("title", "TEXT", true);
    public final TableField publisher = TableField.column("publisher", "TEXT", false);
    public final TableField authors = TableField.column("authors", "TEXT", false);
    public final TableField file = TableField.column("file", "TEXT", false);
    public final TableField language = TableField.column("language", "TEXT", false);
    public final TableField year = TableField.column("year", "TEXT", false);
    public final TableField detail_url = TableField.column("detail_url", "TEXT", true);
    public final TableField download_url = TableField.column("download_url", "TEXT", true);
    public final TableField remark = TableField.column("remark", "TEXT", false);
    public final TableField created = TableField.column("created", "DATETIME", true);
    public final TableField user = TableField.column("user", "TEXT", true);

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
