package com.example.ordering;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.ordering.db.ShopDBManager;
import com.example.ordering.db.UserDBManager;
import com.example.ordering.loginreg.LoginActivity;
import com.example.ordering.structure.MyApplication;
import com.example.ordering.structure.Shop;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static MyApplication app;
    public List<Shop> shopList = new ArrayList<>();
    public ShopDBManager shopDBManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //刷新数据，使服务器数据与客户端相同
        RefreshData refreshData = new RefreshData();
        refreshData.refresh();
        //获取数据列表
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
        app= (MyApplication) getApplication();
        app.setShopList(shopList);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        /**标题栏初始化*/
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);//将Toolbar实例传入,既使用了Toolbar,也让它的外观与功能都与ActionBar一致了
        //标题栏
        ActionBar actionBar = getSupportActionBar();//通过getSupportActionBar方法获取到ActionBar实例，这个ActionBar具体是由ToolBar实现的
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);//让导航按钮HomeAsUp显示出来，此按钮默认图标是返回箭头，含义是返回上一个活动
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }
        findViewById(R.id.login);

        //this.getSupportActionBar().hide();

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);


    }

    //Toolbar中的菜单
    public boolean onCreateOptionsMenu(Menu menu){
        //在onCreateOptionsMenu方法中加载toolbar.xml菜单文件
        getMenuInflater().inflate(R.menu.toolbar,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.login:


                app = (MyApplication) getApplication();
                app.setState(1);
                if (app.getLoginState() == false) {
                    System.out.println("Login success");
                    Intent intent = new Intent("com.example.ordering.login");
                    startActivity(intent);
                    item.setIcon(getResources().getDrawable(R.drawable.ic_settings));
                    app.setLoginstatus(true);
                } else {
                    item.setIcon(getResources().getDrawable(R.drawable.ic_backup));
                    app.setLoginstatus(false);
                    Toast.makeText(MainActivity.this,"退出登录",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
        return true;
    }
}