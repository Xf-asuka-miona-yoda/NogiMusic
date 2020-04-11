package com.example.nogimusic;


import org.litepal.crud.LitePalSupport;

public class Music extends LitePalSupport {
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

    public void setMusic_url(String music_url) {
        this.music_url = music_url;
    }

    public void setState(String state) {
        this.state = state;
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
