package com.example.ordering.ui.settings;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.ordering.structure.MyApplication;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UpdateDataService extends IntentService {
    private UploadUserImage uploadUserImage = new UploadUserImage();
    private MyApplication app;

    public UpdateDataService() {
        super("UpdateDataService");
        app = (MyApplication) MyApplication.getContext().getApplicationContext();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return uploadUserImage;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String imagePath = intent.getStringExtra("imagePath");
        imageUpload(BitmapFactory.decodeFile(imagePath));
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    class UploadUserImage extends Binder{
        public void startUpload(){

        }
    }

    private void imageUpload(Bitmap bitmap){

        File image = convertBitmapToFile(bitmap);
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                //所有图片类型
                MediaType mediaType= MediaType.parse("image/*; charset=utf-8");
                //第一层，说明数据为文件，以及文件类型
                RequestBody fileBody=RequestBody.create(mediaType,image);

                //第二层，指明服务表单的键名，文件名，文件体
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("uid",app.getUid())
                        .addFormDataPart("img",image.getName(),fileBody)
                        .build();
                Request request = new Request.Builder()
                        .url("http://49.234.101.49/ordering/setuserimage.php")
                        .post(requestBody)
                        .build();
                //发送请求
                Call call = client.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure( Call call, IOException e) {
                        //网络故障
                        Looper.prepare();
                        //Toast.makeText(getActivity(),"网络故障！",Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.body()!=null){
                            Looper.prepare();
                            //Toast.makeText(MainActivity.this,response.body().string(),Toast.LENGTH_SHORT).show();
                            Log.d("theresult",response.body().string());
                            Looper.loop();
                        }
                    }
                });


            }
        }).start();

    }

    public File f;
    private File convertBitmapToFile(Bitmap bitmap) {
        try {
            // create a file to write bitmap data
            f = new File(MyApplication.getContext().getCacheDir(), "portrait");
            f.createNewFile();

            // convert bitmap to byte array
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
            byte[] bitmapdata = bos.toByteArray();

            // write the bytes in file
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
        } catch (Exception e) {

        }
        return f;
    }

}