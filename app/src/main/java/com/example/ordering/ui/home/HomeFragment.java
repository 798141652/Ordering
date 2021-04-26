package com.example.ordering.ui.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.ordering.Base64Tool;
import com.example.ordering.db.DBHelper;
import com.example.ordering.db.ShopDBManager;
import com.example.ordering.order.OrderedActivity;
import com.example.ordering.shop.ShopActivity;
import com.example.ordering.structure.MyApplication;
import com.example.ordering.R;
import com.example.ordering.shop.ShopAdapter;
import com.example.ordering.structure.Shop;
import com.example.ordering.structure.ShopImage;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    private List<Shop> shopList = new ArrayList<>();

    private View root;

    private ShopAdapter adapter;//档口适配器

    private SwipeRefreshLayout swipeRefreshLayout;//实现下拉刷新功能的核心类

    private ShopDBManager shopDBManager;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        root = inflater.inflate(R.layout.fragment_homepage, container, false);
        initViews();

        return root;
    }

    //初始化界面
    public void initViews() {
        shopDBManager = new ShopDBManager(MyApplication.getContext());
        shopDBManager.open();
        MyApplication app = (MyApplication) MyApplication.getContext().getApplicationContext();
        FloatingActionButton floatingActionButton = root.findViewById(R.id.fab);
        //悬浮按钮判断是否登录，未登录进入登录界面，已登录进入购物车界面
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!app.getLoginState()) {
                    Intent intent = new Intent("com.example.ordering.login");
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getActivity(), OrderedActivity.class);
                    startActivity(intent);
                }
            }
        });

        //展示档口列表
        for ( int i=shopList.size()-1; i>=0;i--) {
            shopList.remove(i);
        }//清空列表

        if (shopList.size() == 0) {
            Toast.makeText(getActivity(), "请刷新", Toast.LENGTH_SHORT).show();
        } else {
            shopList = homeViewModel.getShopList();
            RecyclerView recyclerView = root.findViewById(R.id.shop_recycler_view);
            GridLayoutManager layoutManager = new GridLayoutManager(MyApplication.getContext(), 1);//第一个参数为Context，第二个参数为列数
            recyclerView.setLayoutManager(layoutManager);
            adapter = new ShopAdapter(shopList);
            recyclerView.setAdapter(adapter);
        }

            //下拉刷新
            swipeRefreshLayout = root.findViewById(R.id.swipe_refresh);//拿到swipeRefreshLayout实例
            swipeRefreshLayout.setColorSchemeResources(R.color.design_default_color_primary);//设置下拉刷新进度条的颜色
            //设置一个下拉刷新的监听器
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {//当触发了下拉刷新操作的时候会回调这个监听器的onRefresh()方法，在这里处理具体的刷新逻辑
                    refreshShops();
                }
            });

    }




    //进行本地刷新操作
    private void refreshShops() {
        //先开启一个线程
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

                    //解析对应json文件
                    Request request3 = new Request.Builder()
                            .url("http://49.234.101.49/ordering/json/shopimageinfo.json")
                            .build();
                    Response response1 = client.newCall(request1).execute();
                    Response response2 = client.newCall(request2).execute();
                    Response response3 = client.newCall(request3).execute();
                    String responseData2 = response2.body().string();
                    String responseData3 = response3.body().string();
                    System.out.println(responseData2);
                    parseJSONWithGSON(responseData2);
                    parseImageJSONWithGSON(responseData3);
                    //Thread.sleep(1000);
                }catch (IOException e){
                    e.printStackTrace();
                }
                //睡眠结束后，runOnUiThread方法将线程切换回主线程
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        initViews();//重新生成数据

                        for ( int i=shopList.size()-1; i>=0;i--) {
                            shopList.remove(i);
                        }//清空列表
                        shopList = homeViewModel.getShopList();
                        if (shopList.size() == 0) {
                            Toast.makeText(getActivity(), "请刷新", Toast.LENGTH_SHORT).show();
                        } else {
                            RecyclerView recyclerView = root.findViewById(R.id.shop_recycler_view);
                            GridLayoutManager layoutManager = new GridLayoutManager(MyApplication.getContext(), 1);//第一个参数为Context，第二个参数为列数
                            recyclerView.setLayoutManager(layoutManager);
                            adapter = new ShopAdapter(shopList);
                            recyclerView.setAdapter(adapter);
                        }

                        //下拉刷新
                        swipeRefreshLayout = root.findViewById(R.id.swipe_refresh);//拿到swipeRefreshLayout实例
                        swipeRefreshLayout.setColorSchemeResources(R.color.design_default_color_primary);//设置下拉刷新进度条的颜色
                        //设置一个下拉刷新的监听器
                        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                            @Override
                            public void onRefresh() {//当触发了下拉刷新操作的时候会回调这个监听器的onRefresh()方法，在这里处理具体的刷新逻辑
                                refreshShops();
                            }
                        });



                        if (shopList.size() == 0) {
                            Toast.makeText(getActivity(), "请检查网络", Toast.LENGTH_SHORT).show();
                        } else {
                            adapter.notifyDataSetChanged();//notifyDataSetChanged方法通知数据发生了变化
                        }
                        swipeRefreshLayout.setRefreshing(false);//用于表示刷新事件结束，并隐藏刷新进度条
                    }
                });
            }
        }).start();
    }



    private void parseJSONWithGSON(String jsonData) {
        Gson gson = new Gson();
        List<Shop> shopList = gson.fromJson(jsonData, new TypeToken<List<Shop>>() {
        }.getType());
        shopDBManager.getDb().delete("shopInfo", null, null);
        for (Shop shop : shopList) {
            shopDBManager.getDb().execSQL("insert into shopInfo(shopID,shopName,shopLocation,shopBrief,shopImage) values(" + shop.getShopID() + ",'" + shop.getShopName() + "','"
                    + shop.getShopLocation() + "','" + shop.getShopBrief() + "','" + shop.getShopImage() + "')");
        }
    }

    //图片json格式转换并存入目录
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
    }

}