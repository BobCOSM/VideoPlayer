package com.maxd.videoplayer.activity;

import androidx.annotation.NonNull;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.maxd.videoplayer.R;
import com.maxd.videoplayer.VideoBarCtrl;
import com.maxd.videoplayer.modle.VideoInfo;
import com.maxd.videoplayer.modle.VideoList;

import java.io.IOException;
import java.util.TimerTask;

public class VideoPlayerSurfaceActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "VideoPlayer";

    private static final int HMSG_UPDATE_SEEKBAR = 0x01;

    private VideoPlayerHandler mHandler = new VideoPlayerHandler();

    private SurfaceView mSurfaceView = null;
    private SurfaceHolder mSurfaceHolder = null;

    private AudioManager mAudioManager = null;
    private AudioFocuseChangeListener mAudioFocuseChangeListener = null;
    private boolean mIsAudioFocus = false;

    private MediaPlayer mMediaPlayer = null;
    private VideoBarCtrl mVideoBarCtrl = null;
    private SurfaceMediaListener mSurfaceMediaListener = null;

    private VideoInfo playVideoInfo = null;

    private String playPath = null;
    private VideoList mVideoList = VideoList.getInstance();

    private View rootView = null;
    private Button playBtn = null;
    private Button nextBtn = null;
    private Button backBtn = null;
    private Button fullBtn = null;
    private TextView videoNameTxt = null;

    private SeekBar playSeekBar = null;
    private TextView durationTxt = null;

    private void requestAudioFouse(){
        if(mAudioManager == null){
            mAudioManager = (AudioManager)getSystemService(AUDIO_SERVICE);
        }
        if(mAudioFocuseChangeListener == null){
            mAudioFocuseChangeListener = new AudioFocuseChangeListener();
        }
        if(!mIsAudioFocus){
            mAudioManager.requestAudioFocus(mAudioFocuseChangeListener,
                    AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        }
    }

    private void playVideo(String path) throws IOException {
        if(mMediaPlayer == null){
            mMediaPlayer = new MediaPlayer();
        }
        mMediaPlayer.reset();
        //mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        //requestAudioFouse();

        mSurfaceMediaListener = new SurfaceMediaListener();
        mMediaPlayer.setOnPreparedListener(mSurfaceMediaListener);
        mMediaPlayer.setOnCompletionListener(mSurfaceMediaListener);
        mMediaPlayer.setOnErrorListener(mSurfaceMediaListener);
        mMediaPlayer.setDataSource(path);
        mMediaPlayer.setDisplay(mSurfaceHolder);
        mMediaPlayer.prepareAsync();
    }

    private void initCtrlView(){
        playBtn = rootView.findViewById(R.id.vp_play_btn);
        nextBtn = rootView.findViewById(R.id.vp_next_btn);
        backBtn = rootView.findViewById(R.id.vp_back_btn);
        fullBtn = rootView.findViewById(R.id.vp_full_btn);
        videoNameTxt = rootView.findViewById(R.id.vp_video_name);

        playSeekBar = rootView.findViewById(R.id.vp_progress_seekbar);
        durationTxt = rootView.findViewById(R.id.vp_duration_txt);

        videoNameTxt.setText(playVideoInfo.getName());
        playBtn.setOnClickListener(this);
        nextBtn.setOnClickListener(this);
        backBtn.setOnClickListener(this);
        fullBtn.setOnClickListener(this);
        rootView.setOnClickListener(this);

        playSeekBar.setOnSeekBarChangeListener(new SeekBarOnClickListener());
    }

    private class SeekBarTimerTask extends TimerTask{

        @Override
        public void run() {
            mHandler.sendEmptyMessage(HMSG_UPDATE_SEEKBAR);
        }
    }

    private boolean isSeekBarUpdate = false;
    private void startUpdateSeekBar(){
        isSeekBarUpdate = true;
        new Thread(){
            @Override
            public void run() {
                while(isSeekBarUpdate){
                    mHandler.sendEmptyMessage(HMSG_UPDATE_SEEKBAR);
                    SystemClock.sleep(1000);
                }
            }
        }.start();

    }

    private void stopUpdateSeekBar(){
        Log.d(TAG," ------------> hideSeekBar ");
        isSeekBarUpdate = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        int playIndex = mVideoList.getPlayIndex();
        playVideoInfo = mVideoList.getVideoInfo(playIndex);
        if(playVideoInfo != null){
            playPath = mVideoList.getVideoInfo(playIndex).getData();
        }

        mSurfaceView = findViewById(R.id.vp_video_surface);
        mSurfaceView.getHolder().addCallback(new SurfaceHolderCallBack());
        //mVideoBarCtrl = new VideoBarCtrl(findViewById(R.id.vp_play_root_view));
        rootView = findViewById(R.id.vp_play_root_view);
        initCtrlView();
    }

    private class SurfaceHolderCallBack implements SurfaceHolder.Callback{

        @Override
        public void surfaceCreated(@NonNull SurfaceHolder holder) {
            mSurfaceHolder = holder;
            if(playPath != null){
                try {
                    playVideo(playPath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
            stopUpdateSeekBar();
            mSurfaceHolder = null;
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
            Log.d(TAG,"------------> surfaceDestroyed ");
        }
    }

    private class SurfaceMediaListener implements MediaPlayer.OnPreparedListener,
            MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener{

        @Override
        public void onCompletion(MediaPlayer mp) {
            Log.d(TAG," play onCompletion");

            stopUpdateSeekBar();
            mMediaPlayer.seekTo(0);
            playSeekBar.setProgress(0);
            playBtn.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24);
        }

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            Log.d(TAG," play onError what " + what + ", extra " + extra);
            return false;
        }

        @Override
        public void onPrepared(MediaPlayer mp) {
            int video_width = mMediaPlayer.getVideoWidth();
            int video_height = mMediaPlayer.getVideoHeight();

            int rootWidth = rootView.getWidth();
            int rootHeight = rootView.getHeight();

            double video_ratio = (video_width * 1.0) / video_height;
            double view_ratio = (rootWidth * 1.0) / rootHeight;

            if(video_ratio < view_ratio){
                video_height = rootHeight;
                video_width = (int) (video_height * video_ratio);
            } else {
                video_width = rootWidth;
                video_height = (int) (video_width / video_ratio);
            }

            mSurfaceHolder.setFixedSize(video_width,video_height);
            durationTxt.setText(mVideoList.getVideoInfo(mVideoList.getPlayIndex()).getStrDuration());
            mMediaPlayer.start();
            playSeekBar.setMax(mMediaPlayer.getDuration());
            startUpdateSeekBar();
        }
    }

    @Override
    public void onClick(View v) {
        if(mMediaPlayer == null){
            return;
        }
        switch (v.getId()){
            case R.id.vp_play_btn:
                if(mMediaPlayer.isPlaying()){
                    //Toast.makeText(this,"play pause",Toast.LENGTH_SHORT).show();
                    playBtn.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24);
                    mMediaPlayer.pause();
                    stopUpdateSeekBar();
                }else{
                    //Toast.makeText(this,"play start",Toast.LENGTH_SHORT).show();
                    playBtn.setBackgroundResource(R.drawable.ic_baseline_pause_24);
                    mMediaPlayer.start();
                    startUpdateSeekBar();
                }
                break;
            case R.id.vp_next_btn:

                break;
            case R.id.vp_back_btn:
                finish();
                break;
            case R.id.vp_full_btn:

                break;
            default:
                break;
        }
    }

    private void updateSeekBarProgress(){
        int seekbar_progress = 0;
        if(mMediaPlayer!=null){
            seekbar_progress = mMediaPlayer.getCurrentPosition();
        }
        Log.d(TAG," seekbar_progress : " + seekbar_progress);
        playSeekBar.setProgress(seekbar_progress);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private class VideoPlayerHandler extends Handler{
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case HMSG_UPDATE_SEEKBAR:
                    updateSeekBarProgress();
                    break;
                default:
                    break;
            }
        }
    }

    private class AudioFocuseChangeListener implements AudioManager.OnAudioFocusChangeListener {

        @Override
        public void onAudioFocusChange(int focusChange) {
            // TODO Auto-generated method stub
            switch(focusChange){
                case AudioManager.AUDIOFOCUS_LOSS: 	//失去音频焦点
                    mIsAudioFocus = false;
                    //mediaPause();
                    break;
                case AudioManager.AUDIOFOCUS_GAIN:	//获得音频焦点
                    mIsAudioFocus = true;
                    //mediaPlay();

                    break;
            }
        }
    }

    private class SeekBarOnClickListener implements SeekBar.OnSeekBarChangeListener{

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if(fromUser){
                mMediaPlayer.seekTo(progress);
            }

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

            mMediaPlayer.pause();
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

            mMediaPlayer.start();
        }
    }
}

