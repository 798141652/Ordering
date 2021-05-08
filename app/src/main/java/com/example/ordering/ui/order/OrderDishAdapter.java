package com.example.ordering.ui.order;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ordering.R;
import com.example.ordering.db.DishDBManager;
import com.example.ordering.dish.DishActivity;
import com.example.ordering.structure.Cart;
import com.example.ordering.structure.Dish;
import com.example.ordering.structure.MyApplication;

import java.util.List;

public class OrderDishAdapter extends RecyclerView.Adapter<OrderDishAdapter.ViewHolder> {

    private Context mContext;

    private List<Cart> cartList;

    private Cart cart;

    private DishDBManager dishDBManager;



    private MyApplication app;

    static class ViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        ImageView dishImage;
        TextView dishName;
        TextView dishPrice;
        TextView dishNum;
        //TextView dishTotalPrice;

        public ViewHolder(View view){
            super(view);
            cardView = (CardView) view;
            dishImage = view.findViewById(R.id.order_dish_image);
            dishName = view.findViewById(R.id.order_dish_name);
            dishPrice = view.findViewById(R.id.order_dish_price);
            dishNum = view.findViewById(R.id.order_dish_num);
            //dishTotalPrice = view.findViewById(R.id.order_dish_total_price);
        }
    }

    public OrderDishAdapter(List<Cart> cartList){
        this.cartList = cartList;
    }

    @NonNull
    @Override
    public OrderDishAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        if(mContext == null){
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.order_dish_item,parent,false);//加载对应的布局

        final ViewHolder holder = new ViewHolder(view);


        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull OrderDishAdapter.ViewHolder holder, int position) {
        dishDBManager = new DishDBManager(mContext);
        dishDBManager.open();
        cart = cartList.get(position);
        String imagePath = dishDBManager.getDishImageByID(cart.getCartDishID());
        Glide.with(mContext).
                load("http://49.234.101.49/ordering/"+imagePath)
                .placeholder(R.drawable.loading)
                .into(holder.dishImage);

        holder.dishName.setText(cart.getCartDishName());
        holder.dishPrice.setText(String.valueOf(cart.getCartDishPrice()));
        holder.dishNum.setText("X "+String.valueOf(cart.getCartDishNum()));
        //holder.dishTotalPrice.setText(String.valueOf(cart.getCartDishPrice()*cart.getCartDishNum()));

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Dish dish = dishDBManager.getDishByID(cartList.get(position).cartDishID);
                Intent intent = new Intent(mContext, DishActivity.class);
                intent.putExtra(DishActivity.DISH_ID,dish.getDishID());
                intent.putExtra(DishActivity.DISH_NAME, dish.getDishName());
                intent.putExtra(DishActivity.DISH_PRICE, dish.getDishPrice());
                intent.putExtra(DishActivity.DISH_IMAGE_ID, dish.getDishImage());
                intent.putExtra(DishActivity.DISH_TYPE, dish.getDishType());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

}
