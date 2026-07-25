package com.github.jsbxyyx.xplay.lib.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LibHostInfo {
    @JsonProperty("contentAvailable")
    public boolean contentAvailable;
    @JsonProperty("domain")
    public String domain;
}
