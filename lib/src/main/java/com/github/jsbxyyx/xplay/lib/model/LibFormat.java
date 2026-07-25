package com.github.jsbxyyx.xplay.lib.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LibFormat extends LibModel {
    public String extension;
    @JsonProperty("filesize")
    public int filesize;
    @JsonProperty("filesizeString")
    public String filesizeString;
    @JsonProperty("hash")
    public String hash;
    @JsonProperty("id")
    public int id;
}
