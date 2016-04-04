/*
 * 系统: U1CityModule
 * 文件名: DataLoader.java
 * 版权: U1CITY Corporation 2015
 * 描述:
 * 创建人: zhengjb
 * 创建时间: 2015-8-10 上午10:24:34
 */
package com.u1city.module.pulltorefresh;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.etsy.android.grid.ExtendableListView;
import com.u1city.module.R;
import com.u1city.module.base.BaseActivity;
import com.u1city.module.base.BaseFragment;
import com.u1city.module.base.U1CityAdapter;
import com.u1city.module.common.Debug;
import com.u1city.module.pulltorefresh.PullToRefreshBase.OnLastItemVisibleListener;
import com.u1city.module.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import com.u1city.module.util.NetUtil;
import com.u1city.module.util.ToastUtil;
import com.u1city.module.widget.HeadableGridView;

import java.util.Date;
import java.util.List;

/**
 * PullToRefresh的数据填充类，在这个框架里，将数据填充的功能从activity、fragment身上剥离，
 * 从而解除PullToRefresh与activity、fragment的耦合，带来可以在activity、fragment上同时
 * 使用多个PullToRefresh而且你不用去实现这些PullToRefresh的数据填充的细节，只需要获取数据即可。
 * 
 * @author zhengjb
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class DataLoader implements OnRefreshListener2, OnLastItemVisibleListener
{
    private static final String TAG = DataLoader.class.getName();

    private Context context;

    private BaseActivity baseActivity;
    private BaseFragment baseFragment;

    private PullToRefreshAdapterViewBase pullToRefreshAdapterViewBase;

    /** 无数据时显示的视图 */
    private View emptyView;
    public PullToRefreshAdapterViewBase getPullToRefreshAdapterViewBase()
    {
        return pullToRefreshAdapterViewBase;
    }

    /** 主体数据列表 */
    private View refreshView;
    /** 没有更多数据加载时显示的视图 */
    private LinearLayout footerLl;

    /** 数据列表的适配器 */
    private U1CityAdapter adapter;

    private DataSource dataSource;

    private ViewHandler viewHandler;

    private DataCache dataCache;

    /** 每个分页的数目 */
    private int pageSize = 20;
    /** 初始页数为1 */
    private int indexPage = 1;
    /** 总的条目数，指的是服务端的相应数据的总条目数，而不是当前app显示的条目数 */
    private int totalCount;

    /** 标志是否正处于加载数据的过程中 */
    private boolean isGettingData = false;
    
    private long startTime;
    
    private boolean isDrawDown;
    
    /** 是否自动加载 */
    private boolean enableAutoLoading = true;

    /**
     * 设置是否自动加载
     * @param enableAutoLoading true为自动加载，false为不自动加载
     * */
    public void setEnableAutoLoading(boolean enableAutoLoading)
    {
        this.enableAutoLoading = enableAutoLoading;
    }

    /**
     * 它是专门为处理PullToRefresh的数据填充而存在的，
     * 所有需要拥有一个PullToRefresh的控件
     * 
     * @param context activity或fragment
     * @param pullToRefreshAdapterViewBase
     */
    public DataLoader(BaseActivity baseActivity, PullToRefreshAdapterViewBase pullToRefreshAdapterViewBase)
    {
        super();

        this.context = baseActivity;
        this.baseActivity = baseActivity;

        init(pullToRefreshAdapterViewBase);
    }

    /**
     * 它是专门为处理PullToRefresh的数据填充而存在的，
     * 所有需要拥有一个PullToRefresh的控件
     * 
     * @param context activity或fragment
     * @param pullToRefreshAdapterViewBase
     */
    public DataLoader(BaseFragment baseFragment, PullToRefreshAdapterViewBase pullToRefreshAdapterViewBase)
    {
        super();

        this.context = baseFragment.getActivity();
        this.baseFragment = baseFragment;

        init(pullToRefreshAdapterViewBase);
    }

    /**
     * 初始化动作
     * 
     * @author zhengjb
     */
    private void init(PullToRefreshAdapterViewBase pullToRefreshAdapterViewBase)
    {
        if (context == null)
        {
            throw new NullPointerException("context should not be null");
        }

        if (pullToRefreshAdapterViewBase == null)
        {
            throw new NullPointerException("pullToRefreshAdapterViewBase is null, can't handler this");
        }

        this.pullToRefreshAdapterViewBase = pullToRefreshAdapterViewBase;

        pullToRefreshAdapterViewBase.setOnRefreshListener(this);
        pullToRefreshAdapterViewBase.setOnLastItemVisibleListener(this);

        refreshView = pullToRefreshAdapterViewBase.getRefreshableView();

        // 当视图是可以添加尾部的视图，则添加尾部
        initFooter();
    }
    
    /**
     * @return 无数据提示视图
     * 
     * */
    public View getEmptyView()
    {
        return emptyView;
    }

    /**
     * @param dataSource 数据源，提供数据的接口
     * */
    public void setDataSource(DataSource dataSource)
    {
        this.dataSource = dataSource;
        
        if(enableAutoLoading){
            getData(true);
        }
    }

    /**
     * @param viewHandler 视图切换处理者，主要是用于数据加载过程中自定义视图的切换
     * 
     * */
    public void setViewHandler(ViewHandler viewHandler)
    {
        this.viewHandler = viewHandler;
    }

    /**
     * 
     * @param dataCache 没有网络时，提供的数据缓存
     * */
    public void setDataCache(DataCache dataCache)
    {
        this.dataCache = dataCache;
    }

    public int getIndexPage()
    {
        return indexPage;
    }

    /** 设置尾部是否可见 */
    public void setFooterVisible(int visibility)
    {
        if (footerLl != null)
        {
            footerLl.setVisibility(visibility);
        }
    }

    public int getPageSize()
    {
        return pageSize;
    }

    /** 请在加载数据之前设置 */
    public void setPageSize(int pageSize)
    {
        this.pageSize = pageSize;
    }

    public U1CityAdapter getAdapter()
    {
        return adapter;
    }

    public void setAdapter(U1CityAdapter adapter)
    {
        pullToRefreshAdapterViewBase.setAdapter(adapter);
        this.adapter = adapter;
    }

    public int getTotalCount()
    {
        return totalCount;
    }

    /** 当视图是可以添加尾部的视图，则添加尾部 */
    private void initFooter()
    {
        footerLl = new LinearLayout(context);
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        footerLl.setLayoutParams(lp);
        footerLl.setGravity(Gravity.CENTER);

        LayoutInflater.from(context).inflate(R.layout.footer_adapterview_none_data, footerLl);

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
        LayoutInflater.from(context).inflate(layoutId, footerLl);
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
            view.setBackgroundColor(context.getResources().getColor(color));
        }
    }

    /** 数据加载后，相应视图的变化 */
    private void viewHandler()
    {
        Debug.d(TAG, "emptyView:" + emptyView);

        if (viewHandler != null)
        {
            viewHandler.handledView();
        }
        else
        {
            handledViewByDefault();
        }
    }

    /** 默认处理视图切换的方法 */
    private void handledViewByDefault()
    {
        if (emptyView != null)
        {
            if (adapter != null && adapter.getCount() > 0)
            {
                emptyView.setVisibility(View.GONE);
                pullToRefreshAdapterViewBase.setVisibility(View.VISIBLE);
            }
            else
            {
                emptyView.setVisibility(View.VISIBLE);
                pullToRefreshAdapterViewBase.setVisibility(View.GONE);
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

    /** 推荐存在一个EmptyView的时候使用此方法，当存在多个PullToRefresh的时候，建议通过设置footer显示emptyView，而不是使用此方法 */
    public void setEmptyView(View emptyView)
    {
        emptyView.setLayoutParams(new android.view.ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        ViewGroup viewGroup = (ViewGroup) pullToRefreshAdapterViewBase.getParent();
        emptyView.setVisibility(View.GONE);

        if (this.emptyView != null)
        {
            viewGroup.removeView(emptyView);
        }

        this.emptyView = emptyView;
        viewGroup.addView(emptyView);
    }

    /** 推荐存在一个EmptyView的时候使用此方法，当存在多个PullToRefresh的时候，建议通过设置footer显示emptyView，而不是使用此方法，参数为布局id */
    public void setEmptyView(int id)
    {
        View emptyView = LayoutInflater.from(context).inflate(id, null);
        setEmptyView(emptyView);
    }

    public void initData()
    {
        getData(true);
    }

    /** 在没有网络的时候使用缓存 */
    private void onNetNotAvailable()
    {
        ToastUtil.showNotNetToast(context);

        if (baseFragment != null)
        {
            baseFragment.stopLoading();
        }

        if (baseActivity != null)
        {
            baseActivity.stopLoading();
        }

        if (dataCache != null)
        {
            dataCache.cacheData();
        }

        pullToRefreshAdapterViewBase.onRefreshComplete();
    };

    public boolean isDrawDown()
    {
        return isDrawDown;
    }

    /**
     * 获取远程数据的方法，我们在这里对网络状态进行了判断，同时处理加载动画，一般刷新页面时，
     * 可以调用getData(true)，同时可以在此之前setFirstLoading(true)，这样加载过程的动画显示会比较合理
     */
    public final void getData(final boolean isDrawDown)
    { 
        this.isDrawDown = isDrawDown;
        
        startTime = new Date().getTime();
        
        if (!NetUtil.isNetworkConnected(context))
        {
            onNetNotAvailable();
            return;
        }

        if (dataSource == null)
        {
            return;
        }

        Debug.d(TAG, "isDrawDown:" + isDrawDown);
        isGettingData = true;

        if (isDrawDown)
        {
            indexPage = 1;
            footerLl.setVisibility(View.GONE);
        }
        else
        {
            indexPage++;
            Debug.d(TAG, "index is:" + indexPage);
        }

        dataSource.onDataPrepare(isDrawDown);
    }

    /** 刷新数据 */
    public void refresh()
    {
        getData(true);
    }

    /** 数据加载完后的页面切换，在onError等情况下也可调用此方法处理 */
    private void executeOnLoadFinish()
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
    public void executeOnLoadDataSuccess(List<?> data, int totalCount, boolean isDrawDown)
    {
        Debug.d(TAG, "getData used time:" + (new Date().getTime() - startTime) + "ms");
        
        this.totalCount = totalCount;

        if (isDrawDown && adapter != null)
        {
            adapter.clear();
            Debug.d(TAG, "clear adapter and index is 1");
        }

        Debug.d(TAG, "adapter is not null:" + (adapter != null) + " -- has data:" + (data != null && data.size() > 0));
        if (adapter != null && data != null && data.size() > 0)
        {
            adapter.addModel(data);
        }

        executeOnLoadFinish();
    }

    /** 在最后一条被显示时，尝试获取新数据 */
    @Override
    public void onLastItemVisible()
    {
        Debug.d(TAG, "onLastItemVisible getting data now? " + (!isGettingData && indexPage * pageSize < totalCount));

        if (!isGettingData)
        {
            // Debug.d(TAG, "has footer: "
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
        String label = DateUtils.formatDateTime(context, System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
        refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

        getData(true);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView)
    {
    }

    /** 数据来源接口 */
    public interface DataSource
    {
        public void onDataPrepare(boolean isDrawDown);
    }

    /** 切换视图的处理者，只有在有需要的时候才设置，否则都是调用默认的视图切换处理方法 **/
    public interface ViewHandler
    {
        public void handledView();
    }

    /** 使用缓存的数据 */
    public interface DataCache
    {
        public void cacheData();
    }
}
