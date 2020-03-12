package com.example.nogimusic;

public class Music {
    private String music_id;
    private String music_name;
    private String music_singer;
    private String music_url;
    private String music_pic_url;
    private String state;

    public Music(String id, String name, String singer, String url, String pic, String state1){
        this.music_id = id;
        this.music_name = name;
        this.music_singer = singer;
        this.music_pic_url = pic;
        this.music_url = url;
        this.state = state1;
    }

    public String getMusic_name() {
        return music_name;
    }

    public String getMusic_pic_url() {
        return music_pic_url;
    }

    public String getMusic_singer() {
        return music_singer;
    }

    public String getMusic_url() {
        return music_url;
    }

    public String getMusic_id() {
        return music_id;
    }

    public String getState() {
        return state;
    }
}
