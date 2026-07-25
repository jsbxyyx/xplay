package com.github.jsbxyyx.xplay.lib.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LibBook extends LibModel {
    @JsonProperty("author")
    public String author;
    @JsonProperty("cover")
    public String cover;
    @JsonProperty("description")
    public String description;
    @JsonProperty("dl")
    public String dl;
    @JsonProperty("date_download")
    public String dlDate;
    @JsonProperty("edition")
    public String edition;
    @JsonProperty("extension")
    public String extension;
    @JsonProperty("filesize")
    public long filesize;
    @JsonProperty("filesizeString")
    public String filesizeString;
    @JsonProperty("hash")
    public String hash;
    @JsonProperty("href")
    public String href;
    @JsonProperty("id")
    public int id;
    @JsonProperty("identifier")
    public String identifier;
    @JsonProperty("sendToEmailAvailable")
    public Boolean isSendToEmailAvailable;
    @JsonProperty("_isUserSavedBook")
    public Boolean isUserSavedBook;
    @JsonProperty("kindleAvailable")
    public Boolean kindleAvailable;
    @JsonProperty("language")
    public String language;
    @JsonProperty("pages")
    public int pages;
    @JsonProperty("preview")
    public String preview;
    @JsonProperty("publisher")
    public String publisher;
    @JsonProperty("readOnlineAvailable")
    public Boolean readOnlineAvailable;
    @JsonProperty("readOnlineUrl")
    public String readOnlineUrl;
    @JsonProperty("series")
    public String series;
    @JsonProperty("title")
    public String title;
    @JsonProperty("volume")
    public String volume;
    @JsonProperty("year")
    public int year;
}
