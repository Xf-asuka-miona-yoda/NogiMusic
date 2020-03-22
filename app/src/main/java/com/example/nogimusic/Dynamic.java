package com.example.nogimusic;

public class Dynamic {
    private String username; //用户昵称
    private String content; //内容
    private String year;
    private String month;
    private String date;
    private String hour;
    private String minute;
    private String second;
    private String zhuanfa; //转发数
    private String pinglun; //评论数
    private String dianzan; //点赞数

    public Dynamic(String name, String input_content, int year, int month, int date, int hour, int minute, int second, String zhuan, String ping, String zan){
        this.username = name;
        this.content = input_content;
        this.year = String.valueOf(year);
        this.month = String.valueOf(month);
        this.date = String.valueOf(date);
        this.hour = String.valueOf(hour);
        this.minute = String.valueOf(minute);
        this.second = String.valueOf(second);
        this.pinglun = ping;
        this.zhuanfa = zhuan;
        this.dianzan = zan;
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

    public String getDianzan() {
        return dianzan;
    }

    public String getPinglun() {
        return pinglun;
    }

    public String getZhuanfa() {
        return zhuanfa;
    }
}
