package com.u1city.module.widget;

import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.WindowManager;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.u1city.module.R;
import com.u1city.module.base.BaseActivity;
import com.u1city.module.util.SimpleImageOption;

/***************************************
 * 
 * @author Tian
 * @date 2015-3-12
 * @time 下午13:04:29
 *       类说明:单张图片放大
 * 
 **************************************/
public class ShopGuideDialog extends BaseDialog
{
    private DisplayImageOptions photoOption = SimpleImageOption.create(R.drawable.list_loading_goods2);
    private DialogShopGuide dialogShopGuide;

    public ShopGuideDialog(BaseActivity context)
    {
        super(context, R.layout.dialog_shopguide_or, R.style.Dialog_reim);
        getWindow().setBackgroundDrawableResource(android.R.color.black);
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
        findViewById(R.id.dialog_shopguide_rl).setOnClickListener(this);
        findViewById(R.id.tv_qrcode).setOnClickListener(this);
        findViewById(R.id.settings_iv).setOnClickListener(this);
        findViewById(R.id.tv_qrcode).setOnLongClickListener(new OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                dialogShopGuide.longClick(ShopGuideDialog.this);
                return false;
            }
        });
    }

    public ShopGuideDialog setQRString(String url)
    {
        ImageLoader.getInstance().displayImage(url, ((ScaleImageView) findViewById(R.id.tv_qrcode)), photoOption);
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
        if(id == R.id.dialog_shopguide_rl)
        {
            dismiss();
        }
        else if(id == R.id.tv_qrcode)
        {
            dismiss();
        }
        else if(id == R.id.settings_iv)
        {
            dialogShopGuide.settings(this);
        }
        
    }

    public interface DialogShopGuide
    {
        public void settings(ShopGuideDialog dialog);

        public void longClick(ShopGuideDialog dialog);
    }

    public ShopGuideDialog setShopGuideDialog(DialogShopGuide dialogShopGuide)
    {
        this.dialogShopGuide = dialogShopGuide;
        return this;
    }

}
