package com.example.ordering;

import android.database.Cursor;
import android.util.Log;
import android.widget.Toast;

import com.example.ordering.db.CartDBManager;
import com.example.ordering.db.CommentDBManager;
import com.example.ordering.structure.Comment;
import com.example.ordering.structure.MyApplication;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UploadData {
    String uploadUrl;

    public UploadData(String uploadUrl) {
        this.uploadUrl = uploadUrl;
    }
    public UploadData(){};

    //服务器端修改用户照片
    public void uploadUserImage(String userID, String filepath){
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        //添加参数
        params.put("userID", userID);
        params.put("type","userImg");

        try {
            //添加文件
            params.put("file", new File(filepath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        client.post(uploadUrl, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    /*try {
                        //获取返回内容
                        String resp = new String(bytes, "utf-8");

                        //在这里处理返回的内容，例如解析json什么的...

                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }*/

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                //在这里处理连接失败的处理...
            }
        });
    }


    //服务器端修改用户姓名
    public void uploadUserName(String userID,String userName){
        MediaType mediaType = MediaType.parse("text/x-markdown; charset=utf-8");
        FormBody.Builder formBodyBuilder = new FormBody.Builder();
        formBodyBuilder.add("userID",userID);
        formBodyBuilder.add("type","userName");
        formBodyBuilder.add("userName",userName);
        Request request = new Request.Builder()
                .url(uploadUrl)
                .post(formBodyBuilder.build())
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("okhttp3", "onFailure: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("okhttp3", response.protocol() + " " +response.code() + " " + response.message());
                Headers headers = response.headers();
                for (int i = 0; i < headers.size(); i++) {
                    //Toast.makeText(MyApplication.getContext(),"请检查网络",Toast.LENGTH_SHORT).show();;
                    Log.d("okhttp3", headers.name(i) + ":" + headers.value(i));
                }
                Log.d("okhttp3", "onResponse: " + response.body().string());
            }
        });
    }

    //服务器端修改用户电话号码
    public void uploadUserTel(String userID,String userTel){
        MediaType mediaType = MediaType.parse("text/x-markdown; charset=utf-8");
        FormBody.Builder formBodyBuilder = new FormBody.Builder();
        formBodyBuilder.add("userID",userID);
        formBodyBuilder.add("type","userTel");
        formBodyBuilder.add("userTel",userTel);
        Request request = new Request.Builder()
                .url(uploadUrl)
                .post(formBodyBuilder.build())
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("okhttp3", "onFailure: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("okhttp3", response.protocol() + " " +response.code() + " " + response.message());
                Headers headers = response.headers();
                for (int i = 0; i < headers.size(); i++) {
                    Toast.makeText(MyApplication.getContext(),"请检查网络",Toast.LENGTH_SHORT).show();;
                    Log.d("okhttp3", headers.name(i) + ":" + headers.value(i));
                }
                Log.d("okhttp3", "onResponse: " + response.body().string());
            }
        });
    }

    //服务器端修改用户密码
    public void uploadUserPwd(String userID,String userPwd){
        MediaType mediaType = MediaType.parse("text/x-markdown; charset=utf-8");
        FormBody.Builder formBodyBuilder = new FormBody.Builder();
        formBodyBuilder.add("userID",userID);
        formBodyBuilder.add("type","userPwd");
        formBodyBuilder.add("userPwd",userPwd);
        Request request = new Request.Builder()
                .url(uploadUrl)
                .post(formBodyBuilder.build())
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("okhttp3", "onFailure: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("okhttp3", response.protocol() + " " +response.code() + " " + response.message());
                Headers headers = response.headers();
                for (int i = 0; i < headers.size(); i++) {
                    Toast.makeText(MyApplication.getContext(),"请检查网络",Toast.LENGTH_SHORT).show();;
                    Log.d("okhttp3", headers.name(i) + ":" + headers.value(i));
                }
                Log.d("okhttp3", "onResponse: " + response.body().string());
            }
        });
    }

    //上传订单以及购物车信息进入服务器
    public void uploadCart(){
        MediaType JSON = MediaType.parse("application/json;charset=UTF-8");
        JSONObject jsonObject=new JSONObject();
        JSONArray jsonArray=new JSONArray();
        CartDBManager cartDBManager = new CartDBManager(MyApplication.getContext());
        cartDBManager.open();

        String searchQuery = "SELECT * FROM cartInfo" ;
        Cursor cursor = cartDBManager.getDb().rawQuery(searchQuery, null );
        JSONArray resultSet = new JSONArray();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            int totalColumn = cursor.getColumnCount();
            JSONObject rowObject = new JSONObject();
            for( int i=0 ;  i< totalColumn ; i++ )
            {
                if( cursor.getColumnName(i) != null )
                {
                    try
                    {
                        if( cursor.getString(i) != null )
                        {
                            //Log.d("userTable", cursor.getString(i) );
                            rowObject.put(cursor.getColumnName(i) ,  cursor.getString(i) );
                        }
                        else
                        {
                            rowObject.put( cursor.getColumnName(i) ,  "" );
                        }
                    }
                    catch( Exception e )
                    {
                        // Log.d("userTable", e.getMessage()  );
                    }
                }
            }
            resultSet.put(rowObject);
            cursor.moveToNext();
        }
        cursor.close();
        cartDBManager.getDb().close();

        //开始上传cart数据库json进入服务器
        String json=resultSet.toString();
        //创建一个OkHttpClient对象
        OkHttpClient okHttpClient = new OkHttpClient();
        //创建一个RequestBody(参数1：数据类型 参数2传递的json串)
        RequestBody requestBody = RequestBody.create(JSON, json);
        //创建一个请求对象
        Request request = new Request.Builder()
                .url("http://49.234.101.49/ordering/update_cartdata.php")
                .post(requestBody)
                .build();

        Call call=okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(MyApplication.getContext(),"请检查网络",Toast.LENGTH_SHORT).show();;
                Log.i("okhttp3", "onFailure: " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.i("okhttp3", "onResponse: " + response.body().string());
            }
        });
    }


    //上传订单以及购物车信息进入服务器
    public void uploadComment(){
        MediaType JSON = MediaType.parse("application/json;charset=UTF-8");
        JSONObject jsonObject=new JSONObject();
        JSONArray jsonArray=new JSONArray();
        CommentDBManager commentDBManager = new CommentDBManager(MyApplication.getContext());
        commentDBManager.open();

        String searchQuery = "SELECT * FROM commentInfo" ;
        Cursor cursor = commentDBManager.getDb().rawQuery(searchQuery, null );
        JSONArray resultSet = new JSONArray();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            int totalColumn = cursor.getColumnCount();
            JSONObject rowObject = new JSONObject();
            for( int i=0 ;  i< totalColumn ; i++ )
            {
                if( cursor.getColumnName(i) != null )
                {
                    try
                    {
                        if( cursor.getString(i) != null )
                        {
                            //Log.d("userTable", cursor.getString(i) );
                            rowObject.put(cursor.getColumnName(i) ,  cursor.getString(i) );
                        }
                        else
                        {
                            rowObject.put( cursor.getColumnName(i) ,  "" );
                        }
                    }
                    catch( Exception e )
                    {
                        // Log.d("userTable", e.getMessage()  );
                    }
                }
            }
            resultSet.put(rowObject);
            cursor.moveToNext();
        }
        cursor.close();
        commentDBManager.getDb().close();

        //开始上传Comment数据库json进入服务器
        String json=resultSet.toString();
        //创建一个OkHttpClient对象
        OkHttpClient okHttpClient = new OkHttpClient();
        //创建一个RequestBody(参数1：数据类型 参数2传递的json串)
        RequestBody requestBody = RequestBody.create(JSON, json);
        //创建一个请求对象
        Request request = new Request.Builder()
                .url("http://49.234.101.49/ordering/update_commentdata.php")
                .post(requestBody)
                .build();

        Call call=okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(MyApplication.getContext(),"请检查网络",Toast.LENGTH_SHORT).show();;
                Log.i("okhttp3", "onFailure: " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.i("okhttp3", "onResponse: " + response.body().string());
            }
        });
    }
}
