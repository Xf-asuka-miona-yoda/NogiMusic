package com.example.nogimusic;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Environment;
import android.os.IBinder;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PlayerActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView musicLength,musicCur;
    private SeekBar seekBar;
    private Timer timer;
    private boolean isSeekBarChanging;//互斥变量，防止进度条与定时器冲突。
    private int currentPosition;//当前音乐播放的进度
    SimpleDateFormat format;

    private TextView musicname,singer;
    private ImageView musicpic;
    private ImageButton shoucang,pinglun,download;
    private ImageButton playorpause,before,next,method;

    private DownloadService.DownloadBinder downloadBinder;
    private ServiceConnection connectiondownload = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            downloadBinder = (DownloadService.DownloadBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private MusicService.MusicBinder musicBinder;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {  //在回调方法中完成ui刷新
            musicBinder = (MusicService.MusicBinder) service;
            musicBinder.initmediaplayer(Global_Variable.musicplayQueue.i);
            seekBar.setMax(musicBinder.mediaPlayer.getDuration());
            musicLength.setText(format.format(musicBinder.mediaPlayer.getDuration())+"");
            musicname.setText(Global_Variable.musicplayQueue.queue.get(Global_Variable.musicplayQueue.i).getMusic_name());
            singer.setText(Global_Variable.musicplayQueue.queue.get(Global_Variable.musicplayQueue.i).getMusic_singer());
            Glide.with(getApplicationContext()).load(Global_Variable.musicplayQueue.queue.get(Global_Variable.musicplayQueue.i).getMusic_pic_url()).into(musicpic);
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
            },0,200);
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

        Intent intentdownload = new Intent(this, DownloadService.class);
        startService(intentdownload);
        bindService(intentdownload, connectiondownload, BIND_AUTO_CREATE); //绑定服务

        initview();

    }

    private void initview() {
        playorpause = (ImageButton) findViewById(R.id.playorpause);
        playorpause.setOnClickListener(this);
        shoucang = (ImageButton) findViewById(R.id.player_shouchang);
        shoucang.setOnClickListener(this);
        before = (ImageButton) findViewById(R.id.before);
        before.setOnClickListener(this);
        next = (ImageButton) findViewById(R.id.next);
        next.setOnClickListener(this);
        method = (ImageButton) findViewById(R.id.player_method);
        method.setOnClickListener(this);
        pinglun = (ImageButton) findViewById(R.id.player_pinglun);
        pinglun.setOnClickListener(this);
        download = (ImageButton) findViewById(R.id.player_download);
        download.setOnClickListener(this);

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
                    playorpause.setImageResource(R.mipmap.play);

                }else {
                    musicBinder.play();
                    playorpause.setImageResource(R.mipmap.pause);
                }
                //监听播放时回调函数
                break;
            case R.id.player_method:
                if (musicBinder.mediaPlayer.isLooping()){
                    musicBinder.mediaPlayer.setLooping(false);
                    Toast.makeText(PlayerActivity.this, "已切换到顺序循环", Toast.LENGTH_SHORT).show();
                    method.setImageResource(R.mipmap.shunxu);
                }else {
                    musicBinder.mediaPlayer.setLooping(true);
                    Toast.makeText(PlayerActivity.this, "已切换到单曲循环", Toast.LENGTH_SHORT).show();
                    method.setImageResource(R.mipmap.danqu);
                }
                break;
            case R.id.player_shouchang:
                //Toast.makeText(PlayerActivity.this,"收藏功能开发中",Toast.LENGTH_SHORT).show();
                sendcollection();
                break;
            case R.id.player_download:
                if( Global_Variable.musicplayQueue.queue.get(Global_Variable.musicplayQueue.i).getState().equals("local")){
                    Toast.makeText(PlayerActivity.this, "已经下载过了", Toast.LENGTH_SHORT).show();
                }else {
                    String fileName = Global_Variable.musicplayQueue.queue.get(Global_Variable.musicplayQueue.i).getMusic_url().substring(Global_Variable.musicplayQueue.queue.get(Global_Variable.musicplayQueue.i).getMusic_url().lastIndexOf("/"));
                    String directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
                    File file = new File(directory + fileName );
                    if (file.exists()){
                        Toast.makeText(PlayerActivity.this, "已经下载过了", Toast.LENGTH_SHORT).show();
                    } else {
                        //Toast.makeText(PlayerActivity.this,"下载功能开发中",Toast.LENGTH_SHORT).show();
                        Music music = Global_Variable.musicplayQueue.queue.get(Global_Variable.musicplayQueue.i);
                        downloadBinder.startDownload(music);
                    }
                }

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
                String musicname = Global_Variable.musicplayQueue.queue.get(Global_Variable.musicplayQueue.i).getMusic_name();
                String musicid = Global_Variable.musicplayQueue.queue.get(Global_Variable.musicplayQueue.i).getMusic_id();
                Intent intent_comments = new Intent(PlayerActivity.this, CommentsActivity.class);
                intent_comments.putExtra("musicname", musicname);
                intent_comments.putExtra("musicid", musicid);
                startActivity(intent_comments);

                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
        unbindService(connectiondownload);
    }

    public void sendcollection(){
        final String userid = Global_Variable.thisuser.id;
        final String musicid = Global_Variable.musicplayQueue.queue.get(Global_Variable.musicplayQueue.i).getMusic_id();
        new Thread(new Runnable() { //耗时操作要开子线程
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder() //请求参数
                            .add("method", "addorcancel") //表明类型
                            .add("userid", userid) //当前用户id
                            .add("musicid", musicid)//当前歌曲id
                            .build();
                    Request request = new Request.Builder()
                            .url(Global_Variable.ip + "NogiMusic//collection") //请求url
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String data = response.body().string();
                    Log.d("NMSL", data);
                    parsejson_colletcion(data); //解析服务端返回的值
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void parsejson_colletcion(String jsondata){
        Gson gson = new Gson();
        List<Result> resultsList = gson.fromJson(jsondata, new TypeToken<List<Result>>(){}.getType());
        for (Result result : resultsList){
            if (result.result.equals("收藏成功")){
                Looper.prepare();
                Toast.makeText(PlayerActivity.this, "收藏成功", Toast.LENGTH_SHORT).show();
                Log.d("NMSL", "收藏ok");
                Looper.loop();
            } else if (result.result.equals("取消收藏")){
                Looper.prepare();
                Toast.makeText(PlayerActivity.this, "已取消收藏", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        }

    }

    class Result{
        public String result;
    }

    public void chenckcollection(){ // 后续待完善,检查是否已经收藏
        final String userid = Global_Variable.thisuser.id;
        final String musicid = Global_Variable.musicplayQueue.queue.get(Global_Variable.musicplayQueue.i).getMusic_id();
        new Thread(new Runnable() { //耗时操作要开子线程
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder() //请求参数
                            .add("method", "check") //表明类型
                            .add("userid", userid) //当前用户id
                            .add("musicid", musicid)//当前歌曲id
                            .build();
                    Request request = new Request.Builder()
                            .url(Global_Variable.ip + "NogiMusic//collection") //请求url
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String data = response.body().string();
                    Log.d("NMSL", data);
                    parsejson_colletcion(data); //解析服务端返回的值
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
