package com.github.jsbxyyx.xplay.lib.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LibInfo extends LibModel {
    @JsonProperty("articles")
    public String articles;
    @JsonProperty("banner")
    public String banner;
    @JsonProperty("books")
    public String books;
    @JsonProperty("success")
    public Integer success;
}
