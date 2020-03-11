package com.example.nogimusic;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.List;

public class Global_Variable {
    public static String ip = "http://10.0.2.2:8080/";  //服务端ip地址
    public static User thisuser = new User();  //登录的用户

    public static MusicQueue musicplayQueue = new MusicQueue();   //播放队列

    public static FragmentManager fragmentManager;
    public static FragmentTransaction fragmentTransaction;
}
