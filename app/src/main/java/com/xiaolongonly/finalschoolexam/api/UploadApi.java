package com.xiaolongonly.finalschoolexam.api;

import android.graphics.Bitmap;

import com.android.volley.Request;
import com.xiaolongonly.finalschoolexam.listener.ResponseListener;
import com.xiaolongonly.finalschoolexam.utils.ConstantUtil;
import com.xiaolongonly.finalschoolexam.utils.FormImage;
import com.xiaolongonly.finalschoolexam.utils.PostUploadRequest;
import com.xiaolongonly.finalschoolexam.utils.VolleyUtil;


public class UploadApi {

    /**
     * 上传图片接口
     * @param bitmap 需要上传的图片
     * @param listener 请求回调
     */
    public static void uploadImg(Bitmap bitmap, ResponseListener listener){
//        List<FormImage> imageList = new ArrayList<FormImage>() ;
        FormImage formImage =new FormImage(bitmap);
        Request request = new PostUploadRequest(ConstantUtil.UploadHost, formImage,listener) ;
        VolleyUtil.getRequestQueue().add(request) ;
    }
}