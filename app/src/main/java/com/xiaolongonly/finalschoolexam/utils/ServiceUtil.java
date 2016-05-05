package com.xiaolongonly.finalschoolexam.utils;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.xiaolongonly.finalschoolexam.service.ChatService;

import java.util.List;

/**
 * Created by Administrator on 5/2/2016.
 */
public class ServiceUtil {
    private static final String TAG="ServiceUtil";
    /**
     * 判断某个服务是否正在运行的方法
     *
     * @param mContext
     * @param serviceName
     *            是包名+服务的类名（例如：net.loonggg.testbackstage.TestService）
     * @return true代表正在运行，false代表服务没有正在运行
     */
    public static boolean isServiceWork(Context mContext, String serviceName) {
        boolean isWork = false;
        ActivityManager myAM = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> myList = myAM.getRunningServices(100);

        if (myList.size() <= 0) {
            return false;
        }
        for (int i = 0; i < myList.size(); i++) {
            String mName = myList.get(i).service.getClassName().toString();
//            Log.i("ServiceList", mName.toString());
            if (mName.equals(serviceName)) {
                isWork = true;
                break;
            }
        }
        return isWork;
    }
    private static ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.i(TAG,"页面绑定服务成功！！");
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.i(TAG,"取消服务绑定！");
        }
    };
    /**
     * 启动服务
     *
     * @param context 上下文
     * @param user_id 用户id
     */
    public static void startService(Context context,Intent serviceIntent, String user_id) {
        serviceIntent.putExtra("userId", user_id);
        context.bindService(serviceIntent, serviceConnection,Context.BIND_AUTO_CREATE);//发送Intent启动Service
        context.startService(serviceIntent);
    }


    /**
     * 关闭服务
     *
     * @param context
     * @param intent
     */
    public static void stopService(Context context, Intent intent) {
        context.stopService(intent);
        context.unbindService(serviceConnection);
    }

}
