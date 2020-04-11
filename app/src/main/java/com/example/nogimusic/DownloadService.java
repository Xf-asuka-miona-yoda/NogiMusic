package com.example.nogimusic;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import java.io.File;

public class DownloadService extends Service {
    private DownloadTask downloadTask;

    private String downloadUrl;

    private Music downlaodmusic;

    NotificationManager notificationManager = null;

    private DownloadListener listener = new DownloadListener() {
        @Override
        public void onProcess(int process) {
            getNotificationManger().notify(1, getNotification(downlaodmusic.getMusic_name() + "下载中...", process));
        }

        @Override
        public void onSuccess() {
            downloadTask = null;
            stopForeground(true);
            getNotificationManger().notify(1, getNotification(downlaodmusic.getMusic_name() + "下载成功", -1));
            Toast.makeText(DownloadService.this, downlaodmusic.getMusic_name() + "下载成功", Toast.LENGTH_SHORT).show();
            File file1 = new File(Environment.getExternalStorageDirectory(),downlaodmusic.getMusic_name());
            downlaodmusic.setMusic_url(file1.getPath());
            downlaodmusic.setState("local");
            downlaodmusic.save();
        }

        @Override
        public void onFailed() {
            downloadTask = null;
            stopForeground(true);
            getNotificationManger().notify(1, getNotification(downlaodmusic.getMusic_name() + "下载失败", -1));
            Toast.makeText(DownloadService.this, downlaodmusic.getMusic_name() + "下载失败", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPaused() {
            downloadTask = null;
            Toast.makeText(DownloadService.this, downlaodmusic.getMusic_name() + "下载暂停", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCanceled() {
            downloadTask = null;
            stopForeground(true);
            Toast.makeText(DownloadService.this, downlaodmusic.getMusic_name() + "下载取消", Toast.LENGTH_SHORT).show();
        }
    };

    public DownloadService() {
    }

    private DownloadBinder mBinder = new DownloadBinder();

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return mBinder;
    }

    class DownloadBinder extends Binder {
        public void startDownload(Music music){
            if (downloadTask == null){
                downlaodmusic = music;
                downloadUrl = Global_Variable.ip + downlaodmusic.getMusic_url();
                downloadTask = new DownloadTask(listener);
                downloadTask.execute(downloadUrl);
                startForeground(1, getNotification(downlaodmusic.getMusic_name() + "下载中", 0));
                Toast.makeText(DownloadService.this, "开始下载" + downlaodmusic.getMusic_name()  , Toast.LENGTH_SHORT).show();
            }
        }

        public void pauseDownload(){
            if (downloadTask != null){
                downloadTask.pauseDownload();
            }
        }

        public void cancelDownload(){
            if (downloadTask != null){
                downloadTask.cancelDownload();
            }

            if (downloadUrl != null){
                String fileName = downloadUrl.substring(downloadUrl.lastIndexOf("/"));
                String directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
                File file = new File(directory + fileName);
                if (file.exists()){
                    file.delete();
                }
                getNotificationManger().cancel(1);
                stopForeground(true);
                Toast.makeText(DownloadService.this, "取消下载" + downlaodmusic.getMusic_name()  , Toast.LENGTH_SHORT).show();
            }
        }
    }



    private NotificationManager getNotificationManger(){
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        return notificationManager;
    }

    private Notification getNotification(String title, int process){
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0 ,intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,"downloadnotify");
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            //修改安卓8.1以上系统报错
            NotificationChannel notificationChannel = new NotificationChannel("downloadnotify", "下载", NotificationManager.IMPORTANCE_MIN);
            notificationChannel.enableLights(false);//如果使用中的设备支持通知灯，则说明此通知通道是否应显示灯
            notificationChannel.setShowBadge(false);//是否显示角标
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_SECRET);
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(notificationChannel);
            builder.setChannelId("downloadnotify");
        }
        builder.setSmallIcon(R.mipmap.logo);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.logo));
        builder.setContentIntent(pi);
        builder.setContentTitle(title);
        if (process >= 0){
            builder.setContentText(process + "%");
            builder.setProgress(100, process, false);
        }
        return builder.build();
    }
}
