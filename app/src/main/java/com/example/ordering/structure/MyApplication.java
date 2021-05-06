package com.example.ordering.structure;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.ArrayList;
import java.util.List;

public class MyApplication extends Application {

    private static Context context;

    private List<List<Shop>> shitangList = new ArrayList<List<Shop>>();
    //private List<Car> shopList = new ArrayList<>();
    //private List<Shop> shopList = new ArrayList<>();

    public int state;
    public Boolean loginstatus = false;
    public String uid;

    public List<List<Shop>> getshitangList() {
        return shitangList;
    }

    public void setshitangList(List<List<Shop>> shopList) {
        this.shitangList = shopList;
    }

    public int getState() {
        return this.state;
    }

    public void setState(int sstate) {
        this.state = sstate;
    }

    public void setLoginstatus(Boolean sstate) {
        this.loginstatus = sstate;
    }

    public Boolean getLoginState() {
        return this.loginstatus;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }



    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getContext(){
        return context;
    }

    public boolean isConnectInternet() {

        ConnectivityManager conManager=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE );

        NetworkInfo networkInfo = conManager.getActiveNetworkInfo();

        if (networkInfo != null ){ // 注意，这个判断一定要的哦，要不然会出错

            return networkInfo.isAvailable();

        }
        return false ;
    }
}
