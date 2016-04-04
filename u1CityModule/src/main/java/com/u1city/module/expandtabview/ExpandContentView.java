package com.u1city.module.expandtabview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.ToggleButton;
/**
 * @author zhengjb
 * 2015/03/23
 * ExpandPopWindow的contentView
 * 用于处理在列表外的点击
 * */
public class ExpandContentView extends LinearLayout{
	
	private ToggleButton tab;

	public ExpandContentView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	public void setTab(ToggleButton tab) {
		this.tab = tab;
	}

	public ExpandContentView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public ExpandContentView(Context context) {
		super(context);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		tab.setChecked(false);
		return true;
	}

}
