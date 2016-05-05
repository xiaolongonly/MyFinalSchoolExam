package com.xiaolongonly.finalschoolexam.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.xiaolongonly.finalschoolexam.websocketclient.client.WebSocketClient;
import com.xiaolongonly.finalschoolexam.websocketclient.drafts.Draft;
import com.xiaolongonly.finalschoolexam.websocketclient.drafts.Draft_10;
import com.xiaolongonly.finalschoolexam.websocketclient.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by Administrator on 5/1/2016.
 */
public class ChatService extends Service{
    private static final String TAG = "ChatService";
    private CommandReceiver cmdReceiver;
    private WebSocketClient client;//连接客户端
    private Draft selectDraft; //这个是协议选择
    public static final String ADDRESS = "ws://120.27.45.196:2347";//地址
    public static final String ENDCODE = "end";//聊天信息尾码
    //    private String knowChatMsg;//当前聊天内容
    private String user_id;

    @Override
    public void onCreate() {//重写onCreate方法
        cmdReceiver = new CommandReceiver();//注册广播接收类
        selectDraft = new Draft_10(); //协议
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {//重写onBind方法
        return mBind;
    }
    private MyBind mBind = new MyBind();
    public class MyBind extends Binder {
        public ChatService getMyService() {
            return ChatService.this;
        }
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {//重写onStartCommand方法
        IntentFilter filter = new IntentFilter();//创建IntentFilter对象
        filter.addAction("MyMsg");
        registerReceiver(cmdReceiver, filter);//注册Broadcast Receiver
        user_id = intent.getStringExtra("userId");
        try {
            doJob();//调用方法启动线程
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    //方法：
    public void doJob() throws URISyntaxException {
        client = new WebSocketClient(new URI(ADDRESS), selectDraft) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                new Thread() {
                    @Override
                    public void run() {
                        Log.i(TAG, "已经连接到服务器" + getURI() + client);
                        client.send(getChatCodeId(user_id) + "00000000");//发送初始聊天码
                    }
                }.start();
            }
            @Override
            public void onMessage(final String message) {
                new Thread() {
                    @Override
                    public void run() {
                        Log.i(TAG, "获取到服务器消息：" + message);
                        String msgend = message.substring(message.length() - 3, message.length());//获取尾码
                        Intent data = new Intent();
                        data.setAction("Chatmsg");
                        //如果不是该用户发送的的结尾又是尾码结尾且长度大于27说明数据校验正确
                        //校验规则改为第8-16位为当前用户的聊天code
                        if (message.length() > 19 && message.substring(8, 16).equals(getChatCodeId(user_id)) && msgend.equals(ENDCODE)) {
//                            String msgtime = message.substring(message.length() - 11, message.length() - 3);//获取时间戳
//                            String msgcontent = message.substring(16, message.length() - 11);//获取内容
//                            data.putExtra("Chatdata", msgcontent + msgtime);
                            data.putExtra("Chatdata", message);
                            sendBroadcast(data);//发送这条消息的广播
                            Log.i(TAG, message);
                        } else if (message.length() == 16) {
//                            data.putExtra("Chatdata", "连接到聊天服务器成功");
                            Log.i(TAG, "连接到聊天服务器成功！！");
                        } else {
//                            data.putExtra("Chatdata", "消息内容：" + message + "消息长度：" + message.length());
                            Log.i(TAG, "消息内容：" + message + "消息长度：" + message.length());
                        }
                    }
                }.start();
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                Log.i(TAG, "服务器连接断开，错误码" + code + "原因：" + reason);
            }

            @Override
            public void onError(Exception ex) {
                Log.i(TAG,ex.toString());
            }
        };
        client.connect();
    }

    private class CommandReceiver extends BroadcastReceiver {//继承自BroadcastReceiver的子类

        @Override
        public void onReceive(Context context, Intent intent) {//重写onReceive方法
            String knowMsg = intent.getStringExtra("knowmyMsg");
//            stopSelf();//停止服务
            client.send(knowMsg);//发送信息
        }
    }

    /**
     * 在销毁当前服务的时候取消注册广播
     */
    @Override
    public void onDestroy() {
        this.unregisterReceiver(cmdReceiver);
        client.close();
        super.onDestroy();
    }

    /**
     * @param id 输入：用户id
     * @return chatCode 返回：用户聊天码
     */
    private String getChatCodeId(String id) {
        String chatCode;
        String str = "10000000";
        chatCode = str.substring(0, 8 - id.length()) + id;
        return chatCode;
    }

}
