package com.example.nogimusic;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class Music_home extends Fragment implements OnBannerListener {
    private View view;

    private Banner banner;
    private ArrayList<String> list_path;
    private ArrayList<String> list_title;

    private List<home_icon> icList = new ArrayList<>();
    private HomeicAdapter adapter = new HomeicAdapter(icList);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        icList.clear(); //清空 一定要清空，不然每次切换会重复加载数据
        view = inflater.inflate(R.layout.music_home, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initBanner();
        initicondata();
        initiconadapter();
        setListener();
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
        list_title.add("好好学习");
        list_title.add("天天向上");
        list_title.add("热爱劳动");
        list_title.add("不搞对象");
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

    //初始化图标数据
    public void initicondata(){
        home_icon singer = new home_icon("歌手", R.mipmap.singer);
        icList.add(singer);
        home_icon paihang = new home_icon("排行榜", R.mipmap.paihang);
        icList.add(paihang);
        home_icon fenlei = new home_icon("歌曲分类", R.mipmap.fenlei);
        icList.add(fenlei);
    }

    public void setListener(){
        adapter.setmOnItemClickListener(new HomeicAdapter.OnItemClickListener() { //三个图标的事件监听
            @Override
            public void onItemClick(View view, int position) {
                home_icon ic = icList.get(position);
                Toast.makeText(view.getContext(), "你点击了"+ic.getIc_name(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
