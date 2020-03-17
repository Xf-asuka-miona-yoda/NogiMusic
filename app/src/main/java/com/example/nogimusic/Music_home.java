package com.example.nogimusic;

import android.content.Context;
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
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Music_home extends Fragment implements OnBannerListener {
    private View view;

    private Banner banner;
    private ArrayList<String> list_path;
    private ArrayList<String> list_title;

    private List<home_icon> icList = new ArrayList<>();
    private List<Music> musicList = new ArrayList<>();
    private HomeicAdapter adapter = new HomeicAdapter(icList);
    private MusicAdapter musicAdapter;

    HomeActivity homeActivity;

    public AllSinger allSinger;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        icList.clear(); //清空 一定要清空，不然每次切换会重复加载数据
        sendrequest_mucihome();
        musicList.clear();
        view = inflater.inflate(R.layout.music_home, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        homeActivity = (HomeActivity) getActivity();  //过早初始化会空指针
        initBanner();

        //initfragments();
        initicondata();
        initiconadapter();

        initmusicadapter();
        setListener();
        musicListener();
    }

    public void initBanner(){ //初始化轮播图
        banner = (Banner)view.findViewById(R.id.banner);
        list_path = new ArrayList<>();
        //放标题的集合
        list_title = new ArrayList<>();

        list_path.add(Global_Variable.ip + "NogiMusic/轮播1.jpg");
        list_path.add(Global_Variable.ip + "NogiMusic/轮播2.jpg");
        list_path.add(Global_Variable.ip + "NogiMusic/轮播3.jpg");
        list_path.add(Global_Variable.ip + "NogiMusic/轮播4.jpg");
        list_title.add("朋友请听好");
        list_title.add("让我快乐");
        list_title.add("很久以后");
        list_title.add("文兆杰全新概念ep");
        //设置内置样式，共有六种可以点入方法内逐一体验使用。
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE);
        //设置图片加载器，图片加载器在下方
        banner.setImageLoader(new MyLoader());
        //设置图片网址或地址的集合
        banner.setImages(list_path);
        //设置轮播的动画效果，内含多种特效，可点入方法内查找后内逐一体验
        banner.setBannerAnimation(Transformer.Default);
        //设置轮播图的标题集合
        banner.setBannerTitles(list_title);
        //设置轮播间隔时间
        banner.setDelayTime(3000);
        //设置是否为自动轮播，默认是“是”。
        banner.isAutoPlay(true);
        //设置指示器的位置，小点点，左中右。
        banner.setIndicatorGravity(BannerConfig.CENTER)
                //以上内容都可写成链式布局，这是轮播图的监听。比较重要。方法在下面。
                .setOnBannerListener(this)
                //必须最后调用的方法，启动轮播图。
                .start();
    }

    @Override
    public void OnBannerClick(int position) {
        Log.d("tag", "你点了第"+position+"张轮播图");
    }

    //自定义的图片加载器
    private class MyLoader extends ImageLoader {
        @Override
        public void displayImage(Context context, Object path, ImageView imageView) {
            Glide.with(context).load((String) path).into(imageView);
        }
    }

    //初始化图标的适配器
    public void initiconadapter(){
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.home_ic);
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    //初始化音乐适配器
    public void initmusicadapter(){
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.music_home_random);
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(layoutManager);
        musicAdapter = new MusicAdapter(musicList, view.getContext());
        recyclerView.setAdapter(musicAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(view.getContext(),DividerItemDecoration.VERTICAL)); //分割线
    }

    //初始化图标数据
    public void initicondata(){
        home_icon singer = new home_icon("歌手", R.mipmap.singer);
        icList.add(singer);
        home_icon paihang = new home_icon("排行榜", R.mipmap.paihang);
        icList.add(paihang);
        home_icon fenlei = new home_icon("歌曲分类", R.mipmap.fenlei);
        icList.add(fenlei);
    }



    //初始化音乐数据
//    public void initMusicdata(){
////        Music qifenle = new Music("起风了", "吴青峰", Global_Variable.ip + "/NogiMusic/华语流行/起风了.mp3", "http://p1.music.126.net/aMVPsO00OqlVTS2yMH8RgA==/109951163785600029.jpg?param=177y177");
////        musicList.add(qifenle);
//        for (int i = 0; i < Global_Variable.musicList_home.size(); i++){
//            Music music = new Music(Global_Variable.musicList_home.get(i).getMusic_name(), Global_Variable.musicList_home.get(i).getMusic_singer(), Global_Variable.musicList_home.get(i).getMusic_url(), Global_Variable.musicList_home.get(i).getMusic_pic_url());
//            musicList.add(music);
//            Log.d("cao", music.getMusic_name());
//            Log.d("cao", music.getMusic_singer());
//            Log.d("cao", music.getMusic_url());
//        }
//
//    }

    //发送网络请求
    public void sendrequest_mucihome(){ //发送音乐请求哦
        new Thread(new Runnable() { //耗时操作要开子线程
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder() //请求参数
                            .add("musicnumber", String.valueOf(10)) //获取的音乐数量
                            .build();
                    Request request = new Request.Builder()
                            .url(Global_Variable.ip + "NogiMusic/musichome") //请求url
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
            Music music = new Music(musicresult1.musicid,musicresult1.musicname, musicresult1.singer, musicresult1.musicurl, musicresult1.musicpic, "net");
            musicList.add(music);
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                musicAdapter.notifyDataSetChanged();
            }
        });
    }

    public void setListener(){
        adapter.setmOnItemClickListener(new HomeicAdapter.OnItemClickListener() { //三个图标的事件监听
            @Override
            public void onItemClick(View view, int position) {

                home_icon ic = icList.get(position);
                Toast.makeText(view.getContext(), "你点击了"+ic.getIc_name(), Toast.LENGTH_SHORT).show();
                if (ic.getIc_name().equals("歌手")){
                    if (allSinger == null){
                        initfragments(1);
                    }
                    showfragment(allSinger);
                    //replacefragment(new AllSinger());
                }
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

    public void replacefragment(Fragment fragment){
        Global_Variable.fragmentManager = getFragmentManager();
        Global_Variable.fragmentTransaction = Global_Variable.fragmentManager.beginTransaction();
        Global_Variable.fragmentTransaction.replace(R.id.homepage,fragment);

        Global_Variable.fragmentTransaction.commit();
    }

    public void initfragments(int id){  //初始化三个fragment
        if (id == 1){
            allSinger = new AllSinger();
            addfragment(allSinger);
        }

    }

    public void addfragment(Fragment fragment){
        Global_Variable.fragmentManager = getFragmentManager();
        Global_Variable.fragmentTransaction = Global_Variable.fragmentManager.beginTransaction();
        Global_Variable.fragmentTransaction.add(R.id.homepage, fragment);
        Global_Variable.fragmentTransaction.commit();
    }

    public void showfragment(Fragment fragment){
        Global_Variable.fragmentManager = getFragmentManager();
        Global_Variable.fragmentTransaction = Global_Variable.fragmentManager.beginTransaction();
        List<Fragment> list = Global_Variable.fragmentManager.getFragments();
        for (int i = 0; i < list.size(); i++){
            Fragment f = list.get(i);
            if (f != null){
                Global_Variable.fragmentTransaction.hide(f);
            }
        }
        Global_Variable.fragmentTransaction.show(fragment);
        Global_Variable.fragmentTransaction.commit();
    }
}
