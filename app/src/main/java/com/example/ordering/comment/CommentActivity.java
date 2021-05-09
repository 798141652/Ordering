package com.example.ordering.comment;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ordering.R;
import com.example.ordering.cart.CartActivity;
import com.example.ordering.cart.CartAdapter;
import com.example.ordering.db.CartDBManager;
import com.example.ordering.db.DishDBManager;
import com.example.ordering.structure.Cart;
import com.example.ordering.structure.Dish;
import com.example.ordering.structure.MyApplication;

import java.util.ArrayList;
import java.util.List;


public class CommentActivity extends AppCompatActivity {

    public MyApplication app;
    public CartDBManager cartDBManager;
    public DishDBManager dishDBManager;
    public Cart[] userCart;//某个用户的所有订单
    public String dishName;
    public String dishPrice;
    public Cart cart = new Cart();//实例化一个订单

    public Dish dish = new Dish();

    public List<Cart> cartList;

    private CommentAdapter commentAdapter;

    private Handler handler;

    private int uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = getWindow();
        //设置修改状态栏
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        //设置状态栏的颜色，和你的APP主题或者标题栏颜色一致就可以了
        window.setStatusBarColor(getResources().getColor(R.color.toolbarblue));

        setContentView(R.layout.activity_comment);

        /**标题栏初始化*/
        Toolbar toolbar = (Toolbar) this.findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);//将Toolbar实例传入,既使用了Toolbar,也让它的外观与功能都与ActionBar一致了
        //标题栏
        ActionBar actionBar = getSupportActionBar();//通过getSupportActionBar方法获取到ActionBar实例，这个ActionBar具体是由ToolBar实现的
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);//让导航按钮HomeAsUp显示出来，此按钮默认图标是返回箭头，含义是返回上一个活动
        }
        actionBar.setTitle("评论");


        app = (MyApplication) getApplication();
        uid = Integer.parseInt(app.getUid());

        cartDBManager = new CartDBManager(this);
        cartDBManager.open();

        Intent intent = getIntent();
        String orderID = intent.getStringExtra("orderID");
        userCart = cartDBManager.queryCartByUser(uid);


        getOrderDishList(orderID);
        RecyclerView recyclerView = findViewById(R.id.comment_dish_list);
        GridLayoutManager layoutManager = new GridLayoutManager(CommentActivity.this, 1);//第一个参数为Context，第二个参数为列数
        recyclerView.setLayoutManager(layoutManager);
        commentAdapter = new CommentAdapter(cartList);
        recyclerView.setAdapter(commentAdapter);
        commentAdapter.notifyDataSetChanged();


    }



    public void getOrderDishList(String orderID) {
        cartList = new ArrayList<>();
        app = (MyApplication) MyApplication.getContext().getApplicationContext();
        cartDBManager = new CartDBManager(MyApplication.getContext());
        dishDBManager = new DishDBManager(MyApplication.getContext());
        cartDBManager.open();
        dishDBManager.open();
        Cursor cursor = cartDBManager.getDb().query("cartInfo", null, "orderID = "+orderID, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                Cart cart = new Cart();
                int dishID = cursor.getInt(cursor.getColumnIndex("dishID"));
                int shopID = cursor.getInt(cursor.getColumnIndex("dishShop"));
                String dishName = cursor.getString(cursor.getColumnIndex("dishName"));
                cart.cartDishName = dishName;
                cart.cartShopID = shopID;
                cart.cartDishID = dishID;
                cart.orderID = cursor.getString(cursor.getColumnIndex("orderID"));
                cart.cartUserID = Integer.parseInt(app.getUid());
                cartList.add(cart);
            } while (cursor.moveToNext());
        }
        cursor.close();
        cartDBManager.getDb().close();
        dishDBManager.getDb().close();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                break;
        }
        return true;
    }

}
