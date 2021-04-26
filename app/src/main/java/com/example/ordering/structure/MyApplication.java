package com.example.ordering.structure;

import android.app.Application;
import android.content.Context;

public class MyApplication extends Application {

    private static Context context;

    public int state;
    public Boolean loginstatus = false;
    public String uid;

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
}
