package com.example.ordering.loginreg;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

//import com.example.ordering.DishMainActivity;
import com.example.ordering.MainActivity;
import com.example.ordering.R;
import com.example.ordering.db.UserDBManager;
import com.example.ordering.structure.MyApplication;

public class LoginActivity extends AppCompatActivity {

    public MyApplication app;
    private Button btnReg, btnLogin;
    private EditText et_userID, et_password;
    private CheckBox remuser;
    private String ufile = "logindata";
    private SharedPreferences message;
    private String userID, ukey;
    UserDBManager dhelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        et_userID = findViewById(R.id.editUid);
        et_password = findViewById(R.id.editPwd);
        remuser = findViewById(R.id.cbRemember);

        app = (MyApplication) getApplication();

        btnReg = findViewById(R.id.btnReg);
        btnLogin = findViewById(R.id.btnLogin);
        // 打开数据库
        dhelper=new UserDBManager(LoginActivity.this);
        dhelper.open();

        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                app.setLoginstatus(false);
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                // 隐式启动
                startActivityForResult(intent, 1);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userID = et_userID.getText().toString().trim();
                System.out.println("UID = " +userID);
                ukey = et_password.getText().toString().trim();
                System.out.println("UID key= " +ukey);
                if (userID.isEmpty() || ukey.isEmpty()) {
                    // 输入判断
                    Toast.makeText(LoginActivity.this, "账号密码不能为空！", Toast.LENGTH_SHORT).show();
                    et_userID.setFocusable(true);
                    et_userID.requestFocus();
                    return;
                }else if(dhelper.haveUid(userID)==false){// 判断是否存在用户
                    Toast.makeText(LoginActivity.this, "不存在该用户", Toast.LENGTH_SHORT).show();
                    return;
                }
                else {

                    // 检查数据库用户名密码是否正确
                    if (dhelper.checkKey(userID,ukey)==false) {
                        Toast.makeText(LoginActivity.this, "账号或密码输入错误！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    // 是否记住账号密码写入SharePreference
                    if (remuser.isChecked()) {
                        WriteUserPreference(userID, ukey, 1);
                    } else {
                        WriteUserPreference(null, null, 0);
                    }

                    // 修改状态
                    app.setLoginstatus(true);
                    app.setUid(userID);


                    // 跳转
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    setResult(RESULT_OK, intent);
                    Toast.makeText(LoginActivity.this, "登陆成功", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
        // 文件获取
        message = LoadUserPreference();
        userID = message.getString("userID", "");
        ukey = message.getString("password", "");
        int ifrem = message.getInt("ifrem", 0);
        // 获取值填充，记住我、账号、密码
        if (ifrem == 1) {
            et_userID.setText(userID);
            et_password.setText(ukey);
            remuser.setChecked(true);
        } else {
            et_userID.setText("");
            et_password.setText("");
            remuser.setChecked(false);
        }
    }
    // 跳转的界面结束，将返回值传回
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Bundle udata = data.getExtras();
                String userID = udata.getString("userID");
                String password = udata.getString("password");
                et_userID.setText(userID);
                et_password.setText(password);
            }
        }
    }
    // SharedPreferences文件读取
    private SharedPreferences LoadUserPreference() {
        int mode = Activity.MODE_PRIVATE;
        SharedPreferences uSetting = getSharedPreferences(ufile, mode);
        return uSetting;
    }
    // SharedPreferences文件写入
    private void WriteUserPreference(String id, String key, int ifrem) {
        int mode = Activity.MODE_PRIVATE;
        SharedPreferences uSetting = getSharedPreferences(ufile, mode);
        SharedPreferences.Editor editor = uSetting.edit();
        editor.putString("userID", id);
        editor.putString("password", key);
        editor.putInt("ifrem", ifrem);
        editor.commit();
    }
}
