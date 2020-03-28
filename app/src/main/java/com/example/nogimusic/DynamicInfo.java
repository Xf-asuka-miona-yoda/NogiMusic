package com.example.nogimusic;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class DynamicInfo extends Fragment {
    private View view;

    private TextView info_name;
    private TextView info_time;
    private TextView info_content;
    private ImageView info_user;
    private EditText dyinfo_input;
    private Button dyinfo_commit;

    private String username;
    private String usertime;
    private String dycontent;
    private String userid;
    private String dyid;
    private String dyinfo_comment;

    private List<Comment> commentList = new ArrayList<>();
    private CommentsAdapter commentsAdapter;

    private int year,month,day,hour,minute,second;


    HomeActivity homeActivity;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    info_name.setText(username);
                    info_time.setText(usertime);
                    info_content.setText(dycontent);
                    //musicList.clear();
                    //sendrequest_singer_music();
                    break;
                default:
                    break;
            }
        }
    };

    public void setinfo(String userid,String id,String name, String time, String content){
        this.userid = userid;
        this.dyid = id;
        this.dycontent = content;
        this.username = name;
        this.usertime = time;
        Log.d("test","更新了" + this.dyid);
        Message message = new Message();
        message.what = 1;
        handler.sendMessage(message);
    }

    public void initadapter(){ //初始化适配器
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.dycommnet_show);
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(layoutManager);
        commentsAdapter = new CommentsAdapter(commentList);
        recyclerView.setAdapter(commentsAdapter);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dynamicinfo_layout, container, false);
        initview();
        initadapter();
        return view;
    }

    public void initview(){
        info_content = (TextView) view.findViewById(R.id.dyinfo_content);
        info_name = (TextView) view.findViewById(R.id.dyinfo_username);
        info_time = (TextView) view.findViewById(R.id.dyinfo_time);
        info_user = (ImageView) view.findViewById(R.id.dyinfo_user_img);
        dyinfo_input = (EditText) view.findViewById(R.id.dyinfo_comment);
        dyinfo_commit = (Button) view.findViewById(R.id.dyinfo_comment_commit);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        homeActivity = (HomeActivity) getActivity();  //过早初始化会空指针
        ImageButton back = (ImageButton) view.findViewById(R.id.backto_socia);
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
                Global_Variable.fragmentTransaction.show(homeActivity.social_contact_fragment);
                Global_Variable.fragmentTransaction.commit();
            }
        });

        info_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(view.getContext(),"点击了" + userid,Toast.LENGTH_SHORT).show();
            }
        });

        dyinfo_commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dyinfo_comment = dyinfo_input.getText().toString();

                Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+08:00")); //获取东八区时间
                year = cal.get(Calendar.YEAR);
                month = cal.get(Calendar.MONTH);
                day = cal.get(Calendar.DATE);

                hour = cal.get(Calendar.HOUR_OF_DAY);//24小时制度
                minute = cal.get(Calendar.MINUTE);
                second = cal.get(Calendar.SECOND);

                if (TextUtils.isEmpty(dyinfo_comment)){
                    Toast.makeText(view.getContext(), "请输入评论", Toast.LENGTH_SHORT).show();
                }else {
                    dyinfo_input.setText("");
                    Comment comment = new Comment(Global_Variable.thisuser.id, Global_Variable.thisuser.username, dyinfo_comment, year, month + 1, day, hour, minute, second);
                    commentList.add(comment);
                    commentsAdapter.notifyDataSetChanged(); //更新界面
                    //sendmycomments(); //发送至服务端
                }
            }
        });

        commentsAdapter.setmOnItemClickListener(new CommentsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Comment comment = commentList.get(position);
                Toast.makeText(view.getContext(), "点击了" + comment.getUserid(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
