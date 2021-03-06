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
        //???????????????????????????????????????????????????
        //RefreshData refreshData = new RefreshData();
        //refreshData.refresh();
        //??????????????????
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

        //?????????????????????
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            //?????????????????????
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //????????????????????????????????????APP?????????????????????????????????????????????
            window.setStatusBarColor(getResources().getColor(R.color.toolbarblue));
        }
        setContentView(R.layout.activity_main);
        /**??????????????????*/
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);//???Toolbar????????????,????????????Toolbar,?????????????????????????????????ActionBar?????????
        //?????????
        ActionBar actionBar = getSupportActionBar();//??????getSupportActionBar???????????????ActionBar???????????????ActionBar????????????ToolBar?????????
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);//???????????????HomeAsUp????????????????????????????????????????????????????????????????????????????????????
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


    //Toolbar????????????
    public boolean onCreateOptionsMenu(Menu menu){
        //???onCreateOptionsMenu???????????????toolbar.xml????????????
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
                Toast.makeText(MainActivity.this,"????????????",Toast.LENGTH_SHORT).show();
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