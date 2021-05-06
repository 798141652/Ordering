package com.example.ordering.cart;

import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.os.Handler;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ordering.R;
import com.example.ordering.db.CartDBManager;
import com.example.ordering.db.ShopDBManager;
import com.example.ordering.shop.ShopActivity;
import com.example.ordering.structure.Cart;
import com.example.ordering.structure.Dish;
import com.example.ordering.structure.MyApplication;
import com.example.ordering.structure.Order;
import com.example.ordering.structure.Shop;

import java.util.List;


public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private Context mContext;

    private List<Order> cartList;

    private List<Cart> dishList;

    private Order order;

    private ShopDBManager shopDBManager;
    private CartDBManager cartDBManager;

    private CartDishAdapter cartDishAdapter;

    private MyApplication app;

    private Handler handler;
    Double cartTotalPrice ;



    static class ViewHolder extends RecyclerView.ViewHolder{
        RelativeLayout cartShop;
        ImageView shopImage;
        TextView shopName;
        TextView shopLocation;
        RecyclerView orderDishList;
        TextView orderTotalPrice;
        Button orderButton;

        public ViewHolder(View view){
            super(view);
            cartShop = view.findViewById(R.id.cart_shop);
            shopImage = view.findViewById(R.id.cart_shop_image);
            shopName = view.findViewById(R.id.cart_shop_name);
            shopLocation = view.findViewById(R.id.cart_shop_location);
            orderDishList = view.findViewById(R.id.cart_dish_list);
            orderTotalPrice = view.findViewById(R.id.cart_total_price);
            orderButton = view.findViewById(R.id.cart_button);
        }
    }

    public CartAdapter(List<Order> cartList,Handler handler){
        this.cartList = cartList;
        this.handler = handler;
    }

    @NonNull
    @Override
    public CartAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(mContext == null){
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.cart_item,parent,false);

        shopDBManager = new ShopDBManager(mContext);
        shopDBManager.open();

        cartDBManager = new CartDBManager(mContext);
        cartDBManager.open();


        final ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapter.ViewHolder holder, int position) {
        order = cartList.get(position);
        cartTotalPrice = 0.0;

        Shop shop = shopDBManager.getShopInfoByID(order.getOrderShop());
        holder.shopName.setText(shop.getShopName());
        holder.shopLocation.setText(shop.getShopLocation());
        Glide.with(mContext).
                load("http://49.234.101.49/ordering/"+shop.getShopImage())
                .placeholder(R.drawable.loading)
                .into(holder.shopImage);

        holder.cartShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyApplication.getContext(), ShopActivity.class);
                intent.putExtra(ShopActivity.SHOP_NAME,shop.getShopName());
                intent.putExtra(ShopActivity.SHOP_IMAGE,shop.getShopImage());
                intent.putExtra(ShopActivity.SHOP_ID,shop.getShopID());
                mContext.startActivity(intent);
            }
        });

        dishList = order.getCartList();
        for(int i = 0;i<dishList.size();i++){
            Cart cart = dishList.get(i);
            cartTotalPrice += cart.getCartDishNum()*cart.getCartDishPrice();
        }

        holder.orderTotalPrice.setText("共计:￥"+String.valueOf(cartTotalPrice));

        GridLayoutManager layoutManager = new GridLayoutManager(mContext, 1);//第一个参数为Context，第二个参数为列数
        holder.orderDishList.setLayoutManager(layoutManager);


        holder.orderButton.setOnClickListener(new View.OnClickListener() {
            int position = holder.getAdapterPosition();
            List<Cart> list = cartList.get(position).getCartList();
            Double orderPrice = 0.0;
            @Override
            public void onClick(View v) {
                for(int i =0;i< list.size();i++){
                    orderPrice+=list.get(i).cartDishNum*list.get(i).cartDishPrice;
                }
                for(int i = 0;i < list.size();i++) {
                    cartDBManager.cartToOrder(list.get(i).cartUserID,orderPrice,list.get(i).cartID);
                }
                cartDBManager.uploadData();
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
                Toast.makeText(mContext,"订单已提交",Toast.LENGTH_SHORT).show();
            }
        });
        cartDishAdapter = new CartDishAdapter(dishList,handler);
        holder.orderDishList.setAdapter(cartDishAdapter);
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

}
