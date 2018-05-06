package com.wekingtan.littlecloud.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * com.wekingtan.littlecloud.gson
 *
 * @author wekingtan
 * @date 2018/5/3
 */
public class HeWeather {

    public String status;

    public Basic basic;

    public Now now;

    public Update update;

    @SerializedName("lifestyle")
    public List<Suggestion> suggestionList;

    @SerializedName("daily_forecast")
    public List<Forecast> forecastList;

}
