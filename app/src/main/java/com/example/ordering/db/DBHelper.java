package com.example.ordering.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.ordering.structure.Shop;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DBHelper extends SQLiteOpenHelper {

    public static final int DBVERSION = 1;
    public static final String DB_NAME = "Ordering.db";
    public static final String SHOP_TABLE = "shopInfo";
    public static final String SHOP_ID = "shopID";
    public static final String SHOP_NAME = "shopName";
    public static final String SHOP_IMAGE = "shopImage";
    public static final String SHOP_LOC = "shopLocation";
    public static final String SHOP_BRI = "shopBrief";


    public static final String DISH_TABLE = "dishInfo";
    public static final String DISH_ID = "dishID";
    public static final String DISH_SHOP_ID = "shopID";
    public static final String DISH_NAME = "dishName";
    public static final String DISH_PRICE = "dishPrice";
    public static final String DISH_TYPE = "dishType";
    public static final String DISH_IMAGE = "dishImage";


    public static final String USER_TABLE = "userInfo";
    public static final String UID = "userID";
    public static final String NAME = "userName";
    public static final String UKEY = "userPwd";
    public static final String PHONE = "userTel";
    public static final String IMAGE = "userImage";

    public static final String CART_TABLE = "cartInfo";
    public static final String CART_UID = "userID";
    public static final String CART_ORDER_ID = "orderID";
    public static final String CART_ID = "cartID";
    public static final String CART_DISH_SHOP = "dishShop";
    public static final String CART_DISH_ID = "dishID";
    public static final String CART_DISH_NAME = "dishName";
    public static final String CART_DISH_NUM = "dishNum";
    public static final String CART_DISH_PRICE = "dishPrice";
    public static final String CART_PRICE = "cartPrice";
    public static final String CART_STATUS = "cartStatus";
    public static final String CART_TIME = "cartTime";

    public static final String COMMENT_TABLE = "commentInfo";
    public static final String COMMENT_ID = "commentID";
    public static final String COMMENT_UID = "userID";
    public static final String COMMENT_SID = "shopID";
    public static final String COMMENT_OID = "orderID";
    public static final String COMMENT_DID = "dishID";
    public static final String COMMENT_TYPE = "commentType";
    public static final String COMMENT = "comment";
    public static final String COMMENT_TIME = "commentTime";



    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + SHOP_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + DISH_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + CART_TABLE);
        onCreate(db);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {



        //db.execSQL("DROP TABLE IF EXISTS " + SHOP_TABLE);
        String sql = "create table if not exists " + SHOP_TABLE + "(" + SHOP_ID + " integer primary key," +
                SHOP_NAME + " text," + SHOP_IMAGE + " text," + SHOP_LOC + " text,"  + SHOP_BRI + " text);";
        db.execSQL(sql);

        //db.execSQL("DROP TABLE IF EXISTS " + DISH_TABLE);
        sql = "create table if not exists " + DISH_TABLE + "(" + DISH_ID + " integer primary key," +
                DISH_SHOP_ID + " integer," + DISH_NAME + " text,"  + DISH_IMAGE + " text,"+
                DISH_PRICE + " float, " + DISH_TYPE + " text);";
        db.execSQL(sql);

        //db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE);
        sql = "create table if not exists " + USER_TABLE + "(" + UID + " integer primary key," +
                NAME + " text," + UKEY + " text,"   + PHONE + " text," + IMAGE + " text);";
        db.execSQL(sql);

        //db.execSQL("DROP TABLE IF EXISTS " + CART_TABLE);
        sql = "create table " + CART_TABLE + "(" + CART_ID + " integer primary key AUTOINCREMENT," +CART_ORDER_ID+" text,"+
                CART_UID + " integer," + CART_DISH_ID + " integer," + CART_DISH_SHOP+" integer, "+CART_DISH_NAME + " text, " +
                CART_DISH_NUM + " integer, " + CART_DISH_PRICE + " float,"+ CART_PRICE + " float default 0," + CART_STATUS + " text ," +
                CART_TIME + " text);";
        db.execSQL(sql);

        //db.execSQL("DROP TABLE IF EXISTS " + COMMENT_TABLE);
        sql = "create table " + COMMENT_TABLE + "(" + COMMENT_ID + " integer primary key AUTOINCREMENT," +
                COMMENT_UID + " integer," + COMMENT_OID + " integer,"+COMMENT_SID+" integer, " +COMMENT_DID+" integer, "+ COMMENT_TYPE+" text, "+COMMENT
                + " text, "+COMMENT_TIME+" text);";
        db.execSQL(sql);

        db.execSQL("delete from shopInfo");
        db.execSQL("update sqlite_sequence set seq=0 where name='shopInfo'");

        db.execSQL("delete from dishInfo");
        db.execSQL("update sqlite_sequence set seq=0 where name='dishInfo'");

        db.execSQL("delete from userInfo");
        db.execSQL("update sqlite_sequence set seq=0 where name='userInfo'");

        db.execSQL("delete from cartInfo");
        db.execSQL("update sqlite_sequence set seq=0 where name='cartInfo'");

        db.execSQL("delete from commentInfo");
        db.execSQL("update sqlite_sequence set seq=0 where name='commentInfo'");
    }

    @Override
    public synchronized void close() {
        super.close();
    }
}
