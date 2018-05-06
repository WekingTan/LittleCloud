package com.wekingtan.littlecloud.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * com.wekingtan.littlecloud.gson
 *
 * @author wekingtan
 * @date 2018/5/4
 */
public class HeWeatherCitySearch {

    public String status;

    @SerializedName("basic")
    public List<BasicSearch> basicSearchList;
}
