package com.dueeeke.videoplayer.controller;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.dueeeke.videoplayer.R;
import com.dueeeke.videoplayer.player.VideoView;
import com.dueeeke.videoplayer.player.VideoViewManager;
import com.dueeeke.videoplayer.util.PlayerUtils;
import com.dueeeke.videoplayer.widget.StatusView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;

/**
 * 控制器基类
 * Created by Devlin_n on 2017/4/12.
 */

public abstract class BaseVideoController extends FrameLayout {

    protected View mControllerView;//控制器视图
    protected MediaPlayerControl mMediaPlayer;//播放器
    protected boolean mShowing;//控制器是否处于显示状态
    protected boolean mIsLocked;
    protected int mDefaultTimeout = 4000;
    private StringBuilder mFormatBuilder;
    private Formatter mFormatter;
    protected int mCurrentPlayState;
    protected StatusView mStatusView;


    public BaseVideoController(@NonNull Context context) {
        this(context, null);
    }

    public BaseVideoController(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public BaseVideoController(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    protected void initView() {
        mControllerView = LayoutInflater.from(getContext()).inflate(getLayoutId(), this);
        mFormatBuilder = new StringBuilder();
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
        mStatusView = new StatusView(getContext());
        setClickable(true);
        setFocusable(true);
    }

    /**
     * 设置控制器布局文件，子类必须实现
     */
    protected abstract int getLayoutId();

    /**
     * 显示
     */
    public void show() {
    }

    /**
     * 隐藏
     */
    public void hide() {
    }

    public void setPlayState(int playState) {
        mCurrentPlayState = playState;
        hideStatusView();
        if (playState == VideoView.STATE_ERROR) {
            mStatusView.setMessage(getResources().getString(R.string.dkplayer_error_message));
            mStatusView.setButtonTextAndAction(getResources().getString(R.string.dkplayer_retry), new OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideStatusView();
                    mMediaPlayer.replay(false);
                }
            });
            this.addView(mStatusView, 0);
        }
    }

    public void showStatusView() {
        this.removeView(mStatusView);
        mStatusView.setMessage(getResources().getString(R.string.dkplayer_wifi_tip));
        mStatusView.setButtonTextAndAction(getResources().getString(R.string.dkplayer_continue_play), new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideStatusView();
                VideoViewManager.instance().setPlayOnMobileNetwork(true);
                mMediaPlayer.start();
            }
        });
        this.addView(mStatusView);
    }

    public void hideStatusView() {
        this.removeView(mStatusView);
    }

    public void setPlayerState(int playerState) {
    }

    protected void doPauseResume() {
        if (mCurrentPlayState == VideoView.STATE_BUFFERING) return;
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
        } else {
            mMediaPlayer.start();
        }
    }

    /**
     * 横竖屏切换
     */
    protected void doStartStopFullScreen() {
        Activity activity = PlayerUtils.scanForActivity(getContext());
        if (activity == null) return;
        if (mMediaPlayer.isFullScreen()) {
            mMediaPlayer.stopFullScreen();
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            mMediaPlayer.startFullScreen();
        }
    }


    protected Runnable mShowProgress = new Runnable() {
        @Override
        public void run() {
            int pos = setProgress();
            if (mMediaPlayer.isPlaying()) {
                postDelayed(mShowProgress, 1000 - (pos % 1000));
            }
        }
    };

    protected final Runnable mFadeOut = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    protected int setProgress() {
        return 0;
    }

    /**
     * 获取当前系统时间
     */
    protected String getCurrentSystemTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        Date date = new Date();
        return simpleDateFormat.format(date);
    }

    protected String stringForTime(int timeMs) {
        int totalSeconds = timeMs / 1000;

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        post(mShowProgress);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeCallbacks(mShowProgress);
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (visibility == VISIBLE) {
            post(mShowProgress);
        }
    }

    /**
     * 改变返回键逻辑，用于activity
     */
    public boolean onBackPressed() {
        return false;
    }

    public void setMediaPlayer(MediaPlayerControl mediaPlayer) {
        this.mMediaPlayer = mediaPlayer;
    }
}
