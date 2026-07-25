package com.github.jsbxyyx.xplay.lib.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LibDonation extends LibModel {
    @JsonProperty("amount")
    public int amount;
    @JsonProperty("date")
    public String date;
    @JsonProperty("refunded")
    public int refunded;
}
