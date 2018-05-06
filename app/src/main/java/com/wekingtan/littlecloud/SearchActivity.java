package com.wekingtan.littlecloud;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import com.wekingtan.littlecloud.adapter.BasicSearchAdapter;
import com.wekingtan.littlecloud.gson.BasicSearch;
import com.wekingtan.littlecloud.gson.HeWeatherCitySearch;
import com.wekingtan.littlecloud.util.HttpUtil;
import com.wekingtan.littlecloud.util.JsonUtil;
import com.wekingtan.littlecloud.util.LogUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * com.wekingtan.littlecloud
 *
 * @author wekingtan
 * @date 2018/5/4
 */
public class SearchActivity extends AppCompatActivity {

    private static final String TAG = "SearchActivity";

    private int cityCount;

    private ImageView imageView;
    private SearchView mSearchView;
    public RecyclerView recyclerView;
    private List<BasicSearch> mBasicSearchList = new ArrayList<>();
    private BasicSearchAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        recyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        imageView = findViewById(R.id.img_back);
        mSearchView = findViewById(R.id.search_view);
        mSearchView.setIconified(false);
        mSearchView.onActionViewExpanded();
        mSearchView.setIconifiedByDefault(false);
        mSearchView.setQueryHint("输入地方查找天气");
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String weatherUrl = "https://search.heweather.com/find?location=" + newText + "&key=844b69d8a1bc4a7e9277d79879b8d59a&group=cn";
                LogUtil.i(TAG, "向和风天气请求数据的 URL：" + weatherUrl);
                HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(SearchActivity.this, "获取地方信息失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String responseText = response.body().string();
                        LogUtil.i(TAG, "和风天气 API 提供到的数据：" + responseText);
                        final HeWeatherCitySearch mCitySearch = JsonUtil.parseCityResponse(responseText);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (mCitySearch != null & "ok".equals(mCitySearch.status)) {
                                    clear();
                                    showCityInfo(mCitySearch);
                                } else if (mCitySearch != null & "unknown city".equals(mCitySearch.status)) {
                                    Toast.makeText(SearchActivity.this, "请输入地方名称", Toast.LENGTH_SHORT).show();
                                    clear();
                                } else if (mCitySearch != null & "param invalid".equals(mCitySearch.status)) {
                                    clear();
                                } else if (mCitySearch != null & "invalid key".equals(mCitySearch.status)) {
                                    Toast.makeText(SearchActivity.this, "请输入正确的地方名称", Toast.LENGTH_SHORT).show();
                                    clear();
                                }
                            }
                        });
                    }
                });
                return true;
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void showCityInfo(HeWeatherCitySearch mCitySearch) {
        for (BasicSearch basicSearch : mCitySearch.basicSearchList) {
            BasicSearch a = new BasicSearch(basicSearch.cityName, basicSearch.provinceName);
            LogUtil.i(TAG, "搜索到的城市:" + basicSearch.cityName + "，" + basicSearch.provinceName);
            mBasicSearchList.add(a);
        }
        adapter = new BasicSearchAdapter(mBasicSearchList);
        cityCount = adapter.getItemCount();
        LogUtil.i(TAG, "adapter 一共加载到的城市数量:" + toString().valueOf(cityCount));
        recyclerView.setAdapter(adapter);
    }

    private void clear() {
        mBasicSearchList.clear();
        adapter = new BasicSearchAdapter(mBasicSearchList);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }
}
