package com.example.ordering.dish;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.example.ordering.order.OrderedActivity;
import com.example.ordering.shop.ShopActivity;
import com.example.ordering.structure.Dish;
import com.example.ordering.structure.MyApplication;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class DishActivity extends AppCompatActivity {


    public static final String DISH_NAME = "dish_name";

    public static final String DISH_IMAGE_ID = "dish_image_id";

    public static final String DISH_TYPE = "dish_type";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dish);

        MyApplication app = (MyApplication) MyApplication.getContext().getApplicationContext();

        //通过悬浮按钮进入购物车
        FloatingActionButton floatingActionButton = findViewById(R.id.cart_dish);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!app.getLoginState()) {
                    Intent intent = new Intent("com.example.ordering.login");
                    startActivity(intent);
                }else {
                    Intent intent = new Intent(DishActivity.this, OrderedActivity.class);
                    startActivity(intent);
                }
            }
        });


        Intent intent = getIntent();
        String dishName = intent.getStringExtra(DISH_NAME);
        int dishImageId = intent.getIntExtra(DISH_IMAGE_ID,0);
        String dishBriefText = intent.getStringExtra(DISH_TYPE);
        ImageView dishImageView = findViewById(R.id.dish_image_view);

        //设置toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);//把Toolbar作为ActionBar显示
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);//启动HomeAsUp按钮，HomeAsUp按钮的默认图标就是一个返回箭头
        }
        actionBar.setTitle(dishName);//将水果名设置成当前界面的标题
        Glide.with(this).load(dishImageId).into(dishImageView);//使用Glide加载传入的水果图片，并设置在标题栏的ImageView上面

        TextView dishBrief = findViewById(R.id.dish_brief);
        TextView dishNameText = findViewById(R.id.show_dish_name);
        dishBrief.setText(dishBriefText);
        dishNameText.setText(dishName);
    }

}