package com.example.ordering.cart;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.ordering.R;
import com.example.ordering.db.CartDBManager;
import com.example.ordering.shop.ShopAdapter;
import com.example.ordering.structure.Cart;
import com.example.ordering.structure.Dish;
import com.example.ordering.structure.MyApplication;
import com.example.ordering.structure.Order;
import android.os.Handler;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class CartActivity extends AppCompatActivity {

    public MyApplication app;
    public CartDBManager dbManager;
    public Cart[] userCart;//某个用户的所有订单
    public String dishName;
    public String dishPrice;
    public Cart cart = new Cart();//实例化一个订单

    public Dish dish = new Dish();

    public List<Order> cartList;

    private CartAdapter cartAdapter;



    private int uid;
    private Double totprice, usumprice;
    private Button btncancel;
    private Button btnok;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        //设置修改状态栏
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        //设置状态栏的颜色，和你的APP主题或者标题栏颜色一致就可以了
        window.setStatusBarColor(getResources().getColor(R.color.toolbarblue));
        setContentView(R.layout.activity_cart);

        /**标题栏初始化*/
        Toolbar toolbar = (Toolbar) this.findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);//将Toolbar实例传入,既使用了Toolbar,也让它的外观与功能都与ActionBar一致了
        //标题栏
        ActionBar actionBar = getSupportActionBar();//通过getSupportActionBar方法获取到ActionBar实例，这个ActionBar具体是由ToolBar实现的
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);//让导航按钮HomeAsUp显示出来，此按钮默认图标是返回箭头，含义是返回上一个活动
        }
        actionBar.setTitle("购物车");


        app = (MyApplication) getApplication();
        uid = Integer.parseInt(app.getUid());

        dbManager = new CartDBManager(this);
        dbManager.open();

        userCart = dbManager.queryCartByUser(uid);
        getCartList();

        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                //此时代码在主线程当中运行
                switch (msg.what){//判断what字段
                    case 110:
                        getCartList();
                        RecyclerView recyclerView = findViewById(R.id.cart_list);
                        GridLayoutManager layoutManager = new GridLayoutManager(CartActivity.this, 1);//第一个参数为Context，第二个参数为列数
                        recyclerView.setLayoutManager(layoutManager);
                        cartAdapter = new CartAdapter(cartList,handler);
                        recyclerView.setAdapter(cartAdapter);
                        cartAdapter.notifyDataSetChanged();
                        break;
                    case 120:
                        getCartList();
                        recyclerView = findViewById(R.id.cart_list);
                        layoutManager = new GridLayoutManager(CartActivity.this, 1);//第一个参数为Context，第二个参数为列数
                        recyclerView.setLayoutManager(layoutManager);
                        cartAdapter = new CartAdapter(cartList,handler);
                        recyclerView.setAdapter(cartAdapter);
                        cartAdapter.notifyDataSetChanged();
                        break;
                    default:
                        break;
                }
            }
        };
        /*
        getCartList();
        RecyclerView recyclerView = findViewById(R.id.cart_list);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);//第一个参数为Context，第二个参数为列数
        recyclerView.setLayoutManager(layoutManager);
        cartAdapter = new CartAdapter(cartList);
        recyclerView.setAdapter(cartAdapter);
        */
    }

    @Override
    protected void onResume() {
        super.onResume();

        RecyclerView recyclerView = findViewById(R.id.cart_list);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);//第一个参数为Context，第二个参数为列数
        recyclerView.setLayoutManager(layoutManager);
        cartAdapter = new CartAdapter(cartList,handler);
        recyclerView.setAdapter(cartAdapter);
        cartAdapter.notifyDataSetChanged();
    }


    public void getCartList() {
        cartList = new ArrayList<Order>();
        app = (MyApplication) MyApplication.getContext().getApplicationContext();
        dbManager = new CartDBManager(MyApplication.getContext());
        dbManager.open();
        Cursor cursor = dbManager.getDb().query("cartInfo", new String[]{"distinct dishShop"}, "cartStatus = '0' and userID =" +
                app.getUid(), null, null, null, "cartID desc");

        if (cursor.moveToFirst()) {
            do {
                List<Cart> dishList = new ArrayList<Cart>();
                Order order = new Order();
                int shopID = cursor.getInt(cursor.getColumnIndex("dishShop"));
                order.setOrderShop(shopID);
                order.setUserID(Integer.parseInt(app.getUid()));
                Cursor cursor1 = dbManager.getDb().rawQuery("select * from cartInfo where dishShop =" +
                        shopID + " and cartStatus = '0' and userID ="+app.getUid(), null);
                if (cursor1.moveToFirst()) {
                    do {
                        order.setOrderShop(cursor1.getInt(cursor1.getColumnIndex("dishShop")));
                        int cartID = cursor1.getInt(cursor1.getColumnIndex("cartID"));
                        int userID = cursor1.getInt(cursor1.getColumnIndex("userID"));
                        int dishID = cursor1.getInt(cursor1.getColumnIndex("dishID"));
                        String dishName = cursor1.getString(cursor1.getColumnIndex("dishName"));
                        int dishNum = cursor1.getInt(cursor1.getColumnIndex("dishNum"));
                        Double dishPrice = cursor1.getDouble(cursor1.getColumnIndex("dishPrice"));
                        Cart cart = new Cart();
                        cart.setCartID(cartID);
                        cart.setCartUserID(userID);
                        cart.setCartShopID(shopID);
                        cart.setCartDishID(dishID);
                        cart.setCartDishName(dishName);
                        cart.setCartDishNum(dishNum);
                        cart.setCartDishPrice(dishPrice);
                        dishList.add(cart);
                    } while (cursor1.moveToNext());
                }
                order.setCartList(dishList);
                cartList.add(order);
                cursor1.close();
            } while (cursor.moveToNext());
        }
        //Cart cart = orderList.get(1).get(0);
        //System.out.println(orderList.get(0).get(0));
        cursor.close();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {//处理HomeAsUp按钮的点击事件
        switch (item.getItemId()){
            case android.R.id.home://当点击这个按钮时，调用finish()方法关闭当前的活动，从而返回上一个活动
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
