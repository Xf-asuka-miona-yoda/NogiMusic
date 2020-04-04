package com.example.nogimusic;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
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

public class MessageActivity extends AppCompatActivity {

    private List<Mymessage> mymessageList = new ArrayList<>();
    private MymessageAdapter mymessageAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    public void initadapter(){
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.msg_recy);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        mymessageAdapter = new MymessageAdapter(mymessageList);
        recyclerView.setAdapter(mymessageAdapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.msg_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mymessageList.clear();
                getmsg();
            }
        });
        initadapter();
        getmsg();
        setlisthener();
    }

    public void getmsg(){
        new Thread(new Runnable() { //耗时操作要开子线程
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder()
                            .add("userid", Global_Variable.thisuser.id) //请求参数
                            .build();
                    Request request = new Request.Builder()
                            .url(Global_Variable.ip + "NogiMusic/message") //请求url
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String data = response.body().string();
                    Log.d("NMSL", data);
                    parsejsonmsg(data); //解析服务端返回的值
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void parsejsonmsg(String data) {
        Gson gson = new Gson();
        List<Mymessage> resultsList = gson.fromJson(data, new TypeToken<List<Mymessage>>(){}.getType());
        for (Mymessage rs : resultsList){
            mymessageList.add(0,rs);
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mymessageAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    public void setlisthener(){
        mymessageAdapter.setmOnItemClickListener(new MymessageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Mymessage mymessage = mymessageList.get(position);
                if (view.getId() == R.id.msg_user_img){
                    Toast.makeText(view.getContext(), "点击了" + mymessage.getUserid(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MessageActivity.this, Userinfo.class);
                    intent.putExtra("userid", mymessage.getUserid());
                    startActivity(intent);
                }else {
                    if (mymessage.getType().equals("dycomment")){ //如果是动态下面的评论的话
                        Intent intent = new Intent(MessageActivity.this, DynamicinfoActivity.class);
                        intent.putExtra("username", Global_Variable.thisuser.username);
                        intent.putExtra("userid", Global_Variable.thisuser.id);
                        intent.putExtra("dyid", mymessage.getObjectid());
                        intent.putExtra("time", mymessage.getObjecttime());
                        intent.putExtra("content", mymessage.getObjectcontent());
                        startActivity(intent);
                    }else if (mymessage.getType().equals("cocomment")){
                        Intent intent = new Intent(MessageActivity.this, CoComment.class);
                        intent.putExtra("username", Global_Variable.thisuser.username);
                        intent.putExtra("userid", Global_Variable.thisuser.id);
                        intent.putExtra("coid", mymessage.getObjectid());
                        intent.putExtra("time", mymessage.getObjecttime());
                        intent.putExtra("content", mymessage.getObjectcontent());
                        startActivity(intent);
                    }else if (mymessage.getType().equals("reply")){
                        Intent intent = new Intent(MessageActivity.this, CoComment.class);
                        intent.putExtra("username", Global_Variable.thisuser.username);
                        intent.putExtra("userid", Global_Variable.thisuser.id);
                        intent.putExtra("coid", mymessage.getObjectid());
                        intent.putExtra("time", mymessage.getObjecttime());
                        intent.putExtra("content", mymessage.getObjectcontent());
                        startActivity(intent);
                    }
                }
            }
        });
    }
}
