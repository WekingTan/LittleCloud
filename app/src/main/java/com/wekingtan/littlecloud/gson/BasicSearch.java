package com.wekingtan.littlecloud.gson;

import com.google.gson.annotations.SerializedName;

/**
 * com.wekingtan.littlecloud.gson
 *
 * @author wekingtan
 * @date 2018/5/4
 */
public class BasicSearch {

    @SerializedName("location")
    public String cityName;

    @SerializedName("admin_area")
    public String provinceName;

    public BasicSearch(String cityName, String provinceName) {
        this.cityName = cityName;
        this.provinceName = provinceName;
    }
}
