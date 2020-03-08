package com.example.nogimusic;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HomeActivity extends AppCompatActivity {

    private FloatingActionButton fab;
    private Music_home music_home_fragment;
    private Music_recommend music_recommend_fragment;
    private Social_contact social_contact_fragment;

    MusicQueue musicQueue = new MusicQueue();//播放队列
    public MusicService.MusicBinder musicBinder;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            musicBinder = (MusicService.MusicBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


    //底部导航栏监听
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    //mTextMessage.setText(R.string.title_home);
                    replaceFragment(music_home_fragment);
                    return true;
                case R.id.navigation_dashboard:
                    //mTextMessage.setText(R.string.title_dashboard);
                    replaceFragment(music_recommend_fragment);
                    return true;
                case R.id.navigation_notifications:
                   // mTextMessage.setText(R.string.title_notifications);
                    replaceFragment(social_contact_fragment);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        Intent intent = new Intent(this, MusicService.class);
        startService(intent);
        bindService(intent, connection, BIND_AUTO_CREATE);//绑定服务
        initfragment();
        replaceFragment(music_home_fragment);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(HomeActivity.this, "悬浮按钮来喽", Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public void onBackPressed() {   //按返回键直接回桌面，不然会退回登录界面
//        super.onBackPressed();
        Intent intent= new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);

    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.homepage, fragment);
        fragmentTransaction.commit();
    }

    private void initfragment(){
        music_home_fragment = new Music_home();
        music_recommend_fragment = new Music_recommend();
        social_contact_fragment = new Social_contact();
    }


}
