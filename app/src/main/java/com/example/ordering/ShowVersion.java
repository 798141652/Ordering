package com.example.ordering;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ShowVersion extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = getWindow();
        //设置修改状态栏
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        //设置状态栏的颜色，和你的APP主题或者标题栏颜色一致就可以了
        window.setStatusBarColor(getResources().getColor(R.color.toolbarblue));

        setContentView(R.layout.activity_show_version);

        /**标题栏初始化*/
        Toolbar toolbar = (Toolbar) this.findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);//将Toolbar实例传入,既使用了Toolbar,也让它的外观与功能都与ActionBar一致了
        //标题栏
        ActionBar actionBar = getSupportActionBar();//通过getSupportActionBar方法获取到ActionBar实例，这个ActionBar具体是由ToolBar实现的
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);//让导航按钮HomeAsUp显示出来，此按钮默认图标是返回箭头，含义是返回上一个活动
        }


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