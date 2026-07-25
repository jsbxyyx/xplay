package com.github.jsbxyyx.xplay.lib.model.plans;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.jsbxyyx.xplay.lib.model.LibModel;

public class LibDlSpeedLimit extends LibModel {

    @JsonProperty("title")
    public String title;
    @JsonProperty("value")
    public String value;

}
