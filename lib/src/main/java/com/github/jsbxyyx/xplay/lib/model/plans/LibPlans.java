package com.github.jsbxyyx.xplay.lib.model.plans;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.jsbxyyx.xplay.lib.model.LibModel;
import com.github.jsbxyyx.xplay.lib.model.plans.LibLimit;

import java.util.List;

public class LibPlans extends LibModel {
    @JsonProperty("basic")
    public LibLimit basic;
    @JsonProperty("limits")
    public List<String> limits;
    @JsonProperty("premium")
    public LibLimit premium;
}
