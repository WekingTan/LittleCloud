package com.wekingtan.littlecloud.gson;

import com.google.gson.annotations.SerializedName;

/**
 * com.wekingtan.littlecloud.gson
 *
 * @author wekingtan
 * @date 2018/5/3
 */
public class Now {

    @SerializedName("tmp")
    public String temperature;

    @SerializedName("cond_txt")
    public String info;

}
