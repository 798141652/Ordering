package com.example.ordering;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.example.ordering.db.ShopDBManager;
import com.example.ordering.structure.MyApplication;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class dbManager extends AppCompatActivity {


    private ShopDBManager shopDBManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_db_manager);

        shopDBManager = new ShopDBManager(MyApplication.getContext());
        shopDBManager.open();
    }

    /*public void saveImage(){
        db = dbHelper.getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put("_id", 1);
        cv.put("avatar", bitmabToBytes(MyApplication.getContext()));//图片转为二进制
        db.insert("User", null, cv);
        db.close();
    }*/

    public byte[] bitmabToBytes(Context context){
        //将图片转化为位图
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.xibeifengwei);
        int size = bitmap.getWidth() * bitmap.getHeight() * 4;
        //创建一个字节数组输出流,流的大小为size
        ByteArrayOutputStream baos= new ByteArrayOutputStream(size);
        try {
            //设置位图的压缩格式，质量为100%，并放入字节数组输出流中
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            //将字节数组输出流转化为字节数组byte[]
            byte[] imagedata = baos.toByteArray();
            return imagedata;
        }catch (Exception e){
        }finally {
            try {
                bitmap.recycle();
                baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new byte[0];
    }

}

