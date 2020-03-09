package com.example.nogimusic;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;

public class PlayerActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView musicLength,musicCur;
    private SeekBar seekBar;
    private Timer timer;
    private boolean isSeekBarChanging;//互斥变量，防止进度条与定时器冲突。
    private int currentPosition;//当前音乐播放的进度
    SimpleDateFormat format;

    private TextView musicname,singer;
    private ImageView musicpic;
    private Button playorpause,shoucang,before,next,method,pinglun;

    private MusicService.MusicBinder musicBinder;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {  //在回调方法中完成ui刷新
            musicBinder = (MusicService.MusicBinder) service;
            musicBinder.initmediaplayer(Global_Variable.musicplayQueue.i);
            seekBar.setMax(musicBinder.mediaPlayer.getDuration());
            musicLength.setText(format.format(musicBinder.mediaPlayer.getDuration())+"");
            musicCur.setText("00:00");
            timer = new Timer();
            timer.schedule(new TimerTask() {

                Runnable updateUI = new Runnable() {
                    @Override
                    public void run() {
                        musicCur.setText(format.format(musicBinder.mediaPlayer.getCurrentPosition())+"");
                        seekBar.setMax(musicBinder.mediaPlayer.getDuration());
                        musicLength.setText(format.format(musicBinder.mediaPlayer.getDuration())+"");
                        musicname.setText(Global_Variable.musicplayQueue.queue.get(Global_Variable.musicplayQueue.i).getMusic_name());
                        singer.setText(Global_Variable.musicplayQueue.queue.get(Global_Variable.musicplayQueue.i).getMusic_singer());
                        Glide.with(getApplicationContext()).load(Global_Variable.musicplayQueue.queue.get(Global_Variable.musicplayQueue.i).getMusic_pic_url()).into(musicpic);

                    }
                };
                @Override
                public void run() {
                    if(!isSeekBarChanging){
                        seekBar.setProgress(musicBinder.mediaPlayer.getCurrentPosition());
                        runOnUiThread(updateUI);
                    }

                }
            },0,50);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_layout);
        format = new SimpleDateFormat("mm:ss");

        Intent intent = new Intent(this, MusicService.class);
        startService(intent);
        bindService(intent, connection, BIND_AUTO_CREATE); //绑定服务

        initview();

    }

    private void initview() {
        playorpause = (Button) findViewById(R.id.playorpause);
        playorpause.setOnClickListener(this);
        shoucang = (Button) findViewById(R.id.player_shouchang);
        shoucang.setOnClickListener(this);
        before = (Button) findViewById(R.id.before);
        before.setOnClickListener(this);
        next = (Button) findViewById(R.id.next);
        next.setOnClickListener(this);
        method = (Button) findViewById(R.id.player_method);
        method.setOnClickListener(this);
        pinglun = (Button) findViewById(R.id.player_pinglun);
        pinglun.setOnClickListener(this);

        musicLength = (TextView) findViewById(R.id.music_length);
        musicCur = (TextView) findViewById(R.id.music_cur);
        musicname = (TextView) findViewById(R.id.player_music_name);
        //musicname.setText(Global_Variable.musicplayQueue.queue.get(Global_Variable.musicplayQueue.i).getMusic_name());
        singer = (TextView) findViewById(R.id.player_music_singer);
        //singer.setText(Global_Variable.musicplayQueue.queue.get(Global_Variable.musicplayQueue.i).getMusic_singer());
        musicpic = (ImageView) findViewById(R.id.player_music_pic);
        //Glide.with(this).load(Global_Variable.musicplayQueue.queue.get(Global_Variable.musicplayQueue.i).getMusic_pic_url()).into(musicpic);

        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new MySeekBar());
    }

    //进度条事件处理
    public class MySeekBar implements SeekBar.OnSeekBarChangeListener {

        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        }

        /*滚动时,应当暂停后台定时器*/
        public void onStartTrackingTouch(SeekBar seekBar) {
            isSeekBarChanging = true;
        }
        /*滑动结束后，重新设置值*/
        public void onStopTrackingTouch(SeekBar seekBar) {
            isSeekBarChanging = false;
            musicBinder.mediaPlayer.seekTo(seekBar.getProgress());
        }
    }

    @Override
    public void onClick(View v) {
        if (musicBinder.musicurl == ""){ //如果当前播放器为空或者与要播放的音乐不一直，初始化播放器
            musicBinder.initmediaplayer(Global_Variable.musicplayQueue.i);
            seekBar.setMax(musicBinder.mediaPlayer.getDuration());
            musicLength.setText(format.format(musicBinder.mediaPlayer.getDuration())+"");
            musicCur.setText("00:00");
        }
        switch (v.getId()){
            case R.id.playorpause:
                if (musicBinder.mediaPlayer.isPlaying()){
                    musicBinder.pause();
                    playorpause.setText("播放");

                }else {
                    musicBinder.play();
                    playorpause.setText("暂停");
                }
                //监听播放时回调函数
                break;
            case R.id.player_method:
                if (musicBinder.mediaPlayer.isLooping()){
                    musicBinder.mediaPlayer.setLooping(false);
                    method.setText("单曲循环");
                }else {
                    musicBinder.mediaPlayer.setLooping(true);
                    method.setText("顺序播放");
                }
                break;
            case R.id.player_shouchang:
                Toast.makeText(PlayerActivity.this,"收藏功能开发中",Toast.LENGTH_SHORT).show();
                break;
            case R.id.before:
                musicBinder.before();
                musicname.setText(Global_Variable.musicplayQueue.queue.get(Global_Variable.musicplayQueue.i).getMusic_name());
                singer.setText(Global_Variable.musicplayQueue.queue.get(Global_Variable.musicplayQueue.i).getMusic_singer());
                Glide.with(this).load(Global_Variable.musicplayQueue.queue.get(Global_Variable.musicplayQueue.i).getMusic_pic_url()).into(musicpic);
                break;
            case R.id.next:   //上一首同理即可，后续要增加一下判断是否为最后一个或第一个
                musicBinder.next();
                musicname.setText(Global_Variable.musicplayQueue.queue.get(Global_Variable.musicplayQueue.i).getMusic_name());
                singer.setText(Global_Variable.musicplayQueue.queue.get(Global_Variable.musicplayQueue.i).getMusic_singer());
                Glide.with(this).load(Global_Variable.musicplayQueue.queue.get(Global_Variable.musicplayQueue.i).getMusic_pic_url()).into(musicpic);
                break;

            case R.id.player_pinglun:
                Toast.makeText(PlayerActivity.this,Global_Variable.musicplayQueue.queue.get(Global_Variable.musicplayQueue.i).getMusic_id() + Global_Variable.thisuser.id,Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
    }
}
