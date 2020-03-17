package com.example.nogimusic;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SIngerinfo extends Fragment {
    public String singerid;
    private String singername;
    private String singeric;
    private String singerpic;

    private View view;

    private TextView singer_name;
    private TextView singer_introduce;
    private ImageView singer_pic;

    private List<Music> musicList = new ArrayList<>();
    private MusicAdapter musicAdapter;

    HomeActivity homeActivity;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    singer_name.setText(singername);
                    singer_introduce.setText(singeric);
                    Glide.with(getActivity()).load(singerpic).into(singer_pic);
                    musicList.clear();
                    sendrequest_singer_music();
                    break;
                default:
                    break;
            }
        }
    };

    public void setinfo(String id, String name, String ic, String pic){
        this.singerid = id;
        this.singername = name;
        this.singerpic = pic;
        this.singeric = ic;
        Log.d("test","更新了" + this.singername);
        Message message = new Message();
        message.what = 1;
        handler.sendMessage(message);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.singinfo_layout, container, false);
        initmusicadapter();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        homeActivity = (HomeActivity) getActivity();  //过早初始化会空指针
        initview();
        ImageButton back = (ImageButton) view.findViewById(R.id.backto_allsinger);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Global_Variable.fragmentManager = getFragmentManager();
                Global_Variable.fragmentTransaction = Global_Variable.fragmentManager.beginTransaction();
                List<Fragment> list = Global_Variable.fragmentManager.getFragments();
                for (int i = 0; i < list.size(); i++){
                    Fragment f = list.get(i);
                    if (f != null){
                        Global_Variable.fragmentTransaction.hide(f);
                    }
                }
                Global_Variable.fragmentTransaction.show(homeActivity.music_home_fragment.allSinger);
                Global_Variable.fragmentTransaction.commit();
            }
        });

        initmusicadapter();
        //sendrequest_singer_music();
        musicListener();
    }




    public void initview(){
        singer_name = (TextView) view.findViewById(R.id.singer_info_singername);
        singer_introduce = (TextView) view.findViewById(R.id.singer_info_singerin);
        singer_pic = (ImageView) view.findViewById(R.id.singer_info_singerpic);
    }

    //初始化音乐适配器
    public void initmusicadapter(){
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.singer_music);
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(layoutManager);
        musicAdapter = new MusicAdapter(musicList, view.getContext());
        recyclerView.setAdapter(musicAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(view.getContext(),DividerItemDecoration.VERTICAL)); //分割线
    }

    public void sendrequest_singer_music(){ //发送音乐请求哦
        new Thread(new Runnable() { //耗时操作要开子线程
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder() //请求参数
                            .add("singerid", singerid) //获取的音乐歌手id
                            .add("singername", singername)
                            .build();
                    Request request = new Request.Builder()
                            .url(Global_Variable.ip + "NogiMusic/singermusic") //请求url
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String data = response.body().string();
                    Log.d("NMSL", data);
                    parsejson_musichome(data); //解析服务端返回的值
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void parsejson_musichome(String jsondata) { //使用GSON解析服务端返回的json数据
        Gson gson = new Gson();
        List<musicresult> resultsList = gson.fromJson(jsondata, new TypeToken<List<musicresult>>(){}.getType());
        for (musicresult musicresult1 : resultsList){
            Music music = new Music(musicresult1.musicid, musicresult1.musicname, musicresult1.singer, musicresult1.musicurl, musicresult1.musicpic, "net");
            musicList.add(music);
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                musicAdapter.notifyDataSetChanged();
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
                if (!Global_Variable.musicplayQueue.isinclude(music.getMusic_name())){//如果没有才能加入，否则会造成重复
                    Global_Variable.musicplayQueue.queue.add(music); //加入播放队列
                }
                Global_Variable.musicplayQueue.i = Global_Variable.musicplayQueue.getindex(music.getMusic_name()); //i记录当前是播放队列中的第几个
                Log.d("cao", String.valueOf(Global_Variable.musicplayQueue.i));
                Log.d("cao", Global_Variable.musicplayQueue.queue.get(Global_Variable.musicplayQueue.i).getMusic_url());
                homeActivity.musicBinder.initmediaplayer(Global_Variable.musicplayQueue.i); //初始化
                homeActivity.musicBinder.play(); //播放
            }
        });
    }
}
