package com.xiaolongonly.finalschoolexam.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.u1city.module.base.U1CityAdapter;
import com.u1city.module.util.StringUtils;
import com.u1city.module.util.ViewHolder;
import com.xiaolongonly.finalschoolexam.R;
import com.xiaolongonly.finalschoolexam.activity.TaskDetailActivity;
import com.xiaolongonly.finalschoolexam.model.TaskModel;

/**
 * Created by GuoXiaolong on 2016/3/31.
 */
public class MyTaskListAdapter extends U1CityAdapter<TaskModel> {
    public MyTaskListAdapter(Context context)
    {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final TaskModel taskModel = getModels().get(position);
        if (convertView==null)
        {
            convertView =inflater.inflate(R.layout.item_task,null);
        }
        if(taskModel ==null)
        {
            return convertView;
        }
//        //使用ViewHolder
//        TextView textView = ViewHolder.get(convertView, R.id.item_tv_title);
//        StringUtils.setText(textView, todayOnhistoryModel.getDate() + "," + todayOnhistoryModel.getTitle());
        TextView textTitle = ViewHolder.get(convertView, R.id.tv_task_title);
//        TextView textContent = ViewHolder.get(convertView, R.id.tv_task_content);
//        TextView createTime = ViewHolder.get(convertView, R.id.tv_task_createtime);
        TextView taskState = ViewHolder.get(convertView, R.id.tv_task_state);
        Button checkDetail = ViewHolder.get(convertView,R.id.btn_checkdetail);
        checkDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goDetail(taskModel.getTask_id());
            }
        });
        StringUtils.setText(textTitle, taskModel.getTask_title());
        switch (Integer.valueOf(taskModel.getTask_statu()))
        {
            case 0:
                StringUtils.setText(taskState, "等待发布者确认");
                break;
            case 1:
                StringUtils.setText(taskState, "未接取");
                break;
            case 2:
                StringUtils.setText(taskState, "未完成");
                break;
            case 3:
                StringUtils.setText(taskState, "已完成");
                break;
            case 4:
                StringUtils.setText(taskState, "已关闭");
                break;
        }

//        StringUtils.setText(taskState, taskModel.getTask_statu());
//        StringUtils.setText(createTime, taskModel.getTask_createtime());
//        StringUtils.setText(location, taskModel.getTask_location());
        return convertView;
    }
    private void goDetail(int task_id) {
        Intent intent = new Intent(getContext(), TaskDetailActivity.class);
        intent.putExtra("task_id",task_id);
        getContext().startActivity(intent);
    }
}
