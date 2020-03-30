package com.example.nogimusic;

import android.content.Intent;

import android.os.Looper;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Calendar;

import java.util.List;
import java.util.TimeZone;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CommentsActivity extends AppCompatActivity {

    private String music_id;
    private String mycontent;

    private TextView musicid;
    private EditText mycomment;
    private Button commit;
    private SwipeRefreshLayout refreshLayout;

    private CommentsAdapter commentsAdapter;
    private List<Comment> commentList = new ArrayList<>();

    private int year,month,day,hour,minute,second;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        Intent intent = getIntent();
        String musicname = intent.getStringExtra("musicname");
        music_id = intent.getStringExtra("musicid");  //通过intent获取音乐id
        getcomments();
        initview();
        musicid.setText(musicname);
        //initdata();
        initadapter();
        setlistener();
    }

    public void initview(){
        musicid = (TextView) findViewById(R.id.music_comments_id);
        mycomment = (EditText) findViewById(R.id.my_comment);
        commit = (Button) findViewById(R.id.comment_commit);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.comment_refresh);
        refreshLayout.setColorSchemeResources(R.color.colorPrimary);
    }

    public void setlistener(){ //设置按钮的事件监听
        commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+08:00")); //获取东八区时间
                year = cal.get(Calendar.YEAR);
                month = cal.get(Calendar.MONTH);
                day = cal.get(Calendar.DATE);

                hour = cal.get(Calendar.HOUR_OF_DAY);//24小时制度
                minute = cal.get(Calendar.MINUTE);
                second = cal.get(Calendar.SECOND);

                mycontent = mycomment.getText().toString(); //获取评论
                if (TextUtils.isEmpty(mycontent)){
                    Toast.makeText(CommentsActivity.this, "请输入评论", Toast.LENGTH_SHORT).show();
                }else {
                    mycomment.setText("");
//                    Comment comment = new Comment(Global_Variable.thisuser.id,Global_Variable.thisuser.username, mycontent, year, month + 1, day, hour, minute, second);
//                    commentList.add(comment);
//                    commentsAdapter.notifyDataSetChanged(); //更新界面
                    sendmycomments(); //发送至服务端
                }
            }
        });


        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                commentList.clear();
                getcomments();
                refreshLayout.setRefreshing(false);
            }
        });

        commentsAdapter.setmOnItemClickListener(new CommentsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Comment comment = commentList.get(position);
                if (view.getId() == R.id.user_img){
                    Toast.makeText(CommentsActivity.this, "点击了" + comment.getUserid(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(CommentsActivity.this, Userinfo.class);
                    intent.putExtra("userid", comment.getUserid());
                    startActivity(intent);
                }else if (view.getId() == R.id.comment_content){
                    Toast.makeText(CommentsActivity.this, "点击了" + comment.getId(), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }


    public void initadapter(){ //初始化适配器
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.comment_recy);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        commentsAdapter = new CommentsAdapter(commentList);
        recyclerView.setAdapter(commentsAdapter);
    }

    public void getcomments(){  //获取当前歌曲的评论
        new Thread(new Runnable() { //耗时操作要开子线程
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder() //请求参数
                            .add("musicid", music_id)
                            .build();
                    Request request = new Request.Builder()
                            .url(Global_Variable.ip + "NogiMusic/comments") //请求url
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String data = response.body().string();
                    Log.d("NMSL", data);
                    parsecommentjson(data); //解析服务端返回的值
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void parsecommentjson(String data) { //解析数据
        Gson gson = new Gson();
        List<Comment> comments = gson.fromJson(data, new TypeToken<List<Comment>>(){}.getType());
        for (Comment rs : comments){
            commentList.add(rs);
            Log.d("fule", rs.getContent());
        }
        //singerList.addAll(s);
        runOnUiThread(new Runnable() { //回到主线程通知适配器刷新
            @Override
            public void run() {
                Log.d("NBN", "OK");
                commentsAdapter.notifyDataSetChanged();
            }
        });
    }

    public void sendmycomments(){
        new Thread(new Runnable() { //耗时操作要开子线程
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder() //请求参数
                            .add("method", "music")
                            .add("objectid", music_id)
                            .add("userid", Global_Variable.thisuser.id)
                            .add("content", mycontent)
                            .add("year", String.valueOf(year))
                            .add("month", String.valueOf(month+1))
                            .add("day", String.valueOf(day))
                            .add("hour", String.valueOf(hour))
                            .add("minute", String.valueOf(minute))
                            .add("second", String.valueOf(second))
                            .build();
                    Request request = new Request.Builder()
                            .url(Global_Variable.ip + "NogiMusic/mycomment") //请求url
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String data = response.body().string();
                    Log.d("NMSL", data);
                    parsejson(data); //解析服务端返回的值
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void parsejson(String data) {
        Gson gson = new Gson();
        List<commentresult> rslist = gson.fromJson(data, new TypeToken<List<commentresult>>(){}.getType());
        for (commentresult rs : rslist){
            if (rs.code.equals("success")){
                Looper.prepare();
                Toast.makeText(CommentsActivity.this, "发表成功", Toast.LENGTH_SHORT).show();
                commentList.clear();
                getcomments();
                Looper.loop();
            }else if (rs.code.equals("failed")){
                Looper.prepare();
                Toast.makeText(CommentsActivity.this, "发表失败，请稍后重试", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        }
    }

    class commentresult{
        public String code;
    }


}
