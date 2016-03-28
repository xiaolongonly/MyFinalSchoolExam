package com.xiaolongonly.finalschoolexam.Activity;

import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ZoomControls;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.u1city.module.base.BaseActivity;
import com.u1city.module.util.DimensUtil;
import com.xiaolongonly.finalschoolexam.Info;
import com.xiaolongonly.finalschoolexam.MyOrientationListener;
import com.xiaolongonly.finalschoolexam.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 3/26/2016.
 */
public class BaseBitmapActivity extends RealBaseActivity {

    /* 地图页面 */
    private MapView mMapView = null;
    protected BaiduMap myBaiduMap;
    //自定义定位图标
    protected BitmapDescriptor mIconLocation;
    //定位相关
    private LocationClient mLocationClient;
    private MyLocationListener myLocationListener;
    private Boolean isFirstIn = true;
    private double mLatitude;
    private double mLongitude;
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
    protected RelativeLayout mMarkerLy;
    private List<Overlay> overlayList = new ArrayList<Overlay>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void initBaiduMap() {
//        setContentView(R.layout.activity_main);
        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.bmapView);
        myBaiduMap = mMapView.getMap();
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
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(15.0f);
        myBaiduMap.setMapStatus(msu);
        //初始化定位
        initLocation();

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
     * 添加覆盖物
     *
     * @param infos
     */
    protected void addOverlays(List<Info> infos) {
        myBaiduMap.clear();
        LatLng latLng = null;
        Marker marker = null;
        OverlayOptions options;
        for (Info info : infos) {
            //经纬度
            latLng = new LatLng(info.getLatitude(), info.getLongitude());
            //图标
            options = new MarkerOptions().position(latLng).icon(mMarker).zIndex(5);//设置显示在地图上的图标信息，需要坐标和图片
            marker = (Marker) myBaiduMap.addOverlay(options);//设置显示在地图上的图标
            Bundle mBundle = new Bundle();
            mBundle.putSerializable("info", info);
            marker.setExtraInfo(mBundle);//为marker设置Bundle
        }
        MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
        myBaiduMap.setMapStatus(msu);

    }

    /**
     * 这个方法用来获取我的位置坐标信息
     *
     * @return
     */
    protected LatLng getMyLocation() {
        return new LatLng(mLatitude, mLongitude);
    }

    //重写onTouchEcent来获取到地点
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Point point = new Point((int) event.getX(), (int) event.getY() - DimensUtil.dpToPixels(BaseBitmapActivity.this,44f));
        LatLng latLng = myBaiduMap.getProjection().fromScreenLocation(point);
        addEmdLocationTip(latLng);
        return super.onTouchEvent(event);
    }

    private void addEmdLocationTip(LatLng latLng) {
        //        myBaiduMap.clear();
        addOverlays(Info.infos);
        OverlayOptions options;
        //图标
        options = new MarkerOptions().position(latLng).icon(mMarker).zIndex(5); //设置显示在地图上的图标信息，需要坐标和图片
        Marker marker = (Marker) myBaiduMap.addOverlay(options);//设置显示在地图上的图标
        GeoCoder mSearch = GeoCoder.newInstance();
        OnGetGeoCoderResultListener listener = new OnGetGeoCoderResultListener() {
            public void onGetGeoCodeResult(GeoCodeResult result) {
                if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                    //没有检索到结果
                }
                //获取地理编码结果
            }

            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
                if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                    //没有找到检索结果
                    Toast.makeText(BaseBitmapActivity.this, result.error.toString(), Toast.LENGTH_LONG).show();
                }
                //获取反向地理编码结果
                if (!result.getAddress().trim().equals("")) {
                    Toast.makeText(BaseBitmapActivity.this, result.getAddress(), Toast.LENGTH_LONG).show();
                }
            }
        };
        mSearch.setOnGetGeoCodeResultListener(listener);
        mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(latLng));
        MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
        myBaiduMap.setMapStatus(msu);
    }

}
