package com.maxd.videoplayer.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.maxd.videoplayer.R;
import com.maxd.videoplayer.modle.VideoInfo;
import com.maxd.videoplayer.ctrl.VideoListAdapter;
import com.maxd.videoplayer.modle.VideoList;

import java.util.ArrayList;

public class VideoListActivity extends Activity implements View.OnClickListener {

    private final static int MSG_UPDATE_VIDEOINFO = 0x01;
    private final static String TAG = "MainActivity";

    private ListView mVideoListView = null;
    private ProgressBar mVideoListProgress = null;

    private Button backBtn;


    private VideoListAdapter mVideoLisAdapter = null;
    //private ArrayList<VideoInfo> mVideoInfoList = new ArrayList<VideoInfo>();
    private VideoList mVideoInfoList = VideoList.getInstance();

    private static final String[] mLocalVideoColumn = {
            MediaStore.Video.Media._ID, //id
            MediaStore.Video.Media.DISPLAY_NAME,//名称
            MediaStore.Video.Media.DURATION,//时长
            MediaStore.Video.Media.TITLE,//标题,
            MediaStore.Video.Media.MIME_TYPE,
            MediaStore.Video.Media.DATA,
    };

    private Handler mHandler = new VideoViewHandler();

    private boolean isInit = false;
    private void initVideoInfoList() {
        //获取视频数据；
        Log.d(TAG, "-------------------> initVideoInfoList");
        if (!isInit){
            new LoadVideoInfoThread(this).start();
        }
    }

    private static final String[] permission = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videolist);
        mVideoLisAdapter = new VideoListAdapter(VideoListActivity.this,mVideoInfoList.getVideoListArray(),mHandler);
        mVideoListView = findViewById(R.id.video_list);
        mVideoListProgress = findViewById(R.id.video_list_progress);

        mVideoListView.setOnItemClickListener(new ListViewItemOnClickListener());

        backBtn = findViewById(R.id.vl_back_btn);
        backBtn.setOnClickListener(this);

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onResume() {
        Log.d(TAG,"-----------> onResume");

        if( checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(permission,1);
        } else {
            initVideoInfoList();

        }
        //initVideoInfoList();
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.d(TAG,"----------------> onPause");
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG,"----------------> onDestroy");
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.vl_back_btn:
                finish();
                break;
            default:
                break;
        }
    }

    private class VideoViewHandler extends Handler{
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case MSG_UPDATE_VIDEOINFO:
                    mVideoListProgress.setVisibility(View.GONE);
                    mVideoListView.setAdapter(mVideoLisAdapter);
                    break;
                default:
                    break;
            }
        }
    }

    private class ListViewItemOnClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mVideoInfoList.setPlayIndex(position);
            //Intent intent = new Intent(VideoListActivity.this,VideoPlayerActivity.class);
            Intent intent = new Intent(VideoListActivity.this,VideoPlayerSurfaceActivity.class);
            //intent.putExtra("video_index",position);
            startActivity(intent);
        }
    }

    private class LoadVideoInfoThread extends Thread{
        private Context mContext = null;
        public LoadVideoInfoThread(Context context){
            mContext = context;
        }

        @Override
        public void run() {
            // 获取游标
            Cursor cursor = mContext.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, mLocalVideoColumn,
                    null,null,MediaStore.Video.Media.DEFAULT_SORT_ORDER);

            if(!isInit && cursor != null && cursor.moveToFirst()){
                isInit = true;
                do{
                    //获取视频信息
                    VideoInfo videoInfo = new VideoInfo(cursor);
                    mVideoInfoList.putVideoInfo(videoInfo);
                    Log.d(TAG,"video title :" + videoInfo.getTitle());
                }while(cursor.moveToNext()); //遍历视频数据；
            } else {
                Log.d(TAG,"-----------------------> cursor error");
            }
            Log.d(TAG,"-----------------------> mVideoInfoList size : " + Integer.toString(mVideoInfoList.size()));
            mHandler.sendEmptyMessage(MSG_UPDATE_VIDEOINFO);
            //
            cursor.close();
        }
    }
}