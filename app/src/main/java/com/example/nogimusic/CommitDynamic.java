package com.example.nogimusic;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;
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

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CommitDynamic extends AppCompatActivity {

    private LocalBroadcastManager localBroadcastManager;
    String my_dynamic;
    EditText dy_comtent;

    private int year,month,day,hour,minute,second;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commit_dynamic);
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        dy_comtent = (EditText) findViewById(R.id.my_dy_content);

        Button commit = (Button) findViewById(R.id.fabu);
        commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                my_dynamic = dy_comtent.getText().toString();
                if (TextUtils.isEmpty(my_dynamic)){
                    Toast.makeText(CommitDynamic.this, "动态内容不可为空哦", Toast.LENGTH_SHORT).show();
                }else {
                    Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+08:00")); //获取东八区时间
                    year = cal.get(Calendar.YEAR);
                    month = cal.get(Calendar.MONTH);
                    day = cal.get(Calendar.DATE);

                    hour = cal.get(Calendar.HOUR_OF_DAY);//24小时制度
                    minute = cal.get(Calendar.MINUTE);
                    second = cal.get(Calendar.SECOND);
                    sendmydynamic();

                }

            }
        });

        Button cancel = (Button) findViewById(R.id.commit_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void sendmydynamic(){
        new Thread(new Runnable() { //耗时操作要开子线程
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder() //请求参数
                            .add("userid", Global_Variable.thisuser.id)
                            .add("content", my_dynamic)
                            .add("year", String.valueOf(year))
                            .add("month", String.valueOf(month+1))
                            .add("day", String.valueOf(day))
                            .add("hour", String.valueOf(hour))
                            .add("minute", String.valueOf(minute))
                            .add("second", String.valueOf(second))
                            .build();
                    Request request = new Request.Builder()
                            .url(Global_Variable.ip + "NogiMusic/commitdynamicservlet") //请求url
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
        List<dynamicresult> rslist = gson.fromJson(data, new TypeToken<List<dynamicresult>>(){}.getType());
        for (dynamicresult rs : rslist){
            if (rs.code.equals("success")){
                Looper.prepare();
                Toast.makeText(CommitDynamic.this, "发表成功", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent("dynamic_commit_success");
                intent.putExtra("code","200");
                localBroadcastManager.sendBroadcast(intent);
                finish();
                Looper.loop();
            }else if (rs.code.equals("failed")){
                Looper.prepare();
                Toast.makeText(CommitDynamic.this, "发表失败，请稍后重试", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        }
    }

    class dynamicresult{
        public String code;
    }


}
