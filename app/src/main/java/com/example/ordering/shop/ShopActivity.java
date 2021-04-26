package com.example.ordering.shop;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.ordering.R;
import com.example.ordering.db.DishDBManager;
import com.example.ordering.db.ShopDBManager;
import com.example.ordering.dish.DishAdapter;
import com.example.ordering.order.OrderedActivity;
import com.example.ordering.structure.Dish;
import com.example.ordering.structure.MyApplication;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class ShopActivity extends AppCompatActivity {

    public static final String SHOP_NAME = "shop_name";

    public static final String SHOP_IMAGE = "shop_image";

    public static final String SHOP_ID = "shop_id";

    public static final String DISH_NAME = "dish_name";

    public static final String DISH_IMAGE_ID = "dish_image_id";

    private ShopDBManager shopDBManager;

    private DishDBManager dishDBManager;

    private SQLiteDatabase db;

    private int shopId;

    private List<Dish> dishList = new ArrayList<>();

    private DishAdapter dishadapter;//菜品适配器

    private SwipeRefreshLayout swipeRefreshLayout;//实现下拉刷新功能的核心类



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);
        initView();

        MyApplication app = (MyApplication) MyApplication.getContext().getApplicationContext();

        //通过悬浮按钮进入购物车
        FloatingActionButton floatingActionButton = findViewById(R.id.cart);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!app.getLoginState()) {
                    Intent intent = new Intent("com.example.ordering.login");
                    startActivity(intent);
                }else {
                    Intent intent = new Intent(ShopActivity.this, OrderedActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    private Activity getActivity(){
        return this;
    }

    private void initView(){
        Intent intent = getIntent();
        shopId = intent.getIntExtra(SHOP_ID,0);
        String shopName = intent.getStringExtra(SHOP_NAME);
        String shopImage = intent.getStringExtra(SHOP_IMAGE);
        ImageView shopImageView = findViewById(R.id.shop_image_view);

        //设置toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);//把Toolbar作为ActionBar显示
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);//启动HomeAsUp按钮，HomeAsUp按钮的默认图标就是一个返回箭头
        }
        actionBar.setTitle(shopName);//将水果名设置成当前界面的标题



        //展示档口的菜单
        dishList = getDishList();
        RecyclerView recyclerView=findViewById(R.id.dish_recycler_view);
        GridLayoutManager layoutManager = new GridLayoutManager(MyApplication.getContext(),1);//第一个参数为Context，第二个参数为列数
        recyclerView.setLayoutManager(layoutManager);
        dishadapter = new DishAdapter(dishList);
        recyclerView.setAdapter(dishadapter);

        actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);//启动HomeAsUp按钮，HomeAsUp按钮的默认图标就是一个返回箭头
        }
        Glide.with(this).load(shopImage).into(shopImageView);//使用Glide加载传入的水果图片，并设置在标题栏的ImageView上面

        //下拉刷新
        /*swipeRefreshLayout = findViewById(R.id.swipe_dish_refresh);//拿到swipeRefreshLayout实例
        swipeRefreshLayout.setColorSchemeResources(R.color.design_default_color_primary);//设置下拉刷新进度条的颜色
        //设置一个下拉刷新的监听器
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {//当触发了下拉刷新操作的时候会回调这个监听器的onRefresh()方法，在这里处理具体的刷新逻辑
                refreshDish();
            }
        });
    }

        private void refreshDish(){
            //先开启一个线程
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(2000);//线程沉睡2秒钟，因为本地刷新操作速度非常快，如果不将线程沉睡的话，刷新立刻就结束了，从而看不到刷新的过程
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                    //睡眠结束后，runOnUiThread方法将线程切换回主线程
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            initView();//重新生成数据
                            dishadapter.notifyDataSetChanged();//notifyDataSetChanged方法通知数据发生了变化
                            swipeRefreshLayout.setRefreshing(false);//用于表示刷新事件结束，并隐藏刷新进度条
                        }
                    });
                }
            }).start();*/
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

    public List<Dish> getDishList(){

        dishDBManager = new DishDBManager(MyApplication.getContext());
        dishDBManager.open();
        Cursor cursor = dishDBManager.getDb().query("dishInfo",null,"shopID=?",new String[]{String.valueOf(shopId)},null,null,null);
        if(cursor.moveToFirst()){
            do {
                int dishID = cursor.getInt(cursor.getColumnIndex("dishID"));
                int shopID = cursor.getInt(cursor.getColumnIndex("shopID"));
                String dishName = cursor.getString(cursor.getColumnIndex("dishName"));
                int dishImage = cursor.getInt(cursor.getColumnIndex("dishImage"));
                Double dishPrice = cursor.getDouble(cursor.getColumnIndex("dishPrice"));
                String dishType = cursor.getString(cursor.getColumnIndex("dishType"));

                Dish dish = new Dish(dishName,dishType,shopID,dishID,dishImage,dishPrice);
                dishList.add(dish);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return dishList;
    }
}