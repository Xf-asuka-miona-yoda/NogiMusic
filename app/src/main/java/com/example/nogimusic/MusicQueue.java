package com.example.nogimusic;

import java.util.Vector;

public class MusicQueue {
    public static Vector<Music> queue = new Vector<Music>();
    public static int i = 0; //记录当前播放的下标

    public boolean isinclude(String name){ //如果没有才能加入，否则会造成重复
        for(Music q : queue){
            if (q.getMusic_name().equals(name) ){
                return true;
            }
        }
        return false;
    }


    public int getindex(String name){
        Music test = new Music(null,null,null,null);
        for(int i = 0; i<queue.size(); i++){
            test = queue.get(i);
            if (test.getMusic_name().equals(name) ){
                return i;
            }
        }
        return -1; //播放队列中无
    }
}
