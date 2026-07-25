package com.github.jsbxyyx.xplay.lib.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class LibUser extends LibModel {
    @JsonProperty("donations_active")
    public Integer donations_active;
    @JsonProperty("donations_expire")
    public String donations_expire;
    @JsonProperty("downloads_limit")
    public Integer downloads_limit;
    @JsonProperty("downloads_today")
    public Integer downloads_today;
    @JsonProperty("email")
    public String email;
    @JsonProperty("id")
    public int id;
    @JsonProperty("confirmed")
    public Integer isConfirmed;
    @JsonProperty("isPremium")
    public Integer isPremium;
    @JsonProperty("kindle_email")
    public String kindle;
    @JsonProperty("name")
    public String name;
    @JsonProperty("personalDomains")
    public List<String> personalDomains;
    @JsonProperty("remix_userkey")
    public String remix_userkey;
}
