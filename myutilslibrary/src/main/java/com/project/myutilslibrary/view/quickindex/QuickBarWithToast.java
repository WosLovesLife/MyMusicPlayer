package com.project.myutilslibrary.view.quickindex;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.project.myutilslibrary.R;

public class QuickBarWithToast extends FrameLayout implements QuickBarView.OnQuickBarTouchListener {

	private TextView mCurrentWord;
	private boolean mIsQuickBarTouching;
	private OnIndexChangedListener mOnIndexChangedListener;
	private QuickBarView mQuickBarView;

	public QuickBarWithToast(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		init(context,attrs);
	}

	public QuickBarWithToast(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public QuickBarWithToast(Context context) {
		this(context, null);
	}

	private void init(Context context, AttributeSet attrs) {
		View view = View.inflate(getContext(), R.layout.view_quick_index, this);
		mQuickBarView = (QuickBarView) view.findViewById(R.id.quickBar);
		mCurrentWord = (TextView) view.findViewById(R.id.currentWord);

		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.QuickBarWithToast);
		int attr_background = typedArray.getResourceId(R.styleable.QuickBarWithToast_barBackground, -1);
		if (attr_background != -1){
			mQuickBarView.setBackgroundResource(attr_background);
		}else {
			attr_background = typedArray.getInt(R.styleable.QuickBarWithToast_barBackground,-1);
			if (attr_background!=-1){
				mQuickBarView.setBackgroundColor(attr_background);
			}
		}
		float attr_textSize = typedArray.getDimension(R.styleable.QuickBarWithToast_toastTextSize, 38f);
		mCurrentWord.setTextSize(attr_textSize);
		int attr_textColor = typedArray.getColor(R.styleable.QuickBarWithToast_toastTextColor, Color.WHITE);
		mCurrentWord.setTextColor(attr_textColor);

		typedArray.recycle();
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		mQuickBarView.setOnQuickBarTouchListener(this);
	}

	@Override
	public void onIndexChanged(String index) {
		mCurrentWord.setText(index);
		if (!mIsQuickBarTouching) {
			mCurrentWord.setVisibility(View.VISIBLE);
			wordAnim();
			mIsQuickBarTouching = true;
		}
		if (mOnIndexChangedListener != null) {
			mOnIndexChangedListener.onIndexChanged(index);
		}
	}

	@Override
	public void onFingerUp() {
		wordAnimEnd();
		mIsQuickBarTouching = false;
	}

	private void wordAnim() {
		ScaleAnimation scaleAnimation = new ScaleAnimation(0f, 1f, 0f, 1f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		scaleAnimation.setDuration(270);
		mCurrentWord.startAnimation(scaleAnimation);
	}

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

			@Override
			public void onAnimationEnd(Animation animation) {
				mCurrentWord.clearAnimation();
				mCurrentWord.setVisibility(View.INVISIBLE);
			}
		});
		mCurrentWord.startAnimation(scaleAnimation);
	}

	public void setOnIndexChangedListener(OnIndexChangedListener onIndexChangedListener) {
		mOnIndexChangedListener = onIndexChangedListener;
	}

	public interface OnIndexChangedListener {
		void onIndexChanged(String index);
	}
}
