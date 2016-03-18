package com.xiaolongonly.finalschoolexam.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.text.SpannableStringBuilder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.u1city.module.base.U1CityAdapter;
import com.u1city.module.common.BaseAnalysis;
import com.u1city.module.common.Debug;
import com.u1city.module.common.JsonAnalysis;
import com.u1city.module.common.StandardCallback;
import com.u1city.module.pulltorefresh.DataLoader;
import com.u1city.module.pulltorefresh.PullToRefreshListView;
import com.u1city.module.util.ListUtils;
import com.u1city.module.util.StringUtils;
import com.xiaolongonly.finalschoolexam.R;
import com.xiaolongonly.finalschoolexam.model.StoreOrderModel;
import com.xiaolongonly.finalschoolexam.utils.RequestApi;
import com.xiaolongonly.finalschoolexam.utils.StringConstantUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends RealBaseActivity {

    /**
     * actionBar的圆角按钮
     */
    private TextView leftTabTv;
    private TextView rightTabTv;

    /* 地图页面 */
    private PullToRefreshListView offLineListView;
    private View headView;
    private DataLoader offLineDataLoader;
    private TextView noticeTv;
    private LinearLayout offLineLl;
    private DrawerLayout mDrawerLayout;
    private LinearLayout llytMeDrawer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_main, R.layout.title_double_tab_grey);
    }

    @Override
    public void onReceiveBroadCast(Context context, Intent intent) {
        super.onReceiveBroadCast(context, intent);

        String action = intent.getAction();
//        /**
//         * 判断接受到的广播意图，执行相应操作
//         */
//        if(StringConstantUtils.ACTION_CHANGE_ORDER_TAB.equals(action)){
//            int tabId = intent.getIntExtra(StringConstantUtils.EXTRA_ORDER_TAB_ID, -1);
//
//            if(tabId >= 0 && tabId < titles.length){
//                onLineViewPager.setCurrentItem(tabId);
//            }
//        }
    }

    @Override
    public void initView() {
        super.initView();
        initTitleBar();
        initDataLoader();
        initNoneDataView();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.requestDisallowInterceptTouchEvent(true);
        llytMeDrawer = (LinearLayout) findViewById(R.id.llytMeDrawer);
        toggleTab(StringConstantUtils.Show_By_BaiduMap);

        //设置广播
//        setIntentFilter(new IntentFilter(StringConstantUtils.ACTION_CHANGE_ORDER_TAB));
    }

    /**
     * 无数据提示
     *
     * @author zhengjb
     */
    private void initNoneDataView()
    {
        ImageView ivNoGoods = (ImageView)findViewById(R.id.empty_view_iv);
        ivNoGoods.setImageResource(R.mipmap.ic_launcher);
        TextView textNoneData = (TextView)findViewById(R.id.empty_view_tv);
        textNoneData.setText("你身边暂时没人使用哦，赶紧去邀请小伙伴来玩吧！");
    }
    /**
     * 初始化第二个页面的DataLoader
     */
    private void initDataLoader() {
        offLineLl = (LinearLayout) findViewById(R.id.activity_my_order_offline_ll);
        offLineListView = (PullToRefreshListView) findViewById(R.id.activity_my_order_ptr_lv);
        headView = LayoutInflater.from(this).inflate(R.layout.head_off_line_order, null);
        offLineListView.getRefreshableView().addHeaderView(headView);
        offLineDataLoader = new DataLoader(this, offLineListView);
        offLineDataLoader.setEmptyView(R.layout.empty_view_custom_default);
        offLineDataLoader.setAdapter(new OffLineOrderAdapter(this));
        offLineDataLoader.setDataSource(new DataLoader.DataSource() {
            @Override
            public void onDataPrepare(boolean isDrawDown) {
//                MainActivity.this.isDrawDown = isDrawDown;
//                int customerId = Constants.cust.getCustomerId();
//                RequestApi.getInstance().getCustomerStoreOrderList(customerId, offLineDataLoader.getIndexPage(), offLineDataLoader.getPageSize(), offLineCallBack);
                offLineDataLoader.executeOnLoadDataSuccess(null, 0, false);
            }
        });
        noticeTv = (TextView) headView.findViewById(R.id.activity_my_order_notice_tv);
    }

    private void initTitleBar() {
        leftTabTv = (TextView) findViewById(R.id.left_tab_tv);
        leftTabTv.setText("地图查看");
        leftTabTv.setOnClickListener(clickListener);

        rightTabTv = (TextView) findViewById(R.id.right_tab_tv);
        rightTabTv.setText("列表查看");
        rightTabTv.setOnClickListener(clickListener);
    }

    private OnClickListener clickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.left_tab_tv:
                    Debug.i(TAG, "left_tab_tv is click");
                    toggleTab(StringConstantUtils.Show_By_BaiduMap);
                    break;

                case R.id.right_tab_tv:
                    Debug.i(TAG, "right_tab_tv is click");
                    toggleTab(StringConstantUtils.Show_By_List);

                    break;
                default:
                    break;
            }
        }
    };
    /**
     * 门店消费数据适配器
     */
    private class OffLineOrderAdapter extends U1CityAdapter<StoreOrderModel> {
        private DecimalFormat df = new DecimalFormat("0.00");

        /**
         * 门店消费数据适配器
         */
        public OffLineOrderAdapter(Context context) {
            super(context);
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final StoreOrderModel storeOrderModel = (StoreOrderModel) getItem(position);
            if (convertView == null) {
                //设置item的view
                convertView = inflater.inflate(R.layout.item_surround, null);
            }

            convertView.setTag(R.id.tag_position, position);
            convertView.setOnClickListener(actionListener);

            //绑定item上的控件
//            TextView storeTv = ViewHolder.get(convertView, R.id.item_order_offline_store_tv);
//            TextView dateTv = ViewHolder.get(convertView, R.id.item_order_offline_date_tv);
//            TextView priceTv = ViewHolder.get(convertView, R.id.item_order_offline_price_tv);
//            Button actionBtn = ViewHolder.get(convertView, R.id.item_order_offline_action_btn);

//            StringUtils.setText(storeTv, storeOrderModel.getStoreName());
//            StringUtils.setCreated(storeOrderModel.getTime(), dateTv);

//            String time = storeOrderModel.getTime();
//
//            if(time.length() > 10){
//                time = time.substring(0, 10);
//            }
//            dateTv.setText(time);
//
//            String price = df.format(storeOrderModel.getConsumpMoney());
//            String text = "消费金额：￥" + price;
//            SpannableStringBuilder spannableStringBuilder = StringUtils.getColoredText(text, "#ff6464", 5);//着色
//            spannableStringBuilder = StringUtils.getBoldText(spannableStringBuilder, 5, text.length());//设置部分粗体
//            priceTv.setText(spannableStringBuilder);
//
//            actionBtn.setTag(R.id.tag_position, position);
//            actionBtn.setOnClickListener(actionListener);

            return convertView;
        }

        private OnClickListener actionListener = new OnClickListener() {

            @Override
            public void onClick(View v) {
                int position = (Integer) v.getTag(R.id.tag_position);
                final StoreOrderModel storeOrderModel = (StoreOrderModel) getItem(position);
                Debug.i("storeOrderModel", storeOrderModel.toString());

//                Intent it = new Intent(MainActivity.this, ConsumeDetailActivity.class);
//                it.putExtra(StringConstantUtils.MODEL_STORE_ORDER, storeOrderModel);
//                MyOrderActivity.this.startActivity(it);
            }
        };
    }

    /**
     * 点击标签，选择地图显示还是ListView列表显示
     */
    private void toggleTab(int type) {
        if (type == StringConstantUtils.Show_By_BaiduMap) {
            offLineLl.setVisibility(View.GONE);
            leftTabTv.setBackgroundResource(R.drawable.brand_mytrace_title_left_select);
            leftTabTv.setTextColor(0xffffffff);
            rightTabTv.setBackgroundResource(R.drawable.brand_mytrace_title_right);
            rightTabTv.setTextColor(0xff7b7b7b);
        } else {

            offLineLl.setVisibility(View.VISIBLE);
            leftTabTv.setBackgroundResource(R.drawable.brand_mytrace_title_left);
            leftTabTv.setTextColor(0xff7b7b7b);
            rightTabTv.setBackgroundResource(R.drawable.brand_mytrace_title_right_select);
            rightTabTv.setTextColor(0xffffffff);
        }
    }

    //        /** 线下消费回调 */
//        private StandardCallback offLineCallBack = new StandardCallback(this)
//        {
//
//            @Override
//            public void onResult(BaseAnalysis analysis)
//            {
//                List<StoreOrderModel> storeOrderModels = new ArrayList<StoreOrderModel>();
//
//                try
//                {
//                    JsonAnalysis<StoreOrderModel> jsonAnalysis = new JsonAnalysis<StoreOrderModel>();
//                    if(!StringUtils.isEmpty(analysis.getResult())){
//                        storeOrderModels = jsonAnalysis.listFromJson(analysis.getStringFromResult("storeOrderList"), StoreOrderModel.class);
//                    }
//
//                    if(isDrawDown && !ListUtils.isEmpty(storeOrderModels)){
//                        String shopName = storeOrderModels.get(0).getTmallShopName();
//                        String text = "您在  “" + shopName + "”  线下门店消费";
//                        SpannableStringBuilder builder = StringUtils.getColoredText(text, "#ffa72d", 4, 6 + shopName.length());
//                        builder = StringUtils.getBoldText(builder, 4, 6 + shopName.length());
//                        noticeTv.setText(builder);
//
//                        headView.setVisibility(View.VISIBLE);
//                    }
//                }
//                catch (Exception e)
//                {
//                    e.printStackTrace();
//                }
//
//                offLineDataLoader.executeOnLoadDataSuccess(storeOrderModels, analysis.getTotal(), isDrawDown);
//            }
//
//            @Override
//            public void onError(int type)
//            {
//                offLineListView.onRefreshComplete();
//            }
//        };


    public void closeDrawer()
    {

        if (mDrawerLayout != null)
        {
            mDrawerLayout.closeDrawer(llytMeDrawer);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        mDrawerLayout.closeDrawer(llytMeDrawer);
//        if(!Constants.isVisitor(getActivity())){
//            initData();
//        }
    }
}
