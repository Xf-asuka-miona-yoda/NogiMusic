package com.example.nogimusic;

import java.util.Vector;

public class MusicQueue {
    public Vector<Music> queue = new Vector<Music>();
    public int i = 0; //记录当前播放的下标

    public boolean isinclude(String id){ //如果没有才能加入，否则会造成重复
        for(Music q : queue){
            if (q.getMusic_id().equals(id) ){
                return true;
            }
        }
        return false;
    }


    public int getindex(String id){
        Music test = new Music(null,null,null,null,null,null);
        for(int i = 0; i<queue.size(); i++){
            test = queue.get(i);
            if (test.getMusic_id().equals(id) ){
                return i;
            }
        }
        return -1; //播放队列中无
    }
}
