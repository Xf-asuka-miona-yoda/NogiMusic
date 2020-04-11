package com.example.nogimusic;

public interface DownloadListener {
    void onProcess(int process);
    void onSuccess();
    void onFailed();
    void onPaused();
    void onCanceled();
}
