package com.github.jsbxyyx.xplay.lib.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LibFileListing extends LibModel {
    @JsonProperty("file")
    public LibFile file;
    @JsonProperty("success")
    public int success;
}
