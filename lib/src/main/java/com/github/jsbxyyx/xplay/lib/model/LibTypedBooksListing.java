package com.github.jsbxyyx.xplay.lib.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class LibTypedBooksListing extends LibModel {
    @JsonProperty("books")
    public List<LibListBook> books;
    @JsonProperty("pagination")
    public LibPagination pagination;
    @JsonProperty("success")
    public int success;
}
