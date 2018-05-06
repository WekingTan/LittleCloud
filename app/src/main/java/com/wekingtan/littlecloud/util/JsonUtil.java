package com.wekingtan.littlecloud.util;

import com.google.gson.Gson;
import com.wekingtan.littlecloud.gson.HeWeatherCitySearch;
import com.wekingtan.littlecloud.gson.HeWeather;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * com.wekingtan.littlecloud.util
 *
 * @author wekingtan
 * @date 2018/5/3
 */
public class JsonUtil {

    /**
     * 将返回的 JSON 数据解析成 Weather 实体类
     */
    public static HeWeather parseWeatherResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather6");
            String weatherContent = jsonArray.getJSONObject(0).toString();
            return new Gson().fromJson(weatherContent, HeWeather.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将返回的 JSON 数据解析成 Weather 实体类
     */
    public static HeWeatherCitySearch parseCityResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather6");
            String cityContent = jsonArray.getJSONObject(0).toString();
            return new Gson().fromJson(cityContent, HeWeatherCitySearch.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
