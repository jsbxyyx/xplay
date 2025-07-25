package com.github.jsbxyyx.xbook.data.bean;

import java.util.ArrayList;
import java.util.List;

public class TableViewTime extends TableStruct {

    public final TableField id = TableField.pk("id");
    public final TableField target_id = TableField.column("target_id", "TEXT", true);
    public final TableField target_type = TableField.column("target_type", "TEXT", true);
    public final TableField time = TableField.column("time", "TEXT", true);
    public final TableField created = TableField.column("created", "TEXT", true);
    public final TableField user = TableField.column("user", "TEXT", true);
    public final TableField remark = TableField.column("remark", "TEXT", false);

    public String getTableName() {
        return "view_time";
    }

    public List<TableField> getAllField(TableField... excludes) {
        List<TableField> list = new ArrayList<>();
        list.add(id);
        list.add(target_id);
        list.add(target_type);
        list.add(time);
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
