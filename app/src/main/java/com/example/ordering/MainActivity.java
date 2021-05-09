package com.example.ordering;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.ordering.db.ShopDBManager;
import com.example.ordering.loginreg.LoginActivity;
import com.example.ordering.structure.MyApplication;
import com.example.ordering.structure.Shop;
import com.example.ordering.ui.settings.UpdateDataService;
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
    public List<Shop> shopList;
    public List<List<Shop>> shitangList = new ArrayList<List<Shop>>();
    public ShopDBManager shopDBManager;

    private SharedPreferences message;

    private IntentFilter intentFilter;
    private NetworkChangeReceiver networkChangeReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        app = (MyApplication) getApplication();
        //app = (MyApplication) MyApplication.getContext().getApplicationContext();
        super.onCreate(savedInstanceState);

        //Intent startIntent = new Intent(this, UpdateDataService.class);
        //startService(startIntent);

        message = getSharedPreferences("logindata", Activity.MODE_PRIVATE);
        if(message.getInt("iflogin",0) == 0){
            if(!app.getLoginState()) {
                Intent intent = new Intent(MyApplication.getContext(), LoginActivity.class);
                startActivity(intent);
            }
        }else {
            showActivityView();
            String userID = message.getString("userID", "");
            app.setLoginstatus(true);
            app.setUid(userID);
        }

    }

    public void showActivityView(){
        //刷新数据，使服务器数据与客户端相同
        //RefreshData refreshData = new RefreshData();
        //refreshData.refresh();
        //获取数据列表
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
        shopDBManager.getDb().close();
        app.setshitangList(shitangList);

        //修改状态栏颜色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            //设置修改状态栏
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //设置状态栏的颜色，和你的APP主题或者标题栏颜色一致就可以了
            window.setStatusBarColor(getResources().getColor(R.color.toolbarblue));
        }
        setContentView(R.layout.activity_main);
        /**标题栏初始化*/
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);//将Toolbar实例传入,既使用了Toolbar,也让它的外观与功能都与ActionBar一致了
        //标题栏
        ActionBar actionBar = getSupportActionBar();//通过getSupportActionBar方法获取到ActionBar实例，这个ActionBar具体是由ToolBar实现的
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);//让导航按钮HomeAsUp显示出来，此按钮默认图标是返回箭头，含义是返回上一个活动
            //actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }

        //this.getSupportActionBar().hide();

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_cart, R.id.navigation_order, R.id.navigation_settings)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

//        navView.setOnNavigationItemSelectedListener((BottomNavigationView.OnNavigationItemSelectedListener) this);
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
            case R.id.logout:
                app = (MyApplication) getApplication();
                //app.setState(1);
                //item.setIcon(getResources().getDrawable(R.drawable.ic_backup));
                app.setLoginstatus(false);
                SharedPreferences uSetting = getSharedPreferences("logindata", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = uSetting.edit();
                editor.putInt("iflogin",0);
                editor.apply();
                Intent intent = new Intent("com.example.ordering.login");
                startActivity(intent);
                Toast.makeText(MainActivity.this,"退出登录",Toast.LENGTH_SHORT).show();
                break;
            default:
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        networkChangeReceiver = new NetworkChangeReceiver();
        registerReceiver(networkChangeReceiver,intentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(networkChangeReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Intent stopIntent = new Intent(this,UpdateDataService.class);
        //stopService(stopIntent);
        unregisterReceiver(networkChangeReceiver);
    }


}