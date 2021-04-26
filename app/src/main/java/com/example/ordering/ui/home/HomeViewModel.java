package com.example.ordering.ui.home;

import android.database.Cursor;

import androidx.lifecycle.ViewModel;

import com.example.ordering.structure.MyApplication;
import com.example.ordering.db.ShopDBManager;
import com.example.ordering.structure.Shop;

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends ViewModel {

    private ShopDBManager shopDBManager;

    private List<Shop> shopList = new ArrayList<>();


    public HomeViewModel(){
        /*shopDBManager = new ShopDBManager(MyApplication.getContext());
        shopDBManager.open();
        shopDBManager.add();
        Cursor cursor = shopDBManager.getDb().query("ShopInfo",null,null,null,null,null,null);

        if(cursor.moveToFirst()){
            do {
                int shopID = cursor.getInt(cursor.getColumnIndex("shopID"));
                String shopName = cursor.getString(cursor.getColumnIndex("shopName"));
                int shopImage = cursor.getInt(cursor.getColumnIndex("shopImage"));
                String shopLocation = cursor.getString(cursor.getColumnIndex("shopLocation"));
                String shopBrief = cursor.getString(cursor.getColumnIndex("shopBrief"));

                Shop shop = new Shop(shopID,shopName,shopImage,shopLocation,shopBrief);
                shopList.add(shop);
            }while (cursor.moveToNext());
        }
        cursor.close();*/
    }

    public void refreshList(){
        shopDBManager = new ShopDBManager(MyApplication.getContext());
        shopDBManager.open();
        Cursor cursor = shopDBManager.getDb().query("shopInfo",null,null,null,null,null,null);

        if(cursor.moveToFirst()){
            do {
                int shopID = cursor.getInt(cursor.getColumnIndex("shopID"));
                String shopName = cursor.getString(cursor.getColumnIndex("shopName"));
                String shopImage = cursor.getString(cursor.getColumnIndex("shopImage"));
                String shopLocation = cursor.getString(cursor.getColumnIndex("shopLocation"));
                String shopBrief = cursor.getString(cursor.getColumnIndex("shopBrief"));
                Shop shop = new Shop(shopID,shopName,shopImage,shopLocation,shopBrief);
                shopList.add(shop);
            }while (cursor.moveToNext());
        }
        cursor.close();
    }

    public List<Shop> getShopList(){
        refreshList();
        return shopList;
    }

}