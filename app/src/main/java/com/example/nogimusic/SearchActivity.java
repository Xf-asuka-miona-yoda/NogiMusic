package com.example.nogimusic;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
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

    private List<Music> musicList = new ArrayList<>();
    private MusicAdapter musicAdapter;

    private EditText search;
    private String input_search = "";
    private Button commit_search;
    private ScrollView before;

    public MusicService.MusicBinder musicBinder;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            musicBinder = (MusicService.MusicBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Intent intent = new Intent(this, MusicService.class);
        startService(intent);
        bindService(intent, connection, BIND_AUTO_CREATE);//绑定服务

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

        RecyclerView musicrecy = (RecyclerView) findViewById(R.id.search_reult_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        musicrecy.setLayoutManager(linearLayoutManager);
        musicAdapter = new MusicAdapter(musicList,this);
        musicrecy.setAdapter(musicAdapter);
        musicrecy.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL)); //分割线
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
                //Toast.makeText(SearchActivity.this, "点击了" + search.getContent(), Toast.LENGTH_SHORT).show();
                input_search = search.getContent();
                getresult();
            }
        });

        searchAdapter.setmOnItemClickListener(new SearchAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                HistorySearch search = searchList.get(position);
                //Toast.makeText(SearchActivity.this, "点击了" + search.getContent(), Toast.LENGTH_SHORT).show();
                input_search = search.getContent();
                getresult();
            }
        });

        musicAdapter.setmOnItemClickListener(new MusicAdapter.OnItemClickListener(){
            @Override
            public void onItemClick(View view, int position) {
                musicBinder.stop();
                Music music = musicList.get(position);
                //Toast.makeText(view.getContext(), "你点击了"+music.getMusic_url(), Toast.LENGTH_SHORT).show();
                if (!Global_Variable.musicplayQueue.isinclude(music.getMusic_name())){//如果没有才能加入，否则会造成重复
                    Global_Variable.musicplayQueue.queue.add(music); //加入播放队列
                }
                Global_Variable.musicplayQueue.i = Global_Variable.musicplayQueue.getindex(music.getMusic_name()); //i记录当前是播放队列中的第几个
                Log.d("cao", String.valueOf(Global_Variable.musicplayQueue.i));
                Log.d("cao", Global_Variable.musicplayQueue.queue.get(Global_Variable.musicplayQueue.i).getMusic_url());
                musicBinder.initmediaplayer(Global_Variable.musicplayQueue.i); //初始化
                musicBinder.play(); //播放
            }
        });
    }

    public void getresult(){  //获取搜索结果
        musicList.clear();
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
                    parsejsonresult(data); //解析服务端返回的值
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void parsejsonresult(String data) {
        Gson gson = new Gson();
        List<musicresult> resultsList = gson.fromJson(data, new TypeToken<List<musicresult>>(){}.getType());
        for (musicresult musicresult1 : resultsList){
            if (musicresult1.musicid.equals("-1")){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(SearchActivity.this,"没有找到你要搜索的歌曲哦，我们已经记录，会尽快补充资源",Toast.LENGTH_LONG).show();
                    }
                });
                break;
            }
            Music music = new Music(musicresult1.musicid, musicresult1.musicname, musicresult1.singer, musicresult1.musicurl, musicresult1.musicpic, "net");
            musicList.add(music);
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                musicAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
    }
}
