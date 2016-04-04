package com.u1city.module.base;

import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.etsy.android.grid.ExtendableListView;
import com.u1city.module.R;
import com.u1city.module.base.U1CityAdapter.OnGetViewListener;
import com.u1city.module.common.Debug;
import com.u1city.module.pulltorefresh.PullToRefreshAdapterViewBase;
import com.u1city.module.pulltorefresh.PullToRefreshBase;
import com.u1city.module.pulltorefresh.PullToRefreshBase.OnLastItemVisibleListener;
import com.u1city.module.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import com.u1city.module.util.NetUtil;
import com.u1city.module.util.ToastUtil;
import com.u1city.module.widget.HeadableGridView;

import java.util.Date;
import java.util.List;

/***************************************
 * 
 * @author lwli
 * @date 2014-12-4
 * @time 上午9:40:46 类说明:列表父类Activity
 * 
 **************************************/
@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class BaseAbsActivity<T extends PullToRefreshAdapterViewBase> extends BaseActivity implements OnRefreshListener2, OnLastItemVisibleListener, OnItemClickListener, OnGetViewListener
{

    /** 数据列表的适配器 */
    protected U1CityAdapter adapter;
    /** 主体ptr */
    protected T pullToRefreshAdapterViewBase;
    /** 无数据时显示的视图 */
    private View customEmptyView;
    /** 主体数据列表 */
    private View refreshView;
    /** 没有更多数据加载时显示的视图 */
    private LinearLayout footerLl;

    /** 每个分页的数目 */
    private int pageSize = 20;
    /** 初始页数为1 */
    protected int indexPage = 1;
    /** 总的条目数，指的是服务端的相应数据的总条目数，而不是当前app显示的条目数 */
    protected int totalCount;

    /** 标志是否正处于加载数据的过程中 */
    private boolean isGettingData = false;

    private long startTime;

    private boolean isDrawDown;

    @Override
    protected void onCreate(Bundle savedInstanceState, int layoutResId, int layoutActionBar)
    {
        super.onCreate(savedInstanceState, layoutResId, layoutActionBar);
    }

    /** 设置尾部是否可见 */
    public void setFooterVisible(int visibility)
    {
        if (footerLl != null)
        {
            footerLl.setVisibility(visibility);
        }
    }

    /**
     * @return 返回pullToRefresh的视图
     * */
    public T getPullToRefreshAdapterViewBase()
    {
        return pullToRefreshAdapterViewBase;
    }

    /**
     * @return 分页的大小
     * */
    public int getPageSize()
    {
        return pageSize;
    }

    /**
     * @return 请求接口的起始页的大小
     */
    public int getIndexPage()
    {
        return indexPage;
    }

    /**
     * @param indexPage 请求接口的起始页的大小
     */
    public void setIndexPage(int indexPage)
    {
        this.indexPage = indexPage;
    }

    /**
     * @param pageSize 分页的大小
     * */
    public void setPageSize(int pageSize)
    {
        this.pageSize = pageSize;
    }

    /**
     * @return 后台所含有的数据的数量
     */
    public int getTotalCount()
    {
        return totalCount;
    }

    @Override
    public void initView()
    {
        super.initView();
        // 通过泛型来兼容更多的列表形式，id是固定的，凡是需要框架自动帮助处理上拉下拉事项的，都需要将列表设置为pull_to_refresh_adapter_view
        pullToRefreshAdapterViewBase = (T) findViewById(R.id.pull_to_refresh_adapter_view);
        // 无数据的提示与列表的切换，通过两个视图的显示设置来实现，需要框架自动处理时，也需要将此视图的id设置为custom_empty_view
        customEmptyView = findViewById(R.id.custom_empty_view);

        pullToRefreshAdapterViewBase.setOnRefreshListener(this);
        pullToRefreshAdapterViewBase.setOnLastItemVisibleListener(this);

        refreshView = pullToRefreshAdapterViewBase.getRefreshableView();

        // 当视图是可以添加尾部的视图，则添加尾部
        initFooter();
    }

    /** 是ptr的设置emptyview的方法，但是大部分情况下我们使用CustomEmptyView来实现我们的目的 */
    public void setEmptyView(View emptyView)
    {
        if (pullToRefreshAdapterViewBase != null)
        {
            pullToRefreshAdapterViewBase.setEmptyView(emptyView);
        }
    }

    /**
     * @return
     *         自定义的无数据提示视图
     */
    public View getCustomEmptyView()
    {
        return customEmptyView;
    }

    /**
     * 
     * @param customEmptyView 自定义的无数据提示视图
     */
    public void setCustomEmptyView(View customEmptyView)
    {
        this.customEmptyView = customEmptyView;
    }

    /** 默认的Adapter，这样我们可以使用onGetView实现Adapter的getView方法，同时不用考虑处理其他事务 */
    public void initAdapter()
    {
        adapter = new U1CityAdapter();
        adapter.setOnGetViewListener(this);

        pullToRefreshAdapterViewBase.setAdapter(adapter);
    }

    /**
     * 设置的是自定义的适配器类的对象，而不是在activity中用onGetView实现
     * 
     * @param 设置主列表的适配器
     * */
    public void initAdapter(U1CityAdapter adapter)
    {
        this.adapter = adapter;
        pullToRefreshAdapterViewBase.setAdapter(adapter);
    }

    /** @return 主列表的适配器 */
    public U1CityAdapter getAdapter()
    {
        return adapter;
    }

    /** 当视图是可以添加尾部的视图，则添加尾部 */
    private void initFooter()
    {
        footerLl = new LinearLayout(this);
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        footerLl.setLayoutParams(lp);
        footerLl.setGravity(Gravity.CENTER);

        LayoutInflater.from(this).inflate(R.layout.footer_adapterview_none_data, footerLl);
        // footerView.setLayoutParams(lp);

        // footerLl.addView(footerView);
        footerLl.setVisibility(View.GONE);

        if (refreshView instanceof ListView)
        {
            ((ListView) refreshView).addFooterView(footerLl);
        }
        else if (refreshView instanceof HeadableGridView)
        {
            ((HeadableGridView) refreshView).addFooterView(footerLl);
        }
        else if (refreshView instanceof ExtendableListView)
        {
            ((ExtendableListView) refreshView).addFooterView(footerLl);
        }
    }

    /** 获取数据列表时可以用此方法，而不需要再调用adapter */
    public List getModels()
    {
        return adapter != null ? adapter.getModels() : null;
    }

    /** 获取数据条目数时可以用此方法，而不需要再调用adapter */
    public int getCount()
    {
        return adapter == null ? 0 : adapter.getCount();
    }

    /** 设置没有更多内容时的提示视图，使用者可以灵活自定义 */
    public void setFooter(View view)
    {
        if (footerLl.indexOfChild(view) == -1)
        {
            footerLl.removeAllViews();
            footerLl.addView(view);
        }
    }

    /** 通过布局id,设置没有更多内容时的提示视图,使用者可以灵活自定义 */
    public void setFooter(int layoutId)
    {
        footerLl.removeAllViews();
        LayoutInflater.from(this).inflate(layoutId, footerLl);
    }

    /**
     * 设置没有更多内容时的提示视图的背景色
     * 
     * @param color 是id值而非颜色值
     */
    public void setFooterViewBgColor(int color)
    {
        View view = footerLl.getChildAt(0);

        if (view != null)
        {
            view.setBackgroundColor(getResources().getColor(color));
        }
    }

    /** 数据加载后，相应视图的变化，一般情况下不需要对此方法进行处理，框架会自动调用，但是如果需要对视图切换进行自定义处理，可以重写此方法 */
    protected void viewHandler()
    {
        Debug.d(TAG, "customEmptyView:" + customEmptyView);
        if (customEmptyView != null)
        {
            
            if (adapter != null && adapter.getCount() > 0)
            {
                customEmptyView.setVisibility(View.GONE);
                pullToRefreshAdapterViewBase.setVisibility(View.VISIBLE);
            }
            else //无数据的情况下，判断是否只显示headView或footView
            {
                if(isShowHeadViewOrFootView){
                    customEmptyView.setVisibility(View.GONE);
                    pullToRefreshAdapterViewBase.setVisibility(View.VISIBLE);
                }else{
                    
                    customEmptyView.setVisibility(View.VISIBLE);
                    pullToRefreshAdapterViewBase.setVisibility(View.GONE);
                }
            }
        }

        if (indexPage * pageSize < totalCount)
        {
            if (footerLl != null)
            {
                footerLl.setVisibility(View.GONE);
            }
        }
        else
        {
            if (footerLl != null)
            {
                footerLl.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void initData()
    {
        super.initData();
        getData(true);
    }

    /** 在网络不可用的时候可以调用重写此方法，做一些相应的处理 */
    public void onNetNotAvailable()
    {

    };

    /**
     * 获取远程数据的方法，我们在这里对网络状态进行了判断，同时处理加载动画，一般刷新页面时，
     * 可以调用getData(true)，同时可以在此之前setFirstLoading(true)，这样加载过程的动画显示会比较合理
     */
    protected final void getData(final boolean isDrawDown)
    {
        this.isDrawDown = isDrawDown;

        startTime = new Date().getTime();

        if (!NetUtil.isNetworkConnected(this))
        {
            ToastUtil.showNotNetToast(this);
            pullToRefreshAdapterViewBase.onRefreshComplete();
            stopLoading();
            onNetNotAvailable();
            return;
        }

        // 第一次加载时显示title的loadingBar
        if (isDrawDown && isFirstLoading())
        {
            startLoading();
            setFirstLoading(false);
        }

        Debug.d(BaseActivity.TAG, "isDrawDown:" + isDrawDown);
        isGettingData = true;

        if (isDrawDown)
        {
            indexPage = 1;
            footerLl.setVisibility(View.GONE);
        }
        else
        {
            indexPage++;
            Debug.d(BaseActivity.TAG, "index is:" + indexPage);
        }

        onDataPrepare(isDrawDown);
    }

    public boolean isDrawDown()
    {
        return isDrawDown;
    }

    /**
     * 获取远程数据前的准备
     * 主要用于远程数据接口的调用和数据处理的实现
     * */
    protected abstract void onDataPrepare(final boolean isDrawDown);

    /** 数据加载完后的页面切换，在onError等情况下也可调用此方法处理 */
    protected void executeOnLoadFinish()
    {
        if (pullToRefreshAdapterViewBase != null)
        {
            pullToRefreshAdapterViewBase.onRefreshComplete();
        }

        isGettingData = false;
        viewHandler();
    }

    /**
     * 一般情况下，成功获取数据后，调用此方法，框架会自动处理数据加载和页面切换
     * 
     * @param data 分页数据
     * @param totalCount 服务端数据条目数
     * @param isDrawDown 是否第一次加载
     * */
    protected void executeOnLoadDataSuccess(List<?> data, int totalCount, boolean isDrawDown)
    {
        Debug.d(TAG, "getData used time:" + (new Date().getTime() - startTime) + "ms");

        this.totalCount = totalCount;

        if (isDrawDown && adapter != null)
        {
            adapter.clear();
            Debug.d(BaseActivity.TAG, "clear adapter and index is 1");
        }

        Debug.d(TAG, "adapter is not null:" + (adapter != null) + " -- has data:" + (data != null && data.size() > 0));
        if (adapter != null && data != null && data.size() > 0)
        {
            adapter.addData(data);
        }

        executeOnLoadFinish();
    }

    /** 在最后一条被显示时，尝试获取新数据 */
    @Override
    public void onLastItemVisible()
    {
        Debug.d(BaseActivity.TAG, "onLastItemVisible getting data now? " + (!isGettingData && indexPage * pageSize < totalCount));

        if (!isGettingData)
        {
            // Debug.d(BaseActivity.TAG, "has footer: "
            // + (footerLl != null));
            if (indexPage * pageSize < totalCount)
            {
                getData(false);
            }
        }

    }

    /** 显示了上拉下拉提示 */
    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView)
    {
        String label = DateUtils.formatDateTime(this, System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
        refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

        getData(true);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView)
    {
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
    }

    protected LinearLayout getFooterLl()
    {
        return footerLl;
    }
    
    
    private boolean isShowHeadViewOrFootView = false;
    
    /**
     * 是否显示headView或footView,为true则显示，false不显示
     * 添加此方法，主要用于在主listview无数据时，根据情况是否显示headView或footView
     */
    public void setShowHeadViewOrFootView(boolean isShowHeadViewOrFootView){
        this.isShowHeadViewOrFootView = isShowHeadViewOrFootView;
    }
}
