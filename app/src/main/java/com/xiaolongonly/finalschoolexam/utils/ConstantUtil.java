package com.xiaolongonly.finalschoolexam.utils;

import android.util.Log;

import com.xiaolongonly.finalschoolexam.model.UserModel;

/**
 * Created by GuoXiaolong on 2016/4/7.
 */
public class ConstantUtil {
    private static final String TAG="ConstantUtil";
    public static final String UploadHost = "http://120.27.45.196/xiaolong/index.php/sql/upload_file" ;
    public static UserModel userModel = null;

    public static void setUserModel(UserModel userModel) {
        ConstantUtil.userModel = userModel;
    }

    public static UserModel getInstance() {
        if (userModel == null) {
            userModel = new UserModel();
        }
        Log.i(TAG,userModel.toString());

        return userModel;
    }
}
