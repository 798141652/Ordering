package com.example.ordering.ui.settings;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.ordering.ChangeUserName;
import com.example.ordering.ChangeUserPwd;
import com.example.ordering.ChangeUserTel;
import com.example.ordering.MainActivity;
import com.example.ordering.R;
import com.example.ordering.ShowVersion;
import com.example.ordering.structure.MyApplication;
import com.example.ordering.structure.User;

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

import static com.example.ordering.loginreg.RegisterActivity.CHOOSE_PHOTO;

public class SettingsFragment extends Fragment {

    private SettingsViewModel settingsViewModel;

    private MyApplication app;

    private User user;

    public UpdateDataService.UploadUserImage uploadUserImage;

    private ImageView imageViewBar;
    private ImageView imageViewUser;
    private TextView textViewUID;
    private TextView textViewUName;
    private LinearLayout changeUserName;
    private LinearLayout changeUserTel;
    private LinearLayout changeUserPwd;
    private LinearLayout showVersion;
    private LinearLayout logout;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        app = (MyApplication) MyApplication.getContext().getApplicationContext();

            settingsViewModel =
                    new ViewModelProvider(this).get(SettingsViewModel.class);
            View root = inflater.inflate(R.layout.fragment_settings, container, false);
            imageViewBar = root.findViewById(R.id.background);
            imageViewUser = root.findViewById(R.id.uimage);
            textViewUID = root.findViewById(R.id.user_id);
            textViewUName = root.findViewById(R.id.user_name);
            changeUserName = root.findViewById(R.id.change_user_name);
            changeUserPwd = root.findViewById(R.id.change_user_pwd);
            changeUserTel = root.findViewById(R.id.change_user_tel);
            showVersion = root.findViewById(R.id.show_version);
            logout = root.findViewById(R.id.logout);
            Glide.with(this).load("http://49.234.101.49/ordering/" + user.getUserImage())
                .placeholder(R.drawable.loading)
                //.skipMemoryCache(true)
                //.diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(imageViewUser);
            initViews();
            System.out.println("init");
            return root;
        }

    @Override
    public void onStart() {
        super.onStart();
        System.out.println("start");
    }

    @Override
    public void onResume() {
        super.onResume();
        System.out.println("resume");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("destroy");
    }

    private void initViews() {
            String uID = app.getUid();
            user = settingsViewModel.getUser(uID);
            /*
            Glide.with(this).load("http://49.234.101.49/ordering/" + user.getUserImage())
                    .placeholder(R.drawable.loading)
                    //.skipMemoryCache(true)
                    //.diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(imageViewUser);

                */
            //Glide.with(this).load("http://49.234.101.49/ordering/image/defaultimage.jpeg")
            //        .placeholder(R.drawable.loading)
            //        .into(imageViewBar);
            imageViewBar.setImageResource(R.drawable.bg);
            textViewUID.setText(""+uID);
            textViewUName.setText(user.getUserName());

            changeUserName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MyApplication.getContext(), ChangeUserName.class);
                    startActivity(intent);
                }
            });

            changeUserPwd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MyApplication.getContext(), ChangeUserPwd.class);
                    startActivity(intent);
                }
            });

            changeUserTel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MyApplication.getContext(), ChangeUserTel.class);
                    startActivity(intent);
                }
            });

            showVersion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MyApplication.getContext(), ShowVersion.class);
                    startActivity(intent);
                }
            });

            imageViewUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        //??????????????????????????????????????????SD????????????????????????????????????
                        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                        }else {
                            openAlbum();
                        }
                }
            });

            logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //app.setState(1);
                    //item.setIcon(getResources().getDrawable(R.drawable.ic_backup));
                    app.setLoginstatus(false);
                    SharedPreferences uSetting = app.getSharedPreferences("logindata", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = uSetting.edit();
                    editor.putInt("iflogin",0);
                    editor.apply();
                    Intent intent = new Intent("com.example.ordering.login");
                    startActivity(intent);
                    Toast.makeText(getActivity(),"????????????",Toast.LENGTH_SHORT).show();
                }
            });

    }

    public void openAlbum(){
        //??????Intent??????
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");//??????????????????
        startActivityForResult(intent,CHOOSE_PHOTO);//?????????????????????????????????CHOOSE_PHOTO
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    openAlbum();
                }else {
                    Toast.makeText(getActivity(),"You Denied ths permission",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CHOOSE_PHOTO:
                if(resultCode == getActivity().RESULT_OK){
                    System.out.println("photo"+data);
                    handleImageOnKitKat(data);
                }
                break;
            default:
                break;
        }
    }

    private String handleImageOnKitKat(Intent data){
        String imagePath = null;
        Uri uri = data.getData();//??????????????????Uri??????
        if(DocumentsContract.isDocumentUri(getActivity(),uri)){
            //?????????document?????????Uri????????????document id??????
            String docId = DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())){
                //??????Uri???authority???media????????????????????????document id????????????????????????????????????????????????????????????????????????????????????????????????id
                String id = docId.split(":")[1];//????????????????????????id
                System.out.println("photoURI id "+id);
                String selection = MediaStore.Images.Media._ID + "=" + id;//?????????id????????????Uri???????????????
                System.out.println("photoURI selection "+selection);
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);//??????getImagePath()????????????????????????????????????
                System.out.println("photoURI imagepath "+imagePath);
            }else if("com.android.providers.dowmloads.documents".equals(uri.getAuthority())){
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),Long.valueOf(docId));
                imagePath = getImagePath(contentUri,null);
            }
        }else if ("content".equalsIgnoreCase(uri.getScheme())){
            //?????????content?????????Uri??????????????????????????????
            imagePath = getImagePath(uri,null);
        }else if ("file".equalsIgnoreCase(uri.getScheme())){
            //?????????file?????????Uri?????????????????????????????????
            imagePath = uri.getPath();
        }

        displayImage(imagePath);//??????????????????????????????

        Intent bindIntent = new Intent(getContext(),UpdateDataService.class);
        bindIntent.putExtra("imagePath",imagePath);
        getActivity().startService(bindIntent);

        //imageUpload(BitmapFactory.decodeFile(imagePath));
        return imagePath;

    }

    private String getImagePath(Uri uri, String selection){
        String path = null;
        //??????Uri???selection??????????????????????????????
        Cursor cursor = getActivity().getContentResolver().query(uri,null,selection,null,null);
        if(cursor != null){
            if(cursor.moveToNext()){
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                System.out.println("PhotoUri "+path);
            }
            cursor.close();
        }
        return path;
    }

    private void displayImage(String imagePath){
        if(imagePath != null){
            Log.d("MainPhoto",imagePath);
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            Glide.with(this).load(bitmap)
                    .placeholder(R.drawable.loading)
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(imageViewUser);
            Log.d("MainPhoto",String.valueOf(bitmap));

        }else {
            Toast.makeText(getActivity(),"failed to get image",Toast.LENGTH_SHORT).show();
        }
    }

    private void imageUpload(Bitmap bitmap){

        File image = convertBitmapToFile(bitmap);
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                //??????????????????
                MediaType mediaType= MediaType.parse("image/*; charset=utf-8");
                //??????????????????????????????????????????????????????
                RequestBody fileBody=RequestBody.create(mediaType,image);

                //???????????????????????????????????????????????????????????????
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("uid",app.getUid())
                        .addFormDataPart("img",image.getName(),fileBody)
                        .build();
                Request request = new Request.Builder()
                        .url("http://49.234.101.49/ordering/setuserimage.php")
                        .post(requestBody)
                        .build();
                //????????????
                Call call = client.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure( Call call, IOException e) {
                        //????????????
                        Looper.prepare();
                        Toast.makeText(getActivity(),"???????????????",Toast.LENGTH_SHORT).show();
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
            f = new File(getActivity().getCacheDir(), "portrait");
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