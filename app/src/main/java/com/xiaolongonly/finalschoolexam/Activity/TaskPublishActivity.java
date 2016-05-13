package com.xiaolongonly.finalschoolexam.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.u1city.module.base.BaseActivity;
import com.u1city.module.common.BaseAnalysis;
import com.u1city.module.common.StandardCallback;
import com.u1city.module.util.ToastUtil;
import com.u1city.module.widget.LoadingDialog;
import com.xiaolongonly.finalschoolexam.R;
import com.xiaolongonly.finalschoolexam.api.RequestApi;
import com.xiaolongonly.finalschoolexam.model.TaskModel;
import com.xiaolongonly.finalschoolexam.utils.ConstantUtil;
import com.xiaolongonly.finalschoolexam.utils.MyAnalysis;
import com.xiaolongonly.finalschoolexam.utils.MyStandardCallback;
import com.xiaolongonly.finalschoolexam.utils.SqlStringUtil;
import com.xiaolongonly.finalschoolexam.utils.StringConstantUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Administrator on 5/10/2016.
 */
public class TaskPublishActivity extends BaseActivity implements View.OnClickListener{
    private LoadingDialog loadingDialog;//加载弹窗
    private EditText etTitle;
    private EditText etContent;
    private EditText etLocation;
    private TaskModel taskModel = new TaskModel();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_taskpublish, R.layout.title_default);
    }

    @Override
    public void initView() {
        super.initView();
        etTitle = (EditText) findViewById(R.id.et_task_title);
        etContent = (EditText) findViewById(R.id.et_task_content);
        etLocation = (EditText) findViewById(R.id.et_task_location);
        initTitle();
    }

    private void initTitle() {
        TextView title = (TextView) findViewById(R.id.tv_title);
        title.setText("发布任务");
        findViewById(R.id.ibt_back).setVisibility(View.VISIBLE);//返回按钮可见
        findViewById(R.id.ibt_back).setOnClickListener(this);
        TextView rightBtn= (TextView) findViewById(R.id.tv_rightBtn);
        rightBtn.setVisibility(View.VISIBLE);
        rightBtn.setText("发布");
        rightBtn.setOnClickListener(this);
    }

    @Override
    public void initData() {
        super.initData();
        Bundle bundle = getIntent().getBundleExtra("data");
        LatLng latLng = bundle.getParcelable("latlng");
        taskModel.setTask_locationx(latLng.latitude + "");
        taskModel.setTask_locationy(latLng.longitude + "");
        setAddressInfoByLatlng(latLng);
        stopLoading();
    }

    private void setAddressInfoByLatlng(final LatLng latLng) {
//        final String addressInfo;
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
                    Toast.makeText(TaskPublishActivity.this, result.error.toString(), Toast.LENGTH_LONG).show();
                }
                //获取反向地理编码结果
                if (!result.getAddress().trim().equals("")) {
//                    Toast.makeText(BaseBitmapActivity.this, result.getAddress(), Toast.LENGTH_LONG).show();
//                    EditText et_location = (EditText) findViewById(R.id.et_newtask_location);
//                    et_location.setText(result.getAddress());
                    etLocation.setText(result.getAddress());
                }
            }
        };
        mSearch.setOnGetGeoCodeResultListener(listener);
        mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(latLng));
    }
    /**
     * 发布任务
     */
    private void publishNewTask() {
        if (etTitle.getText().toString().trim().equals("")) {
            ToastUtil.showToast(this, "标题不能为空！");
            return;
        }
        if (etContent.getText().toString().trim().equals("")) {
            ToastUtil.showToast(this, "写点任务内容呗！");
            return;
        }
        if (etLocation.getText().toString().trim().equals("")) {
            ToastUtil.showToast(this, "描述一下地点啊，方便人家找！");
            return;
        }
        taskModel.setPublisher_id(ConstantUtil.getInstance().getUser_id());
        taskModel.setTask_title(etTitle.getText().toString());//标题
        taskModel.setTask_content(etContent.getText().toString());//内容
        taskModel.setTask_location(etLocation.getText().toString());//地点
        Log.i(TAG, taskModel.getTask_location());
        if (taskModel.getTask_locationx().equals("")) {
            ToastUtil.showToast(this, "地址选择有误！！请重新发布");
            return;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar nowTime = Calendar.getInstance();
        String startTime = sdf.format(nowTime.getTime());
        taskModel.setTask_createtime(startTime);//发布时间
        loadingDialog = new LoadingDialog(this);
        loadingDialog.setLoadingText("正在发布...");
        loadingDialog.show();
        RequestApi.getInstance(this).execSQL(SqlStringUtil.insertIntoTableTask(taskModel), new MyStandardCallback(this) {
//            @Override
//            public void onResult(BaseAnalysis analysis) throws Exception {
////                etTitle.setText("");
////                etContent.setText("");
////                etLocation.setText("");
//                ToastUtil.showToast(TaskPublishActivity.this, "发布成功！！");
////            isGetLocationOn = false;//可以点击地图获取位置信息
//                loadingDialog.dismiss();
//                backToPreActivity(true);
//            }

            @Override
            public void onResult(MyAnalysis analysis) throws Exception {
                ToastUtil.showToast(TaskPublishActivity.this, "发布成功！！");
//            isGetLocationOn = false;//可以点击地图获取位置信息
                loadingDialog.dismiss();
                backToPreActivity(true);
            }

            @Override
            public void onError(int type) {
                ToastUtil.showToast(TaskPublishActivity.this, "发布失败！！");
                loadingDialog.dismiss();
                backToPreActivity(false);
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.ibt_back:
                showBackDialogTip();
                break;
            case R.id.tv_rightBtn:
                publishNewTask();
                break;
        }
    }
    private void backToPreActivity(boolean issuccess)
    {
        if(issuccess)
        {
            Intent data = new Intent(this,MainActivity.class);
            data.putExtra("key", "success");
            setResult(111, data);
        }else
        {
            Intent data = new Intent(this,MainActivity.class);
            data.putExtra("key","faild");
            setResult(000, data);
        }
        finishAnimation();
    }
    /**
     * 菜单返回按键点击事件
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            showBackDialogTip();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    private void showBackDialogTip()
    {
        final AlertDialog alertDialog = new AlertDialog.Builder(TaskPublishActivity.this).create();
        alertDialog.show();
        alertDialog.getWindow().setContentView(R.layout.layout_dialog);// 加载自定义布局

        TextView title = (TextView) alertDialog.getWindow().findViewById(R.id.tv_title);
        title.setText("任务未发布 \n 确认取消？");
        TextView tvCancle= (TextView) alertDialog.getWindow().findViewById(R.id.btn_cancel);
        tvCancle.setText("取消");
        TextView tvConfirm= (TextView) alertDialog.getWindow().findViewById(R.id.btn_ok);
        tvConfirm.setText("确定");
        alertDialog.getWindow().findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {// 取消按键监听

            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.getWindow().findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {// 确定按键监听

            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
//                startActivity(intent,true);
                backToPreActivity(false);
            }
        });
    }
}
