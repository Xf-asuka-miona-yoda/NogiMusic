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

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private Button new_register;

    private EditText re_account;
    private EditText re_username;
    private EditText re_password;
    private EditText re_age;
    private EditText re_safe;

    private String account;
    private String username;
    private String password;
    private String age;
    private String safe;

    private int loginback = -1; //登录标志位


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initview();
        setlistener();
    }

    //初始化控件
    public void initview(){
        new_register = (Button) findViewById(R.id.register);
        re_account = (EditText) findViewById(R.id.register_account);
        re_username = (EditText) findViewById(R.id.register_username);
        re_password = (EditText) findViewById(R.id.register_password);
        re_age = (EditText) findViewById(R.id.register_age);
        re_safe = (EditText) findViewById(R.id.register_safe);
    }

    public void setlistener(){
        new_register.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.register:
                account = re_account.getText().toString();
                username = re_username.getText().toString();
                password = re_password.getText().toString();
                age = re_age.getText().toString();
                safe = re_safe.getText().toString();
                if (TextUtils.isEmpty(account)){ //首先要判断账号和密码是否为空
                    Toast.makeText(RegisterActivity.this, "请输入账号", Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(username)){
                    Toast.makeText(RegisterActivity.this, "请输入昵称", Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(password)){
                    Toast.makeText(RegisterActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(age)){
                    Toast.makeText(RegisterActivity.this, "请输入年龄", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(safe)) {
                    Toast.makeText(RegisterActivity.this, "请输入安全问题", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(RegisterActivity.this, "请稍后", Toast.LENGTH_SHORT).show();
                    sendrequest();
                }
        }
    }



    public void sendrequest(){ //发送登录请求哦
        new Thread(new Runnable() { //耗时操作要开子线程
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder() //请求参数
                            .add("ResgisterAccount", account)
                            .add("ResgisterUsername", username)
                            .add("ResgisterPassword", password)
                            .add("ResgisterAge", age)
                            .add("ResgisterSafe",safe)
                            .build();
                    Request request = new Request.Builder()
                            .url(Global_Variable.ip + "NogiMusic/register") //请求url
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
            if (result.result.equals("注册成功")){
                loginback = 1;
            }else if (result.result.equals("注册失败")){
                loginback = 0;
            }
            Log.d("NMSL", result.result);
            Log.d("NMSL", result.id);
            Log.d("NMSL", result.username);
            if (loginback == 1){
                Looper.prepare();
                Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                startActivity(intent);
                //Log.d("NMSL", "你动啊");
                Looper.loop();
            }else if (loginback == 0){
                Looper.prepare();
                Toast.makeText(RegisterActivity.this, "注册账号已被人抢注", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }else if (loginback == -1){
                Looper.prepare();
                Toast.makeText(RegisterActivity.this, "注册超时", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

        }
    }
}
