package com.github.jsbxyyx.xbook.data.bean;

import java.util.ArrayList;
import java.util.List;

public class TableViewTime extends TableStruct {

    public final TableField id = TableField.ofPK("id");
    public final TableField target_id = TableField.of("target_id", "TEXT", true);
    public final TableField target_type = TableField.of("target_type", "TEXT", true);
    public final TableField time = TableField.of("time", "TEXT", true);
    public final TableField created = TableField.of("created", "TEXT", true);
    public final TableField user = TableField.of("user", "TEXT", true);
    public final TableField remark = TableField.of("remark", "TEXT");

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
