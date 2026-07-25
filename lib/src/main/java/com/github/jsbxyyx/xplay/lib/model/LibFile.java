package com.github.jsbxyyx.xplay.lib.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LibFile extends LibModel {
    public boolean allowDownload;
    @JsonProperty("author")
    public String author;
    @JsonProperty("description")
    public String description;
    @JsonProperty("downloadLink")
    public String dlLink;
    @JsonProperty("extension")
    public String extension;
}
