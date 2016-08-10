package com.zhangheng.mymusicplayer.fragment;

import android.app.Fragment;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.project.myutilslibrary.mp3agic.InvalidDataException;
import com.project.myutilslibrary.mp3agic.UnsupportedTagException;
import com.project.myutilslibrary.pictureloader.PictureLoader;
import com.wosloveslife.utils.wrapper_picture.BlurUtils;
import com.zhangheng.mymusicplayer.R;
import com.zhangheng.mymusicplayer.activity.MainPageActivity;
import com.zhangheng.mymusicplayer.bean.MusicBean;
import com.zhangheng.mymusicplayer.engine.Controller;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by WosLovesLife on 2016/4/30.
 * 播放页面的主页面
 * 和Controller沟通 通过在此页面所做的控制,通过Controller做逻辑处理
 * Controller通过回调方法将播放状态返回给本类更新UI
 */
public class MainPageFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "MainPageFragment";

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

    /* 第一次打开时不显示过度动画 */
    private boolean mIsReshow;

    private Bitmap mAlbumBitmap;
    private Bitmap mBgBitmap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EventBus.getDefault().register(this);

        setRetainInstance(true);// 保留Fragment状态,在Activity销毁重建的过程中不销毁Fragment
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_main_page, container, false);

        mBind = ButterKnife.bind(this, mView);

        return mView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        /** 获取控制器 */
        mController = Controller.newInstance(getActivity());

        setViewFunction();

        initAlbumPictureSize();
    }

    @Override
    public void onStart() {
        super.onStart();

        mIsReshow = true;
    }

    /** 停止监听,节省资源 */
    @Override
    public void onDestroy() {
        super.onDestroy();

        mBind.unbind();

        EventBus.getDefault().unregister(this);

        Log.w(TAG, "onDestroy: ");
    }

    /** 初始化专辑图片的ImageView尺寸 */
    private void initAlbumPictureSize() {

        final CardView albumFrame = (CardView) mView.findViewById(R.id.albumFrame);
        albumFrame.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                albumFrame.getViewTreeObserver().removeOnPreDrawListener(this);

                View albumParentLayout = mView.findViewById(R.id.album_parent_layout);
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) albumParentLayout.getLayoutParams();
                int actionbarHeight = ((AppCompatActivity) getActivity()).getSupportActionBar().getHeight();
                params.setMargins(0, actionbarHeight, 0, 0);

                int widthPixels = albumParentLayout.getWidth();
                int heightPixels = albumParentLayout.getHeight();

                if (widthPixels < heightPixels) {
                    mAlbumPictureSize = (int) (widthPixels * 0.9f);
                } else {
                    mAlbumPictureSize = (int) (heightPixels * 0.9f);
                }

                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) albumFrame.getLayoutParams();
                layoutParams.height = mAlbumPictureSize;
                layoutParams.width = mAlbumPictureSize;
                albumFrame.setLayoutParams(layoutParams);

                /** 监听歌曲信息和播放状态 */
                mController.syncPlayerState();

                return true;
            }
        });
    }

    private void setViewFunction() {
        mProgress_sb.setOnSeekBarChangeListener(new MainPageSeekBarChangeListener());
    }

    @Override
    @OnClick({R.id.play_mainpageButton, R.id.pre_mainpageButton, R.id.next_mainpageButton})
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

    // Event处理
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChangeMusicEvent(Controller.ChangeMusicEvent event) {
        Controller.BaseEvent baseEvent = event.mBaseEvent;
        disposeStateChanged(baseEvent.musicBean, baseEvent.isPlaying, baseEvent.duration, baseEvent.progress);

        if (baseEvent.musicBean == null) return;

        Observable.just(baseEvent.musicBean)
                .map(new Func1<MusicBean, String>() {
                    @Override
                    public String call(MusicBean musicBean) {
                        return musicBean.getPath();
                    }
                })
                .map(new Func1<String, Bitmap>() {
                    @Override
                    public Bitmap call(String musicPath) {
                        try {
                            return PictureLoader.getAlbumFromMp3(musicPath, mAlbumPictureSize, mAlbumPictureSize);
                        } catch (InvalidDataException | IOException | UnsupportedTagException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Bitmap>() {
                    @Override
                    public void onCompleted() {
                        Log.w(TAG, "onCompleted: ");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: ", e);
                    }

                    @Override
                    public void onNext(Bitmap bitmap) {
                        Log.w(TAG, "onNext: ");
                        setPlayerSkin(bitmap);
                    }
                });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPlayStateChangedEvent(Controller.PlayerStateChangedEvent event) {
        Controller.BaseEvent baseEvent = event.mBaseEvent;
        disposeStateChanged(baseEvent.musicBean, baseEvent.isPlaying, baseEvent.duration, baseEvent.progress);
    }

    private void disposeStateChanged(MusicBean musicBean, boolean isPlaying, int duration, int progress) {
        AppCompatActivity a = (AppCompatActivity) getActivity();
        if (a == null) return;

        ActionBar supportActionBar = a.getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setTitle(musicBean == null ? "" : musicBean.getMusicName());
            supportActionBar.setSubtitle(musicBean == null ? "" : musicBean.getSinger());
        }

        updateViewState(isPlaying, duration, progress);
        Log.w(TAG, "onPlayStateChanged: ");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCompleteEvent(Controller.CompleteEvent event) {
        updateViewState(false, 100, 0);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateProgress(Controller.UpdateProgressEvent event) {
        if (!isSeekBarHeld) {
            mProgress_sb.setProgress(event.mCurrentProgress);

            String pro = (String) DateFormat.format("mm:ss", event.mCurrentProgress);
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
        View bgView = ((MainPageActivity) getActivity()).getBgView();

        Bitmap bgBitmap = bitmap;
        if (bitmap == null) {
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.player_default_album);
            bgBitmap = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.playpage_background);
        }

        Drawable targetAlbumDrawable;
        Drawable targetBgDrawable;
        if (mIsReshow) {
            mIsReshow = false;

            /* 设置不带动画的专辑图片 */
            targetAlbumDrawable = new BitmapDrawable(getResources(), bitmap);
            /* 设置不带动画的背景模糊图片 */
            targetBgDrawable = new BitmapDrawable(getResources(), BlurUtils.makePictureBlur(getActivity(), bgBitmap, bgView, 2, 50));
        } else {
            /* 如果原封面为null,则赋默认值 */
            Drawable sourceAlbumDrawable = mAlbumPicture.getDrawable();
            if (sourceAlbumDrawable == null) {
                sourceAlbumDrawable = new BitmapDrawable(getResources(), BitmapFactory.decodeResource(getResources(), android.R.color.white));
            }
            /* 转换成过渡Drawable */
            targetAlbumDrawable = getTransitionDrawable(sourceAlbumDrawable, new BitmapDrawable(getResources(), bitmap), 520);

            /* 如果原背景为null,则赋默认值 */
            Drawable sourceBgDrawable = bgView.getBackground();
            if (sourceBgDrawable == null) {
                sourceBgDrawable = new BitmapDrawable(getResources(), BitmapFactory.decodeResource(getResources(), R.drawable.playpage_background));
            }
            /* 对原图片进行高斯模糊处理, 并转换成过渡Drawable */
            BitmapDrawable shadowBg = new BitmapDrawable(getResources(), BlurUtils.makePictureBlur(getActivity(), bgBitmap, bgView, 2, 50));
            targetBgDrawable = getTransitionDrawable(sourceBgDrawable, shadowBg, 520);
        }
        mAlbumPicture.setImageDrawable(targetAlbumDrawable);
        bgView.setBackground(targetBgDrawable);
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
}
