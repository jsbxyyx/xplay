package com.github.jsbxyyx.xbook.data.bean;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.jsbxyyx.xbook.common.Common;
import com.github.jsbxyyx.xbook.common.JsonUtil;

/**
 * @author jsbxyyx
 * @since 1.0
 */
public class Book implements java.io.Serializable {

    private static final long serialVersionUID = 1L;
    public static final String publisher_key = "publisher";
    public static String content_type_booklist = "booklist";
    public static String content_type_book = "book";

    // net
    private String bid;
    private String isbn;
    private String authors;
    private String title;
    private String description;
    private String coverImage;
    private String detailUrl;
    private String year;
    private String publisher;
    private String language;
    private String file;
    private String downloadUrl;
    private String content_type;
    private String extra;
    // net end

    // db
    private Long id;
    private String remark;
    private String created;
    private String user;
    // db end

    // ext
    private BookReader bookReader;

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

    public String getAuthors() {
        return authors;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public String getDetailUrl() {
        return detailUrl;
    }

    public void setDetailUrl(String detailUrl) {
        this.detailUrl = detailUrl;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getContent_type() {
        return content_type;
    }

    public void setContent_type(String content_type) {
        this.content_type = content_type;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String extractFilePath() {
        return getRemarkProperty("file_path");
    }

    public void fillFilePath(String filePath) {
        putRemarkProperty("file_path", filePath);
    }

    public String extractSha() {
        return getRemarkProperty("sha");
    }

    public void fillSha(String sha) {
        putRemarkProperty("sha", sha);
    }

    public String getRemarkProperty(String key) {
        if (Common.isEmpty(remark)) {
            throw new IllegalArgumentException("remark is empty");
        }
        if (Common.isEmpty(key)) {
            throw new IllegalArgumentException("key is empty");
        }
        JsonNode remarkObject = JsonUtil.readTree(remark);
        return remarkObject.get(key) == null ? null : remarkObject.get(key).asText();
    }

    public void putRemarkProperty(String key, String value) {
        ObjectNode node = null;
        if (Common.isEmpty(remark)) {
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
                "bid='" + bid + '\'' +
                ", isbn='" + isbn + '\'' +
                ", authors='" + authors + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", coverImage='" + coverImage + '\'' +
                ", detailUrl='" + detailUrl + '\'' +
                ", year='" + year + '\'' +
                ", publisher='" + publisher + '\'' +
                ", language='" + language + '\'' +
                ", file='" + file + '\'' +
                ", downloadUrl='" + downloadUrl + '\'' +
                ", content_type='" + content_type + '\'' +
                ", extra='" + extra + '\'' +
                ", id=" + id +
                ", remark='" + remark + '\'' +
                ", created='" + created + '\'' +
                ", user='" + user + '\'' +
                ", bookReader=" + bookReader +
                '}';
    }
}
