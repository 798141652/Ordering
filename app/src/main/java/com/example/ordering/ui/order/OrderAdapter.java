package com.example.ordering.ui.order;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ordering.R;
import com.example.ordering.comment.CommentActivity;
import com.example.ordering.db.CartDBManager;
import com.example.ordering.db.CommentDBManager;
import com.example.ordering.db.ShopDBManager;
import com.example.ordering.shop.ShopActivity;
import com.example.ordering.structure.Cart;
import com.example.ordering.structure.MyApplication;
import com.example.ordering.structure.Order;
import com.example.ordering.structure.Shop;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

    private Context mContext;

    private List<Order> orderList;

    private List<Cart> cartList;

    private Order order;

    private ShopDBManager shopDBManager;

    private CartDBManager cartDBManager;

    private CommentDBManager commentDBManager;

    private OrderDishAdapter orderDishAdapter;

    private MyApplication app;

    private Handler handler;



    static class ViewHolder extends RecyclerView.ViewHolder{
        RelativeLayout orderShop;
        ImageView shopImage;
        TextView shopName;
        TextView shopLocation;
        RecyclerView orderDishList;
        TextView orderTotalPrice;
        TextView orderStatus;
        TextView orderTime;
        Button orderButton;

        public ViewHolder(View view){
            super(view);
            orderShop = view.findViewById(R.id.order_shop);
            shopImage = view.findViewById(R.id.order_shop_image);
            shopName = view.findViewById(R.id.order_shop_name);
            shopLocation = view.findViewById(R.id.order_shop_location);
            orderDishList = view.findViewById(R.id.order_dish_list);
            orderTotalPrice = view.findViewById(R.id.order_total_price);
            orderStatus = view.findViewById(R.id.order_status);
            orderButton = view.findViewById(R.id.order_button);
            orderTime = view.findViewById(R.id.order_time);
        }
    }

    public OrderAdapter(List<Order> orderList, Handler handler){
        this.orderList = orderList;
        this.handler = handler;
    }

    @NonNull
    @Override
    public OrderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(mContext == null){
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.order_item,parent,false);

        shopDBManager = new ShopDBManager(mContext);
        shopDBManager.open();

        cartDBManager = new CartDBManager(mContext);
        cartDBManager.open();


        //给CardView注册了一个点击事件监听器,在事件点击中获得当前点击项的订单项
        // 把它们传入到intent中，最后调用startActivity方法启动ShopActivity
        final ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull OrderAdapter.ViewHolder holder, int position) {
        order = orderList.get(position);
        Double orderTotalPrice = 0.0;

        Shop shop = shopDBManager.getShopInfoByID(order.getOrderShop());
        holder.shopName.setText(shop.getShopName());
        holder.shopLocation.setText(shop.getShopLocation());

        Glide.with(mContext).
                load("http://49.234.101.49/ordering/"+shop.getShopImage())
                .placeholder(R.drawable.loading)
                .into(holder.shopImage);

        holder.orderShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyApplication.getContext(), ShopActivity.class);
                intent.putExtra(ShopActivity.SHOP_NAME,shop.getShopName());
                intent.putExtra(ShopActivity.SHOP_IMAGE,shop.getShopImage());
                intent.putExtra(ShopActivity.SHOP_ID,shop.getShopID());
                mContext.startActivity(intent);
            }
        });

        cartList = order.getCartList();
        for(int i = 0;i<cartList.size();i++){
            Cart cart = cartList.get(i);
            orderTotalPrice += cart.getCartDishNum()*cart.getCartDishPrice();
        }
        holder.orderTime.setText(cartList.get(0).getCartTime());

        holder.orderTotalPrice.setText("共计:￥"+String.valueOf(orderTotalPrice));

        GridLayoutManager layoutManager = new GridLayoutManager(mContext, 1);//第一个参数为Context，第二个参数为列数
        holder.orderDishList.setLayoutManager(layoutManager);


        if("1".equals(order.getOrderStatus())) {//已下单状态
            holder.orderStatus.setVisibility(View.VISIBLE);
            holder.orderButton.setVisibility(View.VISIBLE);

            holder.orderButton.setText("取消");

            holder.orderStatus.setText("正在接单");

            holder.orderButton.setOnClickListener(new View.OnClickListener() {
                int position = holder.getAdapterPosition();
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext, "已取消订单", Toast.LENGTH_SHORT).show();
                    String orderID = orderList.get(position).getOrderID();
                    cartDBManager.orderCancel(orderID);
                    cartDBManager.uploadData();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Message msg = handler.obtainMessage();
                                //uDishMenus = dhelper.querydishByUser(uid);
                                msg.what = 110;
                                handler.sendMessage(msg);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                    //Toast.makeText(mContext,"订单已提交",Toast.LENGTH_SHORT).show();
                }
            });

        }else if(order.getOrderStatus().equals("2")){//已完成订单状态

            holder.orderStatus.setVisibility(View.VISIBLE);
            holder.orderButton.setVisibility(View.VISIBLE);
            holder.orderStatus.setText("已完成订单");
            holder.orderButton.setText("评论订单");
            holder.orderButton.setOnClickListener(new View.OnClickListener() {
                int position = holder.getAdapterPosition();
                String orderID = orderList.get(position).getOrderID();

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, CommentActivity.class);
                    intent.putExtra("orderID",orderID);
                    mContext.startActivity(intent);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Message msg = handler.obtainMessage();
                                //uDishMenus = dhelper.querydishByUser(uid);
                                msg.what = 120;
                                handler.sendMessage(msg);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                    //Toast.makeText(mContext,"订单已提交",Toast.LENGTH_SHORT).show();
                }
            });
        }else if(order.getOrderStatus().equals("3")){//已完成订单状态
            holder.orderStatus.setVisibility(View.VISIBLE);
            holder.orderButton.setVisibility(View.GONE);
            holder.orderStatus.setText("已完成评论");
        }


        orderDishAdapter = new OrderDishAdapter(cartList);
        holder.orderDishList.setAdapter(orderDishAdapter);
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

}
