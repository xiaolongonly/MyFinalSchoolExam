package com.u1city.module.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

public class NoticeOverLayout extends RelativeLayout {
	
	private OnNoticeTouchListener onTouchListener;
	
	private float oldX;
	private float oldY;
	
	private boolean hasActionDowned = false;
	
	public OnNoticeTouchListener getOnNoticeTouchListener() {
		return onTouchListener;
	}

	public void setOnNoticeTouchListener(OnNoticeTouchListener onTouchListener) {
		this.onTouchListener = onTouchListener;
	}

	public NoticeOverLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public NoticeOverLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public NoticeOverLayout(Context context) {
		super(context);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		
		float x = event.getX();
		float y = event.getY();
		
		if(action == MotionEvent.ACTION_DOWN){
			hasActionDowned = true;
			oldX = event.getX();
			oldY = event.getY();
		}
		
		if(action == MotionEvent.ACTION_UP){
			if(hasActionDowned && x == oldX && y == oldY && onTouchListener != null){
				onTouchListener.onTouch(this);
			}
			
			hasActionDowned = false;
		}

		return true;
	}
	
	public interface OnNoticeTouchListener{
		public void onTouch(NoticeOverLayout layout);
	}

}
