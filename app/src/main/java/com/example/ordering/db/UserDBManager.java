package com.example.ordering.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.ordering.UploadData;
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
    public static final String PHONE = "userTel";
    public static final String IMAGE = "userImage";
    public String uploadUrl = "http://49.234.101.49/ordering/update_userdata.php";
    private final Context context;
    private SQLiteDatabase db;
    private DBHelper dbhelper;
    private UploadData uploadData;

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
    }

    // 添加一个用户
    public void insert(User userinfo) {
        ContentValues cv = new ContentValues();
        cv.put(UID, userinfo.userID);
        cv.put(NAME, userinfo.userName);
        cv.put(PASSWORD, userinfo.userPwd);
        cv.put(PHONE, userinfo.userTel);
        cv.put(IMAGE, userinfo.userImage);

        db.insert(TABLE, null, cv);
        upLoad();
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
                new String[]{UID, NAME, PHONE, IMAGE},
                UID + "=" + uid + "",
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
            data.userImage = cursor.getString(cursor.getColumnIndex(IMAGE));
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
    // 修改用户名
    public void changeUsername(String uid,String userName) {
        String sql = "update userInfo set userName = '"+userName+"' where userID = "+uid;
        db.execSQL(sql);
        uploadData = new UploadData(uploadUrl);
        uploadData.uploadUserName(uid,userName);

    }
    // 修改密码
    public void changeUserPwd(String uid,String pwd) {
        db.execSQL("update userInfo set userPwd = '"+pwd+"' where userID = "+uid+";");
        uploadData = new UploadData(uploadUrl);
        uploadData.uploadUserPwd(uid,pwd);
    }
    // 修改电话信息
    public void changeUserTel(String uid,String tel) {
        db.execSQL("update userInfo set userTel = '"+tel+"' where userID = "+uid+";");
        uploadData = new UploadData(uploadUrl);
        uploadData.uploadUserTel(uid,tel);

    }

    // 修改照片
    public int changeUserImg(String uid,String imagePath) {
        ContentValues cv = new ContentValues();
        cv.put(IMAGE, imagePath);
        uploadData = new UploadData(uploadUrl);
        uploadData.uploadUserImage(uid,imagePath);
        return db.update(TABLE, cv, UID + "='" + uid + "'", null);
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
                    +user.getUserPWD()+"','"+user.getUserTel()+"'," + user.getUserImage()+"')");
        }
    }

    public SQLiteDatabase getDb(){
        return db;
    }

    //删除用户表信息
    public void deleteUserInfo() {
        db.execSQL("delete from userInfo");
        db.execSQL("update sqlite_sequence set seq=0 where name='userInfo'");
    }

    public void upLoad(){
        UploadData uploadData = new UploadData();
        uploadData.uploadUser();
    }

}

