package com.wekingtan.littlecloud.gson;

import com.google.gson.annotations.SerializedName;

/**
 * com.wekingtan.littlecloud.gson
 *
 * @author wekingtan
 * @date 2018/5/3
 */
public class Suggestion {

    @SerializedName("type")
    public String type;

    @SerializedName("brf")
    public String brf;

    @SerializedName("txt")
    public String info;
}
