package com.example.ordering.dish;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ordering.R;
import com.example.ordering.db.CartDBManager;
import com.example.ordering.db.CommentDBManager;
import com.example.ordering.db.DishDBManager;
import com.example.ordering.db.ShopDBManager;
import com.example.ordering.structure.Cart;
import com.example.ordering.structure.Comment;
import com.example.ordering.structure.MyApplication;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeoutException;


public class DishCommentAdapter extends RecyclerView.Adapter<DishCommentAdapter.ViewHolder> {

    private Context mContext;

    private List<Comment> commentList;

    private Comment comment;

    private DishDBManager dishDBManager;
    private CommentDBManager commentDBManager;


    private MyApplication app;


    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView commentType;
        TextView comment;
        TextView commentUser;
        TextView commentTime;

        public ViewHolder(View view){
            super(view);
            commentType = view.findViewById(R.id.dish_comment_rating);
            commentTime = view.findViewById(R.id.dish_comment_time);
            comment = view.findViewById(R.id.dish_comment);
            commentUser = view.findViewById(R.id.dish_comment_user);
        }
    }

    public DishCommentAdapter(List<Comment> commentList){
        this.commentList = commentList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        app = (MyApplication) MyApplication.getContext().getApplicationContext();

        if(mContext == null){
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.dish_comment_item,parent,false);

        final ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        commentDBManager = new CommentDBManager(mContext);
        commentDBManager.open();
        dishDBManager = new DishDBManager(mContext);
        dishDBManager.open();
        comment = commentList.get(position);
        holder.commentType.setText(comment.getCommentType());
        holder.comment.setText(comment.getComment());
        holder.commentUser.setText(String.valueOf(comment.getUserID()));
        holder.commentTime.setText(comment.getCommentTime());
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }


}
