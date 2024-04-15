package com.github.jsbxyyx.xbook.data;

/**
 * @author jsbxyyx
 * @since 1.0
 */
public class BookReader implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String bookId;
    private String cur;
    private String pages;
    private String created;
    private String user;
    private String remark;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getCur() {
        return cur;
    }

    public void setCur(String cur) {
        this.cur = cur;
    }

    public String getPages() {
        return pages;
    }

    public void setPages(String pages) {
        this.pages = pages;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "BookReader{" +
                "id='" + id + '\'' +
                ", bookId='" + bookId + '\'' +
                ", cur='" + cur + '\'' +
                ", pages='" + pages + '\'' +
                ", created='" + created + '\'' +
                ", user='" + user + '\'' +
                ", remark='" + remark + '\'' +
                '}';
    }
}
