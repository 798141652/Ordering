package com.example.ordering.dish;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ordering.R;
import com.example.ordering.db.CartDBManager;
import com.example.ordering.order.OrderOneDialog;
import com.example.ordering.order.OrderedActivity;
import com.example.ordering.structure.Cart;
import com.example.ordering.structure.Dish;
import com.example.ordering.structure.MyApplication;

import java.util.List;

public class DishAdapter extends RecyclerView.Adapter<DishAdapter.ViewHolder> {

    public ListView mlistView;

    private Context mContext;

    private List<Dish> mDishList;

    private Dish dish;

    private Cart cartInsert = new Cart();

    private int uid;

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView dishImage;
        TextView dishName;
        ImageView dishAdd;

        public ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;
            dishImage = view.findViewById(R.id.dish_image);
            dishName = view.findViewById(R.id.dish_name);
            dishAdd = view.findViewById(R.id.dish_add);
        }
    }

    public DishAdapter(List<Dish> dishList) {
        mDishList = dishList;
    }

    @NonNull
    @Override
    public DishAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.dish_item, parent, false);

        //判断是否登录
        MyApplication app = (MyApplication) MyApplication.getContext().getApplicationContext();



        //给CardView注册了一个点击事件监听器,在事件点击中获得当前点击项的水果名和水果图片资源id，
        // 把它们传入到intent中，最后调用startActivity方法启动FruitActivity
        final ViewHolder holder = new ViewHolder(view);
        holder.dishImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                dish = mDishList.get(position);
                Intent intent = new Intent(mContext, DishActivity.class);
                intent.putExtra(DishActivity.DISH_NAME, dish.getDishName());
                intent.putExtra(DishActivity.DISH_IMAGE_ID, dish.getDishImage());
                intent.putExtra(DishActivity.DISH_TYPE, dish.getDishType());
                mContext.startActivity(intent);
            }
        });
        holder.dishName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Dish dish = mDishList.get(position);
                Intent intent = new Intent(mContext, DishActivity.class);
                intent.putExtra(DishActivity.DISH_NAME, dish.getDishName());
                intent.putExtra(DishActivity.DISH_IMAGE_ID, dish.getDishImage());
                intent.putExtra(DishActivity.DISH_TYPE, dish.getDishType());
                mContext.startActivity(intent);
            }
        });
        holder.dishAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (app.getLoginState()) {

                    uid = Integer.parseInt(app.getUid());

                    Intent intent = new Intent(MyApplication.getContext(), OrderedActivity.class);

                    int position = holder.getAdapterPosition();//获取当前位置
                    dish = mDishList.get(position);//得到当前菜品

                    final int dishid = dish.dishID;
                    String dishname = dish.dishName;
                    int dishnum;
                    int dishshop = dish.shopID;
                    final double price = dish.dishPrice;

                    Bundle bundle = new Bundle();
                    bundle.putInt("dishid", dishid);
                    bundle.putInt("dishshop",dishshop);
                    bundle.putString("dishname", dishname);
                    bundle.putDouble("price", price);

                    CartDBManager dbManager = new CartDBManager(MyApplication.getContext());
                    dbManager.open();

                    Cart cartExist = dbManager.queryonedish(uid, dishid);
                    if (cartExist != null)
                        dishnum = cartExist.dishNum;
                    else dishnum = 0;
                    final OrderOneDialog orderDlg = new OrderOneDialog(mContext, dishnum);
                    orderDlg.setTitle(dishname);
                    orderDlg.show();

                    orderDlg.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            if (orderDlg.mBtnClicked == OrderOneDialog.ButtonID.BUTTON_OK) {
                                cartInsert.userID = uid;
                                cartInsert.dishID = dishid;
                                cartInsert.dishShop = dishshop;
                                cartInsert.dishName = dishname;
                                cartInsert.dishNum = orderDlg.mNum;
                                cartInsert.dishPrice = price;

                                Cart cartUpdate = dbManager.queryonedish(uid, dishid);
                                if (cartUpdate == null) {
                                    dbManager.insert(cartInsert);
                                } else {
                                    dbManager.updateNumemenu(cartUpdate.userID, cartUpdate.dishID, orderDlg.mNum);
                                }
                                Toast.makeText(mContext, dishname + " " + cartInsert.dishNum + "份已经加入购物车", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else{
                    Intent intent = new Intent("com.example.ordering.login");
                    mContext.startActivity(intent);
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull DishAdapter.ViewHolder holder, int position) {
        Dish dish = mDishList.get(position);
        holder.dishName.setText(dish.getDishName());

        //使用Glide来加载水果照片
        //Glide.with()方法传入一个Context、Activity或Fragment参数
        //然后调用load()方法去加载图片，可以是一个URL地址，也可以是一个本地路径，或者是一个资源id
        //最后调用into()方法将图片设置到具体某一个ImageView中就可以了
        Glide.with(mContext).load(dish.getDishImage()).into(holder.dishImage);
    }

    @Override
    public int getItemCount() {
        return mDishList.size();
    }
}
