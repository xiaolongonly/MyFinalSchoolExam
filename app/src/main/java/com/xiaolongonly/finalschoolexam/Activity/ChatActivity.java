package com.xiaolongonly.finalschoolexam.Activity;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.u1city.module.common.BaseAnalysis;
import com.u1city.module.common.JsonAnalysis;
import com.u1city.module.common.StandardCallback;
import com.u1city.module.util.ToastUtil;
import com.xiaolongonly.finalschoolexam.R;
import com.xiaolongonly.finalschoolexam.adapter.ChatAdapter;
import com.xiaolongonly.finalschoolexam.api.RequestApi;
import com.xiaolongonly.finalschoolexam.model.ChatModel;
import com.xiaolongonly.finalschoolexam.model.TaskModel;
import com.xiaolongonly.finalschoolexam.model.UserModel;
import com.xiaolongonly.finalschoolexam.service.ChatService;
import com.xiaolongonly.finalschoolexam.utils.ConstantUtil;
import com.xiaolongonly.finalschoolexam.utils.MyAnalysis;
import com.xiaolongonly.finalschoolexam.utils.MyStandardCallback;
import com.xiaolongonly.finalschoolexam.utils.ServiceUtil;
import com.xiaolongonly.finalschoolexam.utils.SqlStringUtil;
import com.xiaolongonly.finalschoolexam.websocketclient.client.WebSocketClient;
import com.xiaolongonly.finalschoolexam.websocketclient.drafts.Draft;
import com.xiaolongonly.finalschoolexam.websocketclient.drafts.Draft_10;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 5/1/2016.
 */
public class ChatActivity extends Activity {
    //    private static final String ADDRESS = "ws://120.27.45.196:2347";//地址
    private static final String TAG = "ChatRobotActivity";
    //    private String sendMessage;   //你自己发送的单条信息
    //    private String welcome[];  //放置欢迎信息
    private EditText etMessage;//消息输入框
    private List<ChatModel> chatModels;  //存放所有聊天数据的集合
    private Button sendButton;//发送按钮
    private ListView msgListView;//列表
    private ChatAdapter chatAdapter;//聊天适配器
    //    private WebSocketClient client;//连接客户端
//    private Draft selectDraft; //这个是协议选择
    //    private boolean login=false; //这个用来判别是否登录
//    private String chatCode;//总聊天码
//    private String knowChatMsg;//刚发送的聊天内容
    private SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");//全局的时间格式器
    private String userId = "0";
    private String toUserId = "0";
    private DataReceiver dataReceiver;//广播接收器
    //    private Intent serviceIntent;//服务的Intent
    private TextView chatTitle;//和谁聊天的标题
    private UserModel toUserModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_chat);
        chatModels = new ArrayList<ChatModel>();
//        welcome = getResources().getStringArray(R.array.welcome); //获取我们内置的欢迎信息
//        if(ServiceUtil.isServiceWork(this,"ChatService"))
        initView();
        initData(); // 进入界面，随机显示机器人的欢迎信息
    }

    private void initView() {
        etMessage = (EditText) findViewById(R.id.et_sendmessage);
        sendButton = (Button) findViewById(R.id.btn_send);
        msgListView = (ListView) findViewById(R.id.list_for_msg);
        chatTitle = (TextView) findViewById(R.id.tv_chattitle);
        chatAdapter = new ChatAdapter(this);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendData();//发送数据
            }
        });
        msgListView.setAdapter(chatAdapter);
//        selectDraft = new Draft_10();
    }

    /**
     * 数据需要通过广播发送给服务然后通过服务调用SocketClient来执行发送操作
     */
    private void sendData() {
        Intent myIntent = new Intent();//创建Intent对象
        myIntent.setAction("MyMsg");
//                myIntent.putExtra("cmd", CMD_STOP_SERVICE);
        String chatContent = etMessage.getText().toString();
        String time = sdf.format(new java.util.Date());
        ChatModel chatModel = new ChatModel();
        chatModel.setMessage(chatContent);
        chatModel.setReceive_time(time);
        chatModel.setFromuser_id(userId);
        chatModel.setTouser_id(toUserId);
        myIntent.putExtra("knowmyMsg", getChatCodeId(userId) + getChatCodeId(toUserId) + chatContent + time + ChatService.ENDCODE);

        RequestApi.getInstance(this).execSQL(SqlStringUtil.insertIntoTableChat(chatModel), new MyStandardCallback(this) {
            @Override
            public void onResult(MyAnalysis analysis) throws Exception {
                Log.i(TAG, "服务器保存成功！");
            }

            @Override
            public void onError(int type) {
                Log.i(TAG, "服务器保存失败！");
            }
        });
        showData(chatContent, 1, time,ConstantUtil.getInstance().getUser_name(),ConstantUtil.getInstance().getUser_imageurl());
        sendBroadcast(myIntent);//发送广播
        etMessage.setText("");
    }

    /**
     * 初始化数据
     */
    private void initData() {
        toUserModel = (UserModel) getIntent().getSerializableExtra("userinfo");
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(1001);
//        chatCode = getChatCodeId(ConstantUtil.getInstance().getUser_id()+"") + getChatCodeId(userModel.getUser_id()+"");//id为5的用户给id为6的用户发消息
        userId = ConstantUtil.getInstance().getUser_id() + "";
        toUserId = toUserModel.getUser_id() + "";
        RequestApi.getInstance(this).execSQL(SqlStringUtil.getChatMessageData(toUserId, userId), new MyStandardCallback(this) {
            @Override
            public void onResult(MyAnalysis analysis) throws Exception {
                String jsonList = analysis.getResult();
                JsonAnalysis<ChatModel> jsonAnalysis = new JsonAnalysis<ChatModel>();
                List<ChatModel> webChatModels = jsonAnalysis.listFromJson(jsonList, ChatModel.class);
                for (ChatModel chatModel : webChatModels) {
                    /**
                     * 如果是我发出去的
                     */
                    if (chatModel.getFromuser_id().equals(userId)) {
                        chatModel.setState(1);
                        chatModel.setUserName(ConstantUtil.getInstance().getUser_name());
                        chatModel.setUserImage(ConstantUtil.getInstance().getUser_imageurl());
                    } else {
                        chatModel.setState(2);
                        chatModel.setUserName(toUserModel.getUser_name());
                        chatModel.setUserImage(toUserModel.getUser_imageurl());
                    }
                    chatModels.add(chatModel);
                }
                chatAdapter.addModel(chatModels);
                chatAdapter.notifyDataSetChanged();
                msgListView.setSelection(chatAdapter.getCount() - 1);
            }

            @Override
            public void onError(int type) {
                ToastUtil.showToast(ChatActivity.this, "服务端聊天数据加载失败！");
            }
        });
        chatTitle.setText("和" + toUserModel.getUser_name() + "的对话");
//        startService(ChatActivity.this, userId);
        dataReceiver = new DataReceiver();
        IntentFilter filter = new IntentFilter();//创建IntentFilter对象
        filter.addAction("Chatmsg"); //接收该频道的广播= = ~
        registerReceiver(dataReceiver, filter);//注册Broadcast Receiver
    }

    /**
     *
     * @param msgContent 消息内容
     * @param state      1/2 2为接收到的消息  1为发送的消息
     * @param time       消息发送时间
     * @param userName   用户名
     * @param userImage  用户头像
     */
    private void showData(String msgContent, int state, String time,String userName,String userImage) {
        ChatModel chatModel = new ChatModel();
//        String msgcontent = message.substring(0, message.length() - 8);
//        String time = message.substring(message.length() - 8, message.length());
        chatModel.setMessage(msgContent);
        chatModel.setReceive_time(time);
        chatModel.setState(state); //用于listview的adpter，显示不同的界面
        chatModel.setUserName(userName);
        chatModel.setUserImage(userImage);
        chatModels.add(chatModel);
        chatAdapter.clear();
        chatAdapter.addModel(chatModels);
        chatAdapter.notifyDataSetChanged();
        if (state == 1) {
            msgListView.setSelection(chatAdapter.getCount() - 1);
        }
    }

    /**
     * 注册广播接收器
     */
    private class DataReceiver extends BroadcastReceiver {//继承自BroadcastReceiver的子类

        @Override
        public void onReceive(Context context, Intent intent) {//重写onReceive方法
            String data = intent.getStringExtra("Chatdata");
//            tv.setText(data);
            String msgtime = data.substring(data.length() - 11, data.length() - 3);//获取时间戳
            String msgcontent = data.substring(16, data.length() - 11);//获取内容
            showData(msgcontent, 2, msgtime,toUserModel.getUser_name(),toUserModel.getUser_imageurl());
        }
    }

//    /**
//     * 启动服务
//     *
//     * @param context 上下文
//     * @param user_id 用户id
//     */
//    public void startService(Context context, String user_id) {
//        serviceIntent = new Intent(context, ChatService.class);
//        serviceIntent.putExtra("userId", user_id);
//        context.startService(serviceIntent);//发送Intent启动Service
//    }

//    /**
//     * 关闭服务
//     *
//     * @param context
//     * @param intent
//     */
//    public void stopService(Context context, Intent intent) {
//        context.stopService(intent);
//    }

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

    @Override
    protected void onDestroy() {
        unregisterReceiver(dataReceiver);//取消注册Broadcast Receiver
//        stopService(this, serviceIntent);
        super.onDestroy();
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
//            moveTaskToBack(false);
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }

}
