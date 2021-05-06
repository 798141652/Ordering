package com.example.ordering.ui.shop;

import android.database.Cursor;

import androidx.lifecycle.ViewModel;

import com.example.ordering.structure.MyApplication;
import com.example.ordering.db.ShopDBManager;
import com.example.ordering.structure.Shop;

import java.util.ArrayList;
import java.util.List;

public class ShopViewModel extends ViewModel {

    private ShopDBManager shopDBManager;

    private List<Shop> shopList = new ArrayList<>();
    private List<List<Shop>> shitangList = new ArrayList<List<Shop>>();


    public ShopViewModel(){
    }

    public void refreshList(){

        shopDBManager = new ShopDBManager(MyApplication.getContext());
        shopDBManager.open();
        Cursor cursor = shopDBManager.getDb().query("shopInfo", null, null, null, "shopLocation", null, null);
        if (cursor.moveToFirst()) {
            do {
                shopList = new ArrayList<Shop>();
                String abc = cursor.getString(cursor.getColumnIndex("shopLocation"));
                Cursor cursor1 = shopDBManager.getDb().rawQuery("select * from shopInfo where shopLocation ='"+
                        cursor.getString(cursor.getColumnIndex("shopLocation"))+"'",null);
                if(cursor1.moveToFirst()) {
                    do {
                        int shopID = cursor1.getInt(cursor1.getColumnIndex("shopID"));
                        String shopName = cursor1.getString(cursor1.getColumnIndex("shopName"));
                        String shopImage = cursor1.getString(cursor1.getColumnIndex("shopImage"));
                        String shopLocation = cursor1.getString(cursor1.getColumnIndex("shopLocation"));
                        String shopBrief = cursor1.getString(cursor1.getColumnIndex("shopBrief"));
                        Shop shop = new Shop(shopID, shopName, shopImage, shopLocation, shopBrief);
                        shopList.add(shop);
                    }while (cursor1.moveToNext());
                }
                shitangList.add(shopList);
                cursor1.close();
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    public List<List<Shop>> getshitangList(){
        refreshList();
        return shitangList;
    }

}