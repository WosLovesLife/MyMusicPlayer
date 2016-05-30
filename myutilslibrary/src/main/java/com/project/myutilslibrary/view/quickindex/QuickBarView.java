package com.project.myutilslibrary.view.quickindex;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.project.myutilslibrary.Dp2Px;

public class QuickBarView extends View {

	private int mWidth;
	private int mHeight;
	private Paint mPaint;
	private String[] mIndexes = {"#", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
			"N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };
	private float mCell;
	
	private OnQuickBarTouchListener mOnQuickBarTouchListener;

	public QuickBarView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initPaint();
	}

	public QuickBarView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public QuickBarView(Context context) {
		this(context, null);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		mWidth = w;
		mHeight = h;
		mCell = mHeight * 1F / mIndexes.length;
	}

	private void initPaint() {
		// 创建一个抗锯齿的笔刷
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaint.setColor(Color.WHITE);
		// 设置绘制参照点以x轴中间为原点
		mPaint.setTextAlign(Paint.Align.CENTER);
		mPaint.setTextSize(Dp2Px.toPX(getContext(),14));
	}

	@Override
	protected void onDraw(Canvas canvas) {
		float x = mWidth / 2;
		// 遍历数组，将26个字母全部绘制上来
		for (int i = 0; i < mIndexes.length; i++) {
			String text = mIndexes[i];
			// y:格子高度的一半 + 文字高度的一半 + position*格子高度
			float y = mCell / 2 + getTextHeight(text) / 2F + i * mCell;
			mPaint.setColor(i == mCurrentIndex ? Color.BLACK : Color.WHITE);
			canvas.drawText(text, x, y, mPaint);
		}
	}

	private int getTextHeight(String text) {
		// 作为赋值对象, paint将值付给Rect对象
		Rect bounds = new Rect();
		mPaint.getTextBounds(text, 0, text.length(), bounds);
		return bounds.height();
	}

	int mCurrentIndex = -1;

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_MOVE:
			// 显示悬浮框, 触发索引
			int index = (int) (event.getY() / mCell);
			if (index != mCurrentIndex && index < mIndexes.length && index >-1) {
				Log.w("Quick", "index: " + mIndexes[index]);
				mCurrentIndex = index;
				if (mOnQuickBarTouchListener != null) {
					mOnQuickBarTouchListener.onIndexChanged(mIndexes[mCurrentIndex]);
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			mCurrentIndex = -1;
			if (mOnQuickBarTouchListener != null) {
				mOnQuickBarTouchListener.onFingerUp();
			}
			break;
		}
		invalidate();
		return true;
	}
	
	
	public void setOnQuickBarTouchListener(OnQuickBarTouchListener onQuickBarTouchListener) {
		mOnQuickBarTouchListener = onQuickBarTouchListener;
	}
	
	public interface OnQuickBarTouchListener{
		void onIndexChanged(String index);
		void onFingerUp();
	}
}
