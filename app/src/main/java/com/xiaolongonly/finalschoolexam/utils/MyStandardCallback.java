package com.xiaolongonly.finalschoolexam.utils;

import android.app.Activity;
import android.support.v4.app.Fragment;

import com.android.volley.VolleyError;
import com.u1city.module.common.Debug;
import com.u1city.module.common.HttpCallBack;
import com.u1city.module.common.StandardCallback;
import com.u1city.module.util.ToastUtil;

import org.json.JSONObject;

/**
 * Created by GuoXiaolong on 2016/3/16.
 */

/**
 * 标准回调
 * 封装了标准的请求回调处理
 * 可以直接使用此类来节省开发时间。
 *
 * @author zhengjb
 */
@SuppressWarnings("deprecation")
public abstract class MyStandardCallback extends HttpCallBack {
    private String TAG = StandardCallback.class.getName();

    /**
     * 结果错误，即服务器返回问题
     */
    public static final int TYPE_RESULT_ERROR = 1;
    /**
     * 网络错误
     */
    public static final int TYPE_VOLLEY_ERROR = 2;

    /**
     * 出错提示
     */
    private String errorToast = ToastUtil.TOAST_ERROR;

    /**
     * 是否使用服务端返回错误信息
     */
    private boolean enableMsgToast = false;

    /**
     * 是否使用错误提示
     */
    private boolean enableToastError = true;

    /**
     * @return 是否使用错误提示
     */
    public boolean isEnableToastError() {
        return enableToastError;
    }

    /**
     * @param enableToastError 是否使用错误提示
     */
    public void setEnableToastError(boolean enableToastError) {
        this.enableToastError = enableToastError;
    }

    /**
     * @return 是否使用服务端返回错误信息
     */
    public boolean isEnableMsgToast() {
        return enableMsgToast;
    }

    /**
     * @return 错误提示内容
     */
    public String getErrorToast() {
        return errorToast;
    }

    /**
     * @param enableMsgToast 是否使用服务端返回错误信息
     */
    public void setEnableMsgToast(boolean enableMsgToast) {
        this.enableMsgToast = enableMsgToast;
    }

    /**
     * 设置出错提示，否则使用默认提示
     */
    public void setErrorToast(String errorToast) {
        this.errorToast = errorToast;
    }

    /**
     * * 标准回调
     * 封装了标准的请求回调处理
     * 可以直接使用此类来节省开发时间。
     *
     * @param context Activity实例
     */
    public MyStandardCallback(Activity context) {
        super(context);
    }

    /**
     * * 标准回调
     * 封装了标准的请求回调处理
     * 可以直接使用此类来节省开发时间。
     *
     * @param fragment Fragment实例
     */
    public MyStandardCallback(Fragment fragment) {
        super(fragment);
    }

    /**
     * * 标准回调
     * 封装了标准的请求回调处理
     * 可以直接使用此类来节省开发时间。
     *
     * @param context        Activity实例
     * @param enableMsgToast 是否使用服务端返回错误信息
     */
    public MyStandardCallback(Activity context, boolean enableMsgToast) {
        super(context);
        this.enableMsgToast = enableMsgToast;
    }

    /**
     * * 标准回调
     * 封装了标准的请求回调处理
     * 可以直接使用此类来节省开发时间。
     *
     * @param context          Activity实例
     * @param enableMsgToast   是否使用服务端返回错误信息
     * @param enableToastError 是否使用错误提示
     */
    public MyStandardCallback(Activity context, boolean enableMsgToast, boolean enableToastError) {
        super(context);
        this.enableMsgToast = enableMsgToast;
        this.enableToastError = enableToastError;
    }

    /**
     * * 标准回调
     * 封装了标准的请求回调处理
     * 可以直接使用此类来节省开发时间。
     *
     * @param fragment       Fragment实例
     * @param enableMsgToast 是否使用服务端返回错误信息
     */
    public MyStandardCallback(Fragment fragment, boolean enableMsgToast) {
        super(fragment);
        this.enableMsgToast = enableMsgToast;
    }

    /**
     * * 标准回调
     * 封装了标准的请求回调处理
     * 可以直接使用此类来节省开发时间。
     *
     * @param fragment         Fragment实例
     * @param enableMsgToast   是否使用服务端返回错误信息
     * @param enableToastError 是否使用错误提示
     */
    public MyStandardCallback(Fragment fragment, boolean enableMsgToast, boolean enableToastError) {
        super(fragment);
        this.enableMsgToast = enableMsgToast;
        this.enableToastError = enableToastError;
    }

    @Override
    public final void onSuccess(JSONObject response) {
        Debug.i(TAG, "response:" + response);
        MyAnalysis analysis = new MyAnalysis(response);
        if (analysis.success()) {
            try {
                onResult(analysis);
            } catch (Exception e) {
                e.printStackTrace();
                onError(TYPE_RESULT_ERROR);
            }
        } else {
            if (enableToastError) {
                if (enableMsgToast) {
                    ToastUtil.showToastLong(context, analysis.msg());
                } else {
                    toastError();
                }
            }

            onError(TYPE_RESULT_ERROR);
            onError(analysis);
        }
    }

    /**
     * 当返回成功时的结果
     *
     * @param analysis 用接口数据生成的基础分析类对象
     */
    public abstract void onResult(MyAnalysis analysis) throws Exception;

    /**
     * 出现错误的处理
     *
     * @param type 错误类型，{@link #TYPE_RESULT_ERROR},{@link #TYPE_VOLLEY_ERROR}
     */
    public abstract void onError(int type);

    public void onError(MyAnalysis baseAnalysis) {
    }

    ;

    /**
     * 返回结果失败
     *
     * @param error 错误内容
     */
    @Override
    public final void onFailure(VolleyError error) {

        if (enableToastError) {
            toastError();
        }

        Debug.e(TAG, "response error:" + error.getMessage());
        onError(TYPE_VOLLEY_ERROR);
    }

    /**
     * 弹出错误提示
     */
    private void toastError() {
        if (context != null) {
            ToastUtil.showToastLong(context, errorToast);
        } else if (fragment != null && fragment.getActivity() != null) {
            ToastUtil.showToastLong(fragment.getActivity(), errorToast);
        }
    }
}

