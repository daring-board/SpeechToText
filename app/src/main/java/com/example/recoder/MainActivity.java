package com.example.recoder;

import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Environment;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private static String TAG = "Sample";
    private SpeechRecognizer mRecognizer;
    private RecognitionListener mRecognitionListener;
    private ArrayList<String> sents;
    private MediaRecorder mp;

    // 音声合成用
    private TextToSpeech tts = null;


    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sents = new ArrayList<String>();
        tts = new TextToSpeech(this, this);
        mp = new MediaRecorder();

        mRecognitionListener = new RecognitionListener() {
            @Override
            public void onError(int error) {
                if ((error == SpeechRecognizer.ERROR_NO_MATCH) ||
                        (error == SpeechRecognizer.ERROR_SPEECH_TIMEOUT)) {
                    startSpeechRecognition();
                    return;
                }
                Log.d(TAG, "Recognition Error: " + error);
            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> values = results
                        .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                String val = values.get(0);
                Log.d(TAG, "認識結果: " + val);
                sents.add(val);
                TextView textView = findViewById(R.id.textView2);
                String lines = constractLines();
                textView.setText(lines);
                startSpeechRecognition();
            }

            @Override
            public void onBeginningOfSpeech() {
            }

            @Override
            public void onBufferReceived(byte[] arg0) {
            }

            @Override
            public void onEndOfSpeech() {
            }

            @Override
            public void onEvent(int arg0, Bundle arg1) {
            }

            @Override
            public void onPartialResults(Bundle arg0) {
            }

            @Override
            public void onReadyForSpeech(Bundle arg0) {
            }

            @Override
            public void onRmsChanged(float arg0) {
            }
        };
        // Example of a call to a native method
//        TextView tv = (TextView) findViewById(R.id.sample_text);
//        tv.setText(stringFromJNI());
    }

    private String constractLines(){
        String lines = "";
        for(int i=0;i<sents.size();i++){
            lines += (sents.get(i) + "\n");
        }
        return lines;
    }

    private void startSpeechRecognition() {
        // Need to destroy a recognizer to consecutive recognition?
        if (mRecognizer != null) {
            mRecognizer.destroy();
        }

        // Create a recognizer.
        mRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mRecognizer.setRecognitionListener(mRecognitionListener);

        // Start recognition.
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mRecognizer.startListening(intent);
    }

    private Recoding rec = null;

    public void recod(View view){
        Log.d(TAG, "Recoding start");
        rec = new Recoding();
        rec.start();
    }

    public void stop_recod(View view){
        Log.d(TAG, "Recoding stop");
        if(rec == null) return;
        rec.stop();
    }

    public void start(View view){
        // 認識
        startSpeechRecognition();
    }

    public void stop(View view){
        if (mRecognizer != null) {
            mRecognizer.destroy();
        }
    }

    public void speech(View view){
        // 音声合成して発音
        if(tts.isSpeaking()) {
            tts.stop();
        }

        for(int i = 0;i < sents.size(); i++){
            tts.speak(sents.get(i), TextToSpeech.QUEUE_FLUSH, null);
            while(tts.isSpeaking()) {
                try {
                    Thread.sleep(2000);
                }catch (Exception e){
                    Log.d(TAG, "Speech");
                }
            }
        }
    }

    public void clear(View view){
        String path = Environment.getExternalStorageDirectory().getPath() + "/file" + getNowDate() + ".txt";
        Log.d(TAG, "出力先: " + path);
        try{
            String str = constractLines();
            Log.d(TAG, getPackageName());
            FileOutputStream out = new FileOutputStream( new File(path), true );
            out.write(str.getBytes());
            out.close();
        }catch( IOException e ){
            e.printStackTrace();
        }
        sents.clear();
        TextView textView = findViewById(R.id.textView2);
        textView.setText("Empty");
    }

    private String getNowDate(){
        final DateFormat df = new SimpleDateFormat("yyyyMMdd_HHmmss");
        final Date date = new Date(System.currentTimeMillis());
        return df.format(date);
    }

    @Override
    public void onInit(int status) {
        if(status == TextToSpeech.SUCCESS) {
            // 音声合成の設定を行う

            float pitch = 1.0f; // 音の高低
            float rate = 1.0f; // 話すスピード
            Locale locale = Locale.JAPAN; // 対象言語のロケール
            // ※ロケールの一覧表
            //   http://docs.oracle.com/javase/jp/1.5.0/api/java/util/Locale.html

            tts.setPitch(pitch);
            tts.setSpeechRate(rate);
            tts.setLanguage(locale);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if( tts != null )
        {
            // 破棄
            tts.shutdown();
        }
    }



//    /**
//     * A native method that is implemented by the 'native-lib' native library,
//     * which is packaged with this application.
//     */
//    public native String stringFromJNI();

}




