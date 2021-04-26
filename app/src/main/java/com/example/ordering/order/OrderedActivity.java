package com.example.ordering.order;

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
import com.example.ordering.db.CartDBManager;
import com.example.ordering.structure.Cart;
import com.example.ordering.structure.MyApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderedActivity extends AppCompatActivity {

    public ListView mlistView;
    public TextView mtvTotalPrice; //菜单总价
    MyApplication app;
    CartDBManager dhelper;
    Cart[] userCart;//某个用户的所有订单
    String dishName;
    int dishShop;
    int dishNum;
    String dishPrice;
    Cart cart = new Cart();//实例化一个订单
    private List<Map<String, Object>> cartList = new ArrayList<Map<String, Object>>();//订单列表
    private SimpleAdapter orderadapter;
    private int uid;
    private Button btncancel;
    private Button btnok;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ordered);

        mtvTotalPrice = findViewById(R.id.ordertotalprice);
        mlistView = findViewById(R.id.OrderedListview);
        btncancel = findViewById(R.id.submit_cancel);
        btnok = findViewById(R.id.submit_ok);
        app = (MyApplication) getApplication();
        uid = Integer.parseInt(app.getUid());

        dhelper = new CartDBManager(this);
        dhelper.open();

        userCart = dhelper.querydishByUser(uid);

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
                //dhelper.deletedishByUid(app.getUid());
                Toast.makeText(OrderedActivity.this, "提交成功", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        this.mlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int arg2, long arg3) {
                final String dmenu = mlistView.getItemAtPosition(arg2) + "";
                try {
                    JSONObject dishdata = new JSONObject(dmenu);
                    dishName = dishdata.getString("title");
                    dishNum = Integer.parseInt(dishdata.getString("num"));
                    dishPrice = dishdata.getString("price");
                    System.out.println(dishName + " " + dishNum + " " + dishPrice);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                final OrderOneDialog orderDlg = new OrderOneDialog(OrderedActivity.this, dishNum);
                orderDlg.setTitle(dishName);
                orderDlg.show();

                orderDlg.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        if (orderDlg.mBtnClicked == OrderOneDialog.ButtonID.BUTTON_OK) {
                            cart.userID = uid;
                            userCart = dhelper.querydishByUser(uid);//某个用户的所有订单
                            cart.dishNum = dhelper.querydishIDbyName(uid, dishName);
                            cart.dishName = dishName;
                            cart.dishShop = dishShop;
                            cart.dishNum = orderDlg.mNum;
                            cart.dishPrice = Double.valueOf(dishPrice);
                            Cart dd = dhelper.queryonedish(uid, cart.dishID);
                            if (dd != null) {
                                if (orderDlg.mNum == 0) {
                                    dhelper.deleteonedish(cart, uid);
                                } else {
                                    dhelper.updateNumemenu(cart.userID, cart.dishID, cart.dishNum);
                                }
                            } else {
                                dhelper.insert(cart);
                            }
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Message msg = handler.obtainMessage();
                                        userCart = dhelper.querydishByUser(uid);
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
        Double totprice, usumprice;
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
                bMap.put("title", userCart[i].dishName);
                bMap.put("price", userCart[i].dishPrice);
                bMap.put("num", userCart[i].dishNum);
                usumprice = userCart[i].dishNum * userCart[i].dishPrice;
                totprice += usumprice;
                bMap.put("sumprice", usumprice);
                cartList.add(bMap);

            }
            orderadapter = new SimpleAdapter(OrderedActivity.this, cartList, R.layout.ordereditem_layout,
                    new String[]{"title", "price", "num", "sumprice"},
                    new int[]{R.id.orderitemtitle, R.id.orderitemprice, R.id.orderitemnum, R.id.orderitemsumprice}
            );
            mlistView.setAdapter(orderadapter);
            orderadapter.notifyDataSetChanged();
            mtvTotalPrice.setText(String.valueOf(totprice));
        }
    }


}
