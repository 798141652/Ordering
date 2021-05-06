package com.example.ordering.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.strictmode.SqliteObjectLeakedViolation;

import com.example.ordering.UploadData;
import com.example.ordering.structure.Comment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CommentDBManager {

    public static final int DBVERSION = 1;
    public static final String DB_NAME = "Ordering.db";
    public static final String COMMENT_TABLE = "commentInfo";
    public static final String COMMENT_ID = "commentID";
    public static final String COMMENT_UID = "userID";
    public static final String COMMENT_OID = "orderID";
    public static final String COMMENT_DID = "dishID";
    public static final String COMMENT_TYPE = "commentType";
    public static final String COMMENT = "comment";

    private final Context context;
    private SQLiteDatabase db;
    private DBHelper dbhelper;
    private UploadData uploadData;

    public CommentDBManager(Context context) {
        this.context = context;
    }

    public void open() {
        dbhelper = new DBHelper(context, DB_NAME, null, DBVERSION);
        try {
            db = dbhelper.getWritableDatabase();
        } catch (Exception e) {
            db = dbhelper.getReadableDatabase();
        }
    }

    public SQLiteDatabase getDb() {
        return db;
    }



    //删除评论表信息
    public void deleteCommentInfo() {
        db.execSQL("delete from commentInfo");
        db.execSQL("update sqlite_sequence set seq=0 where name='commentInfo'");
    }

    public void addComment(Comment comment){
        String sql = "insert into commentInfo(userID,orderID,shopID,dishID,commentType,comment,commentTime) values ("+comment.getUserID()
                +",'"+comment.getOrderID()+"',"+comment.getShopID()+","+comment.getDishID()+",'"+comment.getCommentType()+"','"+comment.getComment()+
                "','"+comment.getCommentTime()+"')";
        db.execSQL(sql);
        sql = "update cartInfo set cartStatus = '3' where orderID = '"+comment.getOrderID()+"'";
        db.execSQL(sql);
        uploadData();
    }

    public List<Comment> getDishComment(int dishID){
        String sql = "select * from commentInfo where dishID = "+dishID;
        List<Comment> commentList = new ArrayList<>();
        Cursor cursor = db.rawQuery(sql,null);
        if(cursor.moveToFirst()){
            do{
                Comment comment = new Comment();
                comment.setUserID(cursor.getInt(cursor.getColumnIndex("userID")));
                comment.setCommentTime(cursor.getString(cursor.getColumnIndex("commentTime")));
                comment.setComment(cursor.getString(cursor.getColumnIndex("comment")));
                comment.setCommentType(cursor.getString(cursor.getColumnIndex("commentType")));
                comment.setDishID(cursor.getInt(cursor.getColumnIndex("dishID")));
                commentList.add(comment);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return commentList;
    }

    public void uploadData(){
        uploadData = new UploadData();
        uploadData.uploadComment();
        uploadData.uploadCart();
    }
}
