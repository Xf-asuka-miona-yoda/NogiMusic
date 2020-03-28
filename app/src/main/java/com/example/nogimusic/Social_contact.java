package com.example.nogimusic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

public class Social_contact extends Fragment {
    private View view;

    private LocalBroadcastManager localBroadcastManager; //本地广播管理器
    private LocalRecevier localRecevier;
    private IntentFilter intentFilter;

    private List<Dynamic> dynamicList = new ArrayList<>();
    private DynamicAdapter dynamicAdapter;

    private SwipeRefreshLayout swipeRefreshLayout; //下拉刷新组件

    private DynamicInfo dynamicInfo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.socia_contact, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Button fabu = (Button) view.findViewById(R.id.commit_dynamic);
        fabu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CommitDynamic.class);
                startActivity(intent);
            }
        });

        getdynamic();
        initdyadapter();
        setlisthen();
//        for (int i = 0; i < 10; i++){
//            initdata();
//        }

        /**
         * 本地广播相关操作
         */
        localBroadcastManager = LocalBroadcastManager.getInstance(getActivity());
        intentFilter = new IntentFilter();
        intentFilter.addAction("dynamic_commit_success");
        localRecevier = new LocalRecevier();
        localBroadcastManager.registerReceiver(localRecevier,intentFilter);//注册本地广播监听器

        /**
         * 下拉刷新逻辑实现
         * 注意在刷新之前要先清空动态列表否则会重复
         */

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.dy_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                dynamicList.clear();
                getdynamic();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

    }

    public void initdata(){
        Dynamic dynamic = new Dynamic("1","1","华语乐坛","牛牛牛",2018,8,8,12,3,45,"0","0","0");
        dynamicList.add(dynamic);
    }

    public void initdyadapter(){
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.dy_show);
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(layoutManager);
        dynamicAdapter = new DynamicAdapter(dynamicList, view.getContext());
        recyclerView.setAdapter(dynamicAdapter);
    }

    public void setlisthen(){
        dynamicAdapter.setmOnItemClickListener(new DynamicAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Dynamic dynamic = dynamicList.get(position);
                if (view.getId() == R.id.dy_user_img){
                    Toast.makeText(view.getContext(),"点击了用户id" + dynamic.getUserid(), Toast.LENGTH_SHORT).show();
                } else if (view.getId() == R.id.zhuanfa){
                    Toast.makeText(view.getContext(),"点击了转发", Toast.LENGTH_SHORT).show();
                } else if (view.getId() == R.id.dianzan){
                    //Toast.makeText(view.getContext(),"点击了点赞", Toast.LENGTH_SHORT).show();
                    dianzan(dynamic.getDyid());
                    int realdianzan = Integer.parseInt(dynamicList.get(position).getDianzan());
                    realdianzan = realdianzan + 1;
                    dynamicList.get(position).setDianzan(String.valueOf(realdianzan));
                    dynamicAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(view.getContext(),"点击了动态" + dynamic.getDyid(), Toast.LENGTH_SHORT).show();
                    if (dynamicInfo == null){
                        initfragments();
                    }
                    dynamicInfo.setinfo(dynamic.getUserid(),dynamic.getDyid(),dynamic.getUsername(),dynamic.gettime(),dynamic.getContent());
                    showfragment(dynamicInfo);
                }
            }
        });
    }

    public void getdynamic(){
        new Thread(new Runnable() { //耗时操作要开子线程
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder() //请求参数
                            .add("method", "all")
                            .add("userid", Global_Variable.thisuser.id)
                            .build();
                    Request request = new Request.Builder()
                            .url(Global_Variable.ip + "NogiMusic/dynamic") //请求url
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String data = response.body().string();
                    Log.d("NMSL", data);
                    parsedynamicjson(data); //解析服务端返回的值
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void parsedynamicjson(String data) {
        Gson gson = new Gson();
        List<Dynamic> dynamics = gson.fromJson(data, new TypeToken<List<Dynamic>>(){}.getType());
        for (Dynamic rs : dynamics){
            dynamicList.add(rs);
            Log.d("fule", rs.getContent());
        }
        //singerList.addAll(s);
        getActivity().runOnUiThread(new Runnable() { //回到主线程通知适配器刷新
            @Override
            public void run() {
                Log.d("NBN", "OK");
                dynamicAdapter.notifyDataSetChanged();
            }
        });
    }

    class LocalRecevier extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) { //接收到本地广播之后的业务逻辑
            String code = intent.getStringExtra("code");
            Log.d("success", code);
            if (code.equals("200")){
                dynamicList.clear();
                getdynamic();
            }
        }
    }

    public void dianzan(final String id){
        new Thread(new Runnable() { //耗时操作要开子线程
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder() //请求参数
                            .add("method", "dianzan")
                            .add("userid", id)
                            .build();
                    Request request = new Request.Builder()
                            .url(Global_Variable.ip + "NogiMusic/dynamic") //请求url
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String data = response.body().string();
                    Log.d("NMSL", data);
                    parsedianzanjson(data); //解析服务端返回的值
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void parsedianzanjson(String data) {
        Gson gson = new Gson();
        List<Result> resultsList = gson.fromJson(data, new TypeToken<List<Result>>(){}.getType());
        for (Result result : resultsList){
            if (result.result.equals("点赞成功")){
                Looper.prepare();
                Toast.makeText(view.getContext(), "点赞成功", Toast.LENGTH_SHORT).show();
                Looper.loop();
            } else if (result.result.equals("点赞失败")){
                Looper.prepare();
                Toast.makeText(view.getContext(), "点赞失败，稍后尝试", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        }
    }

    class Result{
        public String result;
    }



    public void initfragments(){  //初始化fragment
        dynamicInfo = new DynamicInfo();
        addfragment(dynamicInfo);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        localBroadcastManager.unregisterReceiver(localRecevier);
    }
}
