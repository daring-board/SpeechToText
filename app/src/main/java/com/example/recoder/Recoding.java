package com.example.recoder;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by mahoto on 2018/03/03.
 */

public class Recoding{

    private MediaRecorder mr;
    private String filePath;

    public Recoding(){
        filePath = Environment.getExternalStorageDirectory().getPath() + "/recoding" + getNowDate() + ".wav";
        File file = new File(filePath);
        if(file.exists()) {
            //ファイルが存在する場合は削除する
            file.delete();
        }
        mr = new MediaRecorder();
    }

    private String getNowDate(){
        final DateFormat df = new SimpleDateFormat("yyyyMMdd_HHmmss");
        final Date date = new Date(System.currentTimeMillis());
        return df.format(date);
    }

    public void start(){
        try {
            //マイクからの音声を録音する
            mr.setAudioSource(MediaRecorder.AudioSource.MIC);
            //ファイルへの出力フォーマット DEFAULTにするとwavが扱えるはず
            mr.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
            //音声のエンコーダーも合わせてdefaultにする
            mr.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
            //ファイルの保存先を指定
            mr.setOutputFile(filePath);
            //録音の準備をする
            mr.prepare();
            //録音開始
            mr.start();
        }catch(IOException e){
            e.printStackTrace();
        }

    }

    public void stop(){
        try{
            //録音停止
            mr.stop();
            mr.reset();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
