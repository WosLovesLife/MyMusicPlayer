package com.project.myutilslibrary.view.quickindex;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.project.myutilslibrary.R;

public class QuickBarWithToast extends FrameLayout implements QuickBarView.OnQuickBarTouchListener {

    private TextView mCurrentWord;
    private boolean mIsQuickBarTouching;
    public OnIndexChangedListener mOnIndexChangedListener;
    private QuickBarView mQuickBarView;

    private int mAttr_background;
    private static final int BG_MODE_DEFAULT = 0;
    private static final int BG_MODE_DRAWABLE = 1;
    private static final int BG_MODE_COLOR = 2;
    private int mBackgroundMode = 0;

    public QuickBarWithToast(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init(context, attrs);
    }

    public QuickBarWithToast(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public QuickBarWithToast(Context context) {
        this(context, null);
    }

    private void init(Context context, AttributeSet attrs) {
        /** 获取QuickBar的默认布局View */
        View view = View.inflate(getContext(), R.layout.view_quick_index, this);
        /** 初始化QuickBar右侧的索引栏 */
        mQuickBarView = (QuickBarView) view.findViewById(R.id.quickBar);

        quickAnimExit();

        /** 初始化显示在屏幕中间的浮动TextView,效果类似于Toast.用于实时显示用户当前滑动到的索引位置 */
        mCurrentWord = (TextView) view.findViewById(R.id.currentWord);

        /** 通过TypeArray拿到自定义属性的值,可以通过自定义属性指定QuickBar索引栏的背景,CurrentWord的背景等 */
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.QuickBarWithToast);

        /** 获取自定义索引栏背景属性,如果设置了背景,就用自定义的背景,否则用默认的背景 */
//        mAttr_background = typedArray.getResourceId(R.styleable.QuickBarWithToast_barBackground, -1);
//        if (mAttr_background != -1) {
//            mBackgroundMode = BG_MODE_DRAWABLE;
//        } else {
//            /** 自定义背景也可以是颜色,所以这里还需要在判断一下 */
//            mAttr_background = typedArray.getColor(R.styleable.QuickBarWithToast_barBackground, -1);
//            if (mAttr_background != -1) {
//                mBackgroundMode = BG_MODE_COLOR;
//            }
//        }

        /** 获取CurrentWord的字体大小属性,默认是30dp, 需要依赖Dp2Px工具类来进行dp2px的转换 */
        float attr_textSize = typedArray.getDimension(R.styleable.QuickBarWithToast_toastTextSize, 38);
        mCurrentWord.setTextSize(attr_textSize);

        /** 获取CurrentWord的字体颜色属性, 默认是白色 */
        int attr_textColor = typedArray.getColor(R.styleable.QuickBarWithToast_toastTextColor, Color.WHITE);
        mCurrentWord.setTextColor(attr_textColor);

        /** 释放TypeArray */
        typedArray.recycle();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mQuickBarView.setOnQuickBarTouchListener(this);
    }

    /** 本类实现了QuickBarView的QuickBarView.OnQuickBarTouchListener接口.触摸时触发此回调方法 */
    @Override
    public void onIndexChanged(String index) {
        /** TextView实时显示用户当前触摸的字母 */
        mCurrentWord.setText(index);
        /** 第一次按下时,伴随动画显示Toast */
        if (!mIsQuickBarTouching) {
            /** 在触摸时显示背景 */
//            if (mBackgroundMode == BG_MODE_DRAWABLE) {
//                mQuickBarView.setBackgroundResource(mAttr_background);
//            } else if (mBackgroundMode == BG_MODE_COLOR) {
//                mQuickBarView.setBackgroundColor(mAttr_background);
//            } else {
//                mQuickBarView.setBackgroundColor(0xfffcfc);
//            }
//            mQuickBarView.setVisibility(View.VISIBLE);

            quickAnimEnter();

            mCurrentWord.setVisibility(View.VISIBLE);
            wordAnim();
            mIsQuickBarTouching = true;
        }
        /** 调用对外的监听者,传出当前触摸的索引位置 */
        if (mOnIndexChangedListener != null) {
            mOnIndexChangedListener.onIndexChanged(index);
        }
    }

    /** OnQuickBarTouchListener接口回调方法,用户手指抬起时伴随动画隐藏CurrentWord */
    @Override
    public void onFingerUp() {
//        mQuickBarView.setVisibility(View.INVISIBLE);
//        mQuickBarView.setBackgroundResource(android.R.color.transparent);

        quickAnimExit();

        wordAnimEnd();
        mIsQuickBarTouching = false;
    }

    private void quickAnimEnter() {
        TranslateAnimation animation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 1, Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
        animation.setDuration(200);
        animation.setFillAfter(true);
        mQuickBarView.startAnimation(animation);
    }

    private void quickAnimExit() {
        TranslateAnimation animation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1,
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
        animation.setDuration(0);
        animation.setFillAfter(true);
        mQuickBarView.startAnimation(animation);
    }

    /** TextView显示的动画 */
    private void wordAnim() {
        ScaleAnimation scaleAnimation = new ScaleAnimation(0f, 1f, 0f, 1f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(270);
        mCurrentWord.startAnimation(scaleAnimation);
    }

    /** TextView隐藏的动画 */
    private void wordAnimEnd() {
        ScaleAnimation scaleAnimation = new ScaleAnimation(1f, 0f, 1f, 0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(270);
        scaleAnimation.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            /** 在动画结束后隐藏TextView */
            @Override
            public void onAnimationEnd(Animation animation) {
                mCurrentWord.clearAnimation();
                mCurrentWord.setVisibility(View.INVISIBLE);
            }
        });
        mCurrentWord.startAnimation(scaleAnimation);
    }

    /** 通过注册该监听,当用户触摸的索引发生改变时,触发回调方法 */
    public void setOnIndexChangedListener(OnIndexChangedListener onIndexChangedListener) {
        mOnIndexChangedListener = onIndexChangedListener;
    }

    /** 回调接口,实现方法返回触摸的索引字符 */
    public interface OnIndexChangedListener {
        void onIndexChanged(String index);
    }
}
