package com.example.ordering.ui.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.ordering.ChangeUserName;
import com.example.ordering.ChangeUserPwd;
import com.example.ordering.ChangeUserTel;
import com.example.ordering.R;
import com.example.ordering.ShowVersion;
import com.example.ordering.loginreg.LoginActivity;
import com.example.ordering.structure.MyApplication;
import com.example.ordering.structure.User;

public class SettingsFragment extends Fragment {

    private SettingsViewModel settingsViewModel;

    private MyApplication app;

    private User user;

    private ImageView imageViewBar;
    private ImageView imageViewUser;
    private TextView textViewUID;
    private TextView textViewUName;
    private LinearLayout changeUserName;
    private LinearLayout changeUserTel;
    private LinearLayout changeUserPwd;
    private LinearLayout showVersion;


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
            initViews();

            return root;
        }

    @Override
    public void onStart() {
        super.onStart();
        initViews();
    }

    private void initViews() {
            String uID = app.getUid();
            user = settingsViewModel.getUser(uID);
            Glide.with(this).load("http://49.234.101.49/ordering/" + user.getUserImage())
                .placeholder(R.drawable.loading)
                .into(imageViewUser);
            Glide.with(this).load("http://49.234.101.49/ordering/image/defaultimage.jpeg")
                    .placeholder(R.drawable.loading)
                    .into(imageViewBar);
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

    }
}