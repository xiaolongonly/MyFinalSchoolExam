package com.xiaolongonly.finalschoolexam.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.u1city.module.base.U1CityAdapter;
import com.u1city.module.common.Debug;
import com.u1city.module.pulltorefresh.DataLoader;
import com.u1city.module.pulltorefresh.PullToRefreshListView;
import com.xiaolongonly.finalschoolexam.Info;
import com.xiaolongonly.finalschoolexam.R;
import com.xiaolongonly.finalschoolexam.model.StoreOrderModel;
import com.xiaolongonly.finalschoolexam.utils.StringConstantUtils;
import java.text.DecimalFormat;

public class MainActivity extends BaseBitmapActivity {

    /**
     * actionBar的圆角按钮
     */
    private TextView leftTabTv;
    private TextView rightTabTv;

    private PullToRefreshListView offLineListView;
    private View headView;
    private DataLoader offLineDataLoader;
    private TextView noticeTv;
    private LinearLayout listInfo;
    /**
     * DrawerLayout控件
     */
    private DrawerLayout mDrawerLayout;
    /**
     * 侧拉框的LinearLayout
     */
    private LinearLayout llytMeDrawer;
    /**
     * 百度地图的relativeLayout用来切换
     */
    private RelativeLayout rl_baidumap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());
        super.onCreate(savedInstanceState, R.layout.activity_main, R.layout.title_double_tab_grey);
        initMark();
    }
    /**初始化百度地图信息*/
    public void initMark()
    {
        initBaiduMap();
        initMarker();//父类的初始化覆盖物类
        setMapListener();//设置地图事件监听
    }




    @Override
    public void onReceiveBroadCast(Context context, Intent intent) {
        super.onReceiveBroadCast(context, intent);

//        String action = intent.getAction();
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
        rl_baidumap = (RelativeLayout) findViewById(R.id.rl_baidumap);
        findViewById(R.id.iv_head_btn).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mDrawerLayout.isDrawerOpen(llytMeDrawer))
                {
                    mDrawerLayout.closeDrawer(llytMeDrawer);
                }
                else {
                    mDrawerLayout.openDrawer(llytMeDrawer);
                }
            }
        });
        toggleTab(StringConstantUtils.Show_By_BaiduMap);
        //设置广播
//        setIntentFilter(new IntentFilter(StringConstantUtils.ACTION_CHANGE_ORDER_TAB));
    }

    private void setMapListener() {
        myBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mMarkerLy.setVisibility(View.GONE);
                myBaiduMap.hideInfoWindow();
            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }
        });
        myBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                Bundle ExtraInfo = marker.getExtraInfo();
                if(ExtraInfo==null)
                {
                    return false;
                }
                Info info = (Info) ExtraInfo.getSerializable("info");
                ImageView iv = (ImageView) mMarkerLy.findViewById(R.id.id_info_img);
                TextView distance = (TextView) mMarkerLy.findViewById(R.id.id_info_distance);
                TextView dname = (TextView) mMarkerLy.findViewById(R.id.id_info_name);
                TextView zan = (TextView) mMarkerLy.findViewById(R.id.id_info_zan);
                iv.setImageResource(info.getImgId());
                distance.setText(info.getDistance());
                dname.setText(info.getName());
                zan.setText(info.getZan() + "");
                InfoWindow infoWindow;
                TextView tv = new TextView(MainActivity.this);
                tv.setBackgroundResource(R.drawable.location_tips);
                tv.setPadding(30, 20, 30, 50);
                tv.setText(info.getName());
                tv.setTextColor(Color.parseColor("#ffffff"));

                final LatLng latLng = marker.getPosition();
                infoWindow = new InfoWindow(tv, latLng, -47);
                myBaiduMap.showInfoWindow(infoWindow);
                mMarkerLy.setVisibility(View.VISIBLE);
                return true;
            }
        });

    }

    /**
     * 初始化覆盖物
     */
    private void initMarker() {
        mMarker =BitmapDescriptorFactory.fromResource(R.mipmap.maker);
        mMarkerLy =(RelativeLayout)findViewById(R.id.id_marker_layout);
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
        listInfo = (LinearLayout) findViewById(R.id.activity_my_order_offline_ll);
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
            listInfo.setVisibility(View.GONE);
            rl_baidumap.setVisibility(View.VISIBLE);
            leftTabTv.setBackgroundResource(R.drawable.brand_mytrace_title_left_select);
            leftTabTv.setTextColor(0xffffffff);
            rightTabTv.setBackgroundResource(R.drawable.brand_mytrace_title_right);
            rightTabTv.setTextColor(0xff7b7b7b);
        } else {
            listInfo.setVisibility(View.VISIBLE);
            rl_baidumap.setVisibility(View.GONE);
            leftTabTv.setBackgroundResource(R.drawable.brand_mytrace_title_left);
            leftTabTv.setTextColor(0xff7b7b7b);
            rightTabTv.setBackgroundResource(R.drawable.brand_mytrace_title_right_select);
            rightTabTv.setTextColor(0xffffffff);
        }
    }



//    public void closeDrawer()
//    {
//
//        if (mDrawerLayout != null)
//        {
//            mDrawerLayout.closeDrawer(llytMeDrawer);
//        }
//
//    }

    @Override
    protected void onResume() {
        super.onResume();
        mDrawerLayout.closeDrawer(llytMeDrawer);
//        if(!Constants.isVisitor(getActivity())){
//            initData();
//        }
    }

    /**
     * 菜单
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.id_map_common:
                myBaiduMap.setMapType(myBaiduMap.MAP_TYPE_NORMAL);
                break;
            case R.id.id_map_site:
                myBaiduMap.setMapType(myBaiduMap.MAP_TYPE_SATELLITE);
                break;
            case R.id.id_map_traffic:
                if (myBaiduMap.isTrafficEnabled()) {
                    myBaiduMap.setTrafficEnabled(false);
                    item.setTitle("实时交通(off)");
                } else {
                    myBaiduMap.setTrafficEnabled(true);
                    item.setTitle("实时交通(on)");
                }
                break;
            case R.id.id_map_mylocation:
                centerToMyLocation();
                break;
            case R.id.id_map_mode_common:
                mLocationMode = MyLocationConfiguration.LocationMode.NORMAL;
                break;
            case R.id.id_map_mode_follow:
                mLocationMode = MyLocationConfiguration.LocationMode.FOLLOWING;
                break;
            case R.id.id_map_mode_compass:
                mLocationMode = MyLocationConfiguration.LocationMode.COMPASS;
                break;
            case R.id.id_add_overlay:
                addOverlays(Info.infos);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
