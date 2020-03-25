package com.example.nogimusic;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;


import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private List<HistorySearch> searchList = new ArrayList<>();
    private SearchAdapter searchAdapter;

    private EditText search;
    private String input_search;
    private Button commit_search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        LitePal.getDatabase();
        initdata();
        initadapter();

        search = (EditText) findViewById(R.id.search_input);
        commit_search = (Button) findViewById(R.id.send_search);
        commit_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                input_search = search.getText().toString();
                localhistory();
            }
        });
//        HistorySearch historySearch = new HistorySearch();
//        historySearch.setContent("十年");
//        historySearch.save();
    }

    public void initdata(){
        searchList = LitePal.limit(5).find(HistorySearch.class);
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
    }

    public void localhistory(){
        HistorySearch historySearch = new HistorySearch();
        historySearch.setContent(input_search);
        searchList.add(historySearch);
        searchAdapter.notifyDataSetChanged();
        historySearch.save(); //保存至数据库
        search.setText("");
    }
}
