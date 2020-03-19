package com.example.nogimusic;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class Classification extends Fragment {
    private View view;
    HomeActivity homeActivity;
    private Classinfo classinfo;

    private class_item[] class_items = {new class_item("华语流行", R.mipmap.huayu), new class_item("古风", R.mipmap.gufeng),
                                        new class_item("日语流行", R.mipmap.rixi),  new class_item("ACG", R.mipmap.acg),
                                        new class_item("民谣", R.mipmap.minyao),    new class_item("RAP", R.mipmap.rap),
                                        new class_item("怀旧", R.mipmap.huaijiu),   new class_item("儿歌", R.mipmap.erge)};

    private List<class_item> class_itemList = new ArrayList<>();
    private ClassAdapter classAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.classification_layout, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        homeActivity = (HomeActivity) getActivity();  //过早初始化会空指针
        ImageButton back = (ImageButton) view.findViewById(R.id.back_class);
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
        initdata();
        initadepter();
        setlisten();
    }

    public void initdata(){
        class_itemList.clear();
        for (int i = 0; i < class_items.length; i++){
            class_itemList.add(class_items[i]);
        }
    }

    public void initadepter(){
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recy_class);
        GridLayoutManager layoutManager = new GridLayoutManager(view.getContext(), 2);
        recyclerView.setLayoutManager(layoutManager);
        classAdapter = new ClassAdapter(class_itemList);
        recyclerView.setAdapter(classAdapter);
    }

    public void setlisten(){
        classAdapter.setmOnItemClickListener(new ClassAdapter.OnItemClickListener(){
            @Override
            public void onItemClick(View view, int position) {
                class_item classItem = class_itemList.get(position);
                //Toast.makeText(view.getContext(), "你点击了" + classItem.getName(), Toast.LENGTH_SHORT).show();
                if (classinfo == null){
                    initfragments();
                }
                classinfo.setinfo(classItem.getName());
                showfragment(classinfo);
            }
        });
    }

    public void initfragments(){  //初始化三个fragment
        classinfo = new Classinfo();
        addfragment(classinfo);
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
