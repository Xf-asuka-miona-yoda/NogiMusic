package com.example.nogimusic;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import java.io.File;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MusicService extends Service {
    private MusicBinder mbinder = new MusicBinder();
    class MusicBinder extends Binder {
        //MusicQueue musicQueue = new MusicQueue();
        public MediaPlayer mediaPlayer = new MediaPlayer();
        public String musicurl = "";
        public void initmediaplayer(int i){ //用当前播放的初始化播放器
            Log.d("cao1", Global_Variable.musicplayQueue.queue.get(i).getMusic_url());
            try {
                if (Global_Variable.musicplayQueue.queue.get(i).getState().equals("net")){
                    musicurl = Global_Variable.ip + Global_Variable.musicplayQueue.queue.get(i).getMusic_url();
                }else if (Global_Variable.musicplayQueue.queue.get(i).getState().equals("local")){ //本地音乐
                    if (Global_Variable.musicplayQueue.queue.get(i).getMusic_id().equals("24")){
                        musicurl = Global_Variable.musicplayQueue.queue.get(i).getMusic_url() + ".flac";
                    }else {
                        musicurl = Global_Variable.musicplayQueue.queue.get(i).getMusic_url() + ".mp3";
                    }
//
                }
                mediaPlayer.setDataSource(musicurl);
                mediaPlayer.prepare();
                //mediaPlayer.setLooping(true); //循环播放
            } catch (Exception e){
                e.printStackTrace();
            }

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    history();
                    next();
                }
            });
        }

        public void play(){
            if (!mediaPlayer.isPlaying()){
                mediaPlayer.start();
            }
        }

        public void pause(){
            if (mediaPlayer.isPlaying()){
                mediaPlayer.pause();
            }
        }

        public void stop(){
            mediaPlayer.reset();
        }

        public void next(){

            if (Global_Variable.musicplayQueue.i + 1 < Global_Variable.musicplayQueue.queue.size()){
                Global_Variable.musicplayQueue.i = Global_Variable.musicplayQueue.i + 1;
                stop();
                initmediaplayer(Global_Variable.musicplayQueue.i);
                play();
            } else {
                Global_Variable.musicplayQueue.i = 0;
                stop();
                initmediaplayer(Global_Variable.musicplayQueue.i);
                play();
            }
        }

        public void before(){
            if (Global_Variable.musicplayQueue.i == 0){
                Global_Variable.musicplayQueue.i = Global_Variable.musicplayQueue.queue.size() - 1;
            }else {
                Global_Variable.musicplayQueue.i = Global_Variable.musicplayQueue.i - 1;
            }
            stop();
            initmediaplayer(Global_Variable.musicplayQueue.i);
            play();
        }

    }

    public MusicService() {
    }


    public void history(){
        new Thread(new Runnable() { //耗时操作要开子线程
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder() //请求参数
                            .add("musicid", Global_Variable.musicplayQueue.queue.get(Global_Variable.musicplayQueue.i).getMusic_id())
                            .add("userid", Global_Variable.thisuser.id)
                            .build();
                    Request request = new Request.Builder()
                            .url(Global_Variable.ip + "NogiMusic/history") //请求url
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String data = response.body().string();
                    Log.d("NMSL", data);

                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }




    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        Log.d("CAO","NMSL");
        return mbinder;
    }
}
