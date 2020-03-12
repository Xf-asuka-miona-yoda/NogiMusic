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
        //MusicQueue musicQueue = new MusicQueue();
        public MediaPlayer mediaPlayer = new MediaPlayer();
        public String musicurl = "";
        public void initmediaplayer(int i){ //用当前播放的初始化播放器
            Log.d("cao1", Global_Variable.musicplayQueue.queue.get(i).getMusic_url());
            try {
                if (Global_Variable.musicplayQueue.queue.get(i).getState().equals("net")){
                    musicurl = Global_Variable.ip + Global_Variable.musicplayQueue.queue.get(i).getMusic_url();
                }else if (Global_Variable.musicplayQueue.queue.get(i).getState().equals("local")){
                    musicurl = Global_Variable.musicplayQueue.queue.get(i).getMusic_url();
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
            stop();
            if (Global_Variable.musicplayQueue.i + 1 < Global_Variable.musicplayQueue.queue.size()){
                Global_Variable.musicplayQueue.i = Global_Variable.musicplayQueue.i + 1;
                initmediaplayer(Global_Variable.musicplayQueue.i);
                play();
            } else {
                Global_Variable.musicplayQueue.i = 0;
                initmediaplayer(Global_Variable.musicplayQueue.i);
                play();
            }
        }

        public void before(){
            stop();
            if (Global_Variable.musicplayQueue.i == 0){
                Global_Variable.musicplayQueue.i = Global_Variable.musicplayQueue.queue.size() - 1;
            }else {
                Global_Variable.musicplayQueue.i = Global_Variable.musicplayQueue.i - 1;
            }
            initmediaplayer(Global_Variable.musicplayQueue.i);
            play();
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
