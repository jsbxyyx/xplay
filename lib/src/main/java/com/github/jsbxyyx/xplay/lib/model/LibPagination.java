package com.github.jsbxyyx.xplay.lib.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LibPagination extends LibModel {
    @JsonProperty("before")
    public boolean beforePage;
    @JsonProperty("current")
    public int current;
    @JsonProperty("limit")
    public int limit;
    @JsonProperty("next")
    public boolean nextPage;
    @JsonProperty("total_items")
    public int totalItems;
    @JsonProperty("total_pages")
    public int totalPages;
}
