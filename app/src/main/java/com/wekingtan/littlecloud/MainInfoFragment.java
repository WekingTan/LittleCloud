package com.wekingtan.littlecloud;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.wekingtan.littlecloud.gson.Forecast;
import com.wekingtan.littlecloud.gson.HeWeather;
import com.wekingtan.littlecloud.gson.Suggestion;
import com.wekingtan.littlecloud.util.HttpUtil;
import com.wekingtan.littlecloud.util.JsonUtil;
import com.wekingtan.littlecloud.util.LogUtil;
import com.wekingtan.littlecloud.util.NetworkConnectedUtil;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainInfoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainInfoFragment extends Fragment {
    private static final String TAG = "MainInfoFragment";
    private static final String ARG_PARAM1 = "district";

    private String suggestionType;
    private String mDistrict;

    private OnFragmentInteractionListener mListener;

    public SwipeRefreshLayout swipeRefreshLayout;
    public ScrollView weatherLayout;
    private TextView updateTimeText;
    public TextView degreeText;
    public TextView weatherInfoText;
    public LinearLayout forecastLayout;
    public LinearLayout suggestionLayout;


    public MainInfoFragment() {
        // Required empty public constructor
    }

    public static MainInfoFragment newInstance(String district) {
        MainInfoFragment fragment = new MainInfoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, district);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.i(TAG, "我是onCreate");
        if (getArguments() != null) {
            mDistrict = getArguments().getString(ARG_PARAM1);
        }
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        //String weatherString = prefs.getString("weather", null);
        /*if (weatherString != null) {
            LogUtil.i(TAG, weatherString);
            HeWeather mHeWeather = JsonUtil.parseWeatherResponse(weatherString);
            showWeatherInfo(mHeWeather);
        } else {*/
            String weatherId = mDistrict;
            requestWeather(weatherId);
        //}
    }

    private void showWeatherInfo(HeWeather mHeWeather) {
        String mUpdateTime = mHeWeather.update.updateTime;
        String mDegree = mHeWeather.now.temperature + "度";
        String mWeatherInfo = mHeWeather.now.info;
        updateTimeText.setText(mUpdateTime);
        degreeText.setText(mDegree);
        weatherInfoText.setText(mWeatherInfo);
        forecastLayout.removeAllViews();
        for (Forecast forecast : mHeWeather.forecastList) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.forecast_item, forecastLayout, false);
            TextView dateText = view.findViewById(R.id.date_text);
            TextView infoText = view.findViewById(R.id.info_text);
            TextView maxText = view.findViewById(R.id.max_text);
            TextView minText = view.findViewById(R.id.min_text);
            dateText.setText(forecast.date);
            infoText.setText(forecast.dInfo);
            maxText.setText(forecast.max);
            minText.setText(forecast.min);
            forecastLayout.addView(view);
        }
        suggestionLayout.removeAllViews();
        for (Suggestion suggestion : mHeWeather.suggestionList) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.suggestion_item, suggestionLayout, false);
            TextView typeText = view.findViewById(R.id.type_text);
            TextView infoSuggestionText = view.findViewById(R.id.info_suggestion_text);
            TextView brfText = view.findViewById(R.id.brf_text);
            if ("comf".equals(suggestion.type)) {
                suggestionType = "舒适度指数";
            } else if ("cw".equals(suggestion.type)) {
                suggestionType = "洗车指数";
            } else if ("drsg".equals(suggestion.type)) {
                suggestionType = "穿衣指数";
            } else if ("flu".equals(suggestion.type)) {
                suggestionType = "感冒指数";
            } else if ("sport".equals(suggestion.type)) {
                suggestionType = "运动指数";
            } else if ("trav".equals(suggestion.type)) {
                suggestionType = "旅游指数";
            } else if ("uv".equals(suggestion.type)) {
                suggestionType = "紫外线指数";
            } else if ("air".equals(suggestion.type)) {
                suggestionType = "空气污染扩散指数";
            }
            LogUtil.i(TAG, "++++++++++++++++++++" + suggestionType);
            typeText.setText(suggestionType);
            infoSuggestionText.setText(suggestion.info);
            brfText.setText(suggestion.brf);
            suggestionLayout.addView(view);
        }
    }

    private void requestWeather(String weatherId) {

        String weatherUrl = "https://free-api.heweather.com/s6/weather?location=" + weatherId + "&key=844b69d8a1bc4a7e9277d79879b8d59a";
        LogUtil.i(TAG, weatherUrl);
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), "获取天气信息失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final HeWeather mHeWeather = JsonUtil.parseWeatherResponse(responseText);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mHeWeather != null & "ok".equals(mHeWeather.status)) {
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
                            editor.putString("weather", responseText);
                            editor.apply();
                            showWeatherInfo(mHeWeather);
                        } else {
                            Toast.makeText(getContext(), "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_main_info, container, false);
        LogUtil.i(TAG, "我是onCreateView");
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        weatherLayout = view.findViewById(R.id.weather_layout);
        updateTimeText = view.findViewById(R.id.update_time);
        degreeText = view.findViewById(R.id.degree_text);
        weatherInfoText = view.findViewById(R.id.weather_info_text);
        forecastLayout = view.findViewById(R.id.forecast_layout);
        suggestionLayout = view.findViewById(R.id.suggestion_layout);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (NetworkConnectedUtil.networkConnected(getActivity()) == false) {
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    String weatherInfo = prefs.getString("weather", null);
                    if (weatherInfo != null) {
                        showWeatherInfo(JsonUtil.parseWeatherResponse(weatherInfo));
                        swipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(getActivity(), "连接网络失败暂时无法更新天气", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    mListener.onFragmentInteraction();
                }
            }
        });
        return view;
    }

    public void onButtonPressed() {
        if (mListener != null) {
            mListener.onFragmentInteraction();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        LogUtil.i(TAG, "好吧！refresh 圈圈要消失了");
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        LogUtil.i(TAG, "我是onAttach");
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        LogUtil.i(TAG, "我是onDetach");
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction();
    }
}
