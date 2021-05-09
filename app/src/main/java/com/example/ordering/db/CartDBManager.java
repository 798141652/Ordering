package com.example.ordering.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.ordering.UploadData;
import com.example.ordering.structure.Cart;
import com.example.ordering.structure.Dish;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CartDBManager {
    public static final int DBVERSION = 1;

    public static final String DB_NAME = "Ordering.db";
    public static final String CART_TABLE = "cartInfo";
    public static final String CART_UID = "userID";
    public static final String CART_ID = "cartID";
    public static final String CART_SHOP_ID = "dishShop";
    public static final String CART_DISH_ID = "dishID";
    public static final String CART_DISH_NAME = "dishName";
    public static final String CART_DISH_NUM = "dishNum";
    public static final String CART_DISH_PRICE = "dishPrice";
    public static final String CART_PRICE = "cartPrice";
    public static final String CART_STATUS = "cartStatus";
    public static final String CART_TIME = "cartTime";
    private final Context context;
    private SQLiteDatabase db;
    private DBHelper dbhelper;
    private UploadData uploadData;

    public CartDBManager(Context context) {
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


//添加一个菜单
    public void insert(Cart cart) {
        ContentValues cv = new ContentValues();
        cv.put(CART_UID, cart.cartUserID);
        cv.put(CART_DISH_ID, cart.cartDishID);
        cv.put(CART_SHOP_ID,cart.cartShopID);
        cv.put(CART_DISH_NAME, cart.cartDishName);
        cv.put(CART_DISH_NUM, cart.cartDishNum);
        cv.put(CART_DISH_PRICE, cart.cartDishPrice);
        cv.put(CART_STATUS,cart.cartStatus);
        db.insert(CART_TABLE, null, cv);
        uploadData = new UploadData();
        uploadData.uploadCart();
    }

    //删除一个菜
    public void deleteonedish(Cart cart, int uid) {
        db.delete(CART_TABLE, CART_STATUS +"='0' and "+ CART_DISH_ID + "=" + cart.cartDishID + " and " + CART_UID + "='" + uid + "'", null);
        uploadData = new UploadData();
        uploadData.uploadCart();
    }

    //删除一个用户的菜单
    public void deletedishByUid(int uid) {
        db.delete(CART_TABLE, CART_UID + "='" + uid + "'", null);
        uploadData = new UploadData();
        uploadData.uploadCart();
    }


    //查询一个菜单项
    public Cart queryonedish(int uid, int id) {
        Cart cart = new Cart();
        String sql = "select * from cartInfo where userID = "+uid+" and dishID = "+id+" and cartStatus ='0'";
        Cursor cursor = db.rawQuery(sql,null);
        int count = cursor.getCount();

        if (count == 0 || !cursor.moveToFirst()) {
            System.out.println("数据表中没有数据");
            return null;
        } else {
            cart.cartUserID = uid;
            cart.cartID = cursor.getInt(cursor.getColumnIndex(CART_ID));
            cart.cartShopID = cursor.getInt(cursor.getColumnIndex(CART_SHOP_ID));
            cart.cartDishID = cursor.getInt(cursor.getColumnIndex(CART_DISH_ID));
            cart.cartDishName = cursor.getString(cursor.getColumnIndex(CART_DISH_NAME));
            cart.cartDishNum = cursor.getInt(cursor.getColumnIndex(CART_DISH_NUM));
            cart.cartDishPrice = cursor.getDouble(cursor.getColumnIndex(CART_DISH_PRICE));
            cart.cartStatus = cursor.getString(cursor.getColumnIndex(CART_STATUS));
            cursor.close();
            return cart;
        }
    }

    //查询一个菜单项
    public int querydishIDbyName(int uid, String dishname) {
        Cursor cursor = db.query(CART_TABLE,
                new String[]{CART_UID, CART_ID, CART_SHOP_ID,CART_DISH_ID,
                        CART_DISH_NAME, CART_DISH_NUM, CART_DISH_PRICE},
                CART_UID + "='" + uid + "'and " + CART_DISH_NAME + "='" + dishname + "'",
                null, null, null, null, null);
        int count = cursor.getCount();
        if (count == 0) {
            System.out.println("数据表中没有数据");
            cursor.close();
            return 0;
        } else {
            cursor.moveToFirst();
            int dishID = cursor.getInt(cursor.getColumnIndex(CART_ID));
            cursor.close();
            return dishID;

        }
    }

    public List<Cart> queryOrderByUID(int uid){
        List<Cart> cartList = new ArrayList<>();
        List<List> orderLisr = new ArrayList<>();
        String sql = "select distinct ";
        return cartList;
    }

    public Dish queryDishByDishID(int dishID){
        Dish dish = new Dish();
        Cursor cursor = db.query("dishInfo",null,
                "dishID" + "='" + dishID,
                null, null, null, null, null);
        int count = cursor.getCount();
        if (count == 0 || !cursor.moveToFirst()) {
            System.out.println("数据表中没有数据");
            cursor.close();
            return null;
        } else {
            dish.dishID = dishID;
            dish.shopID = cursor.getInt(cursor.getColumnIndex("shopID"));
            dish.dishName = cursor.getString(cursor.getColumnIndex("dishName"));
            dish.dishPrice = cursor.getDouble(cursor.getColumnIndex("dishPrice"));
            cursor.close();
            return dish;
        }
    }

    //查询某个用户的菜单
    public Cart[] queryCartByUser(int uid) {
        Cursor cursor = db.query(CART_TABLE, null, CART_UID + "='" + uid + "' and "+CART_STATUS+"='0'",
                null, null, null, null, null
        );
        int count = cursor.getCount();
        if (count == 0 || !cursor.moveToFirst()) {
            cursor.close();
            return null;
        } else {
            Cart[] cartlist = new Cart[count];
            for (int i = 0; i < count; i++) {
                cartlist[i] = new Cart();
                cartlist[i].cartUserID = uid;
                cartlist[i].cartID = cursor.getInt(cursor.getColumnIndex(CART_ID));
                cartlist[i].cartDishID = cursor.getInt(cursor.getColumnIndex(CART_DISH_ID));
                cartlist[i].cartShopID = cursor.getInt(cursor.getColumnIndex(CART_SHOP_ID));
                cartlist[i].cartDishName = cursor.getString(cursor.getColumnIndex(CART_DISH_NAME));
                cartlist[i].cartDishNum = cursor.getInt(cursor.getColumnIndex(CART_DISH_NUM));
                cartlist[i].cartDishPrice = cursor.getDouble(cursor.getColumnIndex(CART_DISH_PRICE));
                cursor.moveToNext();
            }
            cursor.close();
            return cartlist;
        }
    }

    //更新一条记录的数量
    public void updateNumemenu(int userID, int dishID, int sdishnum) {
        ContentValues cv = new ContentValues();
        cv.put(CART_DISH_NUM, sdishnum);
        db.update(CART_TABLE, cv, CART_UID + "='" + userID + "'and " + CART_DISH_ID + "='" + dishID + "' and " + CART_STATUS + "= '0'", null);
        uploadData = new UploadData();
        uploadData.uploadCart();
    }

    public SQLiteDatabase getDb(){
        return this.db;
    }

    //购物车转为订单
    public void cartToOrder(int userID,Double TotalPrice,int cartID){
        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        String orderTime = String.valueOf(formatter.format(date));
        String orderID = String.valueOf(userID)+orderTime;
        orderID=orderID.replace("-","");
        orderID=orderID.replace(":","");
        orderID=orderID.replace(" ","");
        String sql = "update cartInfo set cartPrice = "+TotalPrice+",cartStatus = '1',orderID = '"+orderID+
        "',cartTime = '"+orderTime+"' where userID = "+userID+" and cartStatus = '0' and cartID = "+cartID;
        db.execSQL(sql);
    }

    //购物车转为订单
    public void orderCancel(String orderID){
        String sql = "delete from cartInfo where orderID = '"+orderID+"' and cartStatus ='1'";
        db.execSQL(sql);
    }

    public void uploadData(){
        uploadData = new UploadData();
        uploadData.uploadCart();
    }

    //删除菜单表信息
    public void deleteCartInfo() {
        db.execSQL("delete from cartInfo");
        db.execSQL("update sqlite_sequence set seq=0 where name = 'cartInfo'");
    }

    public List<Dish> FashionDishList(){
        Cursor cursor = db.rawQuery("SELECT count(dishID) num, dishID from cartInfo group by dishID order by count(dishID) desc",null);
        Cursor cursor1 = db.rawQuery("SELECT dishID from dishInfo",null);
        List<Dish> dishList = new ArrayList<>();
        int num = cursor.getCount();
        if(cursor.moveToFirst()&&cursor.getCount()>=4){
            int i = 0;
            do {
                Dish dish = new Dish();
                dish.dishID = cursor.getInt(cursor.getColumnIndex("dishID"));
                dishList.add(dish);
                i++;
            }while(cursor.moveToNext());
        }else if(cursor1.moveToFirst()){
            int i = 0;
            do{
                Dish dish = new Dish();
                dish.dishID = cursor1.getInt(cursor1.getColumnIndex("dishID"));
                dishList.add(dish);
                i++;
            }while (cursor1.moveToNext()&&i<4);
        }
        cursor.close();
        return dishList;
    }
}
