package com.github.jsbxyyx.xbook.data;

/**
 * @author jsbxyyx
 * @since 1.0
 */
public class MLog {

    private String title;
    private String raw;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRaw() {
        return raw;
    }

    public void setRaw(String raw) {
        this.raw = raw;
    }

    @Override
    public String toString() {
        return "MLog{" +
                "title='" + title + '\'' +
                ", raw='" + raw + '\'' +
                '}';
    }
}
