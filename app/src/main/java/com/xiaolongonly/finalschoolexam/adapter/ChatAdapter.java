package com.xiaolongonly.finalschoolexam.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.u1city.module.base.U1CityAdapter;
import com.u1city.module.util.SimpleImageOption;
import com.u1city.module.util.StringUtils;
import com.u1city.module.util.ViewHolder;
import com.xiaolongonly.finalschoolexam.R;
import com.xiaolongonly.finalschoolexam.model.ChatModel;
import com.xiaolongonly.finalschoolexam.utils.ConstantUtil;


/**
 * Created by GuoXiaolong on 2016/3/17.
 */
public class ChatAdapter extends U1CityAdapter<ChatModel> {
    private final static String TAG = "ChatAdapter";
    private  DisplayImageOptions imageOptions = SimpleImageOption.create(R.drawable.ic_default_avatar_guider);
    public ChatAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ChatModel chatModel = getModels().get(position);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.chatting_item_msg_text_left, null);
        }
        if (chatModel == null) {
            return convertView;
        }
        if (1 == chatModel.getState()) {
            convertView = inflater.inflate(R.layout.chatting_item_msg_text_right, null);
        } else {
            convertView = inflater.inflate(R.layout.chatting_item_msg_text_left, null);
        }
        TextView name= ViewHolder.get(convertView,R.id.tv_username);
        TextView content = ViewHolder.get(convertView, R.id.tv_chatcontent);
        ImageView headimg = ViewHolder.get(convertView,R.id.iv_userhead);
        ImageLoader.getInstance().displayImage(chatModel.getUserImage(), headimg, imageOptions);
        TextView messageTime = ViewHolder.get(convertView,R.id.tv_createtime);
        StringUtils.setText(messageTime,chatModel.getReceive_time());
        StringUtils.setText(content, chatModel.getMessage());
        StringUtils.setText(name,chatModel.getUserName());
//        headimg.setImageResource();
        return convertView;
    }
}
