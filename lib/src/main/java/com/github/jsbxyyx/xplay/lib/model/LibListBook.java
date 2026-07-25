package com.github.jsbxyyx.xplay.lib.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LibListBook extends LibModel {
    @JsonProperty("id")
    public int id;
    @JsonProperty("title")
    public String title;
    @JsonProperty("author")
    public String author;
    @JsonProperty("volume")
    public String volume;
    @JsonProperty("year")
    public int year;
    @JsonProperty("edition")
    public String edition;
    @JsonProperty("publisher")
    public String publisher;
    @JsonProperty("identifier")
    public String identifier;
    @JsonProperty("language")
    public String language;
    @JsonProperty("extension")
    public String extension;
    @JsonProperty("pages")
    public int pages;
    @JsonProperty("filesize")
    public long filesize;
    @JsonProperty("series")
    public String series;
    @JsonProperty("cover")
    public String cover;
    @JsonProperty("hash")
    public String hash;
    @JsonProperty("href")
    public String href;
    @JsonProperty("dl")
    public String dl;
    @JsonProperty("description")
    public String description;
    @JsonProperty("filesizeString")
    public String filesizeString;
    @JsonProperty("preview")
    public String preview;
    @JsonProperty("date_download")
    public String dlDate;
    @JsonProperty("kindleAvailable")
    public Boolean kindleAvailable;
    @JsonProperty("sendToEmailAvailable")
    public Boolean isSendToEmailAvailable;
    @JsonProperty("readOnlineAvailable")
    public boolean readOnlineAvailable;
    @JsonProperty("readOnlineUrl")
    public String readOnlineUrl;

}
