package com.example.ordering.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.example.ordering.Base64Tool;
import com.example.ordering.R;
import com.example.ordering.structure.MyApplication;
import com.example.ordering.structure.Shop;
import com.example.ordering.structure.ShopImage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ShopDBManager {

    public static final int DBVERSION = 1;
    public static final String DB_NAME = "Ordering.db";
    public static final String TABLE = "shopInfo";
    public static final String SHOP_ID = "shopID";
    public static final String SHOP_NAME = "shopName";
    public static final String SHOP_IMAGE = "shopImage";
    public static final String SHOP_LOC = "shopLocation";
    public static final String SHOP_BRI = "shopBrief";

    private final Context context;
    private SQLiteDatabase db;
    private DBHelper dbhelper;

    public ShopDBManager(Context context) {
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
        /*new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    OkHttpClient client = new OkHttpClient();

                    //生成json文件
                    Request request1 = new Request.Builder()
                            .url("http://49.234.101.49/ordering/shopdata.php")
                            .build();

                    //解析对应json文件
                    Request request2 = new Request.Builder()
                            .url("http://49.234.101.49/ordering/json/shopinfo.json")
                            .build();

                    //解析对应json文件
                    Request request3 = new Request.Builder()
                            .url("http://49.234.101.49/ordering/json/shopimageinfo.json")
                            .build();
                    Response response1 = client.newCall(request1).execute();
                    Response response2 = client.newCall(request2).execute();
                    Response response3 = client.newCall(request3).execute();
                    String responseData2 = response2.body().string();
                    String responseData3 = response3.body().string();
                    System.out.println(responseData2);
                    parseJSONWithGSON(responseData2);
                    parseImageJSONWithGSON(responseData3);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();*/
    }

    public SQLiteDatabase getDb() {
        return db;
    }


    // 查询档口是否存在 存在true
    public boolean haveshop(String shopid) {
        Cursor cursor = db.query(TABLE, null, "shopId ='" + shopid + "'",
                null, null, null, null, null);
        int count = cursor.getCount();
        if (count == 0) {
            return false;
        } else {
            return true;
        }
    }


    //删除档口
    public void deleteshop(String shopid) {
        db.delete(TABLE, SHOP_ID + "='" + shopid + "'", null);
    }

    /*
    private void parseJSONWithGSON(String jsonData) {
        Gson gson = new Gson();
        List<Shop> shopList = gson.fromJson(jsonData, new TypeToken<List<Shop>>() {
        }.getType());
        db.delete(TABLE, null, null);
        for (Shop shop : shopList) {
            db.execSQL("insert into shopInfo(shopID,shopName,shopLocation,shopBrief,shopImage) values(" + shop.getShopID() + ",'" + shop.getShopName() + "','"
                    + shop.getShopLocation() + "','" + shop.getShopBrief() + "','" + shop.getShopImage() + "')");
        }
    }

    //图片json格式转换并存入目录
    private void parseImageJSONWithGSON(String jsonData) {
        Gson gson = new Gson();
        List<ShopImage> shopImageList = gson.fromJson(jsonData, new TypeToken<List<ShopImage>>() {
        }.getType());

        //db.delete(TABLE,null,null);
        for (ShopImage shopImage : shopImageList) {
            String path = getFilesPath(MyApplication.getContext())+"/shop"+shopImage.getShopID()+".jpg";
            boolean result = Base64Tool.setBase64ToImageFile(shopImage.getShopImage(),path);
            System.out.println("result "+result);
            System.out.println("path "+path);
            db.execSQL("update shopInfo set shopImage = '" + path + "' where shopID = '" + shopImage.getShopID() + "'");
        }
    }


    public String getFilesPath(Context context) {
        String filePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
        //外部存储可用
            filePath = context.getExternalFilesDir(null).getPath();
        } else {
        //外部存储不可用
            filePath = context.getFilesDir().getPath();
        }
        return filePath;
    }
*/
}
