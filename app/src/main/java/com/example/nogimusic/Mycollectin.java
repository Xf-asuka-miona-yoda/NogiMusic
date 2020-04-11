package com.example.nogimusic;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Mycollectin extends Fragment {
    private View view;
    HomeActivity homeActivity;

    private List<Music> musicList = new ArrayList<>();
    private MusicAdapter musicAdapter;

    private TextView title;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.mycollection_layout, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        homeActivity = (HomeActivity) getActivity();  //过早初始化会空指针
        send_my_collection();
        title = (TextView) view.findViewById(R.id.mycollection_title);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.my_collection_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                musicList.clear();
                send_my_collection();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        ImageButton back = (ImageButton) view.findViewById(R.id.mycollection_home);
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
        initmusicadapter();
        musicListener();
    }

    //初始化音乐适配器
    public void initmusicadapter(){
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.my_collection);
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(layoutManager);
        musicAdapter = new MusicAdapter(musicList, view.getContext());
        recyclerView.setAdapter(musicAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(view.getContext(),DividerItemDecoration.VERTICAL)); //分割线
    }

    public void send_my_collection(){
        new Thread(new Runnable() { //耗时操作要开子线程
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder()
                            .add("userid", Global_Variable.thisuser.id) //请求参数
                            .build();
                    Request request = new Request.Builder()
                            .url(Global_Variable.ip + "NogiMusic/mycollection") //请求url
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String data = response.body().string();
                    Log.d("NMSL", data);
                    parsejsonmycollection(data); //解析服务端返回的值
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void parsejsonmycollection(String data) {
        Gson gson = new Gson();
        List<musicresult> resultsList = gson.fromJson(data, new TypeToken<List<musicresult>>(){}.getType());
        for (musicresult musicresult1 : resultsList){
            Music music = new Music(musicresult1.musicid, musicresult1.musicname, musicresult1.singer, musicresult1.musicurl, musicresult1.musicpic, "net");
            musicList.add(music);
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                musicAdapter.notifyDataSetChanged();
                title.setText("我的收藏");
            }
        });
    }

    public void musicListener(){
        musicAdapter.setmOnItemClickListener(new MusicAdapter.OnItemClickListener(){
            @Override
            public void onItemClick(View view, int position) {
                homeActivity.musicBinder.stop();
                Music music = musicList.get(position);
                //Toast.makeText(view.getContext(), "你点击了"+music.getMusic_url(), Toast.LENGTH_SHORT).show();
                if (!Global_Variable.musicplayQueue.isinclude(music.getMusic_id())){//如果没有才能加入，否则会造成重复
                    Global_Variable.musicplayQueue.queue.add(music); //加入播放队列
                }
                Global_Variable.musicplayQueue.i = Global_Variable.musicplayQueue.getindex(music.getMusic_id()); //i记录当前是播放队列中的第几个
                homeActivity.musicBinder.initmediaplayer(Global_Variable.musicplayQueue.i); //初始化
                homeActivity.musicBinder.play(); //播放
            }
        });
    }


}
