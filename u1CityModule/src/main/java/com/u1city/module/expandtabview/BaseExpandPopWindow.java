package com.u1city.module.expandtabview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ToggleButton;

import com.u1city.module.common.Debug;

/**
 * @author zhengjb 2015/03/17 可扩展标签视图的【弹出窗】的基类 最多只有两级列表
 * */
@SuppressWarnings("rawtypes")
public abstract class BaseExpandPopWindow extends PopupWindow
{
    /** 一级列表适配器 */
    private BaseExpandAdapter firstMenuAdapter;
    /** 二级列表适配器 */
    private BaseExpandAdapter secondMenuAdapter;

    /** 一级列表 */
    protected ListView firstMenuList;

    /** 二级列表 */
    protected ListView secondMenuList;

    /** 是否显示第二级列表 */
    private boolean isSecondMenuEnable = false;

    protected ToggleButton tab;

    public void setContentBackgroundColor(int color)
    {
        getContentView().setBackgroundColor(color);
    }

    public void setContentBackgroundResource(int resid)
    {
        getContentView().setBackgroundResource(resid);
    }

    public void setContentBackground(Drawable drawable)
    {
        getContentView().setBackground(drawable);
    }

    public BaseExpandPopWindow(Context context, ToggleButton tab)
    {
        super(context);
        this.tab = tab;

    }

    public BaseExpandPopWindow(Context context, AttributeSet attributeSet, ToggleButton tab)
    {
        super(context, attributeSet);
        this.tab = tab;
    }

    public BaseExpandPopWindow(Context context, AttributeSet attributeSet, int defStyle, ToggleButton tab)
    {
        super(context, attributeSet, defStyle);
        this.tab = tab;
    }

    public BaseExpandAdapter getFirstMenuAdapter()
    {
        return firstMenuAdapter;
    }

    public void setFirstMenuAdapter(final BaseExpandAdapter firstMenuAdapter)
    {
        this.firstMenuAdapter = firstMenuAdapter;
        // 持有tab和popwindow对象
        firstMenuAdapter.setTab(tab);
        firstMenuAdapter.setPopupWindow(this);

        if (firstMenuAdapter != null)
        {
            firstMenuList.setAdapter(firstMenuAdapter);

            firstMenuList.setOnItemClickListener(new OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                {
                    Debug.d("TAG", "onFirstItemClick");
                    // 设置了被选中的项目
                    firstMenuAdapter.setIsItemOnClick(true);
                    onSelectedClick(firstMenuAdapter, position);
                }
            });
        }
    }

//    /** 设置显示的弹出框内容的可视高度 */
//    public void setShowHeight(int height)
//    {
//            getContentView().getLayoutParams().height = height;
//            getContentView().postInvalidate();
//    }

    public BaseExpandAdapter getSecondMenuAdapter()
    {
        return secondMenuAdapter;
    }

    public void setSecondMenuAdapter(final BaseExpandAdapter secondMenuAdapter)
    {
        this.secondMenuAdapter = secondMenuAdapter;
        // 持有tab和popwindow对象
        secondMenuAdapter.setTab(tab);
        secondMenuAdapter.setPopupWindow(this);

        if (secondMenuList != null)
        {
            secondMenuList.setAdapter(secondMenuAdapter);

            secondMenuList.setOnItemClickListener(new OnItemClickListener()
            {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                {
                    Debug.d("TAG", "onSecondItemClick");
                    onSelectedClick(secondMenuAdapter, position);
                }
            });
        }
    }

    private void onSelectedClick(BaseExpandAdapter adapter, int position)
    {
        adapter.setSelectedId(position);
        adapter.notifyDataSetChanged();

        adapter.onSelected();

        if(adapter == firstMenuAdapter && isSecondMenuEnable && secondMenuAdapter.getCount() > 1){//二级列表可选，就不关闭弹窗
            return;
        }
        
        saveSelected();
        tab.setChecked(false);// 两句代码顺序不能打乱
    }

    public boolean isSecondMenuEnable()
    {
        return isSecondMenuEnable;
    }

    /** 最好在设置列表适配器之前设置 */
    public void setSecondMenuEnable(boolean isSecondMenuEnable)
    {
        this.isSecondMenuEnable = isSecondMenuEnable;
        secondMenuList.setVisibility(isSecondMenuEnable ? View.VISIBLE : View.GONE);
    }

    /**
     * 定义如何显示弹出窗
     * 
     * @param anchor
     *            可扩展标签视图的容器
     * */
    public abstract void show(View anchor);

    /** 保存选定的项目 */
    public void saveSelected()
    {
        if(firstMenuAdapter.getLastSelectedId() >= 0){
            firstMenuAdapter.setLastSelectedId(firstMenuAdapter.getSelectedId());
            Debug.d("save", "oldFirstSelectedIndex:" + firstMenuAdapter.getLastSelectedId());
        }

        if (isSecondMenuEnable && secondMenuAdapter.getSelectedId() >= 0)
        {
            secondMenuAdapter.setLastSelectedId(secondMenuAdapter.getSelectedId());
            Debug.d("save", "oldSecondSelectedIndex:" + secondMenuAdapter.getLastSelectedId());
        }
    }

    /** 还原选定的项目 */
    public void restoreSelected()
    {
        if (firstMenuAdapter.getLastSelectedId() >= 0)
        {
            firstMenuAdapter.setSelectedId(firstMenuAdapter.getLastSelectedId());
            firstMenuAdapter.notifyDataSetChanged();
            Debug.d("save", "restore oldFirstSelectedIndex:" + firstMenuAdapter.getLastSelectedId());
        }

        if (!isSecondMenuEnable)
        {
            return;
        }

        if (secondMenuAdapter.getLastSelectedId() >= 0)
        {
            secondMenuAdapter.setSelectedId(secondMenuAdapter.getLastSelectedId());
            secondMenuAdapter.notifyDataSetChanged();
            Debug.d("save", "restore oldSecondSelectedIndex:" + secondMenuAdapter.getLastSelectedId());
        }

    }
}
