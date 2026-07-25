package com.github.jsbxyyx.xplay.lib.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class LibBooksListing extends LibModel {
    @JsonProperty("books")
    public List<LibBook> books;
    @JsonProperty("exactBooksCount")
    public Integer exactBooksCount;
    @JsonProperty("pagination")
    public LibPagination pagination;
    @JsonProperty("success")
    public int success;
}
