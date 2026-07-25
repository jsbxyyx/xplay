package com.github.jsbxyyx.xplay.lib.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LibUserListing extends LibModel {
    @JsonProperty("error")
    public String error;
    @JsonProperty("success")
    public int success;
    @JsonProperty("user")
    public LibUser user;
}
