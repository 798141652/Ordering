package com.example.ordering;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.ordering.structure.MyApplication;

public class LaunchActivity extends AppCompatActivity {

    private IntentFilter intentFilter;
    private NetworkChangeReceiver networkChangeReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ConnectivityManager connectivityManager = (ConnectivityManager) MyApplication.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        networkChangeReceiver = new NetworkChangeReceiver();
        registerReceiver(networkChangeReceiver,intentFilter);

        //加载启动图片
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        setContentView(R.layout.activity_launch);
        //后台处理耗时任务
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (networkInfo != null && networkInfo.isAvailable()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                RefreshData refreshData = new RefreshData();
                                refreshData.refresh();
                                Thread.sleep(2000);
                                Toast.makeText(LaunchActivity.this, "网络可用", Toast.LENGTH_SHORT).show();
                                //跳转至 MainActivity
                                Intent intent = new Intent(LaunchActivity.this, MainActivity.class);
                                startActivity(intent);
                                //结束当前的 Activity
                                LaunchActivity.this.finish();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LaunchActivity.this, "网络不可用", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        }).start();


    }

    @Override
    protected void onResume() {
        super.onResume();
        ConnectivityManager connectivityManager = (ConnectivityManager) MyApplication.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();



        new Thread(new Runnable() {
            @Override
            public void run() {
                if (networkInfo != null && networkInfo.isAvailable()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                RefreshData refreshData = new RefreshData();
                                refreshData.refresh();
                                Thread.sleep(2000);
                                Toast.makeText(LaunchActivity.this, "网络可用", Toast.LENGTH_SHORT).show();
                                //跳转至 MainActivity
                                Intent intent = new Intent(LaunchActivity.this, MainActivity.class);
                                startActivity(intent);
                                //结束当前的 Activity
                                LaunchActivity.this.finish();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LaunchActivity.this, "网络不可用", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(networkChangeReceiver);
    }
}