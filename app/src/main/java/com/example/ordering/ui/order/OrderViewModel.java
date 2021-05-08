package com.example.ordering.ui.order;

import android.database.Cursor;

import androidx.lifecycle.ViewModel;

import com.example.ordering.db.CartDBManager;
import com.example.ordering.structure.Cart;
import com.example.ordering.structure.MyApplication;
import com.example.ordering.db.ShopDBManager;
import com.example.ordering.structure.Order;
import com.example.ordering.structure.Shop;

import java.util.ArrayList;
import java.util.List;

public class OrderViewModel extends ViewModel {

    private CartDBManager cartDBManager;


    private List<Order> orderList = new ArrayList<>();

    public MyApplication app;



    public OrderViewModel(){
    }

    public void refreshList(){
        app = (MyApplication) MyApplication.getContext().getApplicationContext();
        cartDBManager = new CartDBManager(MyApplication.getContext());
        cartDBManager.open();
        Cursor cursor = cartDBManager.getDb().query("cartInfo",new String[]{"distinct orderID"},"cartStatus != '0' and userID ="+app.getUid(),null,null,null,"cartStatus");

        if(cursor.moveToFirst()){
            do {
                List<Cart> cartList = new ArrayList<Cart>();
                Order order = new Order();
                String orderID = cursor.getString(cursor.getColumnIndex("orderID"));
                order.setOrderID(orderID);
                Cursor cursor1 = cartDBManager.getDb().rawQuery("select * from cartInfo  where orderID ='"+
                                orderID+"' and cartStatus != '0' and userID ="+app.getUid(),null);
                if(cursor1.moveToFirst()) {
                    do {
                        order.setOrderShop(cursor1.getInt(cursor1.getColumnIndex("dishShop")));
                        int cartID = cursor1.getInt(cursor1.getColumnIndex("cartID"));
                        int userID = cursor1.getInt(cursor1.getColumnIndex("userID"));
                        int dishID = cursor1.getInt(cursor1.getColumnIndex("dishID"));
                        String dishName = cursor1.getString(cursor1.getColumnIndex("dishName"));
                        String orderTime = cursor1.getString(cursor1.getColumnIndex("cartTime"));
                        int dishNum = cursor1.getInt(cursor1.getColumnIndex("dishNum"));
                        Double dishPrice = cursor1.getDouble(cursor1.getColumnIndex("dishPrice"));
                        String cartStatus = cursor1.getString(cursor1.getColumnIndex("cartStatus"));
                        Cart cart = new Cart();
                        cart.setCartID(cartID);
                        cart.setCartUserID(userID);
                        cart.setCartDishID(dishID);
                        cart.setCartDishName(dishName);
                        cart.setCartDishNum(dishNum);
                        cart.setCartDishPrice(dishPrice);
                        cart.setCartTime(orderTime);
                        cart.setCartStatus(cartStatus);
                        cartList.add(cart);
                        order.setOrderStatus(cartStatus);
                    } while (cursor1.moveToNext());
                }
                order.setCartList(cartList);
                orderList.add(order);
                cursor1.close();
            }while (cursor.moveToNext());
        }

        //Cart cart = orderList.get(1).get(0);
        //System.out.println(orderList.get(0).get(0));
        cursor.close();
        cartDBManager.getDb().close();
    }

    public List<Order> getOrderList(){
        refreshList();
        return orderList;
    }

}