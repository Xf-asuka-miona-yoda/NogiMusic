package com.example.nogimusic;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class AllSinger extends Fragment {

    private View view;
    HomeActivity homeActivity;

    private List<Singer> singerList = new ArrayList<>();
    private SingerAdapter singerAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_all_singer, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        homeActivity = (HomeActivity) getActivity();  //过早初始化会空指针
        initsingers();
        initSingerAdapter();
        setListener();
    }

    public void initsingers(){
        for (int i = 0; i < 20; i++){
            Singer singer = new Singer(String.valueOf(i), "乃木坂46", "http://y.gtimg.cn/music/photo_new/T001R300x300M000003caGxv3AblUU.jpg?max_age=2592000");
            Singer singer1 = new Singer(String.valueOf(i+1), "AKB48", "http://y.gtimg.cn/music/photo_new/T001R300x300M000003caGxv3AblUU.jpg?max_age=2592000");
            singerList.add(singer);
            singerList.add(singer1);
        }
    }

    public void initSingerAdapter(){
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.allsinger);
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(layoutManager);
        singerAdapter = new SingerAdapter(singerList, view.getContext());
        recyclerView.setAdapter(singerAdapter);
    }

    public void setListener(){
        singerAdapter.setmOnItemClickListener(new SingerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Singer singer = singerList.get(position);
                Toast.makeText(view.getContext(), "你点击了" + singer.getSingerid() + singer.getSingername(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
