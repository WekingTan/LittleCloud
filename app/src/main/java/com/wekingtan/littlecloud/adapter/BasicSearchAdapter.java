package com.wekingtan.littlecloud.adapter;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.wekingtan.littlecloud.MainActivity;
import com.wekingtan.littlecloud.R;
import com.wekingtan.littlecloud.gson.BasicSearch;

import java.util.List;

/**
 * com.wekingtan.littlecloud.adapter
 *
 * @author wekingtan
 * @date 2018/5/4
 */
public class BasicSearchAdapter extends RecyclerView.Adapter<BasicSearchAdapter.ViewHolder> {

    private List<BasicSearch> mBasicSearchList;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        /**
         * API23+ 时，inflate() 方法传入三个参数后，会让单个 item 占据整个界面
         */
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.city_search_item, null);
        final ViewHolder holder = new ViewHolder(view);
        holder.searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                BasicSearch basicSearch = mBasicSearchList.get(position);
                Toast.makeText(v.getContext(), basicSearch.cityName, Toast.LENGTH_SHORT).show();
                /**
                 * 选中城市，并且清空活动栈，把之前的 activity 清除
                 */
                Intent intent = new Intent(v.getContext(), MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("City", basicSearch.cityName);
                v.getContext().startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        BasicSearch basicSearch = mBasicSearchList.get(position);
        holder.citySearchName.setText(basicSearch.cityName);
        holder.provinceSearchName.setText(basicSearch.provinceName);
    }


    @Override
    public int getItemCount() {
        return mBasicSearchList.size();
    }

    public BasicSearchAdapter(List<BasicSearch> basicList) {
        mBasicSearchList = basicList;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View searchView;
        TextView citySearchName;
        TextView provinceSearchName;

        public ViewHolder(View view) {
            super(view);
            searchView = view;
            citySearchName = view.findViewById(R.id.city_search_text);
            provinceSearchName = view.findViewById(R.id.province_search_text);
        }
    }
}
