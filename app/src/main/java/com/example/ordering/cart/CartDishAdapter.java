package com.example.ordering.cart;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ordering.R;
import com.example.ordering.db.CartDBManager;
import com.example.ordering.db.DishDBManager;
import com.example.ordering.structure.Cart;
import com.example.ordering.structure.MyApplication;


import java.util.List;

public class CartDishAdapter extends RecyclerView.Adapter<CartDishAdapter.ViewHolder> {

    private Context mContext;

    private List<Cart> cartList;

    private Cart cart;

    private DishDBManager dishDBManager;

    private CartDBManager cartDBManager;

    private Handler handler;

    private MyApplication app;


    static class ViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        ImageView dishImage;
        TextView dishName;
        TextView dishPrice;
        TextView dishNum;


        public ViewHolder(View view){
            super(view);
            cardView = (CardView) view;
            dishImage = view.findViewById(R.id.cart_dish_image);
            dishName = view.findViewById(R.id.cart_dish_name);
            dishPrice = view.findViewById(R.id.cart_dish_price);
            dishNum = view.findViewById(R.id.cart_dish_num);
        }


    }

    public CartDishAdapter(List<Cart> cartList,Handler handler){
        this.cartList = cartList;
        this.handler = handler;
    }

    @NonNull
    @Override
    public CartDishAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(mContext == null){
            mContext = parent.getContext();
        }

        cartDBManager = new CartDBManager(mContext);
        cartDBManager.open();

        View view = LayoutInflater.from(mContext).inflate(R.layout.cart_dish_item,parent,false);//加载对应的布局

        final CartDishAdapter.ViewHolder holder = new CartDishAdapter.ViewHolder(view);



        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Cart cart = cartList.get(position);
                final CartOneDialog orderDlg = new CartOneDialog(mContext, cart.cartDishNum);
                orderDlg.setTitle(cart.cartDishName);
                orderDlg.show();
                orderDlg.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        if (orderDlg.mBtnClicked == CartOneDialog.ButtonID.BUTTON_OK) {
                            /*
                            cartInsert.cartUserID = uid;
                            cartInsert.cartDishID = dishid;
                            cartInsert.cartDishNum = dishnum;
                            cartInsert.cartDishName = dishname;
                            cartInsert.cartShopID = dishshop;
                            cartInsert.cartDishNum = orderDlg.mNum;
                            cartInsert.cartDishPrice = dish.dishPrice;*/
                            int uid = cart.cartUserID;
                            int dishid = cart.cartDishID;
                            Cart cartUpdate = cartDBManager.queryonedish(uid, dishid);
                            if (cartUpdate == null) {//购物车内没有找到菜单项
                                cart.cartStatus = "0";//购物车状态
                                cartDBManager.insert(cart);
                            } else if(cartUpdate.cartStatus.equals("0")){//购物车内找到菜单项
                                if(orderDlg.mNum == 0){
                                    cartDBManager.deleteonedish(cartUpdate,cart.cartUserID);
                                }else {
                                    cartDBManager.updateNumemenu(cartUpdate.cartUserID, cartUpdate.cartDishID, orderDlg.mNum);
                                }
                            }
                            Toast.makeText(mContext, cart.cartDishName + " " + orderDlg.mNum + "份已经加入购物车", Toast.LENGTH_SHORT).show();
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
                        }
                    }
                });

            }
        });
        cartDBManager.getDb().close();
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CartDishAdapter.ViewHolder holder, int position) {
        dishDBManager = new DishDBManager(mContext);
        dishDBManager.open();
        cart = cartList.get(position);
        String imagePath = dishDBManager.getDishImageByID(cart.getCartDishID());
        Glide.with(mContext).
                load("http://49.234.101.49/ordering/"+imagePath)
                .placeholder(R.drawable.loading)
                .into(holder.dishImage);

        holder.dishName.setText(cart.getCartDishName());
        holder.dishNum.setText("X "+String.valueOf(cart.getCartDishNum()));
        holder.dishPrice.setText("￥"+String.valueOf(cart.getCartDishPrice()));
        dishDBManager.getDb().close();
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

}
