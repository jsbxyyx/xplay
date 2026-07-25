package com.github.jsbxyyx.xplay.lib.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LibStatus extends LibModel {
    @JsonProperty("success")
    public int success;
}
