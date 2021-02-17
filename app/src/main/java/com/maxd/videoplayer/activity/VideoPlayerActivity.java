package com.maxd.videoplayer.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;

import com.maxd.videoplayer.R;
import com.maxd.videoplayer.modle.VideoList;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class VideoPlayerActivity extends AppCompatActivity {

    private VideoView mVideoView = null;
    private SurfaceHolder mSurfaceHolder = null;
    private MediaPlayer mMediaPlayer = null;
    private View mVideoCtrl = null;
    private VideoList mVideoList = VideoList.getInstance();

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

        int playIndex = mVideoList.getPlayIndex();
        if(playIndex != -1){
            if(mVideoList.getVideoInfo(playIndex) != null){
                playByPath(mVideoList.getVideoInfo(playIndex).getData());
            }
        }
    }

    private class VideoCompleteListener implements MediaPlayer.OnCompletionListener{

        @Override
        public void onCompletion(MediaPlayer mp) {
            Toast.makeText( VideoPlayerActivity.this,"play completion ",Toast.LENGTH_SHORT).show();
        }
    }

    private class VideoOnErrorListener implements MediaPlayer.OnErrorListener{
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            Toast.makeText( VideoPlayerActivity.this,"play error ",Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private class VideoOnPreparedListener implements MediaPlayer.OnPreparedListener{
        @Override
        public void onPrepared(MediaPlayer mp) {
            mVideoView.start();
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