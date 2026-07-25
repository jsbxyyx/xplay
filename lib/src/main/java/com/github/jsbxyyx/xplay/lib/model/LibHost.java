package com.github.jsbxyyx.xplay.lib.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LibHost extends LibModel {
    @JsonProperty("contentAvailable")
    public Boolean contentAvailable;
    @JsonProperty("domain")
    public String domain;
    @JsonProperty("isRedirector")
    public Boolean isRedirector;
    @JsonProperty("premium")
    public Boolean premium;
}
