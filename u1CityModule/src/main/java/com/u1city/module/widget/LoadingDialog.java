package com.u1city.module.widget;


import android.app.Activity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.u1city.module.R;

/***************************************
 * 
 * @author linjy
 * @date 2015-01-08 
 * @time 下午15:43:29
 * 类说明:图片上传Dialog
 * 
 **************************************/
public class LoadingDialog extends BaseDialog {

	private ImageView loadingIv;
	private TextView loadingTv;

	public LoadingDialog(Activity context) {
		super(context);
		setContentView(R.layout.dialog_loading);
		setCancelable(true);
		setCanceledOnTouchOutside(false);
		loadingIv = (ImageView) findViewById(R.id.img_loadingdialog);
		loadingTv = (TextView) findViewById(R.id.tv_loading_dialog);
	}

	public void setLoadingText(String text){
		loadingTv.setText(text);
	}
	
	@Override
	public void onClick(View v) {

	}

	@Override
	public void show() {
	    super.show();
	    Animation operatingAnim = AnimationUtils.loadAnimation(context, R.anim.rotate_dialog);
        LinearInterpolator lin = new LinearInterpolator();
        operatingAnim.setInterpolator(lin); // 匀速旋转 必须要加不然会停顿
        loadingIv.startAnimation(operatingAnim);
	};
}
