package com.example.nogimusic;

public class Singer {
    private String singerid;
    private String singername;
    private String singerpicurl;
    private String singer_in;

    public Singer(String id, String name, String pic, String ic){
        this.singerid = id;
        this.singername = name;
        this.singerpicurl = pic;
        this.singer_in = ic;
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

    public String getSinger_in() {
        return singer_in;
    }
}
