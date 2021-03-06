package com.example.ordering.ui.shop;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.ordering.GlideImageLoader;
import com.example.ordering.UploadData;
import com.example.ordering.db.CartDBManager;
import com.example.ordering.db.CommentDBManager;
import com.example.ordering.db.DishDBManager;
import com.example.ordering.db.ShopDBManager;
import com.example.ordering.db.UserDBManager;
import com.example.ordering.cart.CartActivity;
import com.example.ordering.dish.DishActivity;
import com.example.ordering.structure.Cart;
import com.example.ordering.structure.Comment;
import com.example.ordering.structure.Dish;
import com.example.ordering.structure.MyApplication;
import com.example.ordering.R;
import com.example.ordering.shop.ShopAdapter;
import com.example.ordering.structure.Shop;
import com.example.ordering.structure.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.youth.banner.Banner;
import com.youth.banner.listener.OnBannerListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ShopFragment extends Fragment {

    private MyApplication app;

    private ShopViewModel shopViewModel;

    private List<List<Shop>> shitangList = new ArrayList<List<Shop>>();
    private List<Shop> shopList = new ArrayList<Shop>();

    private View root;

    private ShopAdapter adapter;//???????????????

    private SwipeRefreshLayout swipeRefreshLayout;//????????????????????????????????????

    private ShopDBManager shopDBManager;
    private DishDBManager dishDBManager;
    private UserDBManager userDBManager;
    private CartDBManager cartDBManager;
    private CommentDBManager commentDBManager;
    private UploadData uploadData;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        shopViewModel =
                new ViewModelProvider(this).get(ShopViewModel.class);

        root = inflater.inflate(R.layout.fragment_cart, container, false);

        //refreshShops();
        initViews();

        return root;
    }

    //???????????????
    public void initViews() {
        //uploadData = new UploadData();
        //uploadData.uploadCart();
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
        app = (MyApplication) MyApplication.getContext().getApplicationContext();
        FloatingActionButton floatingActionButton = root.findViewById(R.id.cart_fab);
        //?????????????????????????????????????????????????????????????????????????????????????????????
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!app.getLoginState()) {
                    Intent intent = new Intent("com.example.ordering.login");
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getActivity(), CartActivity.class);
                    startActivity(intent);
                }
            }
        });

        shitangList = app.getshitangList();//?????????application??????shop??????

        //???????????????
        List images = new ArrayList();
        for(int i=0;i<4;i++) {
            String image = dishDBManager.getDishImageByID(cartDBManager.FashionDishList().get(i).getDishID());
            images.add("http://49.234.101.49/ordering/"+image);
            //images.add("http://49.234.101.49/ordering/image/dishPhoto227.jpeg");
            //images.add("http://49.234.101.49/ordering/image/dishPhoto228.jpeg");
            //images.add("http://49.234.101.49/ordering/image/dishPhoto220.jpeg");
        }
        Banner banner = root.findViewById(R.id.banner);
        //?????????????????????
        banner.setImageLoader(new GlideImageLoader());
        //??????????????????
        banner.setImages(images);
        //banner?????????????????????????????????????????????
        banner.start();
        //??????????????????
        banner.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int position) {
                Dish dish = new Dish();
                dish = dishDBManager.getDishByID(cartDBManager.FashionDishList().get(position).getDishID());
                Intent intent = new Intent(getActivity(), DishActivity.class);
                intent.putExtra(DishActivity.DISH_ID,dish.getDishID());
                intent.putExtra(DishActivity.DISH_NAME, dish.getDishName());
                intent.putExtra(DishActivity.DISH_IMAGE_ID, dish.getDishImage());
                intent.putExtra(DishActivity.DISH_TYPE, dish.getDishType());
                intent.putExtra(DishActivity.DISH_PRICE,dish.getDishPrice());
                startActivity(intent);
            }
        });

        //??????????????????????????????
        Spinner spinner = (Spinner) root.findViewById(R.id.spinner);

        List<String> data_list = new ArrayList();
        ArrayAdapter<String> arr_adapter;
        //??????
        data_list = new ArrayList<String>();
        for(int i=0;i<shitangList.size();i++){
        data_list.add(shitangList.get(i).get(0).shopLocation);
        //data_list.add("??????????????????");
        //data_list.add("??????????????????");
        //data_list.add("????????????");
        //data_list.add("????????????");
        }

        //?????????
        arr_adapter= new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, data_list);
        //????????????
        arr_adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        //???????????????
        spinner.setAdapter(arr_adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                RecyclerView recyclerView = root.findViewById(R.id.shop_recycler_view);
                GridLayoutManager layoutManager = new GridLayoutManager(MyApplication.getContext(), 1);//??????????????????Context???????????????????????????
                recyclerView.setLayoutManager(layoutManager);
                adapter = new ShopAdapter(shitangList.get(position));
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                RecyclerView recyclerView = root.findViewById(R.id.shop_recycler_view);
                GridLayoutManager layoutManager = new GridLayoutManager(MyApplication.getContext(), 1);//??????????????????Context???????????????????????????
                recyclerView.setLayoutManager(layoutManager);
                adapter = new ShopAdapter(shitangList.get(0));
                recyclerView.setAdapter(adapter);
            }
        });




            //????????????
            swipeRefreshLayout = root.findViewById(R.id.swipe_refresh);//??????swipeRefreshLayout??????
            swipeRefreshLayout.setColorSchemeResources(R.color.design_default_color_primary);//????????????????????????????????????
            //????????????????????????????????????
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {//??????????????????????????????????????????????????????????????????onRefresh()?????????????????????????????????????????????
                    refreshShops();
                }
            });

    }


    //????????????????????????
    private void refreshShops() {
        uploadData = new UploadData();
        //uploadData.uploadCart();

        //?????????????????????
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    //????????????json??????
                    Request shopRequest1 = new Request.Builder()
                            .url("http://49.234.101.49/ordering/shopdata.php")
                            .build();
                    //????????????json??????
                    Request dishRequest1 = new Request.Builder()
                            .url("http://49.234.101.49/ordering/dishdata.php")
                            .build();
                    //????????????json??????
                    Request userRequest1 = new Request.Builder()
                            .url("http://49.234.101.49/ordering/userdata.php")
                            .build();
                    //????????????json??????
                    Request cartRequest1 = new Request.Builder()
                            .url("http://49.234.101.49/ordering/cartdata.php")
                            .build();
                    //????????????json??????
                    Request commentRequest1 = new Request.Builder()
                            .url("http://49.234.101.49/ordering/commentdata.php")
                            .build();

                    //??????????????????json??????
                    Request shopRequest2 = new Request.Builder()
                            .url("http://49.234.101.49/ordering/json/shopinfo.json")
                            .build();
                    //??????????????????json??????
                    Request dishRequest2 = new Request.Builder()
                            .url("http://49.234.101.49/ordering/json/dishinfo.json")
                            .build();
                    //??????????????????json??????
                    Request userRequest2 = new Request.Builder()
                            .url("http://49.234.101.49/ordering/json/userinfo.json")
                            .build();
                    //??????????????????json??????
                    Request cartRequest2 = new Request.Builder()
                            .url("http://49.234.101.49/ordering/json/cartinfo.json")
                            .build();
                    //??????????????????json??????
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
                //??????????????????runOnUiThread?????????????????????????????????
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        for ( int i=shitangList.size()-1; i>=0;i--) {
                            shitangList.remove(i);
                        }//????????????
                        shitangList = shopViewModel.getshitangList();
                        app.setshitangList(shitangList);//??????application???shop??????
                        if (shitangList.size() == 0) {
                            Toast.makeText(getActivity(), "?????????", Toast.LENGTH_SHORT).show();
                        } else {
                            //??????????????????????????????
                            Spinner spinner = (Spinner) root.findViewById(R.id.spinner);
                            //??????
                            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    RecyclerView recyclerView = root.findViewById(R.id.shop_recycler_view);
                                    GridLayoutManager layoutManager = new GridLayoutManager(MyApplication.getContext(), 1);//??????????????????Context???????????????????????????
                                    recyclerView.setLayoutManager(layoutManager);
                                    adapter = new ShopAdapter(shitangList.get(position));
                                    recyclerView.setAdapter(adapter);
                                    adapter.notifyDataSetChanged();
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {
                                    RecyclerView recyclerView = root.findViewById(R.id.shop_recycler_view);
                                    GridLayoutManager layoutManager = new GridLayoutManager(MyApplication.getContext(), 1);//??????????????????Context???????????????????????????
                                    recyclerView.setLayoutManager(layoutManager);
                                    adapter = new ShopAdapter(shitangList.get(0));
                                    recyclerView.setAdapter(adapter);
                                    adapter.notifyDataSetChanged();
                                }
                            });
                        }

                        //????????????
                        /*
                        swipeRefreshLayout = root.findViewById(R.id.swipe_refresh);//??????swipeRefreshLayout??????
                        swipeRefreshLayout.setColorSchemeResources(R.color.design_default_color_primary);//????????????????????????????????????
                        //????????????????????????????????????
                        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                            @Override
                            public void onRefresh() {//??????????????????????????????????????????????????????????????????onRefresh()?????????????????????????????????????????????
                                refreshShops();
                            }
                        });
                        */
                        if (shitangList.size() == 0) {
                            Toast.makeText(getActivity(), "???????????????", Toast.LENGTH_SHORT).show();
                        } else {
                            adapter.notifyDataSetChanged();//notifyDataSetChanged?????????????????????????????????
                        }
                        swipeRefreshLayout.setRefreshing(false);//?????????????????????????????????????????????????????????

                    }
                });
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
        //shopDBManager.getDb().close();
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
        //dishDBManager.getDb().close();
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
        //userDBManager.getDb().close();
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
        //cartDBManager.getDb().close();
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
        //commentDBManager.getDb().close();
    }

}