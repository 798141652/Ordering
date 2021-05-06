package com.example.ordering.ui.settings;

import android.database.Cursor;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ordering.db.UserDBManager;
import com.example.ordering.structure.MyApplication;
import com.example.ordering.structure.User;

public class SettingsViewModel extends ViewModel {

    private User user;

    private UserDBManager userDBManager;

    private String userName;

    private String userTel;

    //private String userID;
    public SettingsViewModel() {
    }

    public User getUser(String uID) {
        userDBManager = new UserDBManager(MyApplication.getContext());
        userDBManager.open();
        user = userDBManager.getInfo(uID);
        return user;
    }

}