package com.example.ordering.db;

import android.content.Context;
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

        //获取服务器数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    OkHttpClient client = new OkHttpClient();

                    //生成json文件
                    Request request1 = new Request.Builder()
                            .url("http://49.234.101.49/ordering/dishdata.php")
                            .build();
                    //解析对应json文件
                    Request request2 = new Request.Builder()
                            .url("http://49.234.101.49/ordering/json/dishinfo.json")
                            .build();
                    Response response1 = client.newCall(request1).execute();
                    Response response2 = client.newCall(request2).execute();
                    String responseData = response2.body().string();
                    parseJSONWithGSON(responseData);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();

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
}
