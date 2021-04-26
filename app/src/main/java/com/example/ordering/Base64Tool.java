package com.example.ordering;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Base64Tool {

    /**
     * 字符串进行Base64编码
     * */
    public static String setStringToBase64(String value) {
        String encodedString = Base64.encodeToString(value.getBytes(), Base64.DEFAULT);
        return encodedString;
    }

    /**
     * 字符串进行Base64解码
     * */
    public static String setBase64ToString(String value) {
        String decodedString = new String(Base64.decode(value, Base64.DEFAULT));
        return decodedString;
    }

    /**
     * 对文件进行Base64编码 并 存储
     * @param path 路径:"/storage/emulated/0/pimsecure_debug.txt"
     * @param data 需要存入的数据
     * */
    public static boolean setFileToBase64(String path, byte[] data) {
        File desFile = new File(path);
        FileOutputStream fos = null;
        try {
            byte[] decodeBytes = Base64.encode(data, Base64.DEFAULT);
            fos = new FileOutputStream(desFile);
            fos.write(decodeBytes);
            fos.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 读取文件 并 对文件进行Base64解码
     * @param path 路径:"/storage/emulated/0/pimsecure_debug.txt"
     * */
    public static String setBase64ToFile(String path) {
        File file = new File(path);
        FileInputStream inputFile = null;
        try {
            inputFile = new FileInputStream(file);
            byte[] buffer = new byte[(int) file.length()];
            inputFile.read(buffer);
            inputFile.close();

            byte[] decodeBytes = Base64.decode(buffer, Base64.DEFAULT);
            return new String(decodeBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将图片转换成Base64编码的字符串
     * @param path 路径
     */
    public static String setImageFileToBase64(String path){
        if(TextUtils.isEmpty(path)){
            return null;
        }
        InputStream is = null;
        byte[] data = null;
        String result = null;
        try{
            is = new FileInputStream(path);
            //创建一个字符流大小的数组。
            data = new byte[is.available()];
            //写入数组
            is.read(data);
            //用默认的编码格式进行编码
            result = Base64.encodeToString(data,Base64.DEFAULT);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(null !=is){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return result;
    }

    /**
     * 将Base64编码转换为图片地址（保存图片到本地）
     * @param base64Str base64格式字符串
     * @param path 所保存图片的路径
     * @return true 保存成功或失败
     */
    public static boolean setBase64ToImageFile(String base64Str, String path) {
        byte[] data = Base64.decode(base64Str, Base64.NO_WRAP);
        for (int i = 0; i < data.length; i++) {
            if (data[i] < 0) {
                //调整异常数据
                data[i] += 256;
            }
        }
        OutputStream os = null;
        try {
            os = new FileOutputStream(path);
            os.write(data);
            os.flush();
            os.close();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     　　* 将bitmap转换成base64字符串
     　　* @param bitmap
     　　* @return base64 字符串
     　　*/
    public static String setBitmapToBase64(Bitmap bitmap, int bitmapQuality) {
        String string = null;
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, bitmapQuality, bStream);
        byte[] bytes = bStream.toByteArray();
        string = Base64.encodeToString(bytes, Base64.DEFAULT);
        return string;
    }

    /**
     * 将base64转换成bitmap图片
     * @param string base64字符串
     * @return bitmap
     * */
    public static Bitmap setBase64ToBitmap(String string) {
        // 将字符串转换成Bitmap类型
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray;
            bitmapArray = Base64.decode(string, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

}
