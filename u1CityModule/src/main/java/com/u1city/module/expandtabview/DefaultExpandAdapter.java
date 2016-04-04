/*
 * 系统: U1CityModule
 * 文件名: DefaultExpandAdapter.java
 * 版权: U1CITY Corporation 2015
 * 描述:
 * 创建人: zhengjb
 * 创建时间: 2015-9-28 下午3:05:56
 */
package com.u1city.module.expandtabview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.u1city.module.R;
import com.u1city.module.util.DimensUtil;
import com.u1city.module.util.ListUtils;
import com.u1city.module.util.ViewHolder;

import org.apache.commons.lang.StringUtils;

import java.util.List;

public class DefaultExpandAdapter<T> extends BaseExpandAdapter<T>
{
    public static final int TYPE_SINGLE_TAB = 1;
    public static final int TYPE_DOUBLE_TAB_FIRST = 2;
    public static final int TYPE_DOUBLE_TAB_SECOND = 3;

    private int type;
    private String dafaultText="";

    /**
     * @author zhengjb 2015/03/17
     *         默认的适配器
     *         主要是配置一些默认设置，简化代码
     * */
    public DefaultExpandAdapter(DefaultExpandPopWindow popWindow, Context context)
    {
        super(context);
        this.popupWindow = popWindow;
    }

    /**
     * 标志tab的类型
     * */
    public int getType()
    {
        return type;
    }

    public void setType(int type)
    {
        this.type = type;
    }    
   
    public String getDafaultText()
    {
        return dafaultText;
    }

    public void setDafaultText(String dafaultText)
    {
        this.dafaultText = dafaultText;
    }
    
    @Override
    public int getCount()
    {
        int count = super.getCount();
        
        // 限制最大高度
        int windowHeight = DimensUtil.getDisplayHeight(context);
        int itemHeight = DimensUtil.dpToPixels(context, 44);
        if (count * itemHeight > windowHeight * 0.6)
        {
            ((DefaultExpandPopWindow)getPopupWindow()).setShowHeight((int) (windowHeight * 0.6));
        }
        return super.getCount();
    }
    

    @Override
    public void setSelectedId(int position)
    {
        super.setSelectedId(position);
        
        if(type == TYPE_DOUBLE_TAB_FIRST){
            T t = getSelectedItem();
            ((DefaultExpandPopWindow) popupWindow).onFirstMenuItemSeleted(t);
        }
    }

    @Override
    public void setItems(List<T> items)
    {
        super.setItems(items);

        if(type == TYPE_SINGLE_TAB){
            getTab().setText(((DefaultExpandPopWindow) popupWindow).onGetFirstMenuItemText(0));
        }else if(type == DefaultExpandAdapter.TYPE_DOUBLE_TAB_FIRST){
            // 一级列表默认显示第一项
            if (!ListUtils.isEmpty(getItems()))
            {
                String text = ((DefaultExpandPopWindow) popupWindow).onGetFirstMenuItemText(0);
                
                if("全部".equals(text)){
                	if(!StringUtils.isEmpty(dafaultText))
                		getTab().setText(dafaultText);
                	else
                		getTab().setText("全部分类");
                }else{
                    getTab().setText(text);
                }
                
                setSelection(0);
            }
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LinearLayout orderTypeLl = null;
        TextView orderTypeTv = null;

        if (convertView == null)
        {
            if (type == TYPE_SINGLE_TAB)
            {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_order_type, null);
            }
            else if (type == TYPE_DOUBLE_TAB_FIRST)
            {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_expand_categary_first, null);
            }
            else if (type == TYPE_DOUBLE_TAB_SECOND)
            {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_expand_categary_second, null);
            }
        }

        if (type == TYPE_SINGLE_TAB)
        {
            orderTypeLl = ViewHolder.get(convertView, R.id.item_order_type_ll);
            orderTypeTv = ViewHolder.get(convertView, R.id.item_order_type_tv);
        }
        else if (type == TYPE_DOUBLE_TAB_FIRST)
        {
            orderTypeLl = ViewHolder.get(convertView, R.id.item_expand_categary_frist_rl);
            orderTypeTv = ViewHolder.get(convertView, R.id.item_expand_categary_frist_tv);
        }
        else if (type == TYPE_DOUBLE_TAB_SECOND)
        {
            orderTypeTv = ViewHolder.get(convertView, R.id.item_expand_categary_second_tv);
        }

        if (type == TYPE_SINGLE_TAB || type == TYPE_DOUBLE_TAB_FIRST)
        {
            if (position == getSelectedId())
            {
                orderTypeLl.setBackgroundColor(context.getResources().getColor(R.color.expandtab_text_background_color));
            }
            else
            {
                orderTypeLl.setBackgroundColor(context.getResources().getColor(R.color.white));
            }
        }

        if(type == TYPE_SINGLE_TAB || type == TYPE_DOUBLE_TAB_FIRST){
            orderTypeTv.setText(((DefaultExpandPopWindow) popupWindow).onGetFirstMenuItemText(position));
        }else if(type == TYPE_DOUBLE_TAB_SECOND){
            orderTypeTv.setText(((DefaultExpandPopWindow) popupWindow).onGetSecondMenuItemText(position));
        }
        
        return convertView;
    }

    @Override
    public void onSelected()
    {
        ((DefaultExpandPopWindow)popupWindow).onSelected(type,dafaultText);
    }
}
