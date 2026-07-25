package com.github.jsbxyyx.xplay.lib.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class LibBooksExtensions extends LibModel {
    @JsonProperty("extensions")
    public List<String> extensions;
    @JsonProperty("success")
    public int success;
}
