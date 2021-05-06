package com.example.ordering;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.ordering.db.UserDBManager;
import com.example.ordering.structure.MyApplication;

public class ChangeUserTel extends AppCompatActivity {

    private EditText userTelEdit;

    private MyApplication app;

    private Button submit;

    private UserDBManager userDBManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        //设置修改状态栏
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        //设置状态栏的颜色，和你的APP主题或者标题栏颜色一致就可以了
        window.setStatusBarColor(getResources().getColor(R.color.toolbarblue));
        setContentView(R.layout.activity_change_user_tel);

        /**标题栏初始化*/
        Toolbar toolbar = (Toolbar) this.findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);//将Toolbar实例传入,既使用了Toolbar,也让它的外观与功能都与ActionBar一致了
        //标题栏
        ActionBar actionBar = getSupportActionBar();//通过getSupportActionBar方法获取到ActionBar实例，这个ActionBar具体是由ToolBar实现的
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);//让导航按钮HomeAsUp显示出来，此按钮默认图标是返回箭头，含义是返回上一个活动
        }
        actionBar.setTitle("修改电话");


        userDBManager = new UserDBManager(this);
        userDBManager.open();
        app = (MyApplication) getApplication();
        String userID = app.getUid();
        userTelEdit = findViewById(R.id.edit_user_tel);

        submit = findViewById(R.id.commit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userTel =userTelEdit.getText().toString();
                if (userTel == null || userTel.equals("")) {
                    Toast.makeText(ChangeUserTel.this, "输入不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    userDBManager.changeUserTel(userID, userTel);
                    Toast.makeText(ChangeUserTel.this, "修改成功", Toast.LENGTH_SHORT).show();
                    ChangeUserTel.this.finish();
                }
            }
        });
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