package com.zhangheng.mymusicplayer.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.project.myutilslibrary.pictureloader.PictureLoader;
import com.project.myutilslibrary.wrapper_picture.BlurUtils;
import com.zhangheng.mymusicplayer.R;
import com.zhangheng.mymusicplayer.activity.MainPageActivity;
import com.zhangheng.mymusicplayer.activity.MusicListActivity;
import com.zhangheng.mymusicplayer.bean.MusicBean;
import com.zhangheng.mymusicplayer.engine.Controller;
import com.zhangheng.mymusicplayer.listener.OnPlayerStateChangedListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by WosLovesLife on 2016/4/30.
 * 播放页面的主页面
 * 和Controller沟通 通过在此页面所做的控制,通过Controller做逻辑处理
 * Controller通过回调方法将播放状态返回给本类更新UI
 */
public class MainPageFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "MainPageFragment";

    public static final int REQUEST_CODE_LIST_FRAGMENT = 0;

    /** 控制器对象,当界面上的组件被触发(点击)是调用相关的控制器方法即可实现相应的功能,无需关注判断逻辑 */
    private Controller mController;

    /** Butterknife对象,在Ondestroy的时候Unbinder */
    private Unbinder mBind;

    //--组件-start--
    /** 填充给Activity的View */
    private View mView;

    /** 进度条,拖动改变播放进度 */
    @BindView(R.id.progress_mainpageSeekBar)
    SeekBar mProgress_sb;

    /** 播放/暂停键 */
    @BindView(R.id.play_mainpageButton)
    ImageButton mPlay_bt;

    /** 上一首键 */
    @BindView(R.id.pre_mainpageButton)
    ImageButton mPre_bt;

    /** 下一首键 */
    @BindView(R.id.next_mainpageButton)
    ImageButton mNext_bt;

    /** 显示当前播放的时间节点的TextView */
    @BindView(R.id.currentProgress_TextView)
    TextView mProgress_tv;

    /** 显示歌曲总时长的TextView */
    @BindView(R.id.duration_TextView)
    TextView mDuration_tv;

    /** 专辑图片的组件 */
    @BindView(R.id.albumPicture)
    ImageView mAlbumPicture;
    //--组件-end--

    /** 状态值,如果SeekBar在拖动中,暂停SeekBar的自动进度移动 */
    private boolean isSeekBarHeld;

    /** 显示专辑图片的控件的尺寸,用于决定图片的大小 */
    private int mAlbumPictureSize;

    private boolean mIsReshow;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBind = ButterKnife.bind(getActivity());

        setHasOptionsMenu(true);// 告知FragmentManager本页面包含Menu
        setRetainInstance(true);// 保留Fragment状态,在Activity销毁重建的过程中不销毁Fragment
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_main_page, container, false);

        mNext_bt = (ImageButton) mView.findViewById(R.id.next_mainpageButton);
        mPre_bt = (ImageButton) mView.findViewById(R.id.pre_mainpageButton);
        mPlay_bt = (ImageButton) mView.findViewById(R.id.play_mainpageButton);
        mProgress_sb = (SeekBar) mView.findViewById(R.id.progress_mainpageSeekBar);
        mDuration_tv = (TextView) mView.findViewById(R.id.duration_TextView);
        mProgress_tv = (TextView) mView.findViewById(R.id.currentProgress_TextView);
        mAlbumPicture = (ImageView) mView.findViewById(R.id.albumPicture);

        return mView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mIsReshow = true;

        /** 获取控制器 */
        mController = Controller.newInstance(getActivity());

        /** 监听歌曲信息和播放状态 */
        mController.setOnPlayerStateChangedListener(new MainPagePlayerStateChangedListener());

        setViewFunction();

        initAlbumPictureSize();
    }

    /** 停止监听,节省资源 */
    @Override
    public void onDestroy() {
        super.onDestroy();

        mController.removeOnPlayerStateChangedListener();

        mBind.unbind();

        Log.w(TAG, "onDestroy: ");
    }

    /** 初始化专辑图片的ImageView尺寸 */
    private void initAlbumPictureSize() {

        final CardView albumFrame = (CardView) mView.findViewById(R.id.albumFrame);
        albumFrame.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                albumFrame.getViewTreeObserver().removeOnPreDrawListener(this);

                DisplayMetrics displayMetrics = new DisplayMetrics();
                getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int widthPixels = displayMetrics.widthPixels;
                int heightPixels = displayMetrics.heightPixels / 2;

                if (widthPixels < heightPixels) {
                    mAlbumPictureSize = (int) (widthPixels * 0.8f);
                } else {
                    mAlbumPictureSize = (int) (heightPixels * 0.8f);
                }

                ViewGroup.LayoutParams layoutParams = albumFrame.getLayoutParams();
                layoutParams.height = mAlbumPictureSize;
                layoutParams.width = mAlbumPictureSize;
                albumFrame.setLayoutParams(layoutParams);

                return true;
            }
        });
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

    ///////////==注册Controller的监听接口start==/////////////
    private class MainPagePlayerStateChangedListener implements OnPlayerStateChangedListener {

        @Override
        public void onChangeMusic(boolean isPlaying, int duration, int progress, MusicBean musicBean) {
            onPlayStateChanged(isPlaying, duration, progress, musicBean);

            PictureLoader.newInstance().setCacheBitmapFromMp3Idv3(
                    new PictureLoader.OnPictureLoadHandleListener() {
                        @Override
                        public void onPictureLoadComplete(Bitmap bitmap) {
                            setPlayerSkin(bitmap);
                        }
                    },
                    musicBean.getPath(),
                    mAlbumPicture,
                    mAlbumPictureSize,
                    mAlbumPictureSize);
        }

        @Override
        public void onPlayStateChanged(boolean isPlaying, int duration, int progress, MusicBean musicBean) {
            AppCompatActivity a = (AppCompatActivity) getActivity();
            if (a == null) return;

            ActionBar supportActionBar = a.getSupportActionBar();
            if (supportActionBar != null) {
                supportActionBar.setTitle(musicBean.getMusicName());
                supportActionBar.setSubtitle(musicBean.getSinger());
            }

            updateViewState(isPlaying, duration, progress);

            Log.w(TAG, "onPlayStateChanged: ");
        }

        @Override
        public void onComplete() {
            updateViewState(false, 100, 0);
        }

        @Override
        public void updateProgress(int currentProgress) {
            if (!isSeekBarHeld) {
                mProgress_sb.setProgress(currentProgress);

                String pro = (String) DateFormat.format("mm:ss", currentProgress);
                mProgress_tv.setText(pro);
                Log.w(TAG, "updateViewState: pro: " + pro);
            }
        }

        private void updateViewState(boolean isPlaying, int maxProgress, int currentProgress) {
            /* 改变播放按钮的状态, 播放或者暂停 */
            mPlay_bt.setImageResource(isPlaying ? R.drawable.selector_btn_pause : R.drawable.selector_btn_play);

            mProgress_sb.setMax(maxProgress);   // 最大进度
            mProgress_sb.setProgress(currentProgress);  // 当前播放进度

            String dur = (String) DateFormat.format("mm:ss", maxProgress);  // 设置歌曲失常
            mDuration_tv.setText(dur);

            String pro = (String) DateFormat.format("mm:ss", currentProgress);  // 设置当前播放时常
            mProgress_tv.setText(pro);
        }

        /** 设置播放器的皮肤, 专辑图片和背景图片 */
        private void setPlayerSkin(Bitmap bitmap) {

            Bitmap bgBitmap = bitmap;
            if (bitmap == null) {
                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.player_default_album);
                bgBitmap = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.playpage_background);
            }

            View bgView = ((MainPageActivity) getActivity()).getBgView();
            Drawable sourceBg = bgView.getBackground();

            if (mIsReshow) {
                mIsReshow = false;

                /* 设置不带动画的专辑图片 */
                mAlbumPicture.setImageDrawable(new BitmapDrawable(bitmap));
                /* 设置不带动画的背景模糊图片 */
                BitmapDrawable shadowBg = BlurUtils.makePictureBlur(getActivity(), bgBitmap, bgView, 2, 30);
                bgView.setBackground(shadowBg);
            } else {

                /* 设置带平滑过渡动画的专辑图片 */
                Drawable drawable = mAlbumPicture.getDrawable();
                if (drawable == null) {
                    drawable = new BitmapDrawable(BitmapFactory.decodeResource(getResources(), android.R.color.white));
                }
                TransitionDrawable albumPictureWithShadow = getTransitionDrawable(drawable, new BitmapDrawable(bitmap), 520);
                mAlbumPicture.setImageDrawable(albumPictureWithShadow);

                /* 设置带平滑过渡动画的背景模糊图片 */
                if (sourceBg == null) {
                    sourceBg = new BitmapDrawable(BitmapFactory.decodeResource(getResources(), android.R.color.white));
                }
               /* 对原图片进行高斯模糊处理 */
                BitmapDrawable shadowBg = BlurUtils.makePictureBlur(getActivity(), bgBitmap, bgView, 2, 50);
                TransitionDrawable transitionDrawable = getTransitionDrawable(sourceBg, shadowBg, 520);
                bgView.setBackground(transitionDrawable);
            }
        }
    }

    /** 色彩平滑过渡的动画 */
    private TransitionDrawable getTransitionDrawable(Drawable source, Drawable target, int duration) {
        TransitionDrawable transitionDrawable = new TransitionDrawable(
                new Drawable[]{source, target});
        transitionDrawable.setCrossFadeEnabled(true);
        transitionDrawable.startTransition(duration);
        return transitionDrawable;
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
