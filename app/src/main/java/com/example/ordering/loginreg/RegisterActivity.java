package com.example.ordering.loginreg;

import android.Manifest;
import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.UserManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.ordering.R;
import com.example.ordering.db.DBHelper;
import com.example.ordering.db.UserDBManager;
import com.example.ordering.structure.User;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private final String TAG = "RegisterActivity";
    // 数据库
    UserDBManager dhelper;
    private TextView tv_note;
    private EditText et_userID;
    private EditText et_password;
    private EditText et_confirm;
    private ImageView iv_headPhoto;
    private ImageView login_image;
    private Button select_image;
    private Button take_image;
    private Button btn_takePhoto;
    private Button btn_register, btn_back;
    private String ufile = "logindata";
    private int result;
    private Resources res;

    public String fileName;
    public String filePath;

    //定义文件名
    private String file = "userinfo.txt";


    public static final int TAKE_PHOTO = 1;

    public static final int CHOOSE_PHOTO = 2;

    private Uri imageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = getWindow();
        //设置修改状态栏
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        //设置状态栏的颜色，和你的APP主题或者标题栏颜色一致就可以了
        window.setStatusBarColor(getResources().getColor(R.color.toolbarblue));

        setContentView(R.layout.activity_register);
        res = getResources();
        //获取布局中的各组件
        tv_note = findViewById(R.id.reg_note);
        login_image = findViewById(R.id.login_image);
        select_image = findViewById(R.id.select_image);
        take_image = findViewById(R.id.take_image);
        et_userID = findViewById(R.id.reg_userID);
        et_password = findViewById(R.id.reg_passowrd);
        et_confirm = findViewById(R.id.reg_confirm);
        btn_back = findViewById(R.id.reg_btn_login);
        btn_register = findViewById(R.id.reg_btn_register);
        btn_back.setOnClickListener(this);
        btn_register.setOnClickListener(this);
        select_image.setOnClickListener(this);
        take_image.setOnClickListener(this);
        // 打开数据库
        dhelper = new UserDBManager(RegisterActivity.this);
        dhelper.open();
    }

    // 采用Activity自身作为监听器的方式，点击注册按钮后，提示信息“注册成功！”
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.reg_btn_register: {
                // 获得控件的数据
                String userID = et_userID.getText().toString();
                String password = et_password.getText().toString();
                String confirm = et_confirm.getText().toString();
                // 填入数据合法判断
                if (userID != null && "".equals(userID)) {
                    tv_note.setText("用户名不能为空");
                    et_userID.setFocusable(true);
                    et_userID.requestFocus();
                    return;
                } else if (password != null && "".equals(password)) {
                    tv_note.setText("密码不能为空");
                    et_password.setFocusable(true);
                    et_password.requestFocus();
                    return;
                } else if (confirm != null && "".equals(confirm)) {
                    tv_note.setText("确认密码不能为空");
                    et_confirm.setFocusable(true);
                    et_confirm.requestFocus();
                    return;
                } else if (!confirm.equals(password)) {
                    tv_note.setText("密码不能为空");
                    et_password.setText("");
                    et_confirm.setText("");
                    et_password.setFocusable(true);
                    et_password.requestFocus();
                    return;
                }else if(dhelper.haveUid(userID)){// 数据库查询是否存在用户
                    tv_note.setText("已存在用户"+userID);
                    return;
                }

                tv_note.setText("注册成功");
                // 数据写入bundle中
                Bundle bundle = new Bundle();
                bundle.putString("userID", userID);
                bundle.putString("password", password);
                // 设置intent
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                intent.putExtras(bundle);// 设置传回数据
                setResult(RESULT_OK, intent);// 设置传回状态及携带数据
                result = RESULT_OK;

                int mode = Activity.MODE_PRIVATE;
                SharedPreferences uSetting = getSharedPreferences(ufile, mode);
                SharedPreferences.Editor editor = uSetting.edit();
                editor.putString("userID", userID);
                editor.putString("password", password);
                editor.commit();

                //获取文件输出流，操作模式是私有
                try {
                    FileOutputStream fileOutputStream = openFileOutput(file, Context.MODE_PRIVATE);
                    //将内容写入文件
                    JSONObject json = new JSONObject();// JSON格式
                    try {
                        json.put("userID", userID);
                        json.put("password", password);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    fileOutputStream.write(json.toString().getBytes());
                    fileOutputStream.flush();
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // 数据填入
                User data=new User();
                //data.userID=userName;
                data.userID=userID;
                data.userPwd=password;
                // 写入数据库
                dhelper.insert(data);
                break;
            }
            case R.id.reg_btn_login: {
                if (result != RESULT_OK)
                    setResult(RESULT_CANCELED);
                finish();
                break;
            }
            case R.id.take_image:{

                //创建File对象，用于存储拍摄后的照片
                File outputImage = new File(Environment.getExternalStorageDirectory(),"Picture");

                if(!outputImage.exists()){
                    outputImage.mkdir();
                }

                fileName = "IMG_" + System.currentTimeMillis() + ".jpg";
                filePath = outputImage.getAbsolutePath() + "/" + fileName;

                ContentValues contentValues = new ContentValues();

                if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q){
                    contentValues.put(MediaStore.Images.Media.RELATIVE_PATH,"DCIM/Pictures");
                }else {
                    contentValues.put(MediaStore.Images.Media.DATA,filePath);
                }
                //启动项目程序
                Log.d("MainPhoto",String.valueOf(imageUri));

                contentValues.put(MediaStore.Images.Media.MIME_TYPE,"image/JEPG");
                imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        contentValues);
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);//指定图片的输出地址
                startActivityForResult(intent,TAKE_PHOTO);
                break;
            }
            case R.id.select_image:{
                //为了读取照片，申请授予程序对SD卡读和写能力的运行时权限
                if(ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(RegisterActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                }else {
                    openAlbum();
                }
                break;
            }
            default:
                break;
        }
    }

    public void openAlbum(){
        //创建Intent对象
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");//设置必要参数
        startActivityForResult(intent,CHOOSE_PHOTO);//打开相册，第二个参数为CHOOSE_PHOTO
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    openAlbum();
                }else {
                    Toast.makeText(this,"You Denied ths permission",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        String selection = MediaStore.Images.Media.DISPLAY_NAME + "=? ";

                        Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                new String[]{MediaStore.Images.Media._ID},selection,new String[]{fileName},null);
                        if(cursor != null && cursor.moveToFirst()){
                            do{
                                imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,cursor.getLong(0));
                                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                                login_image.setImageBitmap(bitmap);
                            }while (cursor.moveToNext());
                        }else {
                            Toast.makeText(this,"no photo",Toast.LENGTH_SHORT).show();
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case CHOOSE_PHOTO:
                if(resultCode == RESULT_OK){
                    System.out.println("photo"+data);
                    handleImageOnKitKat(data);
                }
                break;
            default:
                break;
        }
    }

    private void handleImageOnKitKat(Intent data){
        String imagePath = null;
        Uri uri = data.getData();//取出封装过的Uri对象
        if(DocumentsContract.isDocumentUri(this,uri)){
            //如果是document类型的Uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())){
                //如果Uri的authority是media格式的话，需要对document id进行再一次的解析，通过字符串分割的方式取出后半部分得到真正的数字id
                String id = docId.split(":")[1];//解析出数字格式的id
                System.out.println("photoURI id "+id);
                String selection = MediaStore.Images.Media._ID + "=" + id;//取出的id构建新的Uri和条件语句
                System.out.println("photoURI selection "+selection);
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);//传入getImagePath()方法，获得图片的真实路径
                System.out.println("photoURI imagepath "+imagePath);
            }else if("com.android.providers.dowmloads.documents".equals(uri.getAuthority())){
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),Long.valueOf(docId));
                imagePath = getImagePath(contentUri,null);
            }
        }else if ("content".equalsIgnoreCase(uri.getScheme())){
            //如果是content类型的Uri，则使用普通方式处理
            imagePath = getImagePath(uri,null);
        }else if ("file".equalsIgnoreCase(uri.getScheme())){
            //如果是file类型的Uri，直接获取图片路径即可
            imagePath = uri.getPath();
        }

        displayImage(imagePath);//依据图片路径显示图片
    }

    private String getImagePath(Uri uri, String selection){
        String path = null;
        //通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri,null,selection,null,null);
        if(cursor != null){
            if(cursor.moveToNext()){
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                System.out.println("PhotoUri "+path);
            }
            cursor.close();
        }
        return path;
    }

    private void displayImage(String imagePath){
        if(imagePath != null){
            Log.d("MainPhoto",imagePath);
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            Log.d("MainPhoto",String.valueOf(bitmap));
            login_image.setImageBitmap(bitmap);
        }else {
            Toast.makeText(this,"failed to get image",Toast.LENGTH_SHORT).show();
        }
    }


}