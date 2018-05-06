package com.wekingtan.littlecloud;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.wekingtan.littlecloud.util.LogUtil;
import com.wekingtan.littlecloud.util.NetworkConnectedUtil;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MainInfoFragment.OnFragmentInteractionListener {

    private static final String TAG = "MainActivity";

    public LocationClient mLocationClient;

    private LinearLayout mLinearLayout;

    private Toolbar mToolbar;

    private TextView mTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.i(TAG, "我是onCreate");
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(new MyLocationListener());
        setContentView(R.layout.activity_main);

        mLinearLayout = findViewById(R.id.layout_location);
        mToolbar = findViewById(R.id.toolbar_main);
        mTextView = findViewById(R.id.tv_location);

        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.
                permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.
                permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.
                permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(MainActivity.this, permissions, 1);
        } else {
            Intent intent = getIntent();
            String data = intent.getStringExtra("City");
            if (data != null) {
                mTextView.setText(data);
                replaceFragment(MainInfoFragment.newInstance(data));
            } else {
                requestLocation();
            }
        }

        mLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });
    }

    private void requestLocation() {
        LogUtil.i(TAG, "百度定位开始");
        /*if (NetworkConnectedUtil.networkConnected(this) == false) {
            LogUtil.i(TAG, "网络不可以用啊");
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            String locations = prefs.getString("location", null);
            String districts = prefs.getString("district", null);
            LogUtil.i(TAG, locations + districts);
            if (locations != null & districts != null) {
                mTextView.setText(locations);
                replaceFragment(MainInfoFragment.newInstance(districts));
            }
        } else {*/
        initLocation();
        mLocationClient.start();
        //}
    }

    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        //option.setScanSpan(20000);
        /**
         * 设置获取当前位置详细的地址信息， 否则只能够获取经纬度
         */
        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.i(TAG, "我是onDestroy");
        mLocationClient.stop();
    }

    /**
     * Callback for the result from requesting permissions. This method
     * is invoked for every call on {@link #requestPermissions(String[], int)}.
     * <p>
     * <strong>Note:</strong> It is possible that the permissions request interaction
     * with the user is interrupted. In this case you will receive empty permissions
     * and results arrays which should be treated as a cancellation.
     * </p>
     *
     * @param requestCode  The request code passed in {@link #requestPermissions(String[], int)}.
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either {@link PackageManager#PERMISSION_GRANTED}
     *                     or {@link PackageManager#PERMISSION_DENIED}. Never null.
     * @see #requestPermissions(String[], int)
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this, "必须同意所有权限才能使用本程序", Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                    requestLocation();
                } else {
                    Toast.makeText(this, "发生未知错误", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }
    }

    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            StringBuilder currentPosition = new StringBuilder();
            //currentPosition.append("纬度：").append(bdLocation.getLatitude()).append("\n");
            //currentPosition.append("经线：").append(bdLocation.getLongitude()).append("\n");
            //currentPosition.append("国家：").append(bdLocation.getCountry()).append("\n");
            //currentPosition.append("省：").append(bdLocation.getProvince()).append("\n");
            currentPosition.append(bdLocation.getCity());
            currentPosition.append(bdLocation.getDistrict());
            currentPosition.append(bdLocation.getStreet()).append("\n");
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit();
            editor.putString("location", currentPosition.toString());
            editor.putString("district", bdLocation.getDistrict());
            editor.apply();
            LogUtil.i(TAG, currentPosition.toString());
            mTextView.setText(currentPosition);
            replaceFragment(MainInfoFragment.newInstance(bdLocation.getDistrict()));
        }
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frag_main, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onFragmentInteraction() {
        LogUtil.i(TAG, "竞哥让我更新一下数据的");
        /**
         *因为需要把 LocationClient 先 stop，才能再次 start
         */
        mLocationClient.stop();
        requestLocation();
    }
}
