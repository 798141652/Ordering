package com.example.ordering.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.ordering.structure.Shop;
import com.example.ordering.structure.User;

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
    }

    public SQLiteDatabase getDb() {
        return db;
    }


    // 查询档口是否存在 存在true
    public boolean haveshop(String shopid) {
        Cursor cursor = db.query(TABLE, null, "shopId ='" + shopid + "'",
                null, null, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        if (count == 0) {
            return false;
        } else {
            return true;
        }
    }


    //删除档口表信息
    public void deleteShopInfo() {
        db.execSQL("delete from shopInfo");
        db.execSQL("update sqlite_sequence set seq=0 where name='shopInfo'");
    }

    //通过档口ID查找档口信息
    public Shop getShopInfoByID(int shopID) {
        Cursor cursor = db.query(TABLE,
                null,
                "shopID" + "=" + shopID + "",
                null, null, null, null, null);
        Shop shop = new Shop();// 存储数据
        int count = cursor.getCount();
        if (count == 0 || !cursor.moveToFirst()) {
            System.out.println("数据表中没有数据");
            cursor.close();
            return null;
        } else {
            shop.shopID = shopID;
            shop.shopName = cursor.getString(cursor.getColumnIndex("shopName"));
            shop.shopImage = cursor.getString(cursor.getColumnIndex("shopImage"));
            shop.shopBrief = cursor.getString(cursor.getColumnIndex("shopBrief"));
            shop.shopLocation = cursor.getString(cursor.getColumnIndex("shopLocation"));
            cursor.close();
            return shop;
        }
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
