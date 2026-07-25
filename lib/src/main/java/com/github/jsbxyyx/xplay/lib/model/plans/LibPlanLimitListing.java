package com.github.jsbxyyx.xplay.lib.model.plans;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.jsbxyyx.xplay.lib.model.LibModel;

public class LibPlanLimitListing extends LibModel {
    @JsonProperty("plans")
    public LibPlans plans;
    @JsonProperty("success")
    public int success;
}
