package com.xiaolongonly.finalschoolexam.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.u1city.module.base.BaseActivity;
import com.u1city.module.common.JsonAnalysis;
import com.u1city.module.util.ToastUtil;
import com.u1city.module.widget.LoadingDialog;
import com.xiaolongonly.finalschoolexam.R;
import com.xiaolongonly.finalschoolexam.model.TaskModel;
import com.xiaolongonly.finalschoolexam.model.UserModel;
import com.xiaolongonly.finalschoolexam.utils.ConstantUtil;
import com.xiaolongonly.finalschoolexam.utils.MyAnalysis;
import com.xiaolongonly.finalschoolexam.utils.MyStandardCallback;
import com.xiaolongonly.finalschoolexam.api.RequestApi;
import com.xiaolongonly.finalschoolexam.utils.SqlStringUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Administrator on 4/4/2016.
 */
public class TaskDetailActivity extends BaseActivity implements View.OnClickListener{
    private TextView tv_task_title;
    private TextView tv_task_content;
    private TextView tv_task_createtime;
    private TextView tv_task_endtime;
    private TextView tv_endtime_tip;
    private TextView tv_task_statu;
    private TextView tv_task_location;
    private TextView tv_getthistask;
    private int task_id;
    private LoadingDialog loadingDialog;
    private TextView tv_canceltask;
    //接取者创建者信息相关
    private TextView tv_task_getterName;
    private TextView tv_task_creatorName;
    private RelativeLayout rl_task_getterInfo;
    private RelativeLayout rl_task_creatorInfo;
    private RelativeLayout rl_task_endtime;
    private TaskModel taskModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_taskdetail, R.layout.title_default);
    }

    @Override
    public void initView() {
        super.initView();
        Intent intent = getIntent();
        task_id=intent.getIntExtra("task_id",1);
        tv_task_title = (TextView) findViewById(R.id.tv_task_title);
        tv_task_content = (TextView) findViewById(R.id.tv_task_content);
        tv_task_createtime = (TextView) findViewById(R.id.tv_task_createtime);
        tv_task_endtime = (TextView) findViewById(R.id.tv_task_endtime);
        tv_endtime_tip = (TextView) findViewById(R.id.tv_endtime_tip);
        tv_task_statu = (TextView) findViewById(R.id.tv_task_statu);
        tv_task_location = (TextView) findViewById(R.id.tv_task_location);
        tv_getthistask = (TextView) findViewById(R.id.tv_gettask);
        tv_canceltask = (TextView) findViewById(R.id.tv_canceltask);
        tv_task_getterName = (TextView) findViewById(R.id.tv_task_getter);
        tv_task_creatorName = (TextView) findViewById(R.id.tv_task_creator);
        rl_task_creatorInfo = (RelativeLayout) findViewById(R.id.rl_task_creator);
        rl_task_getterInfo = (RelativeLayout) findViewById(R.id.rl_task_getter);
        rl_task_endtime= (RelativeLayout) findViewById(R.id.rl_task_endtime);
        rl_task_creatorInfo.setOnClickListener(this);
        rl_task_getterInfo.setOnClickListener(this);
        loadingDialog= new LoadingDialog(this);
        initTitle();
    }
    private void initTitle() {
        TextView title = (TextView) findViewById(R.id.tv_title);
        title.setText("任务详情");
        title.setTextSize(20);
        ImageButton backBtn = (ImageButton) findViewById(R.id.ibt_back);
        backBtn.setOnClickListener(this);
        backBtn.setVisibility(View.VISIBLE);
        TextView rightBtn = (TextView) findViewById(R.id.tv_rightBtn);
        rightBtn.setText("保存");
        rightBtn.setVisibility(View.GONE);
    }

    @Override
    public void initData() {
        super.initData();
        RequestApi.getInstance(this).execSQL(SqlStringUtil.getTaskInfoByTaskid(task_id), new MyStandardCallback(this) {
            @Override
            public void onResult(MyAnalysis analysis) throws Exception {
                String info = analysis.getResult();
                // 建议使用JsonAnalysis而不是另外写Analysis
                JsonAnalysis<TaskModel> jsonAnalysis = new JsonAnalysis<TaskModel>();
                List<TaskModel> taskModels = jsonAnalysis.listFromJson(info, TaskModel.class);
                taskModel = taskModels.get(0);
                //设置接取者信息
                if (!String.valueOf(taskModel.getPicker_id()).equals("0")) {
                    rl_task_getterInfo.setVisibility(View.VISIBLE);
                    RequestApi.getInstance(TaskDetailActivity.this).execSQL(SqlStringUtil.getuserInfoByUserid(taskModel.getPicker_id()), new MyStandardCallback(TaskDetailActivity.this) {
                        @Override
                        public void onResult(MyAnalysis analysis) throws Exception {
                            String info = analysis.getResult();
                            JsonAnalysis<UserModel> jsonAnalysis = new JsonAnalysis<UserModel>();
                            List<UserModel> userModels = jsonAnalysis.listFromJson(info, UserModel.class);
                            UserModel userModel = userModels.get(0);
                            tv_task_getterName.setText(userModel.getUser_name());
                        }

                        @Override
                        public void onError(int type) {

                        }
                    });
                } else {
                    rl_task_getterInfo.setVisibility(View.GONE);
                }
                //设置发布者者信息
                RequestApi.getInstance(TaskDetailActivity.this).execSQL(SqlStringUtil.getuserInfoByUserid(taskModel.getPublisher_id()), new MyStandardCallback(TaskDetailActivity.this) {
                    @Override
                    public void onResult(MyAnalysis analysis) throws Exception {
                        String info = analysis.getResult();
                        JsonAnalysis<UserModel> jsonAnalysis = new JsonAnalysis<UserModel>();
                        List<UserModel> userModels = jsonAnalysis.listFromJson(info, UserModel.class);
                        UserModel userModel = userModels.get(0);
                        tv_task_creatorName.setText(userModel.getUser_name());
                    }

                    @Override
                    public void onError(int type) {

                    }
                });

                //设置其他数据
                tv_task_title.setText(taskModel.getTask_title());
                tv_task_content.setText(taskModel.getTask_content());
                tv_task_createtime.setText(taskModel.getTask_createtime());
                tv_task_location.setText(taskModel.getTask_location());
                tv_task_endtime.setText(taskModel.getTask_finishtime());

                //设置按钮显示情况
                switch (Integer.valueOf(taskModel.getTask_statu())) {
                    case TaskModel.STATU_UNTAKE://未接取
                        tv_task_statu.setText("未接取");
                        tv_task_statu.setTextColor(Color.RED);
                        tv_getthistask.setVisibility(View.VISIBLE);
                        rl_task_endtime.setVisibility(View.GONE);
                        if (ConstantUtil.getInstance().getUser_id() == taskModel.getPublisher_id()) {
                            //当前用户的任务
                            tv_getthistask.setText("关闭任务");
                            tv_getthistask.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    loadingDialog.show();
                                    RequestApi.getInstance(TaskDetailActivity.this).execSQL(SqlStringUtil.modifyTaskStatu(task_id, TaskModel.STATU_HAVECLOSE, getNowTime()), new MyStandardCallback(TaskDetailActivity.this) {
                                        @Override
                                        public void onResult(MyAnalysis analysis) throws Exception {
                                            ToastUtil.showToast(TaskDetailActivity.this, "关闭成功");
                                            initData();
                                            tv_task_statu.setTextColor(Color.YELLOW);
                                            tv_getthistask.setVisibility(View.GONE);
                                            loadingDialog.dismiss();
                                        }

                                        @Override
                                        public void onError(int type) {
                                            ToastUtil.showToast(TaskDetailActivity.this, "关闭失败");
                                            loadingDialog.dismiss();
                                        }
                                    });
                                }
                            });

                        } else {
                            ;
                            tv_getthistask.setText("接取这个任务");
                            tv_canceltask.setText("");
                            tv_canceltask.setVisibility(View.INVISIBLE);
                            tv_getthistask.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    loadingDialog.show();
                                    RequestApi.getInstance(TaskDetailActivity.this).execSQL(SqlStringUtil.modifySP(task_id, TaskModel.STATU_HAVETAKE, ConstantUtil.getInstance().getUser_id()), new MyStandardCallback(TaskDetailActivity.this) {
                                        @Override
                                        public void onResult(MyAnalysis analysis) throws Exception {
                                            ToastUtil.showToast(TaskDetailActivity.this, "接取成功！！");
                                            initData();
                                            tv_task_statu.setTextColor(Color.GREEN);
                                            tv_getthistask.setVisibility(View.GONE);
                                            loadingDialog.dismiss();
                                        }

                                        @Override
                                        public void onError(int type) {
                                            ToastUtil.showToast(TaskDetailActivity.this, "接取成功！！");
                                        }
                                    });
                                }
                            });
                        }

                        break;
                    case TaskModel.STATU_HAVETAKE://已接取
                        //当前用户的任务
                        tv_task_statu.setText("已接取");
                        rl_task_endtime.setVisibility(View.GONE);
                        if (ConstantUtil.getInstance().getUser_id() == taskModel.getPublisher_id())//当前用户发布的任务
                        {
                            tv_getthistask.setVisibility(View.VISIBLE);
                            tv_getthistask.setText("关闭任务");
                            tv_getthistask.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    loadingDialog.show();
                                    RequestApi.getInstance(TaskDetailActivity.this).execSQL(SqlStringUtil.modifyTaskStatu(task_id, TaskModel.STATU_HAVECLOSE, getNowTime()), new MyStandardCallback(TaskDetailActivity.this) {
                                        @Override
                                        public void onResult(MyAnalysis analysis) throws Exception {
                                            ToastUtil.showToast(TaskDetailActivity.this, "关闭成功");
                                            initData();
                                            loadingDialog.dismiss();
                                        }

                                        @Override
                                        public void onError(int type) {
                                            ToastUtil.showToast(TaskDetailActivity.this, "关闭失败");
                                            loadingDialog.dismiss();
                                        }
                                    });
                                }
                            });

                        } else {
                            if (ConstantUtil.getInstance().getUser_id() == taskModel.getPicker_id())//当前用户接取的任务
                            {
                                tv_getthistask.setVisibility(View.VISIBLE);//完成任务可见
                                tv_canceltask.setVisibility(View.VISIBLE);//放弃任务可见
                                tv_getthistask.setText("完成任务");
                                tv_canceltask.setText("放弃任务");
                                tv_getthistask.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        RequestApi.getInstance(TaskDetailActivity.this).execSQL(SqlStringUtil.modifyTaskStatu(task_id, TaskModel.STATU_HAVEFINISH, getNowTime()), new MyStandardCallback(TaskDetailActivity.this) {
                                            @Override
                                            public void onResult(MyAnalysis analysis) throws Exception {
                                                ToastUtil.showToast(TaskDetailActivity.this, "成功完成任务");
                                                initData();
//                                                tv_canceltask.setText("");
//                                                tv_canceltask.setVisibility(View.GONE);//不可见
//                                                tv_getthistask.setVisibility(View.GONE);
                                                loadingDialog.dismiss();
                                            }

                                            @Override
                                            public void onError(int type) {
                                                ToastUtil.showToast(TaskDetailActivity.this, "完成任务失败");
                                                loadingDialog.dismiss();
                                            }
                                        });
                                    }
                                });
                                tv_canceltask.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        RequestApi.getInstance(TaskDetailActivity.this).execSQL(SqlStringUtil.modifySP(task_id, TaskModel.STATU_UNTAKE, 0), new MyStandardCallback(TaskDetailActivity.this) {
                                            @Override
                                            public void onResult(MyAnalysis analysis) throws Exception {
                                                ToastUtil.showToast(TaskDetailActivity.this, "取消成功");
                                                initData();
//                                                tv_canceltask.setText("");
//                                                tv_canceltask.setVisibility(View.INVISIBLE);//不可见
//                                                tv_getthistask.setText("接取任务");
                                                loadingDialog.dismiss();
                                            }

                                            @Override
                                            public void onError(int type) {
                                                ToastUtil.showToast(TaskDetailActivity.this, "取消失败");
                                                loadingDialog.dismiss();
                                            }
                                        });
                                    }
                                });
                            } else {
                                tv_task_statu.setText("已接取");
                                tv_getthistask.setVisibility(View.GONE);
                                tv_canceltask.setVisibility(View.INVISIBLE);
                            }
                        }
                        break;
                    case TaskModel.STATU_HAVEFINISH://已完成
                        tv_task_statu.setText("已完成");
                        tv_endtime_tip.setText("完成时间：");
                        rl_task_endtime.setVisibility(View.VISIBLE);
                        tv_getthistask.setVisibility(View.GONE);
                        tv_canceltask.setVisibility(View.GONE);

                        break;
                    case TaskModel.STATU_HAVECLOSE://已关闭
                        tv_task_statu.setText("已关闭");
                        tv_endtime_tip.setText("关闭时间：");
                        rl_task_endtime.setVisibility(View.VISIBLE);
                        tv_getthistask.setVisibility(View.GONE);
                        tv_canceltask.setVisibility(View.GONE);
                        break;
                }

            }

            @Override
            public void onError(int type) {

            }
        });
    }
    private String getNowTime()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar nowTime = Calendar.getInstance();
        String finishTime = sdf.format(nowTime.getTime());
        return finishTime;
    }
    @Override
    public void onClick(View view) {
        Intent it;
        switch (view.getId())
        {
            case R.id.ibt_back:
                finishAnimation();
                break;
            case R.id.tv_gettask:
                break;
            case R.id.rl_task_getter:
                //携带getterid的intent
                it =new Intent(TaskDetailActivity.this, UserInfoActivity.class);
                it.putExtra("user_id",String.valueOf(taskModel.getPicker_id()));
                startActivity(it,false);
                break;
            case R.id.rl_task_creator:
                //携带creatorid的intent
                it =new Intent(TaskDetailActivity.this, UserInfoActivity.class);
                it.putExtra("user_id",String.valueOf(taskModel.getPublisher_id()));
                startActivity(it,false);
                break;
        }
    }
}
