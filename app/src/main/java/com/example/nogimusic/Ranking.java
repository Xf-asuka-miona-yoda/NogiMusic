package com.example.nogimusic;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Ranking extends Fragment {
    private View view;
    HomeActivity homeActivity;

    private List<Music> HotmusicList = new ArrayList<>();
    private MusicAdapter HotmusicAdapter;

    private List<Music> NewmusicList = new ArrayList<>();
    private MusicAdapter NewmusicAdapter;

    private List<Music> SoaringmusicList = new ArrayList<>();
    private MusicAdapter SoaringmusicAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.ranking_list_layout, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        homeActivity = (HomeActivity) getActivity();  //过早初始化会空指针
        ImageButton back = (ImageButton) view.findViewById(R.id.back_ranking);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(v.getContext(),"hhh", Toast.LENGTH_SHORT).show();
                Global_Variable.fragmentManager = getFragmentManager();
                Global_Variable.fragmentTransaction = Global_Variable.fragmentManager.beginTransaction();
                List<Fragment> list = Global_Variable.fragmentManager.getFragments();
                for (int i = 0; i < list.size(); i++){
                    Fragment f = list.get(i);
                    if (f != null){
                        Global_Variable.fragmentTransaction.hide(f);
                    }
                }
                Global_Variable.fragmentTransaction.show(homeActivity.music_home_fragment);
                Global_Variable.fragmentTransaction.commit();
            }
        });

        sendHotrequest();
        sendNewrequest();
        sendSoaringrequest();
        inithotmusicadapter();
        initnewmusicadapter();
        initsoaringmusicadapter();
        musicListener();
    }

    public void inithotmusicadapter(){
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.hot_music);
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(layoutManager);
        HotmusicAdapter = new MusicAdapter(HotmusicList, view.getContext());
        recyclerView.setAdapter(HotmusicAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(view.getContext(),DividerItemDecoration.VERTICAL)); //分割线
    }

    public void initnewmusicadapter(){
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.new_music);
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(layoutManager);
        NewmusicAdapter = new MusicAdapter(NewmusicList, view.getContext());
        recyclerView.setAdapter(NewmusicAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(view.getContext(),DividerItemDecoration.VERTICAL)); //分割线
    }

    public void initsoaringmusicadapter(){
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.soaring_music);
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(layoutManager);
        SoaringmusicAdapter = new MusicAdapter(SoaringmusicList, view.getContext());
        recyclerView.setAdapter(SoaringmusicAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(view.getContext(),DividerItemDecoration.VERTICAL)); //分割线
    }



    public void musicListener(){
        HotmusicAdapter.setmOnItemClickListener(new MusicAdapter.OnItemClickListener(){
            @Override
            public void onItemClick(View view, int position) {
                homeActivity.musicBinder.stop();
                Music music = HotmusicList.get(position);
                //Toast.makeText(view.getContext(), "你点击了"+music.getMusic_url(), Toast.LENGTH_SHORT).show();
                if (!Global_Variable.musicplayQueue.isinclude(music.getMusic_id())){//如果没有才能加入，否则会造成重复
                    Global_Variable.musicplayQueue.queue.add(music); //加入播放队列
                }
                Global_Variable.musicplayQueue.i = Global_Variable.musicplayQueue.getindex(music.getMusic_id()); //i记录当前是播放队列中的第几个
                Log.d("cao", String.valueOf(Global_Variable.musicplayQueue.i));
                Log.d("cao", Global_Variable.musicplayQueue.queue.get(Global_Variable.musicplayQueue.i).getMusic_url());
                homeActivity.musicBinder.initmediaplayer(Global_Variable.musicplayQueue.i); //初始化
                homeActivity.musicBinder.play(); //播放
            }
        });

        NewmusicAdapter.setmOnItemClickListener(new MusicAdapter.OnItemClickListener(){
            @Override
            public void onItemClick(View view, int position) {
                homeActivity.musicBinder.stop();
                Music music = NewmusicList.get(position);
                //Toast.makeText(view.getContext(), "你点击了"+music.getMusic_url(), Toast.LENGTH_SHORT).show();
                if (!Global_Variable.musicplayQueue.isinclude(music.getMusic_id())){//如果没有才能加入，否则会造成重复
                    Global_Variable.musicplayQueue.queue.add(music); //加入播放队列
                }
                Global_Variable.musicplayQueue.i = Global_Variable.musicplayQueue.getindex(music.getMusic_id()); //i记录当前是播放队列中的第几个
                Log.d("cao", String.valueOf(Global_Variable.musicplayQueue.i));
                Log.d("cao", Global_Variable.musicplayQueue.queue.get(Global_Variable.musicplayQueue.i).getMusic_url());
                homeActivity.musicBinder.initmediaplayer(Global_Variable.musicplayQueue.i); //初始化
                homeActivity.musicBinder.play(); //播放
            }
        });

        SoaringmusicAdapter.setmOnItemClickListener(new MusicAdapter.OnItemClickListener(){
            @Override
            public void onItemClick(View view, int position) {
                homeActivity.musicBinder.stop();
                Music music = SoaringmusicList.get(position);
                //Toast.makeText(view.getContext(), "你点击了"+music.getMusic_url(), Toast.LENGTH_SHORT).show();
                if (!Global_Variable.musicplayQueue.isinclude(music.getMusic_id())){//如果没有才能加入，否则会造成重复
                    Global_Variable.musicplayQueue.queue.add(music); //加入播放队列
                }
                Global_Variable.musicplayQueue.i = Global_Variable.musicplayQueue.getindex(music.getMusic_id()); //i记录当前是播放队列中的第几个
                Log.d("cao", String.valueOf(Global_Variable.musicplayQueue.i));
                Log.d("cao", Global_Variable.musicplayQueue.queue.get(Global_Variable.musicplayQueue.i).getMusic_url());
                homeActivity.musicBinder.initmediaplayer(Global_Variable.musicplayQueue.i); //初始化
                homeActivity.musicBinder.play(); //播放
            }
        });
    }

    public void sendHotrequest(){
        new Thread(new Runnable() { //耗时操作要开子线程
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder() //请求参数
                            .add("rankingname", "热歌榜") //获取的音乐歌手id
                            .build();
                    Request request = new Request.Builder()
                            .url(Global_Variable.ip + "NogiMusic/hotranking") //请求url
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String data = response.body().string();
                    Log.d("NMSL", data);
                    parsejson_hot(data); //解析服务端返回的值
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    protected void parsejson_hot(String data){
        Gson gson = new Gson();
        List<musicresult> resultsList = gson.fromJson(data, new TypeToken<List<musicresult>>(){}.getType());
        for (musicresult musicresult1 : resultsList){
            Music music = new Music(musicresult1.musicid, musicresult1.musicname, musicresult1.singer, musicresult1.musicurl, musicresult1.musicpic, "net");
            HotmusicList.add(music);
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                HotmusicAdapter.notifyDataSetChanged();
            }
        });
    }

    public void sendNewrequest(){
        new Thread(new Runnable() { //耗时操作要开子线程
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder() //请求参数
                            .add("rankingname", "新歌榜") //获取的音乐歌手id
                            .build();
                    Request request = new Request.Builder()
                            .url(Global_Variable.ip + "NogiMusic/newranking") //请求url
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String data = response.body().string();
                    Log.d("NMSL", data);
                    parsejson_new(data); //解析服务端返回的值
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    protected void parsejson_new(String data){
        Gson gson = new Gson();
        List<musicresult> resultsList = gson.fromJson(data, new TypeToken<List<musicresult>>(){}.getType());
        for (musicresult musicresult1 : resultsList){
            Music music = new Music(musicresult1.musicid, musicresult1.musicname, musicresult1.singer, musicresult1.musicurl, musicresult1.musicpic, "net");
            NewmusicList.add(music);
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                NewmusicAdapter.notifyDataSetChanged();
            }
        });
    }

    public void sendSoaringrequest(){
        new Thread(new Runnable() { //耗时操作要开子线程
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder() //请求参数
                            .add("rankingname", "飙升榜") //获取的音乐歌手id
                            .build();
                    Request request = new Request.Builder()
                            .url(Global_Variable.ip + "NogiMusic/soaringranking") //请求url
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String data = response.body().string();
                    Log.d("NMSL", data);
                    parsejson_soaring(data); //解析服务端返回的值
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    protected void parsejson_soaring(String data){
        Gson gson = new Gson();
        List<musicresult> resultsList = gson.fromJson(data, new TypeToken<List<musicresult>>(){}.getType());
        for (musicresult musicresult1 : resultsList){
            Music music = new Music(musicresult1.musicid, musicresult1.musicname, musicresult1.singer, musicresult1.musicurl, musicresult1.musicpic, "net");
            SoaringmusicList.add(music);
            Log.d("biao","ok");
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                NewmusicAdapter.notifyDataSetChanged();
            }
        });
    }
}
