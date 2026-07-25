package com.github.jsbxyyx.xplay.lib.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class LibDonationsListing extends LibModel {
    @JsonProperty("donations")
    public List<LibDonation> donations;
    @JsonProperty("success")
    public int success;
}
