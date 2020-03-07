package com.example.nogimusic;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class MusicService extends Service {
    private MusicBinder mbinder = new MusicBinder();
    class MusicBinder extends Binder {
        MusicQueue musicQueue = new MusicQueue();
        public MediaPlayer mediaPlayer = new MediaPlayer();
        public String musicurl = "";
        public void initmediaplayer(int i){ //用当前播放的初始化播放器
            try {
                musicurl = MusicQueue.queue.get(i).getMusic_url();
                mediaPlayer.setDataSource(musicurl);
                mediaPlayer.prepare();
                mediaPlayer.setLooping(true); //循环播放
            } catch (Exception e){
                e.printStackTrace();
            }
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
    }

    public MusicService() {
    }





    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        Log.d("CAO","NMSL");
        return mbinder;
    }
}
