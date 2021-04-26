package com.example.ordering;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.ordering.db.ShopDBManager;
import com.example.ordering.shop.ShopAdapter;
import com.example.ordering.structure.MyApplication;
import com.example.ordering.structure.Shop;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RefreshData {

    private ShopDBManager shopDBManager;


    public RefreshData(){
        shopDBManager = new ShopDBManager(MyApplication.getContext());
        shopDBManager.open();
    }
    public void refresh(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    //生成json文件
                    Request request1 = new Request.Builder()
                            .url("http://49.234.101.49/ordering/shopdata.php")
                            .build();
                    //解析对应json文件
                    Request request2 = new Request.Builder()
                            .url("http://49.234.101.49/ordering/json/shopinfo.json")
                            .build();
                    Response response1 = client.newCall(request1).execute();
                    Response response2 = client.newCall(request2).execute();
                    String responseData2 = response2.body().string();
                    System.out.println(responseData2);
                    parseJSONWithGSON(responseData2);
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void parseJSONWithGSON(String jsonData) {
        Gson gson = new Gson();
        List<Shop> shopList = gson.fromJson(jsonData, new TypeToken<List<Shop>>() {
        }.getType());
        shopDBManager.getDb().delete("shopInfo", null, null);
        for (Shop shop : shopList) {
            shopDBManager.getDb().execSQL("insert into shopInfo values("+shop.getShopID()+",'"+shop.getShopName()+"','"+shop.getShopImage()+
                    "','"+shop.getShopLocation()+"','"+shop.getShopBrief()+"')");
        }
    }

    //图片json格式转换并存入目录
    /*
    private void parseImageJSONWithGSON(String jsonData) {
        Gson gson = new Gson();
        List<ShopImage> shopImageList = gson.fromJson(jsonData, new TypeToken<List<ShopImage>>() {
        }.getType());

        //db.delete(TABLE,null,null);
        for (ShopImage shopImage : shopImageList) {
            String path = getFilesPath(MyApplication.getContext())+"/shop"+shopImage.getShopID()+".jpg";
            boolean result = Base64Tool.setBase64ToImageFile(shopImage.getShopImage(),path);
            System.out.println("result "+result);
            System.out.println("path "+path);
            shopDBManager.getDb().execSQL("update shopInfo set shopImage = '" + path + "' where shopID = '" + shopImage.getShopID() + "'");
        }
    }


    public String getFilesPath(Context context) {
        String filePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            //外部存储可用
            filePath = context.getExternalFilesDir(null).getPath();
        } else {
            //外部存储不可用
            filePath = context.getFilesDir().getPath();
        }
        return filePath;
    }*/

}
