package com.example.ordering.dish;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ordering.R;
import com.example.ordering.cart.CartActivity;
import com.example.ordering.cart.CartOneDialog;
import com.example.ordering.db.CartDBManager;
import com.example.ordering.db.CommentDBManager;
import com.example.ordering.db.DishDBManager;
import com.example.ordering.structure.Cart;
import com.example.ordering.structure.Comment;
import com.example.ordering.structure.Dish;
import com.example.ordering.structure.MyApplication;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.youth.banner.loader.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class DishActivity extends AppCompatActivity {


    public static final String DISH_NAME = "dish_name";

    public static final String DISH_IMAGE_ID = "dish_image_id";

    public static final String DISH_ID = "dish_id";

    public static final String DISH_PRICE = "dish_price";

    public static final String DISH_TYPE = "dish_type";

    public CommentDBManager commentDBManager;

    public DishDBManager dishDBManager;

    public Dish dish;

    public int dishNum ;

    public List<Comment> commentList;
    private DishCommentAdapter dishCommentAdapter;
    private Cart cartInsert;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        //设置修改状态栏
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        //设置状态栏的颜色，和你的APP主题或者标题栏颜色一致就可以了
        window.setStatusBarColor(getResources().getColor(R.color.toolbarblue));
        setContentView(R.layout.activity_dish);

        MyApplication app = (MyApplication) MyApplication.getContext().getApplicationContext();
        dishDBManager = new DishDBManager(this);
        dishDBManager.open();

        //通过悬浮按钮进入购物车
        FloatingActionButton floatingActionButton = findViewById(R.id.cart_dish);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!app.getLoginState()) {
                    Intent intent = new Intent("com.example.ordering.login");
                    startActivity(intent);
                }else {
                    Intent intent = new Intent(DishActivity.this, CartActivity.class);
                    startActivity(intent);
                }
            }
        });


        Intent intent = getIntent();

        int dishID = intent.getIntExtra(DISH_ID,0);
        dish = new Dish();
        dish = dishDBManager.getDishByID(dishID);
        int dishShop = dish.getShopID();
        String dishName = intent.getStringExtra(DISH_NAME);
        String dishImage = intent.getStringExtra(DISH_IMAGE_ID);
        String dishBriefText = intent.getStringExtra(DISH_TYPE);
        Double dishPrice = intent.getDoubleExtra(DISH_PRICE,0);
        ImageView dishImageView = findViewById(R.id.dish_image_view);
        ImageView addDish = findViewById(R.id.dish_add);



        addDish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (app.getLoginState()) {

                    int uid = Integer.parseInt(app.getUid());


                    Bundle bundle = new Bundle();
                    bundle.putInt("dishid", dishID);
                    bundle.putInt("dishshop",dishShop);
                    bundle.putString("dishname", dishName);
                    bundle.putDouble("price", dishPrice);

                    CartDBManager dbManager = new CartDBManager(MyApplication.getContext());
                    dbManager.open();

                    //显示增加数量dialog
                    Cart cartExist = dbManager.queryonedish(uid, dishID);
                    if (cartExist != null && cartExist.cartStatus.equals("0"))
                        dishNum = cartExist.cartDishNum;
                    else dishNum = 0;
                    final CartOneDialog orderDlg = new CartOneDialog(DishActivity.this, dishNum);
                    orderDlg.setTitle(dishName);
                    orderDlg.show();

                    orderDlg.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            if (orderDlg.mBtnClicked == CartOneDialog.ButtonID.BUTTON_OK) {
                                cartInsert = new Cart();
                                cartInsert.cartUserID = uid;
                                cartInsert.cartDishID = dishID;
                                cartInsert.cartDishNum = dishNum;
                                cartInsert.cartDishName = dishName;
                                cartInsert.cartShopID = dishShop;
                                cartInsert.cartDishNum = orderDlg.mNum;
                                cartInsert.cartDishPrice = dish.dishPrice;
                                Cart cartUpdate = dbManager.queryonedish(uid, dishID);
                                if (cartUpdate == null) {//购物车内没有找到菜单项
                                    if(orderDlg.mNum != 0) {
                                        cartInsert.cartStatus = "0";//购物车状态
                                        dbManager.insert(cartInsert);
                                        Toast.makeText(DishActivity.this, dishName + " " + cartInsert.cartDishNum + "份已经加入购物车", Toast.LENGTH_SHORT).show();
                                    }else {
                                        Toast.makeText(DishActivity.this, "未选数量!", Toast.LENGTH_SHORT).show();
                                    }
                                } else if(cartUpdate.cartStatus.equals("0")){//购物车内找到菜单项
                                    if(orderDlg.mNum == 0){
                                        dbManager.deleteonedish(cartUpdate,uid);
                                        Toast.makeText(DishActivity.this, "已从购物车删除该项!", Toast.LENGTH_SHORT).show();
                                    }else {
                                        dbManager.updateNumemenu(cartUpdate.cartUserID, cartUpdate.cartDishID, orderDlg.mNum);
                                        Toast.makeText(DishActivity.this, dishName + " " + cartInsert.cartDishNum + "份已经加入购物车", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        }
                    });
                }
                else{
                    Intent intent = new Intent("com.example.ordering.login");
                    startActivity(intent);
                }
            }
        });

        //设置toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);//把Toolbar作为ActionBar显示
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);//启动HomeAsUp按钮，HomeAsUp按钮的默认图标就是一个返回箭头
        }
        actionBar.setTitle(dishName);//将水果名设置成当前界面的标题
        //使用Glide加载传入的档口图片，并设置在标题栏的ImageView上面
        Glide.with(this).
                load("http://49.234.101.49/ordering/"+dishImage)
                .placeholder(R.drawable.loading)
                .into(dishImageView);

        TextView dishBrief = findViewById(R.id.dish_brief);
        TextView dishNameText = findViewById(R.id.show_dish_name);
        TextView dishPriceText = findViewById(R.id.show_dish_price);
        dishBrief.setText(dishBriefText);
        dishNameText.setText(dishName);
        dishPriceText.setText(String.valueOf(dishPrice));


        commentList = new ArrayList<>();
        getCommentList(dishID);
        RecyclerView recyclerView=findViewById(R.id.dish_comment_list);
        GridLayoutManager layoutManager = new GridLayoutManager(MyApplication.getContext(),1);//第一个参数为Context，第二个参数为列数
        recyclerView.setLayoutManager(layoutManager);
        dishCommentAdapter = new DishCommentAdapter(commentList);
        recyclerView.setAdapter(dishCommentAdapter);
        dishDBManager.getDb().close();
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

    public void getCommentList(int dishID){
        commentDBManager = new CommentDBManager(MyApplication.getContext());
        commentDBManager.open();
        commentList = commentDBManager.getDishComment(dishID);
    }
}