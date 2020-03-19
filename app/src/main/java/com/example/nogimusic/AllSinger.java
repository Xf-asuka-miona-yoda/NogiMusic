package com.example.nogimusic;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class AllSinger extends Fragment {

    private View view;
    HomeActivity homeActivity;

    private SIngerinfo sIngerinfo;

    private List<Singer> singerList = new ArrayList<>();
    private SingerAdapter singerAdapter;

    public AllSinger(){
        Log.d("fule", "创建");
        //sendrequest();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //sendrequest();
        view = inflater.inflate(R.layout.fragment_all_singer, container, false);
        //initSingerAdapter();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        homeActivity = (HomeActivity) getActivity();  //过早初始化会空指针
        ImageButton back = (ImageButton) view.findViewById(R.id.back_allsinger);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(v.getContext(),"hhh", Toast.LENGTH_SHORT).show();
                Global_Variable.fragmentManager = getFragmentManager();
                Global_Variable.fragmentTransaction = Global_Variable.fragmentManager.beginTransaction();
                List<Fragment> list = Global_Variable.fragmentManager.getFragments();
                for (int i = 0; i < list.size(); i++){
                    Fragment f = list.get(i);
                    if (f != null){
                        Global_Variable.fragmentTransaction.hide(f);
                    }
                }
                Global_Variable.fragmentTransaction.show(homeActivity.music_home_fragment);
                Global_Variable.fragmentTransaction.commit();
            }
        });
        //initsingers();
        sendrequest();
        initSingerAdapter();
        setListener();
    }

    public void initsingers(){
        Singer singer = new Singer(String.valueOf(1), "乃木坂46", "http://y.gtimg.cn/music/photo_new/T001R300x300M000003caGxv3AblUU.jpg?max_age=2592000", "12");
        singerList.add(singer);
    }

    public void initSingerAdapter(){
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.allsinger);
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(layoutManager);
        singerAdapter = new SingerAdapter(singerList, view.getContext());
        recyclerView.setAdapter(singerAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(view.getContext(),DividerItemDecoration.VERTICAL));
    }

    public void setListener(){
        singerAdapter.setmOnItemClickListener(new SingerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Singer singer = singerList.get(position);
                if (sIngerinfo == null){
                    initfragments();
                }
                sIngerinfo.setinfo(singer.getSingerid(),singer.getSingername(),singer.getSinger_in(),singer.getSingerpicurl());
                showfragment(sIngerinfo);
            }
        });
    }

    public void sendrequest(){
        new Thread(new Runnable() { //耗时操作要开子线程
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder()
                            .add("code", String.valueOf(200)) //请求参数
                            .build();
                    Request request = new Request.Builder()
                            .url(Global_Variable.ip + "NogiMusic/allsinger") //请求url
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String data = response.body().string();
                    Log.d("NMSL", data);
                    parsejsonsinger(data); //解析服务端返回的值
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void parsejsonsinger(String jsondata){
        Gson gson = new Gson();
        List<allsingerresult> singerrs = gson.fromJson(jsondata, new TypeToken<List<allsingerresult>>(){}.getType());
        for (allsingerresult rs : singerrs){
            Singer singer = new Singer(rs.singerid, rs.singername, rs.singerpicurl, rs.singer_in);
            singerList.add(singer);
            Log.d("fule", singer.getSingername());
        }
        //singerList.addAll(s);
        getActivity().runOnUiThread(new Runnable() { //回到主线程通知适配器刷新
            @Override
            public void run() {
                Log.d("NBN", "OK");
                singerAdapter.notifyDataSetChanged();
            }
        });
    }

    class allsingerresult{
        public String singerid;
        public String singername;
        public String singerpicurl;
        public String singer_in;
    }

    public void initfragments(){  //初始化三个fragment
        sIngerinfo = new SIngerinfo();
        addfragment(sIngerinfo);
    }

    public void addfragment(Fragment fragment){
        Global_Variable.fragmentManager = getFragmentManager();
        Global_Variable.fragmentTransaction = Global_Variable.fragmentManager.beginTransaction();
        Global_Variable.fragmentTransaction.add(R.id.homepage, fragment);
        Global_Variable.fragmentTransaction.commit();
    }

    public void showfragment(Fragment fragment){
        Global_Variable.fragmentManager = getFragmentManager();
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
}
