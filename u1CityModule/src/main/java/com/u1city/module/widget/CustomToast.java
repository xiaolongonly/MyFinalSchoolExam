/*
 * 系统: Android_LaiDianYi
 * 文件名: CustomToast.java
 * 版权: U1CITY Corporation 2015
 * 描述: 导购小站保存头象提示
 * 创建人: Tian
 * 创建时间: 2015-8-14 上午10:16:47
 */
package com.u1city.module.widget;

import android.content.Context;
import android.widget.TextView;
import android.widget.Toast;

import com.u1city.module.R;

public class CustomToast extends BaseToast
{
    private TextView text;
    public CustomToast(Context context)
    {
        super(context, R.layout.toast_shopsave,Toast.LENGTH_SHORT);
        text = (TextView) layout.findViewById(R.id.tvTextToast);
    }
    
    public void showToast(String content)
    {
        text.setText(content);
    }
    
}
