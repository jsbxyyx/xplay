package com.github.jsbxyyx.zb;

import org.junit.Test;

import static org.junit.Assert.*;

import com.github.jsbxyyx.xbook.data.bean.TableBookReader;
import com.github.jsbxyyx.xbook.data.bean.TableField;

import java.util.List;
import java.util.StringJoiner;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);

        TableBookReader t = new TableBookReader();
        String create = t.create(true, true, "");
        System.out.println(create);

        String update = t.update(new TableField[]{t.remark, t.id}, t.id, t.book_id);
        System.out.println(update);

        String delete = t.delete(t.id, t.book_id);
        System.out.println(delete);

        String select = t.selectAll(t.id, t.book_id);
        System.out.println(select);

        String allField = t.getAllFieldString(t.id);
        System.out.println(allField);
    }
}