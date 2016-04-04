package com.xiaolongonly.finalschoolexam.utils;


import com.xiaolongonly.finalschoolexam.model.UserModel;

/**
 * Created by GuoXiaolong on 2016/3/14.
 */
public class StringConstantUtils {
    public static int Show_By_BaiduMap = 0;
    public static int Show_By_List=1;
    public static UserModel userModel = null;

    public static void setUserModel(UserModel userModel) {
        StringConstantUtils.userModel = userModel;
    }

    public static UserModel getInstance() {
        if (userModel == null) {
            userModel = new UserModel();
        }
        return userModel;
    }

}
