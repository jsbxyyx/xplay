package com.github.jsbxyyx.xplay.lib.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class LibBookFormats extends LibModel {
    @JsonProperty("books")
    public List<LibFormat> formats;
    @JsonProperty("success")
    public int success;
}
