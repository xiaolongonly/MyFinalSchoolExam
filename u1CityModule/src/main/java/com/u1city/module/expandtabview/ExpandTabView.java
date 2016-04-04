package com.u1city.module.expandtabview;

import android.content.Context;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.u1city.module.R;
import com.u1city.module.common.Debug;
import com.u1city.module.util.DimensUtil;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author zhengjb
 * 
 *         2015/03/17
 * 
 *         可扩展的标签视图的容器 一般用于分类搜索或分类选择
 *         使用此视图时，请不要设置相关popwindow的OnDismissListener和相关tab的OnCheckedChangedListener
 * */
public class ExpandTabView extends LinearLayout {
	private static final String TAG = ExpandTabView.class.getName();

	private ArrayList<ToggleButton> tabs = new ArrayList<ToggleButton>();
	
	/** 弹出窗的集合 */
	private final HashMap<ToggleButton, BaseExpandPopWindow> popWindows = new HashMap<ToggleButton, BaseExpandPopWindow>();

	public ExpandTabView(Context context) {
		super(context);
		init();
	}

	public ExpandTabView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		setOrientation(LinearLayout.HORIZONTAL);
		setPadding(0, DimensUtil.dpToPixels(getContext(), 3), 0,
				DimensUtil.dpToPixels(getContext(), 3));
	}
	
	public ToggleButton newTab(int layoutRes){
		return newTab(layoutRes, 13, "全部分类", R.color.expandtab_text_color);
	}
	
	public ToggleButton newTab(int layoutRes, String text){
		return newTab(layoutRes, 13, text, R.color.expandtab_text_color);
	}
	
	public ToggleButton newTab(int layoutRes, int textSize, String text){
		return newTab(layoutRes, textSize, text, R.color.expandtab_text_color);
	}
	
	/** 定义tab的样式并创建 **/
	public ToggleButton newTab(int layoutRes, int textSize, String text, int textColor){
		ToggleButton tab = (ToggleButton) LayoutInflater.from(getContext()).inflate(layoutRes, null);
		tab.setTextSize(textSize);
		LayoutParams lp = new LayoutParams(0, LayoutParams.MATCH_PARENT);
		lp.weight = 1;
		tab.setLayoutParams(lp);
		tab.setSingleLine(true);
		tab.setEllipsize(TruncateAt.END);
		tab.setPadding(DimensUtil.dpToPixels(getContext(), 55), 0, DimensUtil.dpToPixels(getContext(), 40), 0);

		tab.setText(text);
		
		tab.setTextColor(getResources().getColor(textColor));
		tab.setGravity(Gravity.CENTER);
		return tab;
	}

	/** 获取tab视图 */
	private View getTabBorder(){
		View line = new TextView(getContext());
		LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
		line.setLayoutParams(layoutParams);
		line.setTop(10);
		line.setBottom(10);
		line.setBackgroundResource(R.drawable.brand_type_line);
		
		return line;
//		setTabBorder(line);
	}

	// 添加标签
	public void addTab(final ToggleButton tab, final BaseExpandPopWindow popWindow, boolean hasBorder, final IRefreshCallBack callBack) {
		popWindows.put(tab, popWindow);		
		
		//当tab被点击的时候，相应展开和收缩窗口
		tab.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					if(callBack!=null)
						callBack.refreshData();
					expandOnly(tab);
				}else{
					pullBack(tab);
				}
			}
		});
		

		addView(tab);
		tabs.add(tab);
		
		if(hasBorder){
			addView(getTabBorder());
		}
		
		postInvalidate();
	}
	
	/**刷新数据**/
	public interface IRefreshCallBack{		
		public void refreshData();
	}
	
	/** 设置拦截popwindow以外的点击事件 */
	public void windowTouchIntercepte(){
		if(popWindows == null && popWindows.size() != tabs.size()){
			return;
		}
		
		for (int i = 0; i < popWindows.size(); i++) {
			BaseExpandPopWindow popupWindow = popWindows.get(tabs.get(i));
			popupWindow.setTouchInterceptor(onTouchListener);
		}
	}
	
	
	/** 拦截popwindow以外的点击事件 */
	private OnTouchListener onTouchListener = new OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if(event.getY() < 0){
				int x = (int) event.getX();
				int width = DimensUtil.getDisplayWidth(getContext())/tabs.size();
				
				int i = x / width;
				
				if(tabs.size() > i){
					ToggleButton tab = tabs.get(i);
					tab.setChecked(!tab.isChecked());
				}
				
				return true;
			}
			
			return false;
		}
	};

	/** 展开要显示的窗口，并收回已显示的窗口 */
	private void expandOnly(ToggleButton tab) {
		Debug.d(TAG, "expandOnly");
		int currentTabIndex = indexOfChild(tab);
		Debug.d(TAG, "currentTabIndex:" + currentTabIndex);
		
		// 关闭其他所有未显示的
		for (int i = 0; i < getChildCount(); i++) {
			View view = getChildAt(i);

			if (view instanceof ToggleButton) {
				boolean isExpanded = ((ToggleButton) getChildAt(i)).isChecked();
				Debug.d(TAG, "isExpanded：" + isExpanded + " -- i:" + i);

				if (isExpanded && i != currentTabIndex) {
					((ToggleButton) view).setChecked(false);
				}
			}
		}

		// 显示被点击的tab
		expand(tab);
	}

	/**
	 * 展开 在此进行展开的相应回调和popwindow显示
	 * */
	private void expand(ToggleButton view) {
		Debug.d(TAG, "expand");
		BaseExpandPopWindow baseExpandPopWindow = popWindows.get(view);

		if (baseExpandPopWindow != null && !baseExpandPopWindow.isShowing()) {
			baseExpandPopWindow.show(this);
			baseExpandPopWindow.restoreSelected();
		}
	}

	/**
	 * 收缩 在此进行收缩的相应回调和popwindow的隐藏
	 * */
	private void pullBack(ToggleButton view) {
		Debug.d(TAG, "pullBack");
		BaseExpandPopWindow baseExpandPopWindow = popWindows.get(view);

		if (baseExpandPopWindow != null) {
			baseExpandPopWindow.dismiss();
		}
		
//		view.setChecked(false);
		baseExpandPopWindow.restoreSelected();
	}
}
