package com.example.nogimusic;

public class class_item {
    private String name;
    private int imgid;

    public class_item(String classname, int id){
        this.imgid = id;
        this.name = classname;
    }

    public int getImgid() {
        return imgid;
    }

    public String getName() {
        return name;
    }
}
