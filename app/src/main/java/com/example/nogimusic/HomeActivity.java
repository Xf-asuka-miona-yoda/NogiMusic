package com.example.nogimusic;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Calendar;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HomeActivity extends AppCompatActivity {

    private FloatingActionButton fab;
    public Music_home music_home_fragment;
    public Music_recommend music_recommend_fragment;
    public Social_contact social_contact_fragment;
    public Mycollectin mycollectin;



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
                    if (music_recommend_fragment == null){
                        initfragment(2);
                    }
                    replaceFragment(music_recommend_fragment);
                    return true;
                case R.id.navigation_notifications:
                   // mTextMessage.setText(R.string.title_notifications);
                    if (social_contact_fragment == null){
                        initfragment(3);
                    }
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
        final DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view); //首先要获取navigationView实例
        View headview = navigationView.getHeaderView(0); //获取头部view
        TextView username = (TextView) headview.findViewById(R.id.user_name); //在头部view中定位
        username.setText(Global_Variable.thisuser.username);
        TextView userage = (TextView) headview.findViewById(R.id.user_age);
        Calendar cal = Calendar.getInstance();//万年历
        int year = cal.get(Calendar.YEAR);   //获取年
        int age = year - Integer.parseInt(Global_Variable.thisuser.age);//计算年龄
        userage.setText(String.valueOf(age) + "岁");
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        Intent intent = new Intent(this, MusicService.class);
        startService(intent);
        bindService(intent, connection, BIND_AUTO_CREATE);//绑定服务
        initfragment(1);

        replaceFragment(music_home_fragment);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {  //悬浮按钮监听事件
                //Toast.makeText(HomeActivity.this, "悬浮按钮来喽", Toast.LENGTH_SHORT).show();
                if (Global_Variable.musicplayQueue.queue.size() == 0){
                    Toast.makeText(HomeActivity.this, "当前无歌曲播放", Toast.LENGTH_SHORT).show();
                }else {
                    Intent intent1 = new Intent(HomeActivity.this, PlayerActivity.class);
                    startActivity(intent1);
                }

            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) { //设置侧滑菜单事件监听
                switch (item.getItemId()){
                    case R.id.nav_collection:
                        Toast.makeText(HomeActivity.this, "点击了收藏", Toast.LENGTH_SHORT).show();
                        if (mycollectin == null){
                            initfragment(4);
                        }
                        replaceFragment(mycollectin);
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.nav_comments:
                        Toast.makeText(HomeActivity.this, "点击了评论", Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.nav_local:
                        Toast.makeText(HomeActivity.this, "点击了本地音乐", Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.nav_quit:
                        //Toast.makeText(HomeActivity.this, "点击了登出", Toast.LENGTH_SHORT).show();
                        SharedPreferences.Editor preferences = getSharedPreferences("user", MODE_PRIVATE).edit();
                        preferences.putBoolean("auto", false); //禁止自动登录
                        preferences.commit();
                        Intent quit = new Intent(HomeActivity.this, MainActivity.class);
                        startActivity(quit);
                        break;
                    default:
                }
                return true;
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

    public void hideallfragment(){
        Global_Variable.fragmentManager = getSupportFragmentManager();
        Global_Variable.fragmentTransaction = Global_Variable.fragmentManager.beginTransaction();
        List<Fragment> list = Global_Variable.fragmentManager.getFragments();
        for (int i = 0; i < list.size(); i++){
            Fragment f = list.get(i);
            if (f != null){
                Global_Variable.fragmentTransaction.hide(f);
            }
        }
        Global_Variable.fragmentTransaction.commit();
    }

    private void replaceFragment(Fragment fragment){
        Global_Variable.fragmentManager = getSupportFragmentManager();
        Global_Variable.fragmentTransaction = Global_Variable.fragmentManager.beginTransaction();
        List<Fragment> list = Global_Variable.fragmentManager.getFragments();
        for (int i = 0; i < list.size(); i++){
            Fragment f = list.get(i);
            if (f != null){
                Global_Variable.fragmentTransaction.hide(f);
            }
        }
        Global_Variable.fragmentTransaction.show(fragment);
        Global_Variable.fragmentTransaction.commit();
    }



    private void initfragment(int id){
        if (id == 1){
            music_home_fragment = new Music_home();
            addFragments(music_home_fragment);
        }else if (id == 2){
            music_recommend_fragment = new Music_recommend();
            addFragments(music_recommend_fragment);
        }else if (id == 3){
            social_contact_fragment = new Social_contact();
            addFragments(social_contact_fragment);
        }else if (id == 4){
            mycollectin = new Mycollectin();
            addFragments(mycollectin);
        }
    }

    public void addFragments(Fragment fragment){
        Global_Variable.fragmentManager = getSupportFragmentManager();
        Global_Variable.fragmentTransaction = Global_Variable.fragmentManager.beginTransaction();
        Global_Variable.fragmentTransaction.add(R.id.homepage, fragment);
        Global_Variable.fragmentTransaction.commit();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
    }


}
