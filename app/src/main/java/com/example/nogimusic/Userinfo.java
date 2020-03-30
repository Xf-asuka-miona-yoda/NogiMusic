package com.example.nogimusic;

import android.content.Intent;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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

public class Userinfo extends AppCompatActivity {
    private TextView userinfoname;
    private TextView userinfoage;
    private Button care;

    private String userid;

    private List<Dynamic> dynamicList = new ArrayList<>();
    private DynamicAdapter dynamicAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo);
        Intent intent = getIntent();
        userid = intent.getStringExtra("userid");
        initview();
        getuser();
        getcare();
        getuserdynamic();
    }


    public void initview(){
        userinfoage = (TextView) findViewById(R.id.userinfo_age);
        userinfoname = (TextView) findViewById(R.id.userinfo_name);
        care = (Button) findViewById(R.id.userinfo_care);
        care.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //care.setText("已关注");
                sendcare();
            }
        });

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.userinfo_recy);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        dynamicAdapter = new DynamicAdapter(dynamicList, this);
        recyclerView.setAdapter(dynamicAdapter);
        setlisten();
    }

    public void setlisten(){
        dynamicAdapter.setmOnItemClickListener(new DynamicAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Dynamic dynamic = dynamicList.get(position);
                if (view.getId() == R.id.zhuanfa){
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
                }
            }
        });
    }

    public void getuserdynamic(){
        new Thread(new Runnable() { //耗时操作要开子线程
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder() //请求参数
                            .add("method", "one")
                            .add("userid", userid)
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

        runOnUiThread(new Runnable() { //回到主线程通知适配器刷新
            @Override
            public void run() {
                Log.d("NBN", "OK");
                dynamicAdapter.notifyDataSetChanged();
            }
        });
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
                    parseresultjson(data); //解析服务端返回的值
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void parseresultjson(String data) {
        Gson gson = new Gson();
        List<Result> resultsList = gson.fromJson(data, new TypeToken<List<Result>>(){}.getType());
        for (Result result : resultsList){
            if (result.result.equals("点赞成功")){
                Looper.prepare();
                Toast.makeText(Userinfo.this, "点赞成功", Toast.LENGTH_SHORT).show();
                Looper.loop();
            } else if (result.result.equals("点赞失败")){
                Looper.prepare();
                Toast.makeText(Userinfo.this, "点赞失败，稍后尝试", Toast.LENGTH_SHORT).show();
                Looper.loop();
            } else if (result.result.equals("关注成功")){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(Userinfo.this, "关注成功", Toast.LENGTH_SHORT).show();
                        care.setText("已关注");
                    }
                });
            } else if (result.result.equals("取消关注")){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(Userinfo.this, "取消关注", Toast.LENGTH_SHORT).show();
                        care.setText("关注");
                    }
                });
            } else if (result.result.equals("已关注")){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        care.setText("已关注");
                    }
                });
            }
        }
    }

    class Result{
        public String result;
    }

    public void getuser(){
        new Thread(new Runnable() { //耗时操作要开子线程
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder() //请求参数
                            .add("userid", userid)
                            .build();
                    Request request = new Request.Builder()
                            .url(Global_Variable.ip + "NogiMusic/getoneuser") //请求url
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String data = response.body().string();
                    Log.d("NMSL", data);
                    parseuserjson(data); //解析服务端返回的值
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void parseuserjson(String data) {
        Gson gson = new Gson();
        List<userResult> resultsList = gson.fromJson(data, new TypeToken<List<userResult>>(){}.getType());
        for (final userResult result : resultsList){
            runOnUiThread(new Runnable() { //回到主线程通知适配器刷新
                @Override
                public void run() {
                    userinfoage.setText(result.age);
                    userinfoname.setText(result.username);
                }
            });
        }
    }

    class userResult{
        public String id;
        public String username;
        public String age;
    }

    public void sendcare(){
        new Thread(new Runnable() { //耗时操作要开子线程
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder() //请求参数
                            .add("method", "addorcancel")
                            .add("userid", userid)
                            .add("myid", Global_Variable.thisuser.id)
                            .build();
                    Request request = new Request.Builder()
                            .url(Global_Variable.ip + "NogiMusic/care") //请求url
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String data = response.body().string();
                    Log.d("NMSL", data);
                    parseresultjson(data); //解析服务端返回的值
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void getcare(){
        new Thread(new Runnable() { //耗时操作要开子线程
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder() //请求参数
                            .add("method", "check")
                            .add("userid", userid)
                            .add("myid", Global_Variable.thisuser.id)
                            .build();
                    Request request = new Request.Builder()
                            .url(Global_Variable.ip + "NogiMusic/care") //请求url
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String data = response.body().string();
                    Log.d("NMSL", data);
                    parseresultjson(data); //解析服务端返回的值
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
