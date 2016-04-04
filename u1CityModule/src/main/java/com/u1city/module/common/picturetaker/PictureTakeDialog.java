/*
 * 系统: LaiDianYi
 * 文件名: PictureTakeDialog.java
 * 版权: U1CITY Corporation 2015
 * 描述:
 * 创建人: zhengjb
 * 创建时间: 2015-9-9 下午8:15:33
 */
package com.u1city.module.common.picturetaker;

import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.u1city.module.R;
import com.u1city.module.widget.BaseDialog;

/**
 * 拍照或从相册选择图片的弹窗
 * 
 * @author zhengjb
 */
public class PictureTakeDialog extends BaseDialog
{
    private View customView;

    private Button cancelBtn;

    private Button cameraBtn;
    private Button galleryBtn;

    private PictureTaker pictureTaker;

    /**
     * 拍照或从相册选择图片的弹窗
     * 
     * @param context
     * @param width 弹窗宽度为px或MATCH_PARENT、WRAP_CONTENT
     * @param pictureTaker 图片采集者
     * @author zhengjb
     */
    public PictureTakeDialog(Activity context, int width, PictureTaker pictureTaker)
    {
        super(context, R.layout.dialog_modify_photo);
        setCancelable(true);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        customView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_modify_photo, null);
        setContentView(customView);

        cameraBtn = (Button) customView.findViewById(R.id.dialog_modify_photo_camera_btn);
        cameraBtn.setOnClickListener(this);

        galleryBtn = (Button) customView.findViewById(R.id.dialog_modify_photo_gallery_btn);
        galleryBtn.setOnClickListener(this);

        cancelBtn = (Button) customView.findViewById(R.id.dialog_modify_photo_cancel_btn);
        cancelBtn.setOnClickListener(this);

        getWindow().setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = width;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(params);

        this.pictureTaker = pictureTaker;
    }

    /**
     * 拍照或从相册选择图片的弹窗
     * 默认为MATCH_PARENT
     * 
     * @param context
     * @param pictureTaker 图片采集者
     * @author zhengjb
     */
    public PictureTakeDialog(Activity context, PictureTaker pictureTaker)
    {

        this(context, WindowManager.LayoutParams.MATCH_PARENT, pictureTaker);
    }

    public View getCustomView()
    {
        return customView;
    }

    public TextView getGalleryButton()
    {
        return galleryBtn;
    }

    public TextView getCameraButton()
    {
        return cameraBtn;
    }

    public TextView getCancelButton()
    {
        return cancelBtn;
    }

    @Override
    public void onClick(View v)
    {
        int id = v.getId();
        if (id == R.id.dialog_modify_photo_camera_btn)// 拍照
        {
            pictureTaker.takePhoto();
            dismiss();
        }
        else if (id == R.id.dialog_modify_photo_cancel_btn)
        {// 取消修改头像
            dismiss();
        }
        else if (id == R.id.dialog_modify_photo_gallery_btn)
        {// 从相册获取
            pictureTaker.takeGallery();
            dismiss();
        }
    }
}
