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

public class FindpasswordActivity extends AppCompatActivity {

    private Button findpassword;

    private EditText find_account;
    private EditText new_password;
    private EditText find_safe;

    private String account;
    private String password;
    private String safe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findpassword);
        initview();
        findpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                account = find_account.getText().toString();
                password = new_password.getText().toString();
                safe = find_safe.getText().toString();
                if (TextUtils.isEmpty(account)){
                    Toast.makeText(FindpasswordActivity.this, "请输入账号", Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(password)){
                    Toast.makeText(FindpasswordActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(safe)){
                    Toast.makeText(FindpasswordActivity.this, "请输入安全问题答案", Toast.LENGTH_SHORT).show();
                }else {
                    sendrequest();
                }
            }
        });
    }

    public void initview(){
        findpassword = (Button) findViewById(R.id.find);

        find_account = (EditText) findViewById(R.id.find_account);
        new_password = (EditText) findViewById(R.id.find_password);
        find_safe = (EditText) findViewById(R.id.find_safe);
    }

    public void sendrequest(){
        new Thread(new Runnable() { //耗时操作要开子线程
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder() //请求参数
                            .add("FindAccount", account)
                            .add("NewPassword", password)
                            .add("FindSafe", safe)
                            .build();
                    Request request = new Request.Builder()
                            .url(Global_Variable.ip + "NogiMusic/findaccount") //请求url
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
        List<ChangeResult> resultsList = gson.fromJson(data, new TypeToken<List<ChangeResult>>(){}.getType());
        for (ChangeResult result : resultsList){
            if (result.code.equals("ok")){
                Looper.prepare();
                Toast.makeText(FindpasswordActivity.this, "更新成功", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(FindpasswordActivity.this, MainActivity.class);
                startActivity(intent);
                Looper.loop();
            }else if (result.code.equals("fail")){
                Looper.prepare();
                Toast.makeText(FindpasswordActivity.this, "请确认输入账号和安全问题答案是否正确", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        }
    }

    class ChangeResult{
        public String code;
    }
}
