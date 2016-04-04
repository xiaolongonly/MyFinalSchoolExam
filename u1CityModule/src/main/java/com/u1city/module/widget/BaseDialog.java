package com.u1city.module.widget;

import android.app.Activity;
import android.app.Dialog;

import com.u1city.module.R;

/***************************************
 * 
 * @author lwli
 * @date 2014-8-14
 * @time 下午2:39:01 类说明:基础dialog
 * 
 **************************************/
public abstract class BaseDialog extends Dialog implements
		android.view.View.OnClickListener {
	/**
	 * @param context
	 */
	protected Activity context;

	public BaseDialog(Activity context) {
		super(context, R.style.Dialog);
		this.context = context;
		getWindow().setBackgroundDrawableResource(android.R.color.transparent);
	}
	
	public BaseDialog(Activity context,String transparent) {//背景透明
        super(context, R.style.Dialog_transparent);
        this.context = context;
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

	public BaseDialog(Activity context, int resId) {
		super(context, R.style.dialog_bottom);
		this.context = context;
		setContentView(resId);
	}

	public BaseDialog(Activity context, int resId, int styleId) {
		super(context, styleId);
		this.context = context;
		setContentView(resId);
	}

	public void init() {
		initView();
		initData();
		setListener();
	}

	public void initView() {
	};

	public void initData() {
	};

	public void setListener() {
	};
}
