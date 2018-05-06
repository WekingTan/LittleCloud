package com.wekingtan.littlecloud.gson;

import com.google.gson.annotations.SerializedName;

/**
 * com.wekingtan.littlecloud.gson
 *
 * @author wekingtan
 * @date 2018/5/3
 */
public class Forecast {

    public String date;

    @SerializedName("tmp_max")
    public String max;

    @SerializedName("tmp_min")
    public String min;

    @SerializedName("cond_txt_d")
    public String dInfo;

    @SerializedName("cond_txt_n")
    public String nInfo;

}
