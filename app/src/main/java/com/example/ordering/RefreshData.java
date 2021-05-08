package com.example.ordering;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.ordering.db.CartDBManager;
import com.example.ordering.db.CommentDBManager;
import com.example.ordering.db.DishDBManager;
import com.example.ordering.db.ShopDBManager;
import com.example.ordering.db.UserDBManager;
import com.example.ordering.shop.ShopAdapter;
import com.example.ordering.structure.Cart;
import com.example.ordering.structure.Comment;
import com.example.ordering.structure.Dish;
import com.example.ordering.structure.MyApplication;
import com.example.ordering.structure.Shop;
import com.example.ordering.structure.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RefreshData {

    private ShopDBManager shopDBManager;
    private DishDBManager dishDBManager;
    private UserDBManager userDBManager;
    private CartDBManager cartDBManager;
    private CommentDBManager commentDBManager;


    public RefreshData(){
        shopDBManager = new ShopDBManager(MyApplication.getContext());
        shopDBManager.open();
        dishDBManager = new DishDBManager(MyApplication.getContext());
        dishDBManager.open();
        userDBManager = new UserDBManager(MyApplication.getContext());
        userDBManager.open();
        cartDBManager = new CartDBManager(MyApplication.getContext());
        cartDBManager.open();
        commentDBManager = new CommentDBManager(MyApplication.getContext());
        commentDBManager.open();
    }

    public void refreshUser(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    //生成用户json文件
                    Request userRequest1 = new Request.Builder()
                            .url("http://49.234.101.49/ordering/userdata.php")
                            .build();

                    //解析对应用户json文件
                    Request userRequest2 = new Request.Builder()
                            .url("http://49.234.101.49/ordering/json/userinfo.json")
                            .build();

                    client.newCall(userRequest1).execute();
                    Response userResponse = client.newCall(userRequest2).execute();

                    String userResData = userResponse.body().string();

                    parseUserJSONWithGSON(userResData);

                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
    public void refresh(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    //生成档口json文件
                    Request shopRequest1 = new Request.Builder()
                            .url("http://49.234.101.49/ordering/shopdata.php")
                            .build();
                    //生成菜品json文件
                    Request dishRequest1 = new Request.Builder()
                            .url("http://49.234.101.49/ordering/dishdata.php")
                            .build();
                    //生成用户json文件
                    Request userRequest1 = new Request.Builder()
                            .url("http://49.234.101.49/ordering/userdata.php")
                            .build();
                    //生成菜单json文件
                    Request cartRequest1 = new Request.Builder()
                            .url("http://49.234.101.49/ordering/cartdata.php")
                            .build();
                    //生成评论json文件
                    Request commentRequest1 = new Request.Builder()
                            .url("http://49.234.101.49/ordering/commentdata.php")
                            .build();

                    //解析对应档口json文件
                    Request shopRequest2 = new Request.Builder()
                            .url("http://49.234.101.49/ordering/json/shopinfo.json")
                            .build();
                    //解析对应菜品json文件
                    Request dishRequest2 = new Request.Builder()
                            .url("http://49.234.101.49/ordering/json/dishinfo.json")
                            .build();
                    //解析对应用户json文件
                    Request userRequest2 = new Request.Builder()
                            .url("http://49.234.101.49/ordering/json/userinfo.json")
                            .build();
                    //解析对应菜单json文件
                    Request cartRequest2 = new Request.Builder()
                            .url("http://49.234.101.49/ordering/json/cartinfo.json")
                            .build();
                    //解析对应评论json文件
                    Request commentRequest2 = new Request.Builder()
                            .url("http://49.234.101.49/ordering/json/commentinfo.json")
                            .build();

                    client.newCall(shopRequest1).execute();
                    client.newCall(dishRequest1).execute();
                    client.newCall(userRequest1).execute();
                    client.newCall(cartRequest1).execute();
                    client.newCall(commentRequest1).execute();
                    Response shopResponse = client.newCall(shopRequest2).execute();
                    Response dishResponse = client.newCall(dishRequest2).execute();
                    Response userResponse = client.newCall(userRequest2).execute();
                    Response cartResponse = client.newCall(cartRequest2).execute();
                    Response commentResponse = client.newCall(commentRequest2).execute();

                    String shopResData = shopResponse.body().string();
                    String dishResData = dishResponse.body().string();
                    String userResData = userResponse.body().string();
                    String cartResData = cartResponse.body().string();
                    String commentResData = commentResponse.body().string();

                    parseShopJSONWithGSON(shopResData);
                    parseDishJSONWithGSON(dishResData);
                    parseUserJSONWithGSON(userResData);
                    parseCartJSONWithGSON(cartResData);
                    parseCommentJSONWithGSON(commentResData);
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void parseShopJSONWithGSON(String jsonData) {
        Gson gson = new Gson();
        List<Shop> shopList = gson.fromJson(jsonData, new TypeToken<List<Shop>>() {
        }.getType());
        shopDBManager.deleteShopInfo();
        for (Shop shop : shopList) {
            shopDBManager.getDb().execSQL("insert into shopInfo values("+shop.getShopID()+",'"+shop.getShopName()+"','"+shop.getShopImage()+
                    "','"+shop.getShopLocation()+"','"+shop.getShopBrief()+"')");
        }
        shopDBManager.getDb().close();
    }

    private void parseDishJSONWithGSON(String jsonData) {
        Gson gson = new Gson();
        List<Dish> dishList = gson.fromJson(jsonData, new TypeToken<List<Dish>>() {
        }.getType());
        dishDBManager.deleteDishInfo();
        for (Dish dish : dishList) {
            dishDBManager.getDb().execSQL("insert into dishInfo values("+dish.getDishID()+",'"+dish.getShopID()+"','"+dish.getDishName()+
                    "','"+dish.getDishImage()+"','"+dish.getDishPrice()+"','"+dish.getDishType()+"')");
        }
        dishDBManager.getDb().close();
    }

    private void parseUserJSONWithGSON(String jsonData) {
        Gson gson = new Gson();
        List<User> userList = gson.fromJson(jsonData, new TypeToken<List<User>>() {
        }.getType());
        userDBManager.deleteUserInfo();
        for (User user : userList) {
            userDBManager.getDb().execSQL("insert into userInfo values("+user.getUserID()+",'"+user.getUserName()+"','"+user.getUserPWD()+
                    "','"+user.getUserTel()+"','"+user.getUserImage()+"')");
        }
        userDBManager.getDb().close();
    }

    private void parseCartJSONWithGSON(String jsonData) {
        Gson gson = new Gson();
        List<Cart> cartList = gson.fromJson(jsonData, new TypeToken<List<Cart>>() {
        }.getType());
        cartDBManager.deleteCartInfo();
        if(cartList != null){
            for (Cart cart : cartList) {
                cartDBManager.getDb().execSQL("insert into cartInfo values(null,'"+cart.getOrderID()+"',"+cart.getCartUserID()+
                        ","+cart.getCartDishID()+","+cart.getCartShopID()+",'"+cart.getCartDishName()+"',"+cart.getCartDishNum()+","+cart.getCartDishPrice()+
                        ","+cart.getCartPrice()+",'"+cart.getCartStatus()+"','"+cart.getCartTime()+"')");
            }
        }
        cartDBManager.getDb().close();
    }

    private void parseCommentJSONWithGSON(String jsonData) {
        Gson gson = new Gson();
        List<Comment> commentList = gson.fromJson(jsonData, new TypeToken<List<Comment>>() {
        }.getType());
        commentDBManager.deleteCommentInfo();
        if(commentList != null) {
            for (Comment comment : commentList) {
                commentDBManager.getDb().execSQL("insert into commentInfo values(" + comment.getCommentID() + "," + comment.getUserID() + ",'" + comment.getOrderID() +
                        "'," +comment.getShopID()+","+ comment.getDishID() + ",'" + comment.getCommentType() + "','" + comment.getComment() + "','" + comment.getCommentTime() + "')");
            }
        }
        commentDBManager.getDb().close();
    }

    //图片json格式转换并存入目录
    /*
    private void parseImageJSONWithGSON(String jsonData) {
        Gson gson = new Gson();
        List<ShopImage> shopImageList = gson.fromJson(jsonData, new TypeToken<List<ShopImage>>() {
        }.getType());

        //db.delete(TABLE,null,null);
        for (ShopImage shopImage : shopImageList) {
            String path = getFilesPath(MyApplication.getContext())+"/shop"+shopImage.getShopID()+".jpg";
            boolean result = Base64Tool.setBase64ToImageFile(shopImage.getShopImage(),path);
            System.out.println("result "+result);
            System.out.println("path "+path);
            shopDBManager.getDb().execSQL("update shopInfo set shopImage = '" + path + "' where shopID = '" + shopImage.getShopID() + "'");
        }
    }


    public String getFilesPath(Context context) {
        String filePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            //外部存储可用
            filePath = context.getExternalFilesDir(null).getPath();
        } else {
            //外部存储不可用
            filePath = context.getFilesDir().getPath();
        }
        return filePath;
    }*/

}
