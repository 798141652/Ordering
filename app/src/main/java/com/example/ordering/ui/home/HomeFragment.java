package com.example.ordering.ui.home;

import android.content.Intent;
import android.os.Bundle;
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

import com.example.ordering.order.OrderedActivity;
import com.example.ordering.shop.ShopActivity;
import com.example.ordering.structure.MyApplication;
import com.example.ordering.R;
import com.example.ordering.shop.ShopAdapter;
import com.example.ordering.structure.Shop;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    private List<Shop> shopList = new ArrayList<>();

    private View root;

    private ShopAdapter adapter;//档口适配器

    private SwipeRefreshLayout swipeRefreshLayout;//实现下拉刷新功能的核心类

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
        shopList = homeViewModel.getShopList();
        if (shopList.size() == 0) {
            Toast.makeText(getActivity(), "请检查网络", Toast.LENGTH_SHORT).show();
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

    }

    //进行本地刷新操作
    private void refreshShops() {
        //先开启一个线程
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);//线程沉睡1秒钟，因为本地刷新操作速度非常快，如果不将线程沉睡的话，刷新立刻就结束了，从而看不到刷新的过程
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
                //睡眠结束后，runOnUiThread方法将线程切换回主线程
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        initViews();//重新生成数据
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
}