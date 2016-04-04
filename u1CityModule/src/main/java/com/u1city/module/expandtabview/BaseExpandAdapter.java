package com.u1city.module.expandtabview;

import android.content.Context;
import android.widget.BaseAdapter;
import android.widget.ToggleButton;

import java.util.List;

/**
 * @author zhengjb
 *         2015/03/17
 *         ExpandPopWindow中列表的适配器
 *         注意：PopWindow中的列表已经设置了OnItemClickListener,请不要再设置，防止出错
 * */
@SuppressWarnings("rawtypes")
public abstract class BaseExpandAdapter<T> extends BaseAdapter
{
    
    private List<T> items;

    public BaseExpandAdapter(Context context)
    {
        this.context = context;
    }

    protected BaseExpandPopWindow popupWindow;
    
    protected Context context;

    /** 标签 */
    private ToggleButton tab;

    /** 当前选中项 */
    private int selectedId = 0;
    
    /** 是否ITEM点击 */
    private boolean isItemOnClick = false;

    /** 当前选中对象 */
    private T selectedItem;

    /** 最近的选中项 */
    private int lastSelectedId = -1;

    /** 最近的选中对象 */
    private T lastSeletedItem;
    
    private boolean isSecondMenu = false;

    public boolean isSecondMenu()
    {
        return isSecondMenu;
    }

    public void setSecondMenu(boolean isSecondMenu)
    {
        this.isSecondMenu = isSecondMenu;
    }

    public int getLastSelectedId()
    {
        return lastSelectedId;
    }

    public T getLastSeletedItem()
    {
        return lastSeletedItem;
    }

    public void setLastSelectedId(int position)
    {
        this.lastSelectedId = position;
        setLastSelectedItem(position);
    }

    public List<T> getItems()
    {
        return items;
    }

    public void setItems(List<T> items)
    {
        this.items = items;
    }

    public BaseExpandPopWindow getPopupWindow()
    {
        return popupWindow;
    }

    public void setPopupWindow(BaseExpandPopWindow popupWindow)
    {
        this.popupWindow = popupWindow;
    }

    public T getSelectedItem()
    {
        return selectedItem;
    }

    private void setLastSelectedItem(int position)
    {
        if(position > getCount() -1){
            lastSeletedItem = null;
            return;
        }
        
        this.lastSeletedItem = getItem(position);
    }

    private void setSelectedItem(int position)
    {
        if(position > getCount() -1){
            selectedItem = null;
            return;
        }
        this.selectedItem = getItem(position);
    }

    public ToggleButton getTab()
    {
        return tab;
    }

    public void setTab(ToggleButton tab)
    {
        this.tab = tab;
    }

    public int getSelectedId()
    {
        return selectedId;
    }
    
    public boolean getIsItemOnClick()
    {
        return isItemOnClick;
    }
    
    public void setIsItemOnClick(boolean isItemOnClick)
    {
        this.isItemOnClick=isItemOnClick;
    }

    public void setSelectedId(int position)
    {
        this.selectedId = position;        
        setSelectedItem(position);
    }

    public interface OnItemSelectedListener
    {
        public void onSelected(BaseExpandAdapter baseExpandAdapter);
    }

    @Override
    public int getCount()
    {
        int count = items == null ? 0 : items.size();
        return count;
    }
    
    @Override
    public final long getItemId(int position)
    {
        return position;
    }

    /** 设置被选中项，一般用于设置默认选中项 */
    public void setSelection(int position)
    {
        setSelectedId(position);
        notifyDataSetChanged();
        onSelected();
    }

    @Override
    public final T getItem(int position)
    {
        T obj = items.get(position);

        if (obj == null)
        {
            throw new NullPointerException("列表的item不能为空，否则会导致其他地方的错误");
        }

        return obj;
    }
    
    /** 当结果被选定时并提交时使用者要做的动作，如请求新数据，在tab上显示被选中项等,一般在最后一级列表中调用 */
    public abstract void onSelected();
}
