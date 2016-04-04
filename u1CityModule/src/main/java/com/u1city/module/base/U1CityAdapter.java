package com.u1city.module.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.u1city.module.common.Debug;

import java.util.ArrayList;
import java.util.List;

/***************************************
 * 
 * @author lwli
 * @modify zhengjb
 * @date 2014-10-28
 * @time 上午10:15:41
 *       普通适配器，写好了大致的数据处理
 *       推荐适配器继承，省下一些代码的时间
 * 
 **************************************/
public class U1CityAdapter<T> extends BaseAdapter
{

    private OnGetViewListener onGetViewListener;

    protected List<T> datas = new ArrayList<T>();

    protected LayoutInflater inflater;

    private Context context;

    /***************************************
     * 
     * @author lwli
     * @modify zhengjb
     * @date 2014-10-28
     * @time 上午10:15:41
     *       普通适配器，写好了大致的数据处理
     *       推荐适配器继承，省下一些代码的时间
     *       注意context不为空的情况下，适配器中的属性inflater才可以使用
     * @param context 使用适配器的上下文
     * @param datas 适配器的数据
     * 
     **************************************/
    public U1CityAdapter(Context context, List<T> datas)
    {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.datas = datas;
    }

    /***************************************
     * 
     * @author lwli
     * @modify zhengjb
     * @date 2014-10-28
     * @time 上午10:15:41
     *       普通适配器，写好了大致的数据处理
     *       推荐适配器继承，省下一些代码的时间
     *       注意context不为空的情况下，适配器中的属性inflater才可以使用
     * @param context 使用适配器的上下文
     * 
     **************************************/
    public U1CityAdapter(Context context)
    {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    /***************************************
     * 
     * @author lwli
     * @modify zhengjb
     * @date 2014-10-28
     * @time 上午10:15:41
     *       普通适配器，写好了大致的数据处理
     *       推荐适配器继承，省下一些代码的时间
     *       * 注意在这种情况下，inflater属性为空
     * 
     **************************************/
    public U1CityAdapter()
    {
        super();
    }

    /**设置getView监听，主要在BaseAbsActivity，BaseAbsFragment中使用，随着两者的过期，此方法亦过期
     * @param onGetViewListener getView监听
     * */
    @Deprecated
    public void setOnGetViewListener(OnGetViewListener onGetViewListener)
    {
        this.onGetViewListener = onGetViewListener;
    }

    /**@return
     * 适配器所在上下文*/
    public Context getContext()
    {
        return context;
    }

    /**
     * 设置适配器上下文，同时初始化inflater
     * @param context
     * 适配器所在上下文*/
    public void setContext(Context context)
    {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    /**获取数据总量大小，datas为空的时候大小为0
     * @return 数据总量大小
     * */
    @Override
    public int getCount()
    {
        // Debug.d(BaseActivity.TAG, "count:" + (datas == null ? 0 : datas.size()));
        return datas == null ? 0 : datas.size();
    }

    /**获取item数据
     * @return item数据，可能返回null
     * */
    @Override
    public Object getItem(int position)
    {
        return datas.size() >= position ? datas.get(position) : null;
    }

    /** 因为{@link #getData()}与BaseAbsActivity和BaseAbsFragment之中的getData重名了 */
    @Deprecated
    public void setData(List<T> data)
    {
        datas = data;
        notifyDataSetChanged();
    }

    /**设置数据
     * 
     * @param models 适配器的数据
     * */
    public void setModels(List<T> models)
    {
        datas = models;
        notifyDataSetChanged();
    }

    /** 因为与BaseAbsActivity和BaseAbsFragment之中的getData重名了 */
    @Deprecated
    public List<T> getData()
    {
        return datas == null ? (datas = new ArrayList<T>()) : datas;
    }

    /**
     * 
     * @return 适配器的数据
     * */
    public List<T> getModels()
    {
        return datas == null ? (datas = new ArrayList<T>()) : datas;
    }

    /** 因为{@link #getData()}与BaseAbsActivity和BaseAbsFragment之中的getData重名了 */
    @Deprecated
    public void addData(List<T> data)
    {
        Debug.d(BaseActivity.TAG, "add:" + (data == null ? 0 : data.size()));
        datas.addAll(data);
        notifyDataSetChanged();
    }

    /**添加新的数据到适配器中
     * @param models 新的数据
     * */
    public void addModel(List<T> models)
    {
        Debug.d(BaseActivity.TAG, "add:" + (models == null ? 0 : models.size()));
        datas.addAll(models);
        notifyDataSetChanged();
    }

    /**
     * 添加新的item数据到适配器数据中
     * @param obj item数据
     * */
    public void addItem(T obj)
    {
        datas.add(obj);
        notifyDataSetChanged();
    }

    /**
     * 添加新的item数据到适配器数据中
     * @param pos 添加的位置
     * @param obj item数据
     * */
    public void addItem(int pos, T obj)
    {
        datas.add(pos, obj);
        notifyDataSetChanged();
    }

    /**
     * 从适配器数据中删除item数据
     * @param obj item数据
     * */
    public void removeItem(T obj)
    {
        datas.remove(obj);
        notifyDataSetChanged();
    }

    /**清空适配器数据*/
    public void clear()
    {
        datas.clear();
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        // Debug.d(BaseActivity.TAG, "getView");
        // 在BaseAbs的页面，设置了此listener，因此在那些继承BaseAbs的页面只需要实现onGetView即可，而其他的适配器仍然实现getView
        if (onGetViewListener != null)
        {
            return onGetViewListener.onGetView(position, convertView, parent);
        }

        return null;
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    /**getView监听器，主要在BaseAbsActivity，BaseAbsFragment中使用，随着两者的过期，此方法亦过期
     * */
    public interface OnGetViewListener
    {
        public View onGetView(int position, View convertView, ViewGroup parent);
    };
}
