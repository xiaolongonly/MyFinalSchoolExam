package com.u1city.module.widget;

import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.u1city.module.R;
import com.u1city.module.base.BaseActivity;

/***************************************
 * 
 * @author Tian
 * @date 2015-3-12
 * @time 下午13:04:29
 *       类说明：头象保存
 * 
 **************************************/
public class ShopGuideSaveDialog extends BaseDialog
{
    private DialogShopGuideSave dialogShopGuide;
    private String tag;

    public ShopGuideSaveDialog(BaseActivity context, String tag)
    {
        super(context, R.layout.dialog_save_photos);
        this.tag = tag;
        DisplayMetrics display = context.getResources().getDisplayMetrics();
        WindowManager.LayoutParams p = getWindow().getAttributes();
        p.width = display.widthPixels;
        setCanceledOnTouchOutside(false);
        getWindow().setGravity(Gravity.BOTTOM);
        getWindow().setAttributes(p);
    }

    @Override
    public void initView()
    {
        super.initView();
        findViewById(R.id.shopguide_save_tv).setOnClickListener(this);
        findViewById(R.id.shopguide_cancel_tv).setOnClickListener(this);
    }

    public ShopGuideSaveDialog setQRString(String url)
    {

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
        if(id == R.id.shopguide_save_tv)
        {
            dialogShopGuide.settings(this, tag);
        }
        else if(id == R.id.shopguide_cancel_tv)
        {
            dismiss();
        }
    }

    public interface DialogShopGuideSave
    {
        public void settings(ShopGuideSaveDialog dialog, String tag);
    }

    public ShopGuideSaveDialog setShopGuideSaveDialog(DialogShopGuideSave dialogShopGuide)
    {
        this.dialogShopGuide = dialogShopGuide;
        return this;
    }

}
