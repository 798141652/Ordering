package com.example.ordering.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.ordering.structure.Shop;
import com.example.ordering.structure.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class UserDBManager {
    public static final int DBVERSION = 1;
    public static final String DB_NAME = "Ordering.db";
    public static final String TABLE = "userInfo";
    public static final String UID = "userID";
    public static final String NAME = "userName";
    public static final String PASSWORD = "userPwd";
    public static final String GENDER = "userGender";
    public static final String PHONE = "userTel";
    public static final String IMAGE = "userImage";
    public static final String ADDRESS = "userAddress";
    public static final String TYPE = "userType";
    private final Context context;
    private SQLiteDatabase db;
    private DBHelper dbhelper;

    public UserDBManager(Context context) {
        this.context = context;
    }

    public void open() {
        dbhelper = new DBHelper(context, DB_NAME, null, DBVERSION);
        try {
            db = dbhelper.getWritableDatabase();
        } catch (Exception e) {
            db = dbhelper.getReadableDatabase();
        }

        //获取服务器数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                /*
                try{
                    OkHttpClient client = new OkHttpClient();


                    //生成json文件
                    Request request1 = new Request.Builder()
                            .url("http://49.234.101.49/ordering/userdata.php")
                            .build();
                    //解析对应json文件
                    Request request2 = new Request.Builder()
                            .url("http://49.234.101.49/ordering/json/userinfo.json")
                            .build();
                    System.out.println("shopJson"+request1);
                    Response response1 = client.newCall(request1).execute();
                    Response response2 = client.newCall(request2).execute();
                    String responseData = response2.body().string();
                    parseJSONWithGSON(responseData);
                }catch (Exception e){
                    e.printStackTrace();
                }*/
            }
        }).start();

    }

    // 添加一个用户
    public long insert(User userinfo) {
        ContentValues cv = new ContentValues();
        cv.put(UID, userinfo.userID);
        cv.put(NAME, userinfo.userName);
        cv.put(PASSWORD, userinfo.userPwd);
        cv.put(PHONE, userinfo.userTel);
        cv.put(IMAGE, userinfo.userImage);
        cv.put(TYPE, userinfo.userType);
        return db.insert(TABLE, null, cv);
    }


    // 查询用户是否存在 存在true
    public boolean haveUid(String uid) {
        Cursor cursor = db.query(TABLE,
                new String[]{UID, PASSWORD},
                UID + "='" + uid + "'",
                null, null, null, null, null);
        int count = cursor.getCount();
        if (count == 0) {
            System.out.println("用户不存在");
            return false;
        } else {
            return true;
        }
    }

    // 检查密码
    public boolean checkKey(String uid, String password) {
        Cursor cursor = db.query(TABLE,
                new String[]{UID, PASSWORD},
                UID + "='" + uid + "' and " + PASSWORD + "='" + password + "'",
                null, null, null, null, null);
        int count = cursor.getCount();
        if (count == 0) {
            System.out.println("密码错误");
            return false;
        } else {
            return true;
        }
    }

    // 获取User信息:用户名 电话号码 通讯地址
    public User getInfo(String uid) {
        Cursor cursor = db.query(TABLE,
                new String[]{UID, NAME, PHONE, ADDRESS},
                UID + "='" + uid + "'",
                null, null, null, null, null);
        User data = new User();// 存储数据
        int count = cursor.getCount();
        if (count == 0 || !cursor.moveToFirst()) {
            System.out.println("数据表中没有数据");
            return null;
        } else {
            data.userID = uid;
            data.userName = cursor.getString(cursor.getColumnIndex(NAME));
            data.userTel = cursor.getString(cursor.getColumnIndex(PHONE));
            return data;
        }
    }

    // 修改信息
    public int modifyInfo(User user) {
        ContentValues cv = new ContentValues();
        cv.put(PASSWORD, user.userPwd);
        cv.put(PHONE, user.userTel);
        return db.update(TABLE, cv, UID + "='" + user.userID + "'", null);
    }

    //删除用户
    public void deleteuser(String uid) {
        db.delete(TABLE, UID + "='" + uid , null);
    }

    private void parseJSONWithGSON(String jsonData){
        Gson gson = new Gson();
        List<User> userList = gson.fromJson(jsonData,new TypeToken<List<User>>(){}.getType());
//        db.delete(TABLE,null,null);
        for(User user :userList){
            db.execSQL("insert into userInfo(userID,userName,userPwd,userTel,userImage,userType) " +
                    "values("+user.getUserID()+",'"+user.getUserName()+"','"
                    +user.getUserPWD()+"','"+user.getUserTel()+"'," + user.getUserImage()+",'"
                    +user.getUserType()+"')");
        }
    }
}

