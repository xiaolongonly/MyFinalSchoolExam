package com.xiaolongonly.finalschoolexam.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.u1city.module.base.U1CityAdapter;
import com.u1city.module.util.StringUtils;
import com.u1city.module.util.ViewHolder;
import com.xiaolongonly.finalschoolexam.R;
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
        TextView textContent = ViewHolder.get(convertView, R.id.tv_task_content);
        TextView createTime = ViewHolder.get(convertView, R.id.tv_task_createtime);
        TextView location = ViewHolder.get(convertView, R.id.tv_task_location);
        StringUtils.setText(textTitle, taskModel.getTask_title());
        StringUtils.setText(textContent, taskModel.getTask_content());
        StringUtils.setText(createTime, taskModel.getTask_createtime());
        StringUtils.setText(location, taskModel.getTask_location());
        return convertView;
    }
}
