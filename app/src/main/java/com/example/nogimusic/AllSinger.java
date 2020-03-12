package com.example.nogimusic;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.List;


public class AllSinger extends Fragment {

    private View view;
    HomeActivity homeActivity;

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
        Button button = (Button) view.findViewById(R.id.singers_back);
        button.setOnClickListener(new View.OnClickListener() {
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
                Global_Variable.fragmentTransaction.show(homeActivity.music_home_fragment);
                Global_Variable.fragmentTransaction.commit();
            }
        });
    }
}
