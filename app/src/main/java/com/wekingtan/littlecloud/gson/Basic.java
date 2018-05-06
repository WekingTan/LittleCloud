package com.wekingtan.littlecloud.gson;

import com.google.gson.annotations.SerializedName;

/**
 * com.wekingtan.littlecloud.gson
 *
 * @author wekingtan
 * @date 2018/5/3
 */
public class Basic {

    @SerializedName("location")
    public String cityName;

    @SerializedName("cid")
    public String weatherId;

}
