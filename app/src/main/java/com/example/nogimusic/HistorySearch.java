package com.example.nogimusic;


import org.litepal.crud.LitePalSupport;


public class HistorySearch extends LitePalSupport {
    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
