package com.md.android.sample.audiocapturesample;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String FILE_NAME = "test_audio_capture.3gp";

    TextView mFileName;
    Button mPlayRecordSoundButton;
    Button mRecordSoundButton;

    MediaRecorder mMediaRecorder;
    MediaPlayer mMediaPlayer;

    File mRecordFile;

    boolean mPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initComponents();
        initFile();
        setDisplayFile();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mMediaRecorder != null) {
            mMediaRecorder.release();
            mMediaRecorder = null;
        }

        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    private void initComponents() {
        mFileName = (TextView) findViewById(R.id.tv_file_name);
        mPlayRecordSoundButton = (Button) findViewById(R.id.btn_play_record_sound);
        mPlayRecordSoundButton.setOnClickListener(new OnPlayRecordSoundClickListener());
        mRecordSoundButton = (Button) findViewById(R.id.btn_record_sound);
        mRecordSoundButton.setOnTouchListener(new OnRecordSoundTouchListener());
    }

    private void initFile() {
        File dir = Environment.getExternalStorageDirectory();
        mRecordFile = new File(dir.getAbsolutePath(), FILE_NAME);
    }

    private void setDisplayFile() {
        if (mRecordFile.exists()) {
            mFileName.setText("Press play record sound button !!");
        } else {
            mFileName.setText("Record file not found.");
        }
    }

    private class OnPlayRecordSoundClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if (mRecordFile != null && mRecordFile.exists()) {
                try {
                    if (mPlaying) {
                        mMediaPlayer.release();
                        mMediaPlayer = null;
                    } else {
                        mMediaPlayer = new MediaPlayer();
                        mMediaPlayer.setDataSource(mRecordFile.getAbsolutePath());
                        mMediaPlayer.prepare();
                        mMediaPlayer.start();
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
            }
        }
    }

    private class OnRecordSoundTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                mMediaRecorder = new MediaRecorder();
                mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                mMediaRecorder.setOutputFile(mRecordFile.getAbsolutePath());
                mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

                try {
                    mMediaRecorder.prepare();
                } catch (IOException e) {
                    Log.e(TAG, "prepare() failed");
                }

                mMediaRecorder.start();
            } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                mMediaRecorder.stop();
                mMediaRecorder.release();
                mMediaRecorder = null;
                setDisplayFile();
            }

            return false;
        }
    }
}
