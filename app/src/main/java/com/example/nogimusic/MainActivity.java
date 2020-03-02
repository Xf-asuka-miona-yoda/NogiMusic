package com.example.nogimusic;


import android.content.Intent;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {            //loginActivity,程序启动时的activity，登录界面

    private Button login;
    private Button register;
    private EditText username;
    private EditText password;
    private String input_account;
    private String input_password;
    private int loginback = -1; //登录标志位


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initview();
        setlistener();
    }

    //初始化控件
    public void initview(){
        login = (Button) findViewById(R.id.button_login);
        register = (Button) findViewById(R.id.button_register);
        username = (EditText) findViewById(R.id.input_username);
        password = (EditText) findViewById(R.id.input_password);
    }

    //设置监听
    public void setlistener(){
        login.setOnClickListener(this);
        register.setOnClickListener(this);
    }

    //响应按钮点击事件监听
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_login:  //点击登录按钮
                input_account = username.getText().toString();
                input_password = password.getText().toString();
                if (TextUtils.isEmpty(input_account)){ //首先要判断账号和密码是否为空
                    Toast.makeText(MainActivity.this, "请输入账号", Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(input_password)){
                    Toast.makeText(MainActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
                }else {
                    sendrequest(); //全部符合要求发送登录请求
                }
                Timer timer = new Timer(); //由于网络请求是耗时操作 ，所以开一个子线程在200ms之后检查结果
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if (loginback == 1){
                            Looper.prepare();
                            Toast.makeText(MainActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                            startActivity(intent);
                            //Log.d("NMSL", "你动啊");
                            Looper.loop();
                        }else if (loginback == 0){
                            Looper.prepare();
                            Toast.makeText(MainActivity.this, "账号或密码错误", Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }else if (loginback == -1){
                            Looper.prepare();
                            Toast.makeText(MainActivity.this, "登录超时", Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                    }
                },300);

                break;
            case R.id.button_register:
                Toast.makeText(MainActivity.this, "注册", Toast.LENGTH_SHORT).show();
                //Log.d("NMSL", Global_Variable.thisuser.id);
                break;
            default:
                break;
        }
    }

    public void sendrequest(){ //发送登录请求哦
        new Thread(new Runnable() { //耗时操作要开子线程
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder() //请求参数
                            .add("loginAccount", input_account)
                            .add("loginPassword", input_password)
                            .build();
                    Request request = new Request.Builder()
                            .url(Global_Variable.ip + "NogiMusic/login") //请求url
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

    public void parsejson(String jsondata){ //使用GSON解析服务端返回的json数据
        Gson gson = new Gson();
        List<login_result> resultsList = gson.fromJson(jsondata, new TypeToken<List<login_result>>(){}.getType());
        for (login_result result : resultsList){
            Global_Variable.thisuser.setId(result.id);
            Global_Variable.thisuser.setAge(result.age);
            Global_Variable.thisuser.setUsername(result.username);
            if (result.result.equals("登录成功")){
                loginback = 1;
            }else if (result.result.equals("登录失败")){
                loginback = 0;
            }
            Log.d("NMSL", result.result);
            Log.d("NMSL", result.id);
            Log.d("NMSL", result.username);

        }
    }
}
