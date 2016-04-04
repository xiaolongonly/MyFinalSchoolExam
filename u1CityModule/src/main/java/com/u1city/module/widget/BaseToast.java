/*
 * 系统: U1CityModule
 * 文件名: BaseToast.java
 * 版权: U1CITY Corporation 2015
 * 描述: 自定义Toast基类
 * 创建人: Tian
 * 创建时间: 2015-9-22 上午10:35:17
 */
package com.u1city.module.widget;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

public class BaseToast
{
    public View layout;
    public BaseToast(Context context,int layoutId, int lengthShort)
    {
        LayoutInflater inflater = LayoutInflater.from(context);
        layout = inflater.inflate(layoutId,null);
        Toast toast = new Toast(context);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(lengthShort);
        toast.setView(layout);
        toast.show();
    }
}
