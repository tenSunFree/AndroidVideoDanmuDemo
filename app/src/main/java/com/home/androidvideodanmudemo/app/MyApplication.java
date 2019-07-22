package com.home.androidvideodanmudemo.app;

import android.app.Application;
import com.dueeeke.videoplayer.ijk.IjkPlayerFactory;
import com.dueeeke.videoplayer.player.VideoViewConfig;
import com.dueeeke.videoplayer.player.VideoViewManager;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        InitializeVideoViewManager();
    }

    /**
     * 播放器配置
     * 注意: 此為全局配置, 按需開啟
     */
    private void InitializeVideoViewManager() {
        VideoViewManager.setConfig(VideoViewConfig.newBuilder()
                .setPlayerFactory(IjkPlayerFactory.create(this))
                .build());
    }
}
