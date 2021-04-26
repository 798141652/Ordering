package com.example.ordering;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.ordering.db.ShopDBManager;
import com.example.ordering.db.UserDBManager;
import com.example.ordering.loginreg.LoginActivity;
import com.example.ordering.structure.MyApplication;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {

    public static MyApplication app = new MyApplication();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //数据库初始化

//        ShopDBManager dhelper = new ShopDBManager(MainActivity.this);
//        dhelper.open();
 //       dhelper.add();

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