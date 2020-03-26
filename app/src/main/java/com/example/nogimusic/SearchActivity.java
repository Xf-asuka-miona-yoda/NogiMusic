package com.example.nogimusic;

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
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;


import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SearchActivity extends AppCompatActivity {

    private List<HistorySearch> searchList = new ArrayList<>();
    private SearchAdapter searchAdapter;

    private List<HotSearch> hotSearchList = new ArrayList<>();
    private HotSearchAdapter hotSearchAdapter;

    private EditText search;
    private String input_search = "";
    private Button commit_search;
    private ScrollView before;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        LitePal.getDatabase();
        initdata();
        initadapter();
        gethotsearch();
        search = (EditText) findViewById(R.id.search_input);
        commit_search = (Button) findViewById(R.id.send_search);
        before = (ScrollView) findViewById(R.id.before_search);
        commit_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                input_search = search.getText().toString();
                if (TextUtils.isEmpty(input_search)){
                    Toast.makeText(SearchActivity.this, "还不知道你要搜素什么哦", Toast.LENGTH_SHORT).show();
                }else {
                    localhistory();
                    //before.setVisibility(View.GONE);
                    getresult();
                }
            }
        });
        setlisthen();
    }

    public void initdata(){
        List<HistorySearch> searches = LitePal.findAll(HistorySearch.class);
//        for (int i = 0; i < 5; i++){
//            searchList.add(searches.get(searches.size()-1-i));
//        }
        if (searches.size() > 5){
            for (int i = 0; i < 5; i++){
                searchList.add(searches.get(searches.size()-1-i));
            }
        }else {
            searchList.addAll(searches);
        }
    }

    public void initadapter(){
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.history_search);
        FlexboxLayoutManager manager = new FlexboxLayoutManager(this);
        //设置主轴排列方式
        manager.setFlexDirection(FlexDirection.ROW);
        //设置是否换行
        manager.setFlexWrap(FlexWrap.WRAP);
        manager.setAlignItems(AlignItems.STRETCH);
        recyclerView.setLayoutManager(manager);
        searchAdapter = new SearchAdapter(searchList);
        recyclerView.setAdapter(searchAdapter);

        RecyclerView hotsearchrecy = (RecyclerView) findViewById(R.id.hotsearch_recy);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        hotsearchrecy.setLayoutManager(layoutManager);
        hotSearchAdapter = new HotSearchAdapter(hotSearchList);
        hotsearchrecy.setAdapter(hotSearchAdapter);
    }

    public void localhistory(){
        HistorySearch historySearch = new HistorySearch();
        historySearch.setContent(input_search);
        searchList.add(0,historySearch);
        searchAdapter.notifyDataSetChanged();
        historySearch.save(); //保存至数据库
        search.setText("");
    }

    public void gethotsearch(){
        new Thread(new Runnable() { //耗时操作要开子线程
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder() //请求参数
                            .add("method", "hotsearch") //获取方式，表明是获取热搜
                            .add("content",input_search) //此处无用
                            .build();
                    Request request = new Request.Builder()
                            .url(Global_Variable.ip + "NogiMusic/search") //请求url
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String data = response.body().string();
                    Log.d("NMSL", data);
                    parsejsonhot(data); //解析服务端返回的值
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void parsejsonhot(String data) {
        Gson gson = new Gson();
        List<HotSearch> resultsList = gson.fromJson(data, new TypeToken<List<HotSearch>>(){}.getType());
        for (HotSearch rs : resultsList){
            hotSearchList.add(rs);
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                hotSearchAdapter.notifyDataSetChanged();
            }
        });
    }

    public void setlisthen(){
        hotSearchAdapter.setmOnItemClickListener(new HotSearchAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                HotSearch search = hotSearchList.get(position);
                Toast.makeText(SearchActivity.this, "点击了" + search.getContent(), Toast.LENGTH_SHORT).show();
                input_search = search.getContent();
                getresult();
            }
        });

        searchAdapter.setmOnItemClickListener(new SearchAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                HistorySearch search = searchList.get(position);
                Toast.makeText(SearchActivity.this, "点击了" + search.getContent(), Toast.LENGTH_SHORT).show();
                input_search = search.getContent();
                getresult();
            }
        });
    }

    public void getresult(){
        new Thread(new Runnable() { //耗时操作要开子线程
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder() //请求参数
                            .add("method", "result") //获取方式，表明是获取结果
                            .add("content",input_search)   //要搜素的内容
                            .build();
                    Request request = new Request.Builder()
                            .url(Global_Variable.ip + "NogiMusic/search") //请求url
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String data = response.body().string();
                    Log.d("NMSL", data);
                    //parsejsonhot(data); //解析服务端返回的值
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
