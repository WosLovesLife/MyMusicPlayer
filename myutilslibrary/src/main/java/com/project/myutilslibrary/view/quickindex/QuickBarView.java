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
	/** 索引字符数组,除了26个字母,其他的特殊字符/数字都表示为 # */
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

	/** 在该方法中获取TextView的宽高,以及每个索引栏每个字符栅格的高度 */
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		mWidth = w;
		mHeight = h;

		/** 平分每个字符栅格所能够占据的高度, *1F是为了转换为float类型,使分割更加精准 */
		mCell = mHeight * 1F / mIndexes.length;
	}

	/** 创建一个Paint对象,绘制字符 */
	private void initPaint() {
		/** 创建一个抗锯齿的笔刷 */
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		/** 设置索引栏字符颜色为白色 */
		mPaint.setColor(0xD9202021);
		/** 设置绘制参照点以x轴中间为原点. 注意 绘制的顺序是从下而上的,x也就是文字底边的中间点 */
		mPaint.setTextAlign(Paint.Align.CENTER);
		/** 设置文字大小14dp,此处依赖Dp2Px工具类 */
		mPaint.setTextSize(Dp2Px.toPX(getContext(),14));
	}

	/** 在此方法中绘制字符及字符的位置 */
	@Override
	protected void onDraw(Canvas canvas) {
		float x = mWidth / 2;
		/** 遍历字符数组,每个字符的都居于它前一个字符的下面 */
		for (int i = 0; i < mIndexes.length; i++) {
			String text = mIndexes[i];
			/** 绘制的起始y = 格子高度的一半 + 文字高度的一半 + position*格子高度 */
			float y = mCell / 2 + getTextHeight(text) / 2F + i * mCell;
			mPaint.setColor(i == mCurrentIndex ?  Color.GRAY :Color.BLACK);
			canvas.drawText(text, x, y, mPaint);
		}
	}

	private int getTextHeight(String text) {
		// 作为赋值对象, paint将值付给Rect对象
		Rect bounds = new Rect();
		mPaint.getTextBounds(text, 0, text.length(), bounds);
		return bounds.height();
	}

	/** 索引的默认值是-1是为了方便触摸判断,表示没有触摸任何位置
	 * 因为0是字符集合的第一个值,所以如果该变量默认是0,就变成了一直在触摸第一个字符 */
	int mCurrentIndex = -1;

	/** 处理用户的触摸事件,以触摸的y轴位置为参照,对照y所对应的集合中的字符,触发回调 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_MOVE:
			// 显示悬浮框, 触发索引
			int index = (int) (event.getY() / mCell);
			/** 该判断表示只有触摸的区域超过了一栅格的高度才表示触摸的索引发生变化 */
			if (index != mCurrentIndex && index < mIndexes.length && index >-1) {
				Log.w("Quick", "index: " + mIndexes[index]);
				mCurrentIndex = index;
				if (mOnQuickBarTouchListener != null) {
					mOnQuickBarTouchListener.onIndexChanged(mIndexes[mCurrentIndex]);
				}
			}
			break;
		/** 在用户手指抬起时,复原默认索引为-1 并触发回调 */
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
	
	/** 注册监听器,当用户触摸的索引位置发生改变时触发回调 */
	public void setOnQuickBarTouchListener(OnQuickBarTouchListener onQuickBarTouchListener) {
		mOnQuickBarTouchListener = onQuickBarTouchListener;
	}

	/** 监听器接口 */
	public interface OnQuickBarTouchListener{
		void onIndexChanged(String index);
		void onFingerUp();
	}
}
