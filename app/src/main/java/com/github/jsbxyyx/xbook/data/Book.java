package com.github.jsbxyyx.xbook.data;

import android.text.TextUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.jsbxyyx.xbook.common.JsonUtil;

/**
 * @author jsbxyyx
 * @since 1.0
 */
public class Book implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String bid;
    private String isbn;
    private String coverImage;
    private String title;
    private String publisher;
    private String authors;
    private String file;
    private String language;
    private String year;
    private String detailUrl;
    private String downloadUrl;
    private String remark;
    private String created;
    private String user;

    // ext
    private BookReader bookReader;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBid() {
        return bid;
    }

    public void setBid(String bid) {
        this.bid = bid;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getAuthors() {
        return authors;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getDetailUrl() {
        return detailUrl;
    }

    public void setDetailUrl(String detailUrl) {
        this.detailUrl = detailUrl;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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

    public BookReader getBookReader() {
        return bookReader;
    }

    public void setBookReader(BookReader bookReader) {
        this.bookReader = bookReader;
    }

    public String getRemarkProperty(String key) {
        if (TextUtils.isEmpty(remark)) {
            throw new IllegalArgumentException("remark is empty");
        }
        if (TextUtils.isEmpty(key)) {
            throw new IllegalArgumentException("key is empty");
        }
        JsonNode remarkObject = JsonUtil.readTree(remark);
        return remarkObject.get(key) == null ? null : remarkObject.get(key).asText();
    }

    public void putRemarkProperty(String key, String value) {
        ObjectNode node = null;
        if (TextUtils.isEmpty(remark)) {
            node = JsonUtil.fromJson("{}", ObjectNode.class);
        } else {
            node = JsonUtil.fromJson(remark, ObjectNode.class);
        }
        node.put(key, value);
        remark = JsonUtil.toJson(node);
    }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", bid='" + bid + '\'' +
                ", isbn='" + isbn + '\'' +
                ", coverImage='" + coverImage + '\'' +
                ", title='" + title + '\'' +
                ", publisher='" + publisher + '\'' +
                ", authors='" + authors + '\'' +
                ", file='" + file + '\'' +
                ", language='" + language + '\'' +
                ", year='" + year + '\'' +
                ", detailUrl='" + detailUrl + '\'' +
                ", downloadUrl='" + downloadUrl + '\'' +
                ", remark='" + remark + '\'' +
                ", created='" + created + '\'' +
                ", user='" + user + '\'' +
                '}';
    }
}
