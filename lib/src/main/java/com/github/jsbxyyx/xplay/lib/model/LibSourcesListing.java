package com.github.jsbxyyx.xplay.lib.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class LibSourcesListing extends LibModel {
    @JsonProperty("sources")
    public List<String> sources;
    @JsonProperty("success")
    public int success;
}
