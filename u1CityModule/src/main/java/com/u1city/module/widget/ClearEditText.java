package com.u1city.module.widget;


import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.EditText;

import com.u1city.module.R;

/**
可清除的编辑框
 */
public class ClearEditText extends EditText {
	private final static String TAG = "ClearableEditText";
	private Context mContext;

//    private int mClearDrawableCleanedRes = R.drawable.clear_edit_cleaned;
    private int mClearDrawableRes = R.drawable.clear_edit_normal;

	/** 删除图标宽度 */
    private int mIntrinsicWidth;

	/** 控件第一次出现时在屏幕中的Y坐标 */
	private int locationY;

	public ClearEditText(Context context) {
		super(context);
		mContext = context;
		init();
	}

	public ClearEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		init();
	}

	public ClearEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();
	}
	
	private void init() {
		addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				setDrawable();
			}
		});
		setDrawable();
        mIntrinsicWidth = getContext().getResources().getDrawable(mClearDrawableRes).getIntrinsicWidth();
        Log.v(TAG, "mIntrinsicWidth:" + mIntrinsicWidth + " -- paddingRight:" + getPaddingRight());
	}

	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus) {
		int[] positions = new int[2];
		getLocationOnScreen(positions);

		if(locationY == 0){
			locationY = positions[1];
			Log.v(TAG, "locationY:" + locationY);
		}

		super.onWindowFocusChanged(hasWindowFocus);
	}

	public int getClearDrawableRes() {
		return mClearDrawableRes;
	}

	public void setClearDrawableRes(int clearDrawableRes) {
		this.mClearDrawableRes = clearDrawableRes;
	}

	//设置删除图片
	private void setDrawable() {
		Drawable[] compoundDrawables = getCompoundDrawables();
		
		if(length() < 1){
			Log.v(TAG, "hide");
			compoundDrawables[2] = null;
		}else{
			Log.v(TAG, "show");
			compoundDrawables[2] = getResources().getDrawable(mClearDrawableRes);
		}
		
		setCompoundDrawablesWithIntrinsicBounds(compoundDrawables[0], compoundDrawables[1], compoundDrawables[2], compoundDrawables[3]);
	}

    // 处理删除事件
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            int eventX = (int) event.getRawX();
            int eventY = (int) event.getRawY();
            Log.e(TAG, "eventX = " + eventX + "; eventY = " + eventY);
            Rect rect = new Rect();

            getGlobalVisibleRect(rect);
            rect.left = rect.right - mIntrinsicWidth - getPaddingRight();

			//在adjustPan等情况下控件在屏幕中的Y坐标可能发生变化
			int[] positions = new int[2];
			getLocationOnScreen(positions);
			Log.e(TAG, "postions[0]:" + positions[0] + "; postions[1]: " + positions[1]);
			int changeTop = (locationY - positions[1]);

			rect.top = rect.top - changeTop;
			rect.bottom = rect.bottom - changeTop;

			Log.e(TAG, "rect.left = " + rect.left + "; rect.right = " + rect.right
					+ "; rect.top = " + rect.top + "; rect.bottom = " + rect.bottom);

            if(rect.contains(eventX, eventY)) {
				setText("");
			}


        }

		return super.onTouchEvent(event);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }

}
