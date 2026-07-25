package com.github.jsbxyyx.xplay.lib.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class LibBooksLanguages extends LibModel {
    @JsonProperty("languages")
    public Map<String, String> languages;
    @JsonProperty("success")
    public int success;
}
