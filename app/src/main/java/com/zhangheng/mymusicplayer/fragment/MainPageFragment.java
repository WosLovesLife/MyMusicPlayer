package com.zhangheng.mymusicplayer.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.project.myutilslibrary.pictureloader.PictureLoader;
import com.zhangheng.mymusicplayer.R;
import com.zhangheng.mymusicplayer.activity.MainPageActivity;
import com.zhangheng.mymusicplayer.activity.MusicListActivity;
import com.zhangheng.mymusicplayer.bean.MusicBean;
import com.zhangheng.mymusicplayer.engine.Controller;
import com.zhangheng.mymusicplayer.global.Constants;
import com.zhangheng.mymusicplayer.listener.OnPlayerStateChangedListener;

/**
 * Created by zhangH on 2016/4/30.
 */
public class MainPageFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = Constants.TAG;

    public static final int REQUEST_CODE_LIST_FRAGMENT = 0;

    /** 控制器对象,当界面上的组件被触发(点击)是调用相关的控制器方法即可实现相应的功能,无需关注判断逻辑 */
    private Controller mController;

    //--组件-start--
    /** 进度条,拖动改变播放进度 */
    private SeekBar mProgress_sb;
    /** 播放/暂停键 */
    private ImageButton mPlay_bt;
    /** 上一首键 */
    private ImageButton mPre_bt;
    /** 下一首键 */
    private ImageButton mNext_bt;
    //--组件-end--

    /** 状态值,如果SeekBar在拖动中,暂停SeekBar的自动进度移动 */
    private boolean isSeekBarHeld;
    private TextView mProgress_tv;
    private TextView mDuration_tv;

    /** 专辑图片ImageView */
    private CardView mAlbumPicture;

    /** 监听播放状态 */
    private MainPagePlayerStateChangedListener mMainPagePlayerStateChangedListener;

    private int mAlbumPictureSize;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);// 告知FragmentManager本页面包含Menu
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main_page, container, false);
        mNext_bt = (ImageButton) v.findViewById(R.id.next_mainpageButton);
        mPre_bt = (ImageButton) v.findViewById(R.id.pre_mainpageButton);
        mPlay_bt = (ImageButton) v.findViewById(R.id.play_mainpageButton);
        mProgress_sb = (SeekBar) v.findViewById(R.id.progress_mainpageSeekBar);
        mDuration_tv = (TextView) v.findViewById(R.id.duration_TextView);
        mProgress_tv = (TextView) v.findViewById(R.id.currentProgress_TextView);

        mAlbumPicture = (CardView) v.findViewById(R.id.albumPicture);

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        /** 获取控制器 */
        mController = Controller.newInstance(getActivity());

        /** 监听歌曲信息和播放状态 */
        mMainPagePlayerStateChangedListener = new MainPagePlayerStateChangedListener();
        mController.setOnPlayerStateChangedListener(mMainPagePlayerStateChangedListener);

        setViewFunction();

        initAlbumPictureSize();
    }

    /** 停止监听,节省资源 */
    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mMainPagePlayerStateChangedListener != null){
            mMainPagePlayerStateChangedListener = null;
        }

        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
    }

    /** 初始化专辑图片的ImageView尺寸 */
    private void initAlbumPictureSize() {
        mAlbumPicture.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mAlbumPicture.getViewTreeObserver().removeOnPreDrawListener(this);

                DisplayMetrics displayMetrics = new DisplayMetrics();
                getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int widthPixels = displayMetrics.widthPixels;
                int heightPixels = displayMetrics.heightPixels / 2;

                if (widthPixels < heightPixels) {
                    mAlbumPictureSize = (int) (widthPixels * 0.8f);
                } else {
                    mAlbumPictureSize = (int) (heightPixels * 0.8f);
                }

                ViewGroup.LayoutParams layoutParams = mAlbumPicture.getLayoutParams();
                layoutParams.height = mAlbumPictureSize;
                layoutParams.width = mAlbumPictureSize;
                mAlbumPicture.setLayoutParams(layoutParams);

                return true;
            }
        });

//        int measuredWidth = mAlbumPicture.getMeasuredWidth();
//        int measuredHeight = mAlbumPicture.getMeasuredHeight();
//        int reference;
//        if (measuredWidth > measuredHeight) {
//            reference = measuredHeight;
//        } else {
//            reference = measuredWidth;
//        }
//
//        int margin = (int) (reference * 0.15 + 0.5);
//        mAlbumPictureSize = reference - margin;
//
//        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mAlbumPicture.getLayoutParams();
//        params.height = mAlbumPictureSize;
//        params.width = mAlbumPictureSize;
//        params.setMargins(margin, margin, margin, 0);
//
//        mAlbumPicture.setLayoutParams(params);
    }

    private void setViewFunction() {
        mPlay_bt.setOnClickListener(this);
        mPre_bt.setOnClickListener(this);
        mNext_bt.setOnClickListener(this);

        MainPageSeekBarChangeListener mainPagePlayerStateChangedListener = new MainPageSeekBarChangeListener();
        mProgress_sb.setOnSeekBarChangeListener(mainPagePlayerStateChangedListener);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.play_mainpageButton:
                mController.play();
                break;
            case R.id.pre_mainpageButton:
                mController.pre();
                break;
            case R.id.next_mainpageButton:
                mController.next();
                break;
        }
    }

    ///////////== 关于ActionBar的方法start==/////////////
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main_page_list, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_list:
                Intent i = new Intent(getActivity(), MusicListActivity.class);
                getActivity().startActivityForResult(i, REQUEST_CODE_LIST_FRAGMENT);
                return true;// 表示处理该事件
        }
        return super.onOptionsItemSelected(item);
    }
    ///////////== 关于ActionBar的方法end==/////////////

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0: // 给获取处理完的背景图片
                    setPlayerSkin((Bitmap) msg.obj);
                    break;
            }
        }
    };

    private void setPlayerSkin(Bitmap bitmap) {
        MainPageActivity activity = (MainPageActivity) getActivity();
        if (bitmap != null) {
            mAlbumPicture.setBackgroundDrawable(new BitmapDrawable(bitmap));
            activity.setPlayerBg(bitmap);
        } else {
            mAlbumPicture.setBackgroundResource(R.drawable.app_widget_default);
            activity.setPlayerBg(BitmapFactory.decodeResource(getResources(), R.drawable.local_upgrade_success_bg));
        }

    }

    ///////////==注册Controller的监听接口start==/////////////
    private class MainPagePlayerStateChangedListener implements OnPlayerStateChangedListener {

        @Override
        public void onPlayStateChanged(boolean isPlaying, int duration, int progress, MusicBean musicBean) {
            AppCompatActivity a = (AppCompatActivity) getActivity();
            android.support.v7.app.ActionBar supportActionBar = a.getSupportActionBar();
            if (supportActionBar != null) {
                supportActionBar.setTitle(musicBean.getMusicName());
                supportActionBar.setSubtitle(musicBean.getSinger());
            }

            Log.w(TAG, "onPlayStateChanged: ");
            PictureLoader.newInstance(getActivity()).setCacheBitmapFromMp3Idv3(mHandler, musicBean.getPath(), mAlbumPicture, mAlbumPictureSize, mAlbumPictureSize);


            updateViewState(isPlaying, duration, progress);
        }

        @Override
        public void onComplete() {
            updateViewState(false, 100, 0);
        }

        @Override
        public void updateProgress(int currentProgress) {
            if (!isSeekBarHeld) {
                mProgress_sb.setProgress(currentProgress);
                mProgress_tv.setText(DateFormat.format("mm:ss", currentProgress));
                String pro = (String) DateFormat.format("mm:ss", currentProgress);
                Log.w(TAG, "updateViewState: pro: " + pro);
            }
        }

        private void updateViewState(boolean isPlaying, int maxProgress, int currentProgress) {
            mPlay_bt.setImageResource(isPlaying ? R.drawable.selector_btn_pause : R.drawable.selector_btn_play);
            mProgress_sb.setMax(maxProgress);
            mProgress_sb.setProgress(currentProgress);
            String dur = (String) DateFormat.format("mm:ss", maxProgress);
            Log.w(TAG, "updateViewState: dur: " + dur);
            mDuration_tv.setText(dur);
            String pro = (String) DateFormat.format("mm:ss", currentProgress);
            Log.w(TAG, "updateViewState: pro: " + pro);
            mProgress_tv.setText(pro);
            Log.w(TAG, "updateViewState: max: " + mProgress_sb.getMax() + "; progress: " + mProgress_sb.getProgress());
        }
    }
    ///////////==注册Controller的监听接口end==/////////////


    /////////////==进度条SeekBar的事件监听start==////////////////
    private class MainPageSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            isSeekBarHeld = true;
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            mController.seekTo(seekBar.getProgress());
            isSeekBarHeld = false;
        }
    }
    /////////////==进度条SeekBar的事件监听end==////////////////
}
