package com.github.jsbxyyx.xplay.lib.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class LibHostsData extends LibModel {
    @JsonProperty("domains")
    public List<LibHostInfo> domains;
}
