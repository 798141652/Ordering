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

public class ChangeUserPwd extends AppCompatActivity {

    private EditText userPwdEdit;

    private EditText userPwdConfirm;

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
        setContentView(R.layout.activity_change_user_pwd);

        /**标题栏初始化*/
        Toolbar toolbar = (Toolbar) this.findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);//将Toolbar实例传入,既使用了Toolbar,也让它的外观与功能都与ActionBar一致了
        //标题栏
        ActionBar actionBar = getSupportActionBar();//通过getSupportActionBar方法获取到ActionBar实例，这个ActionBar具体是由ToolBar实现的
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);//让导航按钮HomeAsUp显示出来，此按钮默认图标是返回箭头，含义是返回上一个活动
        }
        actionBar.setTitle("修改密码");

        userDBManager = new UserDBManager(this);
        userDBManager.open();
        app = (MyApplication) getApplication();
        String userID = app.getUid();
        userPwdEdit = findViewById(R.id.edit_user_pwd);
        userPwdConfirm = findViewById(R.id.confirm);
        submit = findViewById(R.id.commit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userPwd = userPwdEdit.getText().toString();
                String confirm = userPwdConfirm.getText().toString();
                if(userPwd == null && userPwd.equals("")){
                    Toast.makeText(ChangeUserPwd.this,"密码输入为空",Toast.LENGTH_SHORT).show();
                    return;
                }else if(confirm == null && confirm.equals("")) {
                    Toast.makeText(ChangeUserPwd.this, "确认密码为空", Toast.LENGTH_SHORT).show();
                    return;
                }else if(confirm != userPwd && !confirm.equals(userPwd)){
                    Toast.makeText(ChangeUserPwd.this, "两次密码不相同", Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    userDBManager.changeUserPwd(userID,userPwd);
                    Toast.makeText(ChangeUserPwd.this,"修改成功",Toast.LENGTH_SHORT).show();
                    ChangeUserPwd.this.finish();
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