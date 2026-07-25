package com.github.jsbxyyx.xplay.lib.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class LibHostsListing extends LibModel {
    @JsonProperty("domains")
    public List<LibHost> domains;
    @JsonProperty("success")
    public int success;
}
