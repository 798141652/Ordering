package com.example.ordering.shop;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
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
import com.example.ordering.structure.Shop;

import java.util.List;

public class ShopAdapter extends RecyclerView.Adapter<ShopAdapter.ViewHolder> {

    private Context mContext;

    private List<Shop> mShopList;

    static class ViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        ImageView shopImage;
        TextView shopName;

        public ViewHolder(View view){
            super(view);
            cardView = (CardView) view;
            shopImage = view.findViewById(R.id.shop_image);
            shopName = view.findViewById(R.id.shop_name);

        }
    }

    public ShopAdapter(List<Shop> shopList){
        mShopList = shopList;
    }

    @NonNull
    @Override
    public ShopAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(mContext == null){
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.shop_item,parent,false);

        //给CardView注册了一个点击事件监听器,在事件点击中获得当前点击项的档口名和档口图片资源id，
        // 把它们传入到intent中，最后调用startActivity方法启动ShopActivity
        final ViewHolder holder = new ViewHolder(view);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Shop shop = mShopList.get(position);
                Intent intent = new Intent(mContext,ShopActivity.class);
                intent.putExtra(ShopActivity.SHOP_NAME,shop.getShopName());
                intent.putExtra(ShopActivity.SHOP_IMAGE,shop.getShopImage());
                intent.putExtra(ShopActivity.SHOP_ID,shop.getShopID());
                mContext.startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ShopAdapter.ViewHolder holder, int position) {
        Shop shop = mShopList.get(position);
        holder.shopName.setText(shop.getShopName());

        //使用Glide来加载水果照片
        //Glide.with()方法传入一个Context、Activity或Fragment参数
        //然后调用load()方法去加载图片，可以是一个URL地址，也可以是一个本地路径，或者是一个资源id
        //最后调用into()方法将图片设置到具体某一个ImageView中就可以了
        Glide.with(mContext).
                load(shop.getShopImage())
                .placeholder(R.drawable.loading)
                .into(holder.shopImage);
    }

    @Override
    public int getItemCount() {
        return mShopList.size();
    }
}
