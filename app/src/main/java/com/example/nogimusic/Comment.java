package com.example.nogimusic;

public class Comment {
    private String id;
    private String userid;
    private String username; //用户昵称
    private String content; //内容
    private String year;
    private String month;
    private String date;
    private String hour;
    private String minute;
    private String second;

    public Comment(String id,String name, String input_content, int year, int month, int date, int hour, int minute, int second){
        this.userid = id;
        this.username = name;
        this.content = input_content;
        this.year = String.valueOf(year);
        this.month = String.valueOf(month);
        this.date = String.valueOf(date);
        this.hour = String.valueOf(hour);
        this.minute = String.valueOf(minute);
        this.second = String.valueOf(second);
    }


    public String getId() {
        return id;
    }

    public String getUserid() {
        return userid;
    }

    public String getContent() {
        return content;
    }

    public String getUsername() {
        return username;
    }

    public String gettime(){
        String time = this.year + "-" + this.month + "-" + this.date + "  " + this.hour + ":" + this.minute + ":" + this.second;
        return time;
    }
}
