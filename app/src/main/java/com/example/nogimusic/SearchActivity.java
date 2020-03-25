package com.example.nogimusic;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private List<HistorySearch> searchList = new ArrayList<>();
    private SearchAdapter searchAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initdata();
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
    }

    public void initdata(){
        for (int i = 0; i < 5; i++){
            HistorySearch search = new HistorySearch();
            search.setContent("陈奕迅陈奕迅陈奕");
            searchList.add(search);
        }
    }
}
