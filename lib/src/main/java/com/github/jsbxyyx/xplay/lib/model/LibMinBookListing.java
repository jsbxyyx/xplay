package com.github.jsbxyyx.xplay.lib.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class LibMinBookListing extends LibModel {
    @JsonProperty("books")
    public List<LibMinBook> books;
    @JsonProperty("success")
    public int success;
}
