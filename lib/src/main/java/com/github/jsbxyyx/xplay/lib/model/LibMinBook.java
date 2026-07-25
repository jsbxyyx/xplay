package com.github.jsbxyyx.xplay.lib.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LibMinBook extends LibModel {
    @JsonProperty("author")
    public String author;
    @JsonProperty("cover")
    public String cover;
    @JsonProperty("hash")
    public String hash;
    @JsonProperty("id")
    public int id;
    @JsonProperty("title")
    public String title;
}
