package com.example.ordering.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.ordering.R;
import com.example.ordering.structure.Dish;
import com.example.ordering.structure.Shop;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DishDBManager {


    public static final int DBVERSION = 1;
    public static final String DB_NAME = "Ordering.db";
    public static final String TABLE = "dishInfo";
    public static final String DISH_ID = "dishID";
    public static final String SHOP_ID = "shopID";
    public static final String DISH_NAME = "dishName";
    public static final String DISH_PRICE = "dishPrice";
    public static final String DISH_TYPE = "dishType";
    public static final String DISH_IMAGE = "dishImage";

    private final Context context;
    private SQLiteDatabase db;
    private DBHelper dbhelper;


    public DishDBManager(Context context) {
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

    public SQLiteDatabase getDb(){
        return db;
    }

    //服务器json数据传入sqlite数据库
    private void parseJSONWithGSON(String jsonData){
        Gson gson = new Gson();
        List<Dish> dishList = gson.fromJson(jsonData,new TypeToken<List<Dish>>(){}.getType());
        db.delete(TABLE,null,null);
        for(Dish dish :dishList){
            db.execSQL("insert into dishInfo(dishID,shopID,dishName,dishImage,dishPrice,dishType) values("+dish.getDishID()+","+dish.getShopID()+",'"
                  +dish.getDishName()+"','"+dish.getDishImage()+"',"+ dish.getDishPrice()+",'"+dish.getDishType()+"')");
        }
    }

    public String getDishImageByID(int dishID){
        Cursor cursor = db.rawQuery("select dishImage from dishInfo where dishId = "+dishID,null);
        int count = cursor.getCount();
        if (count == 0 || !cursor.moveToFirst()) {
            System.out.println("数据表中没有数据");
            return null;
        } else {
            return cursor.getString(cursor.getColumnIndex("dishImage"));
        }
    }

    public Dish getDishByID(int dishID){
        Cursor cursor = db.rawQuery("select * from dishInfo where dishId = "+dishID,null);
        int count = cursor.getCount();
        if (count == 0 || !cursor.moveToFirst()) {
            System.out.println("数据表中没有数据");
            return null;
        } else {
            Dish dish = new Dish();
            dish.dishID = cursor.getInt(cursor.getColumnIndex("dishID"));
            dish.dishName = cursor.getString(cursor.getColumnIndex("dishName"));
            dish.dishImage = cursor.getString(cursor.getColumnIndex("dishImage"));
            dish.shopID = cursor.getInt(cursor.getColumnIndex("shopID"));
            dish.dishPrice = cursor.getDouble(cursor.getColumnIndex("dishPrice"));
            dish.dishType = cursor.getString(cursor.getColumnIndex("dishType"));
            return dish;
        }
    }

    //删除菜品表信息
    public void deleteDishInfo() {
        db.execSQL("delete from dishInfo");
        db.execSQL("update sqlite_sequence set seq=0 where name='dishInfo'");
    }
}
