package com.u1city.module.widget;


import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

import com.u1city.module.common.Debug;


/***************************************
 * 
 * @author lwli
 * @date 2014-8-9
 * @time 下午12:10:10 类说明:商品管理滑动View
 * 
 **************************************/
public class ScrollLayout extends ViewGroup {
	private static final String TAG = "ScrollLayout";
	private Scroller mScroller;
	// 滑屏速率跟踪
	private VelocityTracker mVelocityTracker;
	private int mCurScreen;
	private int mDefaultScreen = 0;
	// 停止状态
	private static final int TOUCH_STATE_REST = 0;
	// 滑动状态
	private static final int TOUCH_STATE_SCROLLING = 1;
	// 手持的速率，当超过这个速率的就切换到下一屏
	public static final int SNAP_VELOCITY = 600;
	// 触屏的状态
	private int mTouchState = TOUCH_STATE_REST;
	private int mTouchSlop;
	private float mLastMotionX;
	private float mLastMotionY;
	private OnViewChangeListener mOnViewChangeListener;

	/**
	 * 设置是否可左右滑动
	 */
	private boolean isScroll = true;

	public void setIsScroll(boolean b) {
		this.isScroll = b;
	}

	public ScrollLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ScrollLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mScroller = new Scroller(context);
		mCurScreen = mDefaultScreen;
		mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int childLeft = 0;
		final int childCount = getChildCount();
		Log.e(TAG, "onLayout childCount " + childCount);
		for (int i = 0; i < childCount; i++) {
			final View childView = getChildAt(i);
			if (childView.getVisibility() != View.GONE) {
				final int childWidth = childView.getMeasuredWidth();
				childView.layout(childLeft, 0, childLeft + childWidth,
						childView.getMeasuredHeight());
				childLeft += childWidth;
			}
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// Log.e(TAG, "onMeasure");
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		final int width = MeasureSpec.getSize(widthMeasureSpec);
		final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		if (widthMode != MeasureSpec.EXACTLY) {
			throw new IllegalStateException(
					"ScrollLayout only canmCurScreen run at EXACTLY mode!");
		}
		final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		if (heightMode != MeasureSpec.EXACTLY) {
			throw new IllegalStateException(
					"ScrollLayout only can run at EXACTLY mode!");
		}

		// The children are given the same width and height as the scrollLayout
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
		}
		Log.e(TAG, "moving to screen " + mCurScreen);
		scrollTo(mCurScreen * width, 0);
	}

	/**
	 * According to the position of current layout scroll to the destination
	 * page.
	 */
	public void snapToDestination() {
		final int screenWidth = getWidth();
		final int destScreen = (getScrollX() + screenWidth / 2) / screenWidth;
		snapToScreen(destScreen);
	}

	public void snapToScreen(int whichScreen) {
		// 是否可滑动
		if (!isScroll) {
			this.setToScreen(whichScreen);
			return;
		}

		scrollToScreen(whichScreen);
	}

	public void scrollToScreen(int whichScreen) {
		// get the valid layout page
		whichScreen = Math.max(0, Math.min(whichScreen, getChildCount() - 1));
		Debug.println("....scrollToScreen....11..........>" + whichScreen + ";"
				+ getScrollX() + ";" + whichScreen * getWidth());
		if (getScrollX() != (whichScreen * getWidth())
				|| getScrollX() == whichScreen * getWidth()) {
			final int delta = whichScreen * getWidth() - getScrollX();
			mScroller.startScroll(getScrollX(), 0, delta, 0,
					Math.abs(delta) * 1);// 持续滚动时间 以毫秒为单位
			mCurScreen = whichScreen;
			invalidate(); // Redraw the layout
			Debug.println("........scrollToScreen......mCurScreen..........>"
					+ mCurScreen);
			if (mOnViewChangeListener != null) {
				mOnViewChangeListener.onViewChange(mCurScreen);
			}
		}
	}

	public void setToScreen(int whichScreen) {
		whichScreen = Math.max(0, Math.min(whichScreen, getChildCount() - 1));
		mCurScreen = whichScreen;
		scrollTo(whichScreen * getWidth(), 0);

		if (mOnViewChangeListener != null) {
			mOnViewChangeListener.onViewChange(mCurScreen);
		}
	}

	public int getCurScreen() {
		return mCurScreen;
	}

	/*
	 * 为了易于控制滑屏控制，Android框架提供了 computeScroll()方法去控制这个流程。在绘制View时，会在draw()过程调用该
	 * 方法。因此， 再配合使用Scroller实例，我们就可以获得当前应该的偏移坐标，手动使View/ViewGroup偏移至该处。
	 * computeScroll()方法原型如下，该方法位于ViewGroup.java类中
	 */
	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			postInvalidate();
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// 是否可滑动
		if (!isScroll) {
			return false;
		}

		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement(event);
		final int action = event.getAction();
		final float x = event.getX();
		final float y = event.getY();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			// Log.e(TAG, "event down!");
			if (!mScroller.isFinished()) {
				mScroller.abortAnimation();
			}
			mLastMotionX = x;

			// ---------------New Code----------------------
			mLastMotionY = y;
			// ---------------------------------------------

			break;
		case MotionEvent.ACTION_MOVE:
			int deltaX = (int) (mLastMotionX - x);

			// ---------------New Code----------------------
			int deltaY = (int) (mLastMotionY - y);
			if (Math.abs(deltaX) < 200 && Math.abs(deltaY) > 10)
				break;
			mLastMotionY = y;
			// -------------------------------------

			mLastMotionX = x;
			scrollBy(deltaX, 0);
			break;
		case MotionEvent.ACTION_UP:
			// Log.e(TAG, "event : up");
			// if (mTouchState == TOUCH_STATE_SCROLLING) {
			final VelocityTracker velocityTracker = mVelocityTracker;
			velocityTracker.computeCurrentVelocity(1000);
			int velocityX = (int) velocityTracker.getXVelocity();
			// Log.e(TAG, "velocityX:" + velocityX);
			if (velocityX > SNAP_VELOCITY && mCurScreen > 0) {
				// Fling enough to move left
				// Log.e(TAG, "snap left");
				snapToScreen(mCurScreen - 1);
			} else if (velocityX < -SNAP_VELOCITY
					&& mCurScreen < getChildCount() - 1) {
				// Fling enough to move right
				// Log.e(TAG, "snap right");
				snapToScreen(mCurScreen + 1);
			} else {
				snapToDestination();
			}
			if (mVelocityTracker != null) {
				mVelocityTracker.recycle();
				mVelocityTracker = null;
			}
			// }
			mTouchState = TOUCH_STATE_REST;
			break;
		case MotionEvent.ACTION_CANCEL:
			mTouchState = TOUCH_STATE_REST;
			break;
		}
		return true;
	}

	/*
	 * 这个方法里面，如果发现用户是在滑动（即偏移量大于某个值），就进行拦截
	 * 
	 * 我们唯一的一次想拦截移动事件是我们在拖动模式。
	 * 
	 * @see
	 * android.view.ViewGroup#onInterceptTouchEvent(android.view.MotionEvent)
	 */

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		Log.e(TAG, "onInterceptTouchEvent-slop:" + mTouchSlop);
		// final int action = ev.getAction();
		// if ((action == MotionEvent.ACTION_MOVE)
		// && (mTouchState != TOUCH_STATE_REST)) {
		// return true;
		// }
		// final float x = ev.getX();
		// final float y = ev.getY();
		// switch (action) {
		// case MotionEvent.ACTION_MOVE:
		// final int xDiff = (int) Math.abs(mLastMotionX - x);
		// if (xDiff > mTouchSlop) {
		// mTouchState = TOUCH_STATE_SCROLLING;
		// }
		// break;
		// case MotionEvent.ACTION_DOWN:
		// mLastMotionX = x;
		// mLastMotionY = y;
		// mTouchState = mScroller.isFinished() ? TOUCH_STATE_REST
		// : TOUCH_STATE_SCROLLING;
		// break;
		// case MotionEvent.ACTION_CANCEL:
		// case MotionEvent.ACTION_UP:
		// mTouchState = TOUCH_STATE_REST;
		// break;
		// }
		// return mTouchState != TOUCH_STATE_REST;
		final int action = ev.getAction();

		if ((action == MotionEvent.ACTION_MOVE)

		&& (mTouchState != TOUCH_STATE_REST)) {

			return true;

		}

		final float x = ev.getX();

		final float y = ev.getY();

		switch (action) {

		case MotionEvent.ACTION_DOWN:
			mLastMotionX = x;

			mLastMotionY = y;

			mTouchState = mScroller.isFinished() ? TOUCH_STATE_REST

			: TOUCH_STATE_SCROLLING;
			if(mOnViewChangeListener!=null)mOnViewChangeListener.onViewScroing();
			break;

		case MotionEvent.ACTION_MOVE:

			final int xDiff = (int) Math.abs(mLastMotionX - x);

			if (xDiff > mTouchSlop) {

				if (Math.abs(mLastMotionY - y) / Math.abs(mLastMotionX - x) < 1)

					mTouchState = TOUCH_STATE_SCROLLING;

			}
			if(mOnViewChangeListener!=null)mOnViewChangeListener.onViewScroing();

			break;

		case MotionEvent.ACTION_CANCEL:

		case MotionEvent.ACTION_UP:

			mTouchState = TOUCH_STATE_REST;
			if(mOnViewChangeListener!=null)mOnViewChangeListener.actionUp();
			break;

		}

		return mTouchState != TOUCH_STATE_REST;
	}

	/**
	 * 设置屏幕切换监听器
	 * 
	 * @param listener
	 */
	public void SetOnViewChangeListener(OnViewChangeListener listener) {
		mOnViewChangeListener = listener;
	}

	/**
	 * 屏幕切换监听器
	 * 
	 * @author liux
	 */
	public interface OnViewChangeListener {
		public void onViewChange(int view);
		public void onViewScroing();
		public void actionUp();
	}
}