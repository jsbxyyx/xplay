package com.github.jsbxyyx.xplay.lib.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LibServiceInfo extends LibModel {
    @JsonProperty("service")
    public LibServiceData service;
    @JsonProperty("success")
    public Integer success;
}
