package com.u1city.module.expandtabview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ToggleButton;

import com.u1city.module.R;
import com.u1city.module.common.Debug;

import org.apache.commons.lang.StringUtils;

import java.util.List;

/**
 * @author zhengjb 2015/03/17
 *         默认的弹出窗
 *         在此主要做一些样式上的调整
 * */
@SuppressWarnings({"rawtypes","unchecked"})
public abstract class DefaultExpandPopWindow extends BaseExpandPopWindow
{
    private static final String TAG = DefaultExpandPopWindow.class.getSimpleName();
    
    private final ImageView footerIv1;

    private final ImageView footerIv2;
    
    private LinearLayout popLl;

    private Context context;

    public DefaultExpandPopWindow(Context context, final ToggleButton tab)
    {
        super(context, tab);

        this.context = context;

        // 默认全屏
        ExpandContentView contentView = (ExpandContentView) LayoutInflater.from(context).inflate(R.layout.popwindow_expand_default, null);
        contentView.setTab(tab);
        setContentView(contentView);

        setBackgroundDrawable(context.getResources().getDrawable(R.drawable.background_transparent));

        popLl = (LinearLayout) getContentView().findViewById(R.id.popwindow_expand_default_ll);
        firstMenuList = (ListView) getContentView().findViewById(R.id.popwindow_expand_default_first_lv);
        secondMenuList = (ListView) getContentView().findViewById(R.id.popwindow_expand_default_second_lv);

        footerIv1 = (ImageView) getContentView().findViewById(R.id.popwindow_expand_default_footer_iv1);
        footerIv2 = (ImageView) getContentView().findViewById(R.id.popwindow_expand_default_footer_iv2);

        getContentView().findViewById(R.id.popwindow_expand_default_footer_rl).setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                tab.setChecked(false);
            }
        });

        setWidth(LayoutParams.MATCH_PARENT);
        setHeight(LayoutParams.MATCH_PARENT);

        setFocusable(true);
    }
    
    /** 设置显示的弹出框内容的可视高度 */
    public void setShowHeight(int height)
    {
        popLl.getLayoutParams().height = height;
        popLl.postInvalidate();
    }


    @Override
    public void setSecondMenuEnable(boolean isSecondMenuEnable)
    {
        super.setSecondMenuEnable(isSecondMenuEnable);

        if (isSecondMenuEnable)
        {
            DefaultExpandAdapter firstMenuAdapter = new DefaultExpandAdapter(this, context);
            firstMenuAdapter.setType(DefaultExpandAdapter.TYPE_DOUBLE_TAB_FIRST);
            setFirstMenuAdapter(firstMenuAdapter);
            
            DefaultExpandAdapter secondMenuAdapter = new DefaultExpandAdapter(this, context);
            secondMenuAdapter.setType(DefaultExpandAdapter.TYPE_DOUBLE_TAB_SECOND);
            setSecondMenuAdapter(secondMenuAdapter);
        }
        else
        {
            DefaultExpandAdapter firstMenuAdapter = new DefaultExpandAdapter(this, context);
            firstMenuAdapter.setType(DefaultExpandAdapter.TYPE_SINGLE_TAB);
            setFirstMenuAdapter(firstMenuAdapter);
        }
    }
    
    public void setFirstMenuItems(List items,String dafaultText){
    	((DefaultExpandAdapter)firstMenuList.getAdapter()).setDafaultText(dafaultText);
        ((DefaultExpandAdapter)firstMenuList.getAdapter()).setItems(items);
    }

    public void setFristMenuDivider(Drawable drawable)
    {
        firstMenuList.setDivider(drawable);
    }

    public void setSecondMenuDivider(Drawable drawable)
    {
        secondMenuList.setDivider(drawable);
    }

    /** 设置底部图片 */
    public void setFooterBackgroundRes1(int resid)
    {
        footerIv1.setBackgroundResource(resid);
    }

    /** 设置底部图片 */
    public void setFooterBackgroundRes2(int resid)
    {
        footerIv2.setBackgroundResource(resid);
    }

    @Override
    public void show(View anchor)
    {
        this.showAsDropDown(anchor, 1, 1);
    }

    /** 第一列条目被选中时的处理 */
    public void onFirstMenuItemSeleted(Object object){
        DefaultExpandAdapter adapter = (DefaultExpandAdapter) getSecondMenuAdapter();
        adapter.setItems(getSecondMenuItems(object));
        adapter.notifyDataSetChanged();                
    };
    
    public void onSelected(int type,String dafaultText){
        if(type == DefaultExpandAdapter.TYPE_DOUBLE_TAB_FIRST){
            DefaultExpandAdapter adapter = (DefaultExpandAdapter) getFirstMenuAdapter();
            
            Debug.d(TAG, "selectedId:" + adapter.getSelectedId());
            List secondList = getSecondMenuItems(adapter.getSelectedItem());

            Debug.d(TAG, "secondList:" + secondList.size());

            if (secondList == null || secondList.size() <= 1)
            {
                String name = onGetFirstMenuItemText(adapter.getSelectedId());
                boolean isItemOnClick = adapter.getIsItemOnClick();
                String typeName=name;
                if(isItemOnClick)
                	typeName=name;
                else
                {
                	if(!StringUtils.isEmpty(dafaultText))
                		typeName=dafaultText;
                }
                if("全部".equals(typeName))
                	typeName="全部分类";
                tab.setText(typeName);                
                getSecondMenuAdapter().setSelectedId(0);                
                refresh();
            }
            
        }else if(type == DefaultExpandAdapter.TYPE_DOUBLE_TAB_SECOND){
            DefaultExpandAdapter adapter = (DefaultExpandAdapter) getSecondMenuAdapter();
            String subName = onGetSecondMenuItemText(adapter.getSelectedId());
            String name = onGetFirstMenuItemText(getFirstMenuAdapter().getSelectedId());

            if ("全部".equals(subName))
            {
                tab.setText(name);
            }
            else
            {
                tab.setText(subName);
            }
            
            refresh();
        }else if(type == DefaultExpandAdapter.TYPE_SINGLE_TAB){
          tab.setText(onGetFirstMenuItemText(getFirstMenuAdapter().getSelectedId()));
          refresh();
        }
    }
    
    /** 获取第一列条目的特定文字 */
    public abstract String onGetFirstMenuItemText(int position);
    /** 获取第二列条目的特定文字 */
    public String onGetSecondMenuItemText(int position){
        return null;
    };
    
    /** 获取第二列条目的数据 */
    public List getSecondMenuItems(Object object){
        return null;
    };
    
    /** 刷新 */
    public abstract void refresh();
}
