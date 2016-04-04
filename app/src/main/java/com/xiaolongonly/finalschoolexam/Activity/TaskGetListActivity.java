package com.xiaolongonly.finalschoolexam.activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.u1city.module.base.BaseActivity;
import com.u1city.module.common.JsonAnalysis;
import com.u1city.module.pulltorefresh.DataLoader;
import com.u1city.module.pulltorefresh.PullToRefreshListView;
import com.u1city.module.util.ToastUtil;
import com.xiaolongonly.finalschoolexam.R;
import com.xiaolongonly.finalschoolexam.adapter.MyTaskListAdapter;
import com.xiaolongonly.finalschoolexam.model.TaskModel;
import com.xiaolongonly.finalschoolexam.utils.MyAnalysis;
import com.xiaolongonly.finalschoolexam.utils.MyStandardCallback;
import com.xiaolongonly.finalschoolexam.utils.RequestApi;
import com.xiaolongonly.finalschoolexam.utils.SqlStringUtil;
import com.xiaolongonly.finalschoolexam.utils.StringConstantUtils;

import java.util.List;

public class TaskGetListActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    /**
     * 顶部title
     */
    private TextView title;
    // 券类型
    private int level = 0;
    private int pickerid = StringConstantUtils.getInstance().getUser_id();
    private DataLoader dataLoader;
    private PullToRefreshListView pullToRefreshListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_tasklist, R.layout.title_default);
    }

    @Override
    public void initView() {
        super.initView();
        initTitle();
        pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_to_refresh_adapter_view);
        isShowTaskTypeDialog(false);
    }

    @Override
    public void initData() {
        super.initData();
        dataLoader = new DataLoader(this, pullToRefreshListView);// 必须要有pullToRefreshListView
        dataLoader.setEmptyView(R.layout.empty_view_custom_default);// 如果不切换显示无数据提示的话，可以不设置
        final MyTaskListAdapter adapter = new MyTaskListAdapter(this);
        dataLoader.setAdapter(adapter); // 继承U1cityAdapter的适配器
        pullToRefreshListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(adapter.getCount()==position-1)
                {
                    return;
                }
                TaskModel taskModel = (TaskModel) adapter.getItem(position - 1);
                if (taskModel == null) {
                    return;
                }
                ToastUtil.showToastLong(TaskGetListActivity.this, taskModel.toString());
                goDetail(taskModel.getTask_id());
            }
        });
        dataLoader.setDataSource(new DataLoader.DataSource() {
            @Override
            public void onDataPrepare(boolean b) {
                RequestApi.getInstance(TaskGetListActivity.this).execSQL(SqlStringUtil.pageindex(SqlStringUtil.getTaskListByPickerid(pickerid, level),dataLoader.getIndexPage(),dataLoader.getPageSize()), myStandardCallback);
            }
        });
//        dataLoader.setFooterViewBgColor(android.R.color.black); // 设置footer的背景色，常用于实现背景色的一致
    }

    private void goDetail(int task_id) {
        Intent intent = new Intent(TaskGetListActivity.this,TaskDetailActivity.class);
        intent.putExtra("task_id", task_id);
        startActivity(intent,false);
    }

    private void initTitle() {
        title = (TextView) findViewById(R.id.tv_title);
        switch (level) {
            case 0:
                title.setText("全部");
                break;
//            case 1:
//                title.setText("未接取");
//                break;
            case 2:
                title.setText("已接取");
                break;
            case 3:
                title.setText("已完成");
                break;
            case 4:
                title.setText("已关闭");
                break;
        }
        title.setTextSize(20);
        title.setOnClickListener(this);
        findViewById(R.id.ibt_back).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_title:
                isShowTaskTypeDialog(true);
                inittasksTypePopWin();
                break;
            case R.id.ibt_back:
                finishAnimation();
                break;
        }
    }

    /**
     * 判断是否弹出券类型选择框
     *
     * @param b
     */
    public void isShowTaskTypeDialog(boolean b) {
        if (b) {
            Drawable drawable = getResources().getDrawable(R.drawable.ic_drop_up);
            // / 这一步必须要做,否则不会显示.
            drawable.setBounds(0, 0, 30, 30);
            title.setCompoundDrawables(null, null, drawable, null);
        } else {
            Drawable drawable = getResources().getDrawable(R.drawable.ic_drop_down);
            // / 这一步必须要做,否则不会显示.
//            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            drawable.setBounds(0, 0, 30, 30);
            title.setCompoundDrawables(null, null, drawable, null);
        }
    }

    //    MyStandardCallback mystandardCallback = new MyStandardCallback(this) {
//        @Override
//        public void onResult(MyAnalysis baseAnalysis) throws Exception {
//        }
//
//        @Override
//        public void onError(int i) {
//
//        }
//    };

    /**
     * 任务状态
     */
    private void inittasksTypePopWin() {
        View view = LayoutInflater.from(TaskGetListActivity.this).inflate(R.layout.item_task_list, null);
        final PopupWindow pop = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        pop.setBackgroundDrawable(new BitmapDrawable());
        pop.setOutsideTouchable(true);
        pop.setFocusable(true);
        pop.showAsDropDown(title, 0, 12);

        pop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                isShowTaskTypeDialog(false);
            }
        });
        LinearLayout alltasksLL = (LinearLayout) view.findViewById(R.id.ll_all_tasks);
        LinearLayout untaketasksLL = (LinearLayout) view.findViewById(R.id.ll_untake_tasks);
        untaketasksLL.setVisibility(View.GONE);//不需要未接取
        LinearLayout havetaketasksLL = (LinearLayout) view.findViewById(R.id.ll_havetake_tasks);
        LinearLayout havefinishtasksLL = (LinearLayout) view.findViewById(R.id.ll_havefinish_tasks);
        LinearLayout haveclosetasksLL = (LinearLayout) view.findViewById(R.id.ll_haveclose_tasks);
        final TextView allRb = (TextView) view.findViewById(R.id.tv_all_tasks_type);
        final TextView untakeRb = (TextView) view.findViewById(R.id.tv_untake_tasks_type);
        final TextView havetakeRb = (TextView) view.findViewById(R.id.tv_havetake_tasks_type);
        final TextView havefinishRb = (TextView) view.findViewById(R.id.tv_havefinish_tasks_type);
        final TextView havecloseRb = (TextView) view.findViewById(R.id.tv_haveclose_tasks_type);
        final View allRbiv = view.findViewById(R.id.iv_all_tasks_type);
        final View untakeRbiv = view.findViewById(R.id.iv_untake_tasks_type);
        final View havetakeRbiv = view.findViewById(R.id.iv_havetake_tasks_type);
        final View havefinishRbiv = view.findViewById(R.id.iv_havefinish_tasks_type);
        final View havecloseRbiv = view.findViewById(R.id.iv_haveclose_tasks_type);
        switch (level) {
            case 0:
                allRb.setSelected(true);
                allRbiv.setSelected(true);
                break;
            case 1:
                untakeRb.setSelected(true);
                untakeRbiv.setSelected(true);
                break;
            case 2:
                havetakeRb.setSelected(true);
                havetakeRbiv.setSelected(true);
                break;
            case 3:
                havefinishRb.setSelected(true);
                havefinishRbiv.setSelected(true);
                break;
            case 4:
                havecloseRb.setSelected(true);
                havecloseRbiv.setSelected(true);
                break;
        }
        LinearLayout popwinLayout = (LinearLayout) view.findViewById(R.id.ll_popwin_layout);
        popwinLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pop.dismiss();
            }
        });

        alltasksLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                allRb.setSelected(true);
                untakeRb.setSelected(false);
                havetakeRb.setSelected(false);
                havefinishRb.setSelected(false);
                havecloseRb.setSelected(false);
                title.setText("全部");
                pop.dismiss();
                level = 0;
                dataLoader.setDataSource(new DataLoader.DataSource() {
                    @Override
                    public void onDataPrepare(boolean b) {
                        RequestApi.getInstance(TaskGetListActivity.this).execSQL(SqlStringUtil.pageindex(SqlStringUtil.getTaskListByPickerid(pickerid, level), dataLoader.getIndexPage(), dataLoader.getPageSize()), myStandardCallback);
                    }
                });
                startLoading();
            }
        });
        havetaketasksLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                untakeRb.setSelected(false);
                havetakeRb.setSelected(true);
                havefinishRb.setSelected(false);
                havecloseRb.setSelected(false);
                title.setText("未完成");
                pop.dismiss();
                level = 2;
                dataLoader.setDataSource(new DataLoader.DataSource() {
                    @Override
                    public void onDataPrepare(boolean b) {
                        RequestApi.getInstance(TaskGetListActivity.this).execSQL(SqlStringUtil.pageindex(SqlStringUtil.getTaskListByPickerid(pickerid, level), dataLoader.getIndexPage(), dataLoader.getPageSize()), myStandardCallback);
                    }
                });
                startLoading();
            }
        });
        havefinishtasksLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                untakeRb.setSelected(false);
                havetakeRb.setSelected(false);
                havefinishRb.setSelected(true);
                havecloseRb.setSelected(false);
                title.setText("已完成");
                pop.dismiss();
                level = 3;
                dataLoader.setDataSource(new DataLoader.DataSource() {
                    @Override
                    public void onDataPrepare(boolean b) {
                        RequestApi.getInstance(TaskGetListActivity.this).execSQL(SqlStringUtil.pageindex(SqlStringUtil.getTaskListByPickerid(pickerid, level), dataLoader.getIndexPage(), dataLoader.getPageSize()), myStandardCallback);
                    }
                });
                startLoading();
            }
        });
        haveclosetasksLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                untakeRb.setSelected(false);
                havetakeRb.setSelected(false);
                havefinishRb.setSelected(false);
                havecloseRb.setSelected(true);
                title.setText("已关闭");
                pop.dismiss();
                level = 4;
                dataLoader.setDataSource(new DataLoader.DataSource() {
                    @Override
                    public void onDataPrepare(boolean b) {
                        RequestApi.getInstance(TaskGetListActivity.this).execSQL(SqlStringUtil.pageindex(SqlStringUtil.getTaskListByPickerid(pickerid, level),dataLoader.getIndexPage(), dataLoader.getPageSize()), myStandardCallback);
                    }
                });
                startLoading();
            }
        });

    }

    MyStandardCallback myStandardCallback = new MyStandardCallback(TaskGetListActivity.this) {
        @Override
        public void onResult(MyAnalysis analysis) throws Exception {
            String info = analysis.getResult();
            // 建议使用JsonAnalysis而不是另外写Analysis
            JsonAnalysis<TaskModel> jsonAnalysis = new JsonAnalysis<TaskModel>();
            List<TaskModel> taskModels = jsonAnalysis.listFromJson(info, TaskModel.class);
            Log.i(TAG, info);
            int total=1;
            if(taskModels.size()>0) {
                total = Integer.valueOf(taskModels.get(0).getTotal());
            }
            // 该对象需要implements Serializable，如果对象中包含子对象，则需要包含有子对象数组的属性，详情请点击ActivityNewsModel
            dataLoader.executeOnLoadDataSuccess(taskModels, total, dataLoader.isDrawDown());
        }

        @Override
        public void onError(MyAnalysis analysis) {
            pullToRefreshListView.onRefreshComplete();
        }

        @Override
        public void onError(int type) {
        }
    };
}
