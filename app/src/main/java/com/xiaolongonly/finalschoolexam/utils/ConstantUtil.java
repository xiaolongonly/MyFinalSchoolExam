package com.xiaolongonly.finalschoolexam.utils;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.u1city.module.common.JsonAnalysis;
import com.u1city.module.util.PreferencesUtils;
import com.xiaolongonly.finalschoolexam.api.RequestApi;
import com.xiaolongonly.finalschoolexam.model.UserModel;

import java.util.List;

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
    public static void getUserInfo(Context context)
    {
        //说明用户信息已经被清除
        String useraccount = PreferencesUtils.getStringValue(context, "account");
        String userpassword = PreferencesUtils.getStringValue(context, "password");
        RequestApi.getInstance(context).execSQL(SqlStringUtil.getuserInfoByAP(useraccount, userpassword), new MyStandardCallback((Activity) context) {
            @Override
            public void onResult(MyAnalysis analysis) throws Exception {
                String json = analysis.getResult();
                JsonAnalysis<UserModel> jsonAnalysis = new JsonAnalysis<UserModel>();
                List<UserModel> userModels = jsonAnalysis.listFromJson(json, UserModel.class);
                ConstantUtil.setUserModel(userModels.get(0));
            }
            @Override
            public void onError(int type) {
            }
        });
    }


}
