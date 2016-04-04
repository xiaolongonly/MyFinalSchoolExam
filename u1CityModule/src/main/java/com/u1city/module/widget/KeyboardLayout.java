package com.u1city.module.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.RelativeLayout;

public class KeyboardLayout extends RelativeLayout {
 
    private ICallBack callBack;
    private static final String TAG ="KeyboardLayoutTAG";
    private boolean mShowKeyboard = false;
     
    public KeyboardLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }
 
    public KeyboardLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }
 
    public KeyboardLayout(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }
     
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // TODO Auto-generated method stub
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.d(TAG, "onMeasure-----------");
    }
 
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // TODO Auto-generated method stub
        super.onLayout(changed, l, t, r, b);
        Log.d(TAG, "onLayout-------------------");
    }
     
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        // TODO Auto-generated method stub
        super.onSizeChanged(w, h, oldw, oldh);
        Log.d(TAG, "--------------------------------------------------------------");
        Log.d(TAG, "w----" + w + "\n" + "h-----" + h + "\n" + "oldW-----" + oldw + "\noldh----" + oldh);
        if (null != callBack && 0 != oldw && 0 != oldh) {
            if (h < oldh) {
                mShowKeyboard = true;
            } else {
                mShowKeyboard = false;
            }
            callBack.onChanged(mShowKeyboard);
            Log.d(TAG, "mShowKeyboard-----      " + mShowKeyboard);
        }
    }
    public void setCallBack(ICallBack callBack) {
		this.callBack = callBack;
	}
 
     public  interface ICallBack{
         
        void onChanged(boolean showKeyboard);
    }
     
}