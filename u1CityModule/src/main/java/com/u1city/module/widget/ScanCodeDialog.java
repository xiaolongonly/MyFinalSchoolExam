package com.u1city.module.widget;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.u1city.module.R;
import com.u1city.module.common.CreateZxingImage;

/***************************************
 * 
 * @author joan
 * @date 2015-3-12
 * @time 下午13:04:29
 *       类说明:扫描二维码Dialog
 * 
 **************************************/
public class ScanCodeDialog extends BaseDialog
{
    public ScanCodeDialog(Activity context)
    {
        super(context, R.layout.dialog_qrcode);
        DisplayMetrics display = context.getResources().getDisplayMetrics();
        WindowManager.LayoutParams p = getWindow().getAttributes();
        p.width = display.widthPixels;
        p.height = display.heightPixels;
        getWindow().setAttributes(p);
        setCanceledOnTouchOutside(false);
    }

    @Override
    public void initView()
    {
        super.initView();
        findViewById(R.id.iv_close).setOnClickListener(this);
    }

    public ScanCodeDialog setQRString(String url)
    {
        new CreateZxingImage().createQRImage(url, ((ImageView) findViewById(R.id.tv_qrcode)));
        return this;
    }

    @Override
    public void show()
    {
        super.show();
        super.init();
    }

    @Override
    public void onClick(View v)
    {
        int id = v.getId();

        if (id == R.id.iv_close)
        {
            dismiss();
        }
    }

}
