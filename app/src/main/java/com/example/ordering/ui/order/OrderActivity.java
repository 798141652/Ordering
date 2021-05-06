package com.example.ordering.ui.order;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ordering.R;
import com.example.ordering.cart.CartOneDialog;
import com.example.ordering.db.CartDBManager;
import com.example.ordering.structure.Cart;
import com.example.ordering.structure.Dish;
import com.example.ordering.structure.MyApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderActivity extends AppCompatActivity {

    public ListView mlistView;
    public TextView mtvTotalPrice; //菜单总价
    public MyApplication app;
    public CartDBManager dbManager;
    public Cart[] userCart;//某个用户的所有订单
    public String dishName;
    public int dishID;
    public int dishShop;
    public int dishNum;
    public int cartID;
    public String dishPrice;
    public Cart cart = new Cart();//实例化一个订单

    public Dish dish = new Dish();

    private List<Map<String, Object>> cartList = new ArrayList<Map<String, Object>>();//订单列表
    private SimpleAdapter orderadapter;
    private int uid;
    private Double totprice, usumprice;
    private Button btncancel;
    private Button btnok;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cart_item);

        /*
        mtvTotalPrice = findViewById(R.id.ordertotalprice);
        mlistView = findViewById(R.id.OrderedListview);
        btncancel = findViewById(R.id.submit_cancel);
        btnok = findViewById(R.id.submit_ok);
        */

        app = (MyApplication) getApplication();
        uid = Integer.parseInt(app.getUid());

        dbManager = new CartDBManager(this);
        dbManager.open();

        userCart = dbManager.queryCartByUser(uid);

        final Handler handler = new Handler(Looper.getMainLooper()){
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 110) {
                    UpdateOrderList();
                }
            }
        };
        btncancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println(app.getUid());
                //dbManager.cartToOrder(Integer.valueOf(app.getUid()),totprice,);
                Toast.makeText(OrderActivity.this, "提交成功", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        this.mlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            //点击每一项显示对应的Dialog
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int arg2, long arg3) {
                final String dmenu = mlistView.getItemAtPosition(arg2) + "";
                try {
                    JSONObject dishdata = new JSONObject(dmenu);
                    dishName = dishdata.getString("title");
                    dishShop = dishdata.getInt("shop");
                    dishID = dishdata.getInt("dishid");
                    cartID = dishdata.getInt("cartid");
                    dishNum = Integer.parseInt(dishdata.getString("num"));
                    dishPrice = dishdata.getString("price");
                    System.out.println(dishName + " " + dishNum + " " + dishPrice);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                final CartOneDialog orderDlg = new CartOneDialog(OrderActivity.this, dishNum);
                orderDlg.setTitle(dishName);
                orderDlg.show();
                userCart = dbManager.queryCartByUser(uid);//某个用户的所有订单

                orderDlg.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        if (orderDlg.mBtnClicked == CartOneDialog.ButtonID.BUTTON_OK) {
                            cart.cartID = cartID;
                            cart.cartUserID = uid;
                            cart.cartDishID = dishID;
                            cart.cartDishNum = dbManager.querydishIDbyName(uid, dishName);
                            cart.cartDishName = dishName;
                            cart.cartShopID = dishShop;
                            cart.cartDishNum = orderDlg.mNum;
                            cart.cartDishPrice = Double.valueOf(dishPrice);
                            Cart dd = dbManager.queryonedish(uid, cart.cartDishID);
                            if (dd != null) {
                                if (orderDlg.mNum == 0) {
                                    dbManager.deleteonedish(cart, uid);
                                } else {
                                    dbManager.updateNumemenu(cart.cartUserID, cart.cartDishID, cart.cartDishNum);
                                }
                            } else {
                                cart.cartStatus = "0";//购物车状态
                                dbManager.insert(cart);
                            }
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Message msg = handler.obtainMessage();
                                        userCart = dbManager.queryCartByUser(uid);
                                        msg.what = 110;
                                        handler.sendMessage(msg);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).start();

                        }

                    }
                });

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        UpdateOrderList();
    }


    private void UpdateOrderList() {

        if (userCart == null) {
            cartList.clear();
            cartList.removeAll(cartList);
            mlistView.setAdapter(null);
            mtvTotalPrice.setText(Double.toString(0.0));

        } else {
            totprice = 0.0;
            cartList.clear();
            cartList.removeAll(cartList);
            mlistView.setAdapter(null);
            for (int i = 0; i < userCart.length; i++) {
                Map<String, Object> bMap = new HashMap<String, Object>();
                bMap.put("cartid",userCart[i].cartID);
                bMap.put("dishid",userCart[i].cartDishID);
                bMap.put("shop",userCart[i].cartShopID);
                bMap.put("title", userCart[i].cartDishName);
                bMap.put("price", userCart[i].cartDishPrice);
                bMap.put("num", userCart[i].cartDishNum);
                usumprice = userCart[i].cartDishNum * userCart[i].cartDishPrice;
                totprice += usumprice;
                bMap.put("sumprice", usumprice);
                cartList.add(bMap);
            }
            orderadapter = new SimpleAdapter(OrderActivity.this, cartList, R.layout.cartitem_layout,
                    new String[]{"title", "price", "num", "sumprice"},
                    new int[]{R.id.orderitemtitle, R.id.orderitemprice, R.id.orderitemnum, R.id.orderitemsumprice}
            );
            mlistView.setAdapter(orderadapter);
            orderadapter.notifyDataSetChanged();
            mtvTotalPrice.setText(String.valueOf(totprice));
        }
    }


}
