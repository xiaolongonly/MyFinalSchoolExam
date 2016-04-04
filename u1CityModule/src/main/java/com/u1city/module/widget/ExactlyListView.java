package com.u1city.module.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/***************************************
 * 
 * @author lwli
 * @modify zhengjb
 * @date 2014-9-1 
 * @time 下午12:30:15
 * 类说明:显示最大的ListView,没有滑动
 * 
 **************************************/
public class ExactlyListView extends ListView {
	public ExactlyListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	public ExactlyListView(Context context) {
		super(context);
	}
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	      int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);  
		super.onMeasure(widthMeasureSpec, expandSpec);
	}
}
