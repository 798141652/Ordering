package com.example.ordering.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.ordering.structure.Cart;

public class CartDBManager {
    public static final int DBVERSION = 1;

    public static final String DB_NAME = "Ordering.db";
    public static final String CART_TABLE = "userCart";
    public static final String CART_UID = "userID";
    public static final String CART_ID = "cartID";
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
    public long insert(Cart cart) {
        ContentValues cv = new ContentValues();
        cv.put(CART_UID, cart.userID);
        cv.put(CART_DISH_ID, cart.dishID);
        cv.put(CART_DISH_NAME, cart.dishName);
        cv.put(CART_DISH_NUM, cart.dishNum);
        cv.put(CART_DISH_PRICE, cart.dishPrice);
        return db.insert(CART_TABLE, null, cv);
    }

    //删除一个菜
    public void deleteonedish(Cart cart, int uid) {
        db.delete(CART_TABLE, CART_DISH_ID + "='" + cart.dishID + "'and " + CART_UID + "='" + uid + "'", null);
    }

    //删除一个用户的菜单
    public void deletedishByUid(int uid) {
        db.delete(CART_TABLE, CART_UID + "='" + uid + "'", null);
    }


    //查询一个菜单
    public Cart queryonedish(int uid, int id) {
        Cart cart = new Cart();
        Cursor cursor = db.query(CART_TABLE,
                new String[]{CART_UID, CART_ID, CART_DISH_ID,
                        CART_DISH_NAME, CART_DISH_NUM, CART_DISH_PRICE},
                CART_UID + "='" + uid + "'and " + CART_DISH_ID + "='" +
                        id + "'", null, null, null, null, null);
        int count = cursor.getCount();
        if (count == 0 || !cursor.moveToFirst()) {
            System.out.println("数据表中没有数据");
            return null;
        } else {
            cart.userID = uid;
            cart.cartID = cursor.getInt(cursor.getColumnIndex(CART_ID));
            cart.dishID = cursor.getInt(cursor.getColumnIndex(CART_DISH_ID));
            cart.dishName = cursor.getString(cursor.getColumnIndex(CART_DISH_NAME));
            cart.dishNum = cursor.getInt(cursor.getColumnIndex(CART_DISH_NUM));
            cart.dishPrice = cursor.getDouble(cursor.getColumnIndex(CART_DISH_PRICE));
            return cart;
        }
    }

    //查询一个菜单
    public int querydishIDbyName(int uid, String dishname) {
        Cursor cursor = db.query(CART_TABLE,
                new String[]{CART_UID, CART_ID, CART_DISH_ID,
                        CART_DISH_NAME, CART_DISH_NUM, CART_DISH_PRICE},
                CART_UID + "='" + uid + "'and " + CART_DISH_NAME + "='" + dishname + "'",
                null, null, null, null, null);
        int count = cursor.getCount();
        if (count == 0) {
            System.out.println("数据表中没有数据");
            return 0;
        } else {
            cursor.moveToFirst();
            return cursor.getInt(cursor.getColumnIndex(CART_ID));
        }
    }

    //查询某个用户的菜单
    public Cart[] querydishByUser(int uid) {
        Cursor cursor = db.query(CART_TABLE, new String[]{CART_UID, CART_ID, CART_DISH_ID,
                        CART_DISH_NAME, CART_DISH_NUM, CART_DISH_PRICE}, CART_UID + "='" + uid + "'",
                null, null, null, null, null
        );
        int count = cursor.getCount();
        if (count == 0 || !cursor.moveToFirst()) {
            return null;
        } else {
            Cart[] dishlist = new Cart[count];
            for (int i = 0; i < count; i++) {
                dishlist[i] = new Cart();
                dishlist[i].userID = uid;
                dishlist[i].dishID = cursor.getInt(cursor.getColumnIndex(CART_DISH_ID));
                dishlist[i].cartID = cursor.getInt(cursor.getColumnIndex(CART_ID));
                dishlist[i].dishName = cursor.getString(cursor.getColumnIndex(CART_DISH_NAME));
                dishlist[i].dishNum = cursor.getInt(cursor.getColumnIndex(CART_DISH_NUM));
                dishlist[i].dishPrice = cursor.getDouble(cursor.getColumnIndex(CART_DISH_PRICE));
                cursor.moveToNext();
            }
            return dishlist;
        }
    }

    //更新一条记录的数量
    public int updateNumemenu(int userID, int dishID, int sdishnum) {
        ContentValues cv = new ContentValues();
        cv.put(CART_DISH_NUM, sdishnum);
        System.out.println("userId = "+userID+"\ndishID = "+dishID+"\nnum = "+sdishnum);
        return db.update(CART_TABLE, cv, CART_UID + "='" + userID + "'and " + CART_DISH_ID + "='" + dishID + "'" + CART_STATUS + "= '0'", null);
    }
}
