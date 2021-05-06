package com.example.ordering.ui.order;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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

import com.example.ordering.R;
import com.example.ordering.db.CartDBManager;
import com.example.ordering.db.CommentDBManager;
import com.example.ordering.db.DishDBManager;
import com.example.ordering.db.ShopDBManager;
import com.example.ordering.db.UserDBManager;
import com.example.ordering.cart.CartActivity;
import com.example.ordering.structure.Cart;
import com.example.ordering.structure.Dish;
import com.example.ordering.structure.MyApplication;
import com.example.ordering.structure.Order;
import com.example.ordering.structure.Shop;
import com.example.ordering.structure.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OrderFragment extends Fragment {

    private MyApplication app;

    private OrderViewModel orderViewModel;

    private List<Order> orderList = new ArrayList<>();

    private View root;

    private OrderAdapter adapter;//档口适配器

    private SwipeRefreshLayout swipeRefreshLayout;//实现下拉刷新功能的核心类

    private Handler handler;

    private ShopDBManager shopDBManager;
    private DishDBManager dishDBManager;
    private UserDBManager userDBManager;
    private CartDBManager cartDBManager;
    private CommentDBManager commentDBManager;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        orderViewModel =
                new ViewModelProvider(this).get(OrderViewModel.class);

        root = inflater.inflate(R.layout.fragment_order, container, false);

        initViews();

        return root;
    }

    //初始化订单界面
    public void initViews() {
        shopDBManager = new ShopDBManager(MyApplication.getContext());
        shopDBManager.open();
        dishDBManager = new DishDBManager(MyApplication.getContext());
        dishDBManager.open();
        userDBManager = new UserDBManager(MyApplication.getContext());
        userDBManager.open();
        commentDBManager = new CommentDBManager(MyApplication.getContext());
        commentDBManager.open();
        cartDBManager = new CartDBManager(MyApplication.getContext());
        cartDBManager.open();
        app = (MyApplication) MyApplication.getContext().getApplicationContext();
        FloatingActionButton floatingActionButton = root.findViewById(R.id.order_fab);
        //悬浮按钮判断是否登录，未登录进入登录界面，已登录进入购物车界面
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!app.getLoginState()) {
                    Intent intent = new Intent("com.example.ordering.login");
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getActivity(), CartActivity.class);
                    startActivity(intent);
                }
            }
        });

        orderList = orderViewModel.getOrderList();

        //下拉刷新
        swipeRefreshLayout = root.findViewById(R.id.swipe_refresh);//拿到swipeRefreshLayout实例
        swipeRefreshLayout.setColorSchemeResources(R.color.design_default_color_primary);//设置下拉刷新进度条的颜色
        //设置一个下拉刷新的监听器
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {//当触发了下拉刷新操作的时候会回调这个监听器的onRefresh()方法，在这里处理具体的刷新逻辑
                refreshOrders();
            }
        });
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                //此时代码在主线程当中运行
                switch (msg.what) {//判断what字段
                    case 110:
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (orderList.size() == 0) {
                                    Toast.makeText(getActivity(), "没有订单", Toast.LENGTH_SHORT).show();
                                }
                                for (int i = orderList.size() - 1; i >= 0; i--) {
                                    orderList.remove(i);
                                }//清空列表
                                //app.setShopList(shopList);//设置application的shop列表
                                orderList = orderViewModel.getOrderList();
                                RecyclerView recyclerView = root.findViewById(R.id.order_recycler_view);
                                GridLayoutManager layoutManager = new GridLayoutManager(MyApplication.getContext(), 1);//第一个参数为Context，第二个参数为列数
                                recyclerView.setLayoutManager(layoutManager);
                                adapter = new OrderAdapter(orderList, handler);
                                recyclerView.setAdapter(adapter);
                                System.out.println(recyclerView.getItemDecorationCount());

                                //下拉刷新
                                swipeRefreshLayout = root.findViewById(R.id.swipe_refresh);//拿到swipeRefreshLayout实例
                                swipeRefreshLayout.setColorSchemeResources(R.color.design_default_color_primary);//设置下拉刷新进度条的颜色
                                //设置一个下拉刷新的监听器
                                swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                                    @Override
                                    public void onRefresh() {//当触发了下拉刷新操作的时候会回调这个监听器的onRefresh()方法，在这里处理具体的刷新逻辑
                                        refreshOrders();
                                    }
                                });

                                adapter.notifyDataSetChanged();//notifyDataSetChanged方法通知数据发生了变化
                                swipeRefreshLayout.setRefreshing(false);//用于表示刷新事件结束，并隐藏刷新进度条
                            }
                        });
                        break;
                    case 120:
                        break;
                    default:
                        break;
                }
            }
        };

    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (orderList.size() == 0) {
                    Toast.makeText(getActivity(), "没有订单", Toast.LENGTH_SHORT).show();
                }
                for (int i = orderList.size() - 1; i >= 0; i--) {
                    orderList.remove(i);
                }//清空列表
                //app.setShopList(shopList);//设置application的shop列表
                orderList = orderViewModel.getOrderList();
                RecyclerView recyclerView = root.findViewById(R.id.order_recycler_view);
                GridLayoutManager layoutManager = new GridLayoutManager(MyApplication.getContext(), 1);//第一个参数为Context，第二个参数为列数
                recyclerView.setLayoutManager(layoutManager);
                adapter = new OrderAdapter(orderList, handler);
                recyclerView.setAdapter(adapter);
                System.out.println(recyclerView.getItemDecorationCount());

                //下拉刷新
                swipeRefreshLayout = root.findViewById(R.id.swipe_refresh);//拿到swipeRefreshLayout实例
                swipeRefreshLayout.setColorSchemeResources(R.color.design_default_color_primary);//设置下拉刷新进度条的颜色
                //设置一个下拉刷新的监听器
                swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {//当触发了下拉刷新操作的时候会回调这个监听器的onRefresh()方法，在这里处理具体的刷新逻辑
                        refreshOrders();
                    }
                });

                adapter.notifyDataSetChanged();//notifyDataSetChanged方法通知数据发生了变化
                swipeRefreshLayout.setRefreshing(false);//用于表示刷新事件结束，并隐藏刷新进度条
            }
        });
    }

    //进行本地刷新操作
    private void refreshOrders() {
        //先开启一个线程
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();

                    //生成菜单json文件
                    Request cartRequest1 = new Request.Builder()
                            .url("http://49.234.101.49/ordering/cartdata.php")
                            .build();

                    //解析对应菜单json文件
                    Request cartRequest2 = new Request.Builder()
                            .url("http://49.234.101.49/ordering/json/cartinfo.json")
                            .build();

                    client.newCall(cartRequest1).execute();

                    Response cartResponse = client.newCall(cartRequest2).execute();

                    String cartResData = cartResponse.body().string();

                    parseCartJSONWithGSON(cartResData);
                    //parseCommentJSONWithGSON(commentResData);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //睡眠结束后，runOnUiThread方法将线程切换回主线程
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (orderList.size() == 0) {
                            Toast.makeText(getActivity(), "没有订单", Toast.LENGTH_SHORT).show();
                        }
                        for (int i = orderList.size() - 1; i >= 0; i--) {
                            orderList.remove(i);
                        }//清空列表
                        //app.setShopList(shopList);//设置application的shop列表
                        orderList = orderViewModel.getOrderList();
                        RecyclerView recyclerView = root.findViewById(R.id.order_recycler_view);
                        GridLayoutManager layoutManager = new GridLayoutManager(MyApplication.getContext(), 1);//第一个参数为Context，第二个参数为列数
                        recyclerView.setLayoutManager(layoutManager);
                        adapter = new OrderAdapter(orderList, handler);
                        recyclerView.setAdapter(adapter);
                        System.out.println(recyclerView.getItemDecorationCount());

                        //下拉刷新
                        swipeRefreshLayout = root.findViewById(R.id.swipe_refresh);//拿到swipeRefreshLayout实例
                        swipeRefreshLayout.setColorSchemeResources(R.color.design_default_color_primary);//设置下拉刷新进度条的颜色
                        //设置一个下拉刷新的监听器
                        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                            @Override
                            public void onRefresh() {//当触发了下拉刷新操作的时候会回调这个监听器的onRefresh()方法，在这里处理具体的刷新逻辑
                                refreshOrders();
                            }
                        });

                        adapter.notifyDataSetChanged();//notifyDataSetChanged方法通知数据发生了变化
                        swipeRefreshLayout.setRefreshing(false);//用于表示刷新事件结束，并隐藏刷新进度条
                    }
                });
            }
        }).start();
    }

    private void parseCartJSONWithGSON(String jsonData) {
        Gson gson = new Gson();
        List<Cart> cartList = gson.fromJson(jsonData, new TypeToken<List<Cart>>() {
        }.getType());
        cartDBManager.deleteCartInfo();
        if (cartList != null) {
            for (Cart cart : cartList) {
                shopDBManager.getDb().execSQL("insert into cartInfo values(" + cart.getCartID() + ",'" + cart.getOrderID() + "'," + cart.getCartUserID() +
                        "," + cart.getCartDishID() + "," + cart.getCartShopID() + ",'" + cart.getCartDishName() + "'," + cart.getCartDishNum() +
                        "," + cart.getCartDishPrice() + "," + cart.getCartPrice() + ",'" + cart.getCartStatus() + "','" + cart.getCartTime() + "')");
            }
        }
    }

    private void parseCommentJSONWithGSON(String jsonData) {
        Gson gson = new Gson();
        List<Shop> shopList = gson.fromJson(jsonData, new TypeToken<List<Shop>>() {
        }.getType());
        shopDBManager.deleteShopInfo();
        for (Shop shop : shopList) {
            shopDBManager.getDb().execSQL("insert into shopInfo values(" + shop.getShopID() + ",'" + shop.getShopName() + "','" + shop.getShopImage() +
                    "','" + shop.getShopLocation() + "','" + shop.getShopBrief() + "')");
        }
    }
}