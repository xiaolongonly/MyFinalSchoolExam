package com.xiaolongonly.finalschoolexam.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.u1city.module.common.Debug;
import com.u1city.module.common.JsonAnalysis;
import com.u1city.module.pulltorefresh.DataLoader;
import com.u1city.module.pulltorefresh.PullToRefreshListView;
import com.u1city.module.util.PreferencesUtils;
import com.u1city.module.util.SimpleImageOption;
import com.u1city.module.util.ToastUtil;
import com.u1city.module.widget.LoadingDialog;
import com.xiaolongonly.finalschoolexam.R;
import com.xiaolongonly.finalschoolexam.adapter.MyTaskListAdapter;
import com.xiaolongonly.finalschoolexam.api.RequestApi;
import com.xiaolongonly.finalschoolexam.model.TaskModel;
import com.xiaolongonly.finalschoolexam.model.UserModel;
import com.xiaolongonly.finalschoolexam.utils.ConstantUtil;
import com.xiaolongonly.finalschoolexam.utils.ImageLoaderConfig;
import com.xiaolongonly.finalschoolexam.utils.MyAnalysis;
import com.xiaolongonly.finalschoolexam.utils.MyStandardCallback;
import com.xiaolongonly.finalschoolexam.utils.SqlStringUtil;
import com.xiaolongonly.finalschoolexam.utils.StringConstantUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class MainActivity extends com.xiaolongonly.finalschoolexam.Activity.BaseBitmapActivity {

    /**
     * actionBar的圆角按钮
     */
    private TextView leftTabTv;
    private TextView rightTabTv;

    //列表相关
    private PullToRefreshListView taskListListView;
    private DataLoader taskListDataLoader;
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
    private LinearLayout ll_newTask;
    //先把publisherId默认为1后期有做登录再做关联
    public int publisherId;
    //新任务相关
    private EditText etTitle;
    private EditText etContent;
    private EditText etLocation;
    private LoadingDialog loadingDialog;//加载弹窗
    private List<TaskModel> allTaskModels = new ArrayList<TaskModel>();
    private ImageView iv_head_btn;
    private ImageView ivCenterToMyLoc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());
        super.onCreate(savedInstanceState, R.layout.activity_main, R.layout.title_double_tab_grey);
        //初始化百度地图信息
        initBaiduMap();
        initMarker();//父类的初始化覆盖物类
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        final ImageView iv_my_logo = (ImageView) findViewById(R.id.iv_my_logo);
        final TextView tv_user_name = (TextView) findViewById(R.id.tv_my_username);
        ImageLoaderConfig.setConfig(this);
        final  DisplayImageOptions imageOptions = SimpleImageOption.create(R.drawable.ic_default_avatar_guider);
        if(ConstantUtil.getInstance().getUser_id()==0)
        {
            //说明用户信息已经被清除
            String useraccount = PreferencesUtils.getStringValue(MainActivity.this, "account");
            String userpassword = PreferencesUtils.getStringValue(MainActivity.this, "password");
            RequestApi.getInstance(MainActivity.this).execSQL(SqlStringUtil.getuserInfoByAP(useraccount, userpassword), new MyStandardCallback(this) {
                @Override
                public void onResult(MyAnalysis analysis) throws Exception {
                    String json = analysis.getResult();
                    JsonAnalysis<UserModel> jsonAnalysis = new JsonAnalysis<UserModel>();
                    List<UserModel> userModels = jsonAnalysis.listFromJson(json, UserModel.class);
                    ConstantUtil.setUserModel(userModels.get(0));
                    tv_user_name.setText(ConstantUtil.getInstance().getUser_name());
                    publisherId = ConstantUtil.getInstance().getUser_id();
                    ImageLoader.getInstance().displayImage(ConstantUtil.getInstance().getUser_imageurl(), iv_my_logo, imageOptions);
                    ImageLoader.getInstance().displayImage(ConstantUtil.getInstance().getUser_imageurl(), iv_head_btn, imageOptions);
                }
                @Override
                public void onError(int type) {

                }
            });
        }else {
            tv_user_name.setText(ConstantUtil.getInstance().getUser_name());
            ImageLoader.getInstance().displayImage(ConstantUtil.getInstance().getUser_imageurl(), iv_my_logo, imageOptions);
            ImageLoader.getInstance().displayImage(ConstantUtil.getInstance().getUser_imageurl(), iv_head_btn, imageOptions);
            publisherId = ConstantUtil.getInstance().getUser_id();
        }
    }

    /**
     * 初始化侧拉栏
     */
    private void initDrawerLayout() {
        //发布新任务
        findViewById(R.id.rl_publish_newtask).setOnClickListener(clickListener);
        //我接收的任务
        findViewById(R.id.rl_mytaketask).setOnClickListener(clickListener);
        //我发布的任务
        findViewById(R.id.rl_mypublishtask).setOnClickListener(clickListener);
        //退出登录
        findViewById(R.id.rl_logout).setOnClickListener(clickListener);
        //发布新任务的确定和取消
        findViewById(R.id.tv_cancel).setOnClickListener(clickListener);
        findViewById(R.id.tv_submit).setOnClickListener(clickListener);
        findViewById(R.id.rl_modifypassword).setOnClickListener(clickListener);
        etTitle = (EditText) findViewById(R.id.et_newtask_title);
        etContent = (EditText) findViewById(R.id.et_newtask_content);
        etLocation = (EditText) findViewById(R.id.et_newtask_location);

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
        ll_newTask = (LinearLayout) findViewById(R.id.ll_newtask);
        iv_head_btn= (ImageView) findViewById(R.id.iv_head_btn);
        ivCenterToMyLoc = (ImageView) findViewById(R.id.iv_center_tomyloc);
        ivCenterToMyLoc.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                centerToMyLocation();
            }
        });
        iv_head_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mDrawerLayout.isDrawerOpen(llytMeDrawer)) {
                    mDrawerLayout.closeDrawer(llytMeDrawer);
                } else {
                    mDrawerLayout.openDrawer(llytMeDrawer);
                }
                mMarkerLy.setVisibility(View.GONE);
                ll_newTask.setVisibility(View.GONE);
            }
        });
        findViewById(R.id.my_info_detail).setOnClickListener(clickListener);
        toggleTab(StringConstantUtils.Show_By_BaiduMap);
        initDrawerLayout();
        getAllTaskInfo();
    }
    //获取全部的任务信息
    public void getAllTaskInfo()
    {
        RequestApi.getInstance(this).execSQL(SqlStringUtil.getTaskList(TaskModel.STATU_HAVETAKE), new MyStandardCallback(this) {
            @Override
            public void onResult(MyAnalysis analysis) throws Exception {
                String jsonList = analysis.getResult();
                JsonAnalysis<TaskModel> jsonAnalysis = new JsonAnalysis<TaskModel>();
                allTaskModels = jsonAnalysis.listFromJson(jsonList, TaskModel.class);
                addOverlays(allTaskModels);
            }
            @Override
            public void onError(int type) {

            }
        });
    }
    /**
     * 初始化覆盖物
     */
    private void initMarker() {
        mMarker = BitmapDescriptorFactory.fromResource(R.mipmap.maker);
        mMarkerLy = (RelativeLayout) findViewById(R.id.id_marker_layout);
    }

    /**
     * 无数据提示
     *
     * @author zhengjb
     */
    private void initNoneDataView() {
        TextView textNoneData = (TextView) findViewById(R.id.empty_view_tv);
        textNoneData.setText("你身边暂时没人使用哦，赶紧去邀请小伙伴来玩吧！");
    }

    /**
     * 初始化第二个页面的DataLoader
     */
    private void initDataLoader() {
        listInfo = (LinearLayout) findViewById(R.id.activity_tasklist);
        taskListListView = (PullToRefreshListView) findViewById(R.id.activity_tasklist_ptr_lv);
        taskListDataLoader = new DataLoader(this, taskListListView);
        taskListDataLoader.setEmptyView(R.layout.empty_view_custom_default);
        final MyTaskListAdapter taskListadapter = new MyTaskListAdapter(MainActivity.this);
        taskListDataLoader.setAdapter(taskListadapter);
        taskListDataLoader.setDataSource(new DataLoader.DataSource() {
            @Override
            public void onDataPrepare(boolean isDrawDown) {
                RequestApi.getInstance(MainActivity.this).execSQL(SqlStringUtil.pageindex(SqlStringUtil.getTaskList(TaskModel.STATU_HAVETAKE), taskListDataLoader.getIndexPage(), taskListDataLoader.getPageSize()), taskListStandardCallback);
            }
        });
        taskListDataLoader.setFooter(new View(MainActivity.this));
        taskListListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (taskListadapter.getCount() == position - 1) {
                    return;
                }
                TaskModel taskModel = (TaskModel) taskListadapter.getItem(position - 1);
                if (taskModel == null) {
                    return;
                }
                goDetail(taskModel.getTask_id());
//                ToastUtil.showToastLong(MainActivity.this, taskModel.toString());
            }
        });
    }
    private void goDetail(int task_id) {
        Intent intent = new Intent(MainActivity.this, TaskDetailActivity.class);
        intent.putExtra("task_id", task_id);
//        ToastUtil.showToast(this,task_id+"");
        startActivity(intent, false);
    }

    private MyStandardCallback taskListStandardCallback = new MyStandardCallback(MainActivity.this) {
        @Override
        public void onResult(MyAnalysis analysis) throws Exception {
            String jsonList = analysis.getResult();
            JsonAnalysis<TaskModel> jsonAnalysis = new JsonAnalysis<TaskModel>();
            List<TaskModel> taskModels = jsonAnalysis.listFromJson(jsonList, TaskModel.class);
            int total = 1;
            if (taskModels.size() > 0) {
                total = Integer.valueOf(taskModels.get(0).getTotal());
                Collections.sort(taskModels);
            }
            // 该对象需要implements Serializable，如果对象中包含子对象，则需要包含有子对象数组的属性，详情请点击ActivityNewsModel
            Log.e(TAG, taskModels.toString());
            taskListDataLoader.executeOnLoadDataSuccess(taskModels, total, taskListDataLoader.isDrawDown());
        }
        @Override
        public void onError(MyAnalysis analysis) {
            taskListListView.onRefreshComplete();
        }

        @Override
        public void onError(int type) {
        }
    };
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
            Intent it;
            switch (v.getId()) {
                case R.id.left_tab_tv:
                    Debug.i(TAG, "left_tab_tv is click");
                    toggleTab(StringConstantUtils.Show_By_BaiduMap);
                    break;

                case R.id.right_tab_tv:
                    Debug.i(TAG, "right_tab_tv is click");
                    toggleTab(StringConstantUtils.Show_By_List);
                    break;
                case R.id.rl_publish_newtask:
                    //这边需要显示一个正在发布的dialog而且旁边不能被点击在取消或者finish之前isGetLocationOn这个参数都要设置为true
                    ll_newTask.setVisibility(View.VISIBLE);//将其置为可见
                    toggleTab(StringConstantUtils.Show_By_BaiduMap);
                    isGetLocationOn = true;//可以点击地图获取位置信息
                    closeDrawer();
                    break;
                case R.id.rl_mytaketask:
                    //我接取的任务
                    it = new Intent(MainActivity.this, TaskGetListActivity.class);
                    startActivity(it,false);
                    closeDrawer();
                    break;
                case R.id.rl_mypublishtask:
                    //我发布的任务
                    it = new Intent(MainActivity.this,TaskListActivity.class);
                    startActivity(it,false);
                    closeDrawer();
                    break;
                case R.id.tv_cancel:
                    ll_newTask.setVisibility(View.GONE);//将其置为不可见
                    isGetLocationOn = false;//可以点击地图获取位置信息
                    getAllTaskInfo();
                    break;
                case R.id.tv_submit:
                    publishNewTask();
                    break;
                case R.id.my_info_detail:
                    //到个人中心页面
                    it = new Intent(MainActivity.this, MyInfoActivity.class);
                    startActivity(it,false);
                    break;
                case R.id.rl_modifypassword:
                    it = new Intent(MainActivity.this, ModifyPswActivity.class);
                    startActivity(it,false);
                    break;
                case R.id.rl_logout:
                    it = new Intent(MainActivity.this, LoginActivity.class);
                    ConstantUtil.setUserModel(new UserModel());//清空缓存数据
                    it.putExtra("logout", "logout");
                    startActivity(it, true);
                    break;
                default:
                    break;
            }
        }
    };

    private void publishNewTask() {
        if (etTitle.getText().toString().trim().equals("")) {
            ToastUtil.showToast(MainActivity.this, "标题不能为空！");
            return;
        }
        if (etContent.getText().toString().trim().equals("")) {
            ToastUtil.showToast(MainActivity.this, "写点任务内容呗！");
            return;
        }
        if (etLocation.getText().toString().trim().equals("")) {
            ToastUtil.showToast(MainActivity.this, "描述一下地点啊，方便人家找！");
            return;
        }
        TaskModel taskModel = new TaskModel();
        taskModel.setPublisher_id(publisherId);
        taskModel.setTask_title(etTitle.getText().toString());//标题
        taskModel.setTask_content(etContent.getText().toString());//内容
        taskModel.setTask_location(etLocation.getText().toString());//地点
        Log.i(TAG, taskModel.getTask_location());
        if(taskLatLng==null)
        {
            ToastUtil.showToast(MainActivity.this,"选个地址吧！");
            return;
        }
        taskModel.setTask_locationx(String.valueOf(taskLatLng.latitude));//设置纬度
        taskModel.setTask_locationy(String.valueOf(taskLatLng.longitude));//设置经度
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar nowTime = Calendar.getInstance();
        String startTime = sdf.format(nowTime.getTime());
//        nowTime.add(Calendar.HOUR, 5);
//        String endTime = sdf.format(nowTime.getTime());
//        System.out.println(sdf.format(nowTime.getTime()));
        taskModel.setTask_createtime(startTime);//起止时间
//        taskModel.setTask_endtime(endTime);
        RequestApi.getInstance(MainActivity.this).execSQL(SqlStringUtil.insertIntoTableTask(taskModel), myStandardCallback);
        loadingDialog = new LoadingDialog(MainActivity.this);
        loadingDialog.setLoadingText("正在发布...");
        loadingDialog.show();
    }
    private MyStandardCallback myStandardCallback = new MyStandardCallback(this) {
        @Override
        public void onResult(MyAnalysis analysis) throws Exception {
            ll_newTask.setVisibility(View.GONE);//将其置为不可见
            //清空已输入的数据
            etTitle.setText("");
            etContent.setText("");
            etLocation.setText("");
            ToastUtil.showToast(MainActivity.this, "发布成功！！");
            isGetLocationOn = false;//可以点击地图获取位置信息
            loadingDialog.dismiss();
            getAllTaskInfo();
        }

        @Override
        public void onError(MyAnalysis baseAnalysis) {
            ToastUtil.showToast(MainActivity.this, "发布失败！！");
            loadingDialog.dismiss();
        }

        @Override
        public void onError(int type) {

        }
    };

    /**
     * 点击标签，选择地图显示还是ListView列表显示
     */
    private void toggleTab(int type) {
        if (type == StringConstantUtils.Show_By_BaiduMap) {
            listInfo.setVisibility(View.GONE);
            rl_baidumap.setVisibility(View.VISIBLE);
            ivCenterToMyLoc.setVisibility(View.VISIBLE);
            leftTabTv.setBackgroundResource(R.drawable.brand_mytrace_title_left_select);
            leftTabTv.setTextColor(0xffffffff);
            rightTabTv.setBackgroundResource(R.drawable.brand_mytrace_title_right);
            rightTabTv.setTextColor(0xff499BF7);
        } else {
            listInfo.setVisibility(View.VISIBLE);
            rl_baidumap.setVisibility(View.GONE);
            ivCenterToMyLoc.setVisibility(View.GONE);
            leftTabTv.setBackgroundResource(R.drawable.brand_mytrace_title_left);
            leftTabTv.setTextColor(0xff499BF7);
            rightTabTv.setBackgroundResource(R.drawable.brand_mytrace_title_right_select);
            rightTabTv.setTextColor(0xffffffff);
        }
    }


    public void closeDrawer() {

        if (mDrawerLayout != null) {
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
    /**
     * 菜单
     *
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
//            case R.id.id_map_mylocation:
//                centerToMyLocation();
//                break;
            case R.id.id_map_mode_common:
                mLocationMode = MyLocationConfiguration.LocationMode.NORMAL;
                break;
            case R.id.id_map_mode_follow:
                mLocationMode = MyLocationConfiguration.LocationMode.FOLLOWING;
                break;
            case R.id.id_map_mode_compass:
                mLocationMode = MyLocationConfiguration.LocationMode.COMPASS;
                break;
//            case R.id.id_add_overlay:
//                addOverlays(allTaskModels);
//                break;
            default:
                break;
        }
        toggleTab(StringConstantUtils.Show_By_BaiduMap);
        return super.onOptionsItemSelected(item);
    }
}
