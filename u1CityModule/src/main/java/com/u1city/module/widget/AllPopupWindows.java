/*
 * 系统: Android_LaiDianYi
 * 文件名: AllPopupWindows.java
 * 版权: U1CITY Corporation 2015
 * 描述: 图片展示
 * 创建人: Administrator Tianbf
 * 创建时间: 2015-12-11 下午3:05:20
 */
package com.u1city.module.widget;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;

import com.u1city.module.util.DimensUtil;

//相片详情page
public class AllPopupWindows extends PopupWindow
{

    @SuppressWarnings("deprecation")
    public AllPopupWindows(Context mContext,View viewlayout)
    {
        setWidth(LayoutParams.MATCH_PARENT);
        setHeight(DimensUtil.getDisplayHeight(mContext));

        setBackgroundDrawable(new BitmapDrawable());
        setFocusable(true);
        setOutsideTouchable(false);
        setContentView(viewlayout);
    }

    public void showDown(View parent)
    {
        showAtLocation(parent, Gravity.CENTER_VERTICAL, 0, 0);
    }
}