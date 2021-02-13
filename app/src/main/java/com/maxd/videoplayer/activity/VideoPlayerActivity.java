package com.maxd.videoplayer.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.maxd.videoplayer.R;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class VideoPlayerActivity extends AppCompatActivity {

    private VideoView mVideoView = null;
    private SurfaceHolder mSurfaceHolder = null;
    private MediaPlayer mMediaPlayer = null;
    private View mVideoCtrl = null;

    AudioManager mAudioManager = null;
    AudioManager.OnAudioFocusChangeListener mAudioFocuseChangeListener = null;

    private final static String TAG = "VideoPalyerActivity";

    private void initVideoView() {
        mVideoView.setOnPreparedListener(new VideoOnPreparedListener());
        mVideoView.setOnErrorListener(new VideoOnErrorListener());
        mVideoView.setOnCompletionListener(new VideoCompleteListener());
        mVideoView.setMediaController(new MediaController(this));
    }

    private void playByPath(String path){
        mVideoView.setVideoPath(path);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player_1);
        mVideoView = findViewById(R.id.vp_video_view);
        mVideoCtrl = findViewById(R.id.vp_ctrl);

        initVideoView();
        Intent intent = getIntent();
        if(intent != null ){
            String path = intent.getStringExtra("video_path");
            if( path != null) {
                playByPath(path);
            }
        }
    }

    private class VideoCompleteListener implements MediaPlayer.OnCompletionListener{

        @Override
        public void onCompletion(MediaPlayer mp) {

        }
    }

    private class VideoOnErrorListener implements MediaPlayer.OnErrorListener{
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            return false;
        }
    }

    private class VideoOnPreparedListener implements MediaPlayer.OnPreparedListener{
        @Override
        public void onPrepared(MediaPlayer mp) {
            mVideoView.start();
        }
    }

    private class VideoViewCallBack implements SurfaceHolder.Callback{

        @Override
        public void surfaceCreated(@NonNull SurfaceHolder holder) {

        }

        @Override
        public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(@NonNull SurfaceHolder holder) {

        }
    }

    class AudioFocuseChangeListener implements AudioManager.OnAudioFocusChangeListener {

        @Override
        public void onAudioFocusChange(int focusChange) {
            // TODO Auto-generated method stub
            switch(focusChange){
                case AudioManager.AUDIOFOCUS_LOSS: 	//失去音频焦点
                    break;
                case AudioManager.AUDIOFOCUS_GAIN:	//获得音频焦点
                    break;
            }
        }
    }
}