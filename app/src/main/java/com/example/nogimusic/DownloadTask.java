package com.example.nogimusic;

import android.os.AsyncTask;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DownloadTask extends AsyncTask<String, Integer, Integer> {
    public static final int TYPE_SUCCESS = 0;
    public static final int TYPE_FAILED = 1;
    public static final int TYPE_PAUSED = 2;
    public static final int TYPE_CANCELED = 3;

    private DownloadListener listener;

    private boolean isCanceled = false;
    private boolean isPaused = false;

    private int lastprocess;

    public DownloadTask(DownloadListener listener){
        this.listener = listener;
    }

    @Override
    protected Integer doInBackground(String... params) {
        InputStream is = null;
        RandomAccessFile saveFile = null;
        File file = null;

        try {
            long downloadedLength = 0; //记录已下载的文件长度
            String downloadUrl = params[0];
            String fileName = downloadUrl.substring(downloadUrl.lastIndexOf("/"));
            String directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
            file = new File(directory + fileName);
            if (file.exists()){
                downloadedLength = file.length();
            }

            long contentLength = getContentLength(downloadUrl);

            if (contentLength == 0){
                return TYPE_FAILED;
            } else if (contentLength == downloadedLength){
                //已下载字节数和总字节数相同，说明下载完成
                return TYPE_SUCCESS;
            }

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .addHeader("RANGE", "bytes=" + downloadedLength + "-")
                    .url(downloadUrl)
                    .build();
            Response response = client.newCall(request).execute();
            if (response != null){
                is = response.body().byteStream();
                saveFile = new RandomAccessFile(file, "rw");
                saveFile.seek(downloadedLength); //跳过已下载字节
                byte[] b = new byte[1024];
                int total = 0;
                int len;
                while ((len = is.read(b)) != -1){
                    if (isCanceled){
                        return TYPE_CANCELED;
                    } else if (isPaused){
                        return TYPE_PAUSED;
                    } else {
                        total += len;
                        saveFile.write(b,0,len);
                        int process = (int) ((total + downloadedLength) * 100 / contentLength);
                        publishProgress(process);
                    }
                }

                response.body().close();
                return TYPE_SUCCESS;
            }
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            try {
                if (is != null){
                    is.close();
                }
                if (saveFile != null){
                    saveFile.close();
                }
                if (isCanceled && file != null){
                    file.delete();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return TYPE_FAILED;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        int process = values[0];
        if (process > lastprocess){
            listener.onProcess(process);
            lastprocess = process;
        }
    }

    @Override
    protected void onPostExecute(Integer status) {
        switch (status){
            case TYPE_SUCCESS :
                listener.onSuccess();
                break;
            case TYPE_FAILED :
                listener.onFailed();
                break;
            case TYPE_PAUSED :
                listener.onPaused();
                break;
            case TYPE_CANCELED :
                listener.onCanceled();
                break;
            default:
                break;
        }
    }

    public void pauseDownload(){
        isPaused = true;
    }

    public void cancelDownload(){
        isCanceled = true;
    }

    private long getContentLength(String downloadUrl) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(downloadUrl)
                .build();
        Response response = client.newCall(request).execute();
        if (response != null && response.isSuccessful()){
            long contentlength = response.body().contentLength();
            response.body().close();
            return contentlength;
        }
        return 0;
    }
}
