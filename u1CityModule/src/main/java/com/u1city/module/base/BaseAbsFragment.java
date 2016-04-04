package com.u1city.module.base;

import android.text.format.DateUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
 * @date 2014-10-29
 * @time 下午5:17:55 类说明:顶部滑动Fragment且内容是List
 * 
 **************************************/
@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class BaseAbsFragment<T extends PullToRefreshAdapterViewBase> extends BaseFragment implements OnRefreshListener2, OnLastItemVisibleListener, OnItemClickListener, OnGetViewListener
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

    public BaseAbsFragment()
    {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, int resource, boolean attachToRoot, boolean hasTitle)
    {
        return super.onCreateView(inflater, container, resource, attachToRoot, hasTitle);
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

    public U1CityAdapter getAdapter()
    {
        return adapter;
    }

    public void setPageSize(int pageSize)
    {
        this.pageSize = pageSize;
    }

    public int getTotalCount()
    {
        return totalCount;
    }

    public int getIndexPage()
    {
        return indexPage;
    }

    public void setIndexPage(int indexPage)
    {
        this.indexPage = indexPage;
    }

    public T getPullToRefreshAdapterViewBase()
    {
        return pullToRefreshAdapterViewBase;
    }

    @Override
    public void initView()
    {
        super.initView();
        // 通过泛型来兼容更多的列表形式，id是固定的，凡是需要框架自动帮助处理上拉下拉事项的，都需要将列表设置为pull_to_refresh_adapter_view
        pullToRefreshAdapterViewBase = (T) view.findViewById(R.id.pull_to_refresh_adapter_view);
        // 无数据的提示与列表的切换，通过两个视图的显示设置来实现，需要框架自动处理时，也需要将此视图的id设置为custom_empty_view
        customEmptyView = view.findViewById(R.id.custom_empty_view);

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

    public View getCustomEmptyView()
    {
        return customEmptyView;
    }

    public void setCustomEmptyView(View customEmptyView)
    {
        this.customEmptyView = customEmptyView;
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

    /** 默认的Adapter，这样我们可以使用onGetView实现Adapter的getView方法，同时不用考虑处理其他事务 */
    public void initAdapter()
    {
        adapter = new U1CityAdapter();
        adapter.setOnGetViewListener(this);
        pullToRefreshAdapterViewBase.setAdapter(adapter);
    }
    
    public void initAdapter(U1CityAdapter adapter){
        this.adapter = adapter;
        pullToRefreshAdapterViewBase.setAdapter(adapter);
    }

    /** 当视图是可以添加尾部的视图，则添加尾部 */
    private void initFooter()
    {
        footerLl = new LinearLayout(getActivity());
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        footerLl.setLayoutParams(lp);
        footerLl.setGravity(Gravity.CENTER);
        footerLl.setOrientation(LinearLayout.VERTICAL);

        LayoutInflater.from(getActivity()).inflate(R.layout.footer_adapterview_none_data, footerLl);

        // footerLl.addView(footerView);
        footerLl.setVisibility(View.GONE);

        // 暂时只有这两个类型才可以添加footer
        if (refreshView instanceof ListView)
        {
            ((ListView) refreshView).addFooterView(footerLl);
        }
        else if (refreshView instanceof HeadableGridView)
        {
            Debug.d(TAG, "add footer");
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

    /** 通过布局id,设置没有更多内容时的提示视图,使用者可以灵活自定义 */
    public void setFooter(int layoutId)
    {
        footerLl.removeAllViews();
        LayoutInflater.from(getActivity()).inflate(layoutId, footerLl);
    }

    /** 数据加载后，相应视图的变化，一般情况下不需要对此方法进行处理，框架会自动调用，但是如果需要对视图切换进行自定义处理，可以重写此方法 */
    protected void viewHandler()
    {
        Debug.d(TAG, "has customEmptyView:" + (customEmptyView != null));
        if (customEmptyView != null)
        {
            if (adapter != null && adapter.getCount() > 0)
            {
                Debug.d(TAG, "customEmptyView hide");
                customEmptyView.setVisibility(View.GONE);
                pullToRefreshAdapterViewBase.setVisibility(View.VISIBLE);
            }
            else
            {
                Debug.d(TAG, "customEmptyView show");
                customEmptyView.setVisibility(View.VISIBLE);
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

    public boolean isDrawDown()
    {
        return isDrawDown;
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
        
        if (!NetUtil.isNetworkConnected(getActivity()))
        {
            ToastUtil.showNotNetToast(getActivity());
            pullToRefreshAdapterViewBase.onRefreshComplete();
            stopLoading();
            onNetNotAvailable();
            return;
        }

        Debug.d(TAG, "isDrawDown:" + isDrawDown);
        isGettingData = true;

        // 第一次加载时显示title的loadingBar
        if (isDrawDown && isFirstLoading())
        {
            startLoading();
            setFirstLoading(false);
        }

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

        onDataPrepare(isDrawDown);
    }

    /**
     * 获取远程数据前的准备 主要用于远程数据接口的调用和数据处理的实现
     * */
    protected abstract void onDataPrepare(final boolean isDrawDown);

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
    }

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
            Debug.d(TAG, "clear adapter and index is 1");
        }

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
        Debug.d(TAG, "onLastItemVisible getting data now? " + (!isGettingData && indexPage * pageSize < totalCount));

        if (!isGettingData)
        {
            // Debug.d(TAG, "has footer: " + (footerLl != null));

            // Debug.d(TAG, "indexPage:" + indexPage + " -- pageSize:" +
            // pageSize + " -- totalCount:" + totalCount);
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
        String label = DateUtils.formatDateTime(getActivity(), System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
        refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

        getData(true);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView)
    {
    }

    public LinearLayout getFooterLl()
    {
        return footerLl;
    }
}
