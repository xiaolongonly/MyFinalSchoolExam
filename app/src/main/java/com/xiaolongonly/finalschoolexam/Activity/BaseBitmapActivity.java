package com.xiaolongonly.finalschoolexam.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomControls;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.utils.DistanceUtil;
import com.etsy.android.grid.ExtendableListView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.u1city.module.pulltorefresh.PullToRefreshBase;
import com.u1city.module.util.DimensUtil;
import com.u1city.module.util.SimpleImageOption;
import com.u1city.module.widget.RoundedImageView;
import com.xiaolongonly.finalschoolexam.api.RequestApi;
import com.xiaolongonly.finalschoolexam.listener.MyOrientationListener;
import com.xiaolongonly.finalschoolexam.R;
import com.xiaolongonly.finalschoolexam.model.TaskModel;
import com.xiaolongonly.finalschoolexam.model.UserModel;
import com.xiaolongonly.finalschoolexam.utils.ConstantUtil;
import com.xiaolongonly.finalschoolexam.utils.ImageLoaderConfig;
import com.xiaolongonly.finalschoolexam.utils.MyAnalysis;
import com.xiaolongonly.finalschoolexam.utils.MyStandardCallback;
import com.xiaolongonly.finalschoolexam.utils.SqlStringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 3/26/2016.
 */
public class BaseBitmapActivity extends com.xiaolongonly.finalschoolexam.activity.RealBaseActivity {

    /* 地图页面 */
    private MapView mMapView = null;
    protected BaiduMap myBaiduMap;
    //自定义定位图标
    protected BitmapDescriptor mIconLocation;
    //定位相关
    private LocationClient mLocationClient;
    private MyLocationListener myLocationListener;
    protected Boolean isFirstIn = true;
    public double mLatitude;
    protected double mLongitude;
    /**
     * 方向
     */
    protected MyOrientationListener myOrientationListener;
    private float mCurrentX;
    /**
     * 模式
     */
    protected MyLocationConfiguration.LocationMode mLocationMode;
    //覆盖物相关
    protected BitmapDescriptor mMarker;
    protected BitmapDescriptor userMarker;
    protected RelativeLayout mMarkerLy;
    //这个用来控制是不是在获取当前位置
    protected boolean isGetLocationOn;
    //用来存放task的Latlng
    protected LatLng taskLatLng;
//    private List<TaskModel> taskModels = new ArrayList<TaskModel>();//用来存放当前集合
//    private List<UserModel> userModels = new ArrayList<UserModel>();//用来存放当前集合
//    private Marker preMarker; //之前写的用来记录上一个任务标志位位置

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImageLoaderConfig.setConfig(this);
    }

    protected void initBaiduMap() {
//        setContentView(R.layout.activity_main);
        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.bmapView);
        myBaiduMap = mMapView.getMap();
        setMapListener();//设置地图事件监听
        // 隐藏缩放控件
        int childCount = mMapView.getChildCount();
        View zoom = null;
        for (int i = 0; i < childCount; i++) {
            View child = mMapView.getChildAt(i);
            if (child instanceof ZoomControls) {
                zoom = child;
                break;
            }
        }
        zoom.setVisibility(View.GONE);

        // 隐藏指南针
        UiSettings mUiSettings = myBaiduMap.getUiSettings();
        mUiSettings.setCompassEnabled(false);
        // 删除百度地图logo
        mMapView.removeViewAt(1);
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(18.0f);
        myBaiduMap.setMapStatus(msu);
        //初始化定位
        initLocation();
    }

    private void setMapListener() {
        myBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mMarkerLy.setVisibility(View.GONE);
                myBaiduMap.hideInfoWindow();
                if (isGetLocationOn) {
                    addEmdLocationTip(latLng);
                    isGetLocationOn = false;
                }
            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }
        });
        myBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (isGetLocationOn) {
                    return false;
                }
                Bundle ExtraInfo = marker.getExtraInfo();
                if (ExtraInfo == null) {
                    return false;
                }
                final TaskModel taskModel = (TaskModel) ExtraInfo.getSerializable("taskModel");
                final UserModel userModel = (UserModel) ExtraInfo.getSerializable("userModel");
                if (taskModel != null) {
                    TextView taskContent = (TextView) mMarkerLy.findViewById(R.id.tv_task_content);
                    TextView distance = (TextView) mMarkerLy.findViewById(R.id.id_info_distance);
                    TextView dname = (TextView) mMarkerLy.findViewById(R.id.id_info_name);
                    TextView createTime = (TextView) mMarkerLy.findViewById(R.id.id_info_zan);
                    mMarkerLy.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            goDetail(taskModel.getTask_id());
                        }
                    });
                    taskContent.setText(taskModel.getTask_content());
                    LatLng taskLatLng = new LatLng(Double.valueOf(taskModel.getTask_locationx()), Double.valueOf(taskModel.getTask_locationy()));
                    LatLng myLatLng = getMyLocation();
                    int intDistance = getDistance(taskLatLng, myLatLng);
                    if ((intDistance / 1000) > 0) {
                        float floatDistance = (float) intDistance / 1000;
                        distance.setText("离你" + (floatDistance) + "公里");
                    } else {
                        distance.setText("离你" + (intDistance) + "米");
                    }
                    dname.setText(taskModel.getTask_title());
                    createTime.setText(taskModel.getTask_createtime());
                    InfoWindow infoWindow;
                    TextView tv = new TextView(BaseBitmapActivity.this);
                    findViewById(R.id.get_this_task).setVisibility(View.GONE);
                    findViewById(R.id.task_havebeen_take).setVisibility(View.GONE);
                    tv.setBackgroundResource(R.drawable.location_tips);
                    tv.setPadding(30, 20, 30, 50);
                    tv.setText(taskModel.getTask_title());
                    tv.setTextColor(Color.parseColor("#ffffff"));
                    tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            goDetail(taskModel.getTask_id());
                        }
                    });
                    final LatLng latLng = marker.getPosition();
                    infoWindow = new InfoWindow(tv, latLng, -47);
                    myBaiduMap.showInfoWindow(infoWindow);
                    mMarkerLy.setVisibility(View.VISIBLE);
                } else {
                    DisplayImageOptions imageOptions = SimpleImageOption.create(R.drawable.ic_default_avatar_guider);
                    InfoWindow infoWindow;
                    View view = getLayoutInflater().inflate(R.layout.layout_user, null);
                    RoundedImageView roundedImageView = (RoundedImageView) view.findViewById(R.id.iv_userimg);
                    TextView textView = (TextView) view.findViewById(R.id.tv_username);
                    ImageLoader.getInstance().displayImage(userModel.getUser_imageurl(), roundedImageView, imageOptions);
                    textView.setText(userModel.getUser_name());
                    roundedImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent it = new Intent(BaseBitmapActivity.this, UserInfoActivity.class);
                            it.putExtra("user_id", String.valueOf(userModel.getUser_id()));
                            startActivity(it, false);
                        }
                    });
                    final LatLng latLng = marker.getPosition();
                    infoWindow = new InfoWindow(view, latLng, -47);
                    myBaiduMap.showInfoWindow(infoWindow);
                }
                return true;
            }
        });

    }

    /**
     * 进入任务详情页面
     *
     * @param task_id
     */
    private void goDetail(int task_id) {
        Intent intent = new Intent(BaseBitmapActivity.this, TaskDetailActivity.class);
        intent.putExtra("task_id", task_id);
        startActivity(intent, false);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if(!Constants.isVisitor(getActivity())){
//            initData();
//        }
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
    }


    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //开启定位
        myBaiduMap.setMyLocationEnabled(true);
        if (!mLocationClient.isStarted()) ;
        mLocationClient.start();
        //开启方向传感器
        myOrientationListener.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //停止定位
        myBaiduMap.setMyLocationEnabled(false);
        mLocationClient.stop();
        //停止方向传感器
        myOrientationListener.stop();
    }

    /**
     * 初始化位置，模式地点图标显示等等..
     */
    private void initLocation() {
        /**
         * 默认的模式为普通模式
         */
        mLocationMode = MyLocationConfiguration.LocationMode.NORMAL;
        mLocationClient = new LocationClient(this);
        myLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(myLocationListener);
        LocationClientOption option = new LocationClientOption();
        option.setCoorType("bd09ll");
        option.setIsNeedAddress(true);
        option.setOpenGps(true);
        option.setScanSpan(1000);
        mLocationClient.setLocOption(option);
        //初始化图标
        mIconLocation = BitmapDescriptorFactory.fromResource(R.mipmap.navi_map_gps_locked);
        myOrientationListener = new MyOrientationListener(BaseBitmapActivity.this);
        myOrientationListener.setOnOrientationListener(new MyOrientationListener.OnOrientationListener() {
            @Override
            public void onOrientationChanged(float x) {
                mCurrentX = x;
            }
        });
    }

    /**
     * 我的位置实时监听
     */
    protected class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            MyLocationData data = new MyLocationData.Builder()//
                    .direction(mCurrentX)//
                    .accuracy(bdLocation.getRadius())//
                    .latitude(bdLocation.getLatitude())//
                    .longitude(bdLocation.getLongitude())//
                    .build();
            myBaiduMap.setMyLocationData(data);
            //设置自定义图标
            MyLocationConfiguration config = new MyLocationConfiguration(mLocationMode, true, mIconLocation);
            myBaiduMap.setMyLocationConfigeration(config);
            //更新经纬度
            mLatitude = bdLocation.getLatitude();
            mLongitude = bdLocation.getLongitude();
            if (isFirstIn) {
//                LatLng latLng = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
//                MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
//                myBaiduMap.animateMapStatus(msu);
                centerToMyLocation();
                isFirstIn = false;
            }
        }
    }

    /**
     * 定位到我的位置
     */
    protected void centerToMyLocation() {
        LatLng latLng = new LatLng(mLatitude, mLongitude);
        MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
        myBaiduMap.animateMapStatus(msu);
    }

    /**
     * 添加一片覆盖物
     *
     * @param taskModels
     */
    protected void addOverlays(final List<TaskModel> taskModels, final List<UserModel> userModels) {
        myBaiduMap.clear();
        /**
         * 添加任务的覆盖物
         */
        if (taskModels.size() > 0) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (final TaskModel taskModel : taskModels) {
//                        LatLng taskLatLng = new LatLng(Double.valueOf(taskModel.getTask_locationx()), Double.valueOf(taskModel.getTask_locationy()));
//                        if (getDistance(taskLatLng, getMyLocation()) < 5000) {
                            addTsakOverlay(taskModel);
//                        }
                    }
                }
            }).start();
        }
        if (userModels.size() > 0) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (final UserModel userModel : userModels) {
//                        Log.i(TAG,userModels.toString());
                        if(userModel.getDef_locx()==null||userModel.getDef_locy()==null)
                        {
                            userModel.setDef_locx("0");
                            userModel.setDef_locy("0");
                        }
//                        LatLng userLatlng = new LatLng(Double.valueOf(userModel.getDef_locx()), Double.valueOf(userModel.getDef_locy()));
//                        if (getDistance(userLatlng, getMyLocation()) < 5000) {
//                            addUserOverlay(userModel);
//                        }
                        addUserOverlay(userModel);
                    }
                }
            }).start();
        }
//        this.taskModels = taskModels;
//        this.userModels = userModels;
    }

    /**
     * 根据坐标获取距离
     *
     * @param taskLatLng
     * @param myLocation
     * @return
     */
    private int getDistance(LatLng taskLatLng, LatLng myLocation) {
        int distance = (int) (DistanceUtil.getDistance(myLocation, taskLatLng));
        return distance;
    }

    /**
     * 添加单个任务覆盖物
     *
     * @param taskModel
     */
    private void addTsakOverlay(TaskModel taskModel) {
        //经纬度
        LatLng latLng = new LatLng(Double.valueOf(taskModel.getTask_locationx()), Double.valueOf(taskModel.getTask_locationy()));
        OverlayOptions options;
        options = new MarkerOptions().position(latLng).icon(mMarker).zIndex(5);//设置显示在地图上的图标信息，需要坐标和图片
        Marker marker = (Marker) myBaiduMap.addOverlay(options);//设置显示在地图上的图标
        Bundle mBundle = new Bundle();
        mBundle.putSerializable("taskModel", taskModel);
        marker.setExtraInfo(mBundle);//为marker设置Bundle
        MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
        myBaiduMap.setMapStatus(msu);
    }

    /**
     * 添加单个用户覆盖物
     *
     * @param userModel
     */
    private void addUserOverlay(UserModel userModel) {
        Log.i(TAG,userModel.toString());
        //经纬度
        LatLng latLng = new LatLng(Double.valueOf(userModel.getDef_locx()), Double.valueOf(userModel.getDef_locy()));
        Log.i(TAG, latLng.toString());
        OverlayOptions options = new MarkerOptions().position(latLng).icon(userMarker).zIndex(5);//设置显示在地图上的图标信息，需要坐标和图片
        Marker marker = (Marker) myBaiduMap.addOverlay(options);//设置显示在地图上的图标
        Bundle mBundle = new Bundle();
        mBundle.putSerializable("userModel", userModel);
        marker.setExtraInfo(mBundle);//为marker设置Bundle
//        MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
//        myBaiduMap.setMapStatus(msu);
    }

    /**
     * 这个方法用来获取我的位置坐标信息
     *
     * @return
     */
    protected LatLng getMyLocation() {
        return new LatLng(mLatitude, mLongitude);
    }

    /**
     * 通过坐标反向地理编码
     */
    private void addEmdLocationTip(final LatLng latLng) {
        OverlayOptions options;
        options = new MarkerOptions().position(latLng).icon(mMarker).zIndex(5); //设置显示在地图上的图标信息，需要坐标和图片
        Marker marker = (Marker) myBaiduMap.addOverlay(options);//设置显示在地图上的图标
        MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
        myBaiduMap.setMapStatus(msu);
        Intent intent = new Intent();
        intent.setClass(BaseBitmapActivity.this, TaskPublishActivity.class);
        Bundle bundle  = new Bundle();
        bundle.putParcelable("latlng", latLng);
        intent.putExtra("data", bundle);
        startActivityForResult(intent, 1111);
    }
}
