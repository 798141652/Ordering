package com.example.ordering.comment;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ordering.R;
import com.example.ordering.db.CartDBManager;
import com.example.ordering.db.CommentDBManager;
import com.example.ordering.db.DishDBManager;
import com.example.ordering.db.ShopDBManager;
import com.example.ordering.structure.Cart;
import com.example.ordering.structure.Comment;
import com.example.ordering.structure.Dish;
import com.example.ordering.structure.MyApplication;
import com.example.ordering.structure.Order;
import com.example.ordering.structure.Shop;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private Context mContext;

    private List<Cart> cartList;

    private Cart cart;

    private DishDBManager dishDBManager;
    private CommentDBManager commentDBManager;


    private Comment comment;

    private MyApplication app;


    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView dishImage;
        TextView dishName;
        EditText comment;
        RatingBar rating;
        TextView ratingText;
        Button addComment;

        public ViewHolder(View view){
            super(view);
            dishImage = view.findViewById(R.id.comment_dish_image);
            dishName = view.findViewById(R.id.comment_dish_name);
            comment = view.findViewById(R.id.comment);
            rating = view.findViewById(R.id.rating_star);
            ratingText = view.findViewById(R.id.rating_text);
            addComment = view.findViewById(R.id.add_comment);
        }
    }

    public CommentAdapter(List<Cart> cartList){
        this.cartList = cartList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        app = (MyApplication) MyApplication.getContext().getApplicationContext();

        if(mContext == null){
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.comment_dish_item,parent,false);

        final ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        commentDBManager = new CommentDBManager(mContext);
        commentDBManager.open();
        dishDBManager = new DishDBManager(mContext);
        dishDBManager.open();
        cart = cartList.get(position);
        holder.dishName.setText(cart.getCartDishName());
        String dishImage = dishDBManager.getDishImageByID(cart.getCartDishID());
        Glide.with(mContext).
                load("http://49.234.101.49/ordering/"+dishImage)
                .placeholder(R.drawable.loading)
                .into(holder.dishImage);
        /*
        cart = cartList.get(position);
        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        String commentTime = String.valueOf(formatter.format(date));
        comment.setUserID(cart.cartUserID);
        comment.setOrderID(cart.orderID);
        comment.setDishID(cart.getCartDishID());
        comment.setComment(holder.comment.getText().toString());
        comment.setCommentTime(commentTime);
        */
        comment = new Comment();
        holder.rating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                String rate = null;
                if(rating == 1.0){
                    rate = "差评";
                    holder.ratingText.setText(rate);
                }else if(rating == 2.0){
                    rate = "中评";
                    holder.ratingText.setText(rate);
                }else{
                    rate = "好评";
                    holder.ratingText.setText(rate);
                }
                comment.setCommentType(rate);
            }
        });
        holder.addComment.setOnClickListener(new View.OnClickListener() {
            int position = holder.getAdapterPosition();
            @Override
            public void onClick(View v) {
                cart = cartList.get(position);
                SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date(System.currentTimeMillis());
                String commentTime = String.valueOf(formatter.format(date));
                comment.setUserID(cart.cartUserID);
                comment.setOrderID(cart.orderID);
                comment.setShopID(cart.cartShopID);
                comment.setDishID(cart.getCartDishID());
                comment.setComment(holder.comment.getText().toString());
                comment.setCommentTime(commentTime);
                commentDBManager.addComment(comment);
            }
        });
    }



    @Override
    public int getItemCount() {
        return cartList.size();
    }


}
