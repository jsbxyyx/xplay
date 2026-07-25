package com.github.jsbxyyx.xplay.lib.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LibBookSingle extends LibModel {
    @JsonProperty("book")
    public LibBook book;
    @JsonProperty("success")
    public int success;
}
