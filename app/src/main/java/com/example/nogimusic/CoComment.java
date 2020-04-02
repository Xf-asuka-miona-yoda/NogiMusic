package com.example.nogimusic;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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
import android.widget.ImageButton;
import android.widget.ImageView;
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

public class CoComment extends AppCompatActivity {

    private TextView info_name;
    private TextView info_time;
    private TextView info_content;
    private ImageView info_user;
    private EditText dyinfo_input;
    private Button dyinfo_commit;

    private String username;
    private String usertime;
    private String dycontent;
    private String userid;
    private String dyid;
    private String dyinfo_comment;

    private String replyid;

    private SwipeRefreshLayout refreshLayout;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    info_name.setText(username);
                    info_time.setText(usertime);
                    info_content.setText(dycontent);
                    commentList.clear();
                    getcomments();
                    break;
                default:
                    break;
            }
        }
    };

    public void setinfo(String userid,String id,String name, String time, String content){
        this.userid = userid;
        this.dyid = id;
        this.dycontent = content;
        this.username = name;
        this.usertime = time;
        Log.d("test","更新了" + this.dyid);
        Message message = new Message();
        message.what = 1;
        handler.sendMessage(message);
    }

    private List<Comment> commentList = new ArrayList<>();
    private CommentsAdapter commentsAdapter;

    private int year,month,day,hour,minute,second;

    public void initadapter(){ //初始化适配器
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.cocommnet_show);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        commentsAdapter = new CommentsAdapter(commentList);
        recyclerView.setAdapter(commentsAdapter);
    }

    public void initview(){
        info_content = (TextView) findViewById(R.id.coinfo_content);
        info_name = (TextView) findViewById(R.id.coinfo_username);
        info_time = (TextView) findViewById(R.id.coinfo_time);
        info_user = (ImageView) findViewById(R.id.coinfo_user_img);
        dyinfo_input = (EditText) findViewById(R.id.coinfo_comment);
        dyinfo_commit = (Button) findViewById(R.id.coinfo_comment_commit);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.co_info_refresh);
        refreshLayout.setColorSchemeResources(R.color.colorPrimary);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_co_comment);
        initview();
        Intent intent = getIntent();
        String getid = intent.getStringExtra("coid");
        String getuserid = intent.getStringExtra("userid");
        String getname = intent.getStringExtra("username");
        String gettime = intent.getStringExtra("time");
        String getcontent = intent.getStringExtra("content");

        setinfo(getuserid,getid,getname,gettime,getcontent);

        initadapter();

        ImageButton back = (ImageButton) findViewById(R.id.backto_dyinfo);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        info_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(CoComment.this,"点击了" + userid,Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(CoComment.this, Userinfo.class);
                intent.putExtra("userid", userid);
                startActivity(intent);
            }
        });

        dyinfo_commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dyinfo_comment = dyinfo_input.getText().toString();

                Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+08:00")); //获取东八区时间
                year = cal.get(Calendar.YEAR);
                month = cal.get(Calendar.MONTH);
                day = cal.get(Calendar.DATE);

                hour = cal.get(Calendar.HOUR_OF_DAY);//24小时制度
                minute = cal.get(Calendar.MINUTE);
                second = cal.get(Calendar.SECOND);

                if (TextUtils.isEmpty(dyinfo_comment)){
                    Toast.makeText(CoComment.this, "请输入评论", Toast.LENGTH_SHORT).show();
                }else {
                    if (dyinfo_comment.contains("回复")){
                        Toast.makeText(CoComment.this, "回复", Toast.LENGTH_SHORT).show();
                        sendreply();
                    }
                    dyinfo_input.setText("");
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
                    Toast.makeText(CoComment.this, "点击了" + comment.getUserid(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(CoComment.this, Userinfo.class);
                    intent.putExtra("userid", comment.getUserid());
                    startActivity(intent);
                }else if (view.getId() == R.id.comment_content){
                    Toast.makeText(CoComment.this, "点击了" + comment.getId(), Toast.LENGTH_SHORT).show();
                    replyid = comment.getUserid();
                    dyinfo_input.setText("回复" + comment.getUsername() + ":");
                    dyinfo_input.requestFocus();
                    dyinfo_input.setSelection(dyinfo_input.getText().toString().length());
                }
            }
        });
    }

    public void getcomments(){
        new Thread(new Runnable() { //耗时操作要开子线程
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder() //请求参数
                            .add("dycommentid", dyid)
                            .build();
                    Request request = new Request.Builder()
                            .url(Global_Variable.ip + "NogiMusic/cocomment") //请求url
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

    private void parsecommentjson(String data) {
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
                            .add("method","comment")
                            .add("objectid", dyid)
                            .add("userid", Global_Variable.thisuser.id)
                            .add("content", dyinfo_comment)
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
                Toast.makeText(CoComment.this, "发表成功", Toast.LENGTH_SHORT).show();
                commentList.clear();
                getcomments();
                Looper.loop();
            }else if (rs.code.equals("failed")){
                Looper.prepare();
                Toast.makeText(CoComment.this, "发表失败，请稍后重试", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        }
    }

    class commentresult{
        public String code;
    }

    public void sendreply(){
        new Thread(new Runnable() { //耗时操作要开子线程
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder() //请求参数
                            .add("replyid", replyid)
                            .add("commitid", Global_Variable.thisuser.id)
                            .add("content", dyinfo_comment)
                            .add("dycommentid", dyid)
                            .build();
                    Request request = new Request.Builder()
                            .url(Global_Variable.ip + "NogiMusic/reply") //请求url
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String data = response.body().string();
                    Log.d("NMSL", data);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
