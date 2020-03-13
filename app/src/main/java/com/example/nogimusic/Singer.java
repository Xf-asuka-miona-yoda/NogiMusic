package com.example.nogimusic;

public class Singer {
    private String singerid;
    private String singername;
    private String singerpicurl;

    public Singer(String id, String name, String pic){
        this.singerid = id;
        this.singername = name;
        this.singerpicurl = pic;
    }

    public String getSingerid() {
        return singerid;
    }

    public String getSingername() {
        return singername;
    }

    public String getSingerpicurl() {
        return singerpicurl;
    }
}
