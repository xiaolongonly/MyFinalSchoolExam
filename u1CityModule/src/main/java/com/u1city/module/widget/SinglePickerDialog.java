/*
 * 系统: U1CityModule
 * 文件名: SimplePickerDialog.java
 * 版权: U1CITY Corporation 2015
 * 描述: 
 * 创建人: zhengjb
 * 创建时间: 2015-10-22 下午3:19:20
 */
package com.u1city.module.widget;

import android.app.Activity;
import android.app.Dialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.u1city.module.R;
import com.u1city.module.util.ListUtils;

import java.util.List;

/**
 * 单选弹窗
 * 
 * @author zhengjb
 */
public class SinglePickerDialog extends Dialog implements OnClickListener
{
    private TextView titleTv;
    private Button confirmBtn;
    private PickerView pickerView;
    
    private OnSinglePickedListener singlePickedListener;
    
    private List<String> datas;

    public void setDatas(List<String> datas)
    {
        this.datas = datas;
        if(!ListUtils.isEmpty(datas)){
            pickerView.setData(datas);
        }
    }

    /**
     * 单选弹窗
     * 
     * @param context
     */
    public SinglePickerDialog(Activity context)
    {
        super(context, R.style.Achievement_Dialog);
        
        View contentView = LayoutInflater.from(context).inflate(R.layout.dialog_single_picker, null);
        titleTv = (TextView) contentView.findViewById(R.id.dialog_single_picker_title_tv);
        confirmBtn =  (Button) contentView.findViewById(R.id.dialog_single_picker_confirm_btn);
        pickerView =  (PickerView) contentView.findViewById(R.id.dialog_single_picker_view);
        confirmBtn.setOnClickListener(this);
        setContentView(contentView);
        
        Window window = getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    }

    public TextView getTitleTv()
    {
        return titleTv;
    }

    public Button getConfirmBtn()
    {
        return confirmBtn;
    }

    public PickerView getPickerView()
    {
        return pickerView;
    }
    
    public void setOnSinglePickedListener(OnSinglePickedListener singlePickedListener)
    {
        this.singlePickedListener = singlePickedListener;
    }

    @Override
    public void onClick(View v)
    {
        int id = v.getId();
        
        if(id == R.id.dialog_single_picker_confirm_btn){
            dismiss();
            
            if(singlePickedListener != null && !ListUtils.isEmpty(datas)){
                singlePickedListener.onPicked(datas.get(pickerView.getCurrentSelected()));
            }
        }
    }
}
