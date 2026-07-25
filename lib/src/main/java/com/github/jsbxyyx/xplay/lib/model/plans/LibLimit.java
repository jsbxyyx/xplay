package com.github.jsbxyyx.xplay.lib.model.plans;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.jsbxyyx.xplay.lib.model.LibModel;
import com.github.jsbxyyx.xplay.lib.model.plans.LibDlHistoryLimit;
import com.github.jsbxyyx.xplay.lib.model.plans.LibDlLimit;
import com.github.jsbxyyx.xplay.lib.model.plans.LibDlSpeedLimit;

public class LibLimit extends LibModel {

    @JsonProperty("converter")
    public LibDlHistoryLimit converter;
    @JsonProperty("download_history")
    public LibDlHistoryLimit download_history;
    @JsonProperty("download_speed")
    public LibDlSpeedLimit download_speed;
    @JsonProperty("downloads")
    public LibDlLimit downloads;
    @JsonProperty("one_click_download")
    public LibDlHistoryLimit oneClickDl;
    @JsonProperty("recomendations")
    public LibDlHistoryLimit recommendations;
    @JsonProperty("search_results")
    public LibDlHistoryLimit searchResults;
    @JsonProperty("send_to_gdrive")
    public LibDlHistoryLimit sendToGdrive;
    @JsonProperty("send_to_kindle")
    public LibDlHistoryLimit sendToKindle;
    @JsonProperty("send_to_email")
    public LibDlHistoryLimit sendToMail;
    @JsonProperty("telegram_bot")
    public LibDlHistoryLimit telegramBot;

}
