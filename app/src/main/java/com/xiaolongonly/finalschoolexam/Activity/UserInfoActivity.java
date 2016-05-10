package com.xiaolongonly.finalschoolexam.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.u1city.module.base.BaseActivity;
import com.u1city.module.common.JsonAnalysis;
import com.u1city.module.util.SimpleImageOption;
import com.u1city.module.util.ToastUtil;
import com.xiaolongonly.finalschoolexam.R;
import com.xiaolongonly.finalschoolexam.api.RequestApi;
import com.xiaolongonly.finalschoolexam.model.UserModel;
import com.xiaolongonly.finalschoolexam.utils.ImageLoaderConfig;
import com.xiaolongonly.finalschoolexam.utils.MyAnalysis;
import com.xiaolongonly.finalschoolexam.utils.MyStandardCallback;
import com.xiaolongonly.finalschoolexam.utils.SqlStringUtil;

import java.util.List;

/**
 * Created by Administrator on 4/5/2016.
 */
public class UserInfoActivity extends BaseActivity implements View.OnClickListener{
    private TextView tv_nickname;//昵称
    private TextView tv_qq; //QQ
    private TextView tv_tel;//手机
    private String userid;//用户id
    private TextView title;//标题信息
    private ImageView useImage;//用户头像
    private DisplayImageOptions imageOptions = SimpleImageOption.create(R.drawable.ic_default_avatar_guider);
    private UserModel userModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState , R.layout.activity_userinfo, R.layout.title_default);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ibt_back:
                finishAnimation();
                break;
            case R.id.user_info_image:
                //弹窗显示大图~~
                ToastUtil.showToast(UserInfoActivity.this,"这里弹窗显示头像大图");
                break;
            case R.id.rl_dochat:
                Intent it = new Intent();
                it.putExtra("userinfo",userModel);
                it.setClass(this,ChatActivity.class);
                startActivity(it,false);
                break;
        }
    }

    @Override
    public void initView() {
        super.initView();
        tv_nickname= (TextView) findViewById(R.id.tv_nickname);
        tv_qq= (TextView) findViewById(R.id.tv_user_qq);
        tv_tel= (TextView) findViewById(R.id.tv_user_tel);
        useImage = (ImageView) findViewById(R.id.user_info_image);
        useImage.setOnClickListener(this);
        initTitle();
        Intent it =getIntent();
        userid = it.getStringExtra("user_id");
    }

    private void initTitle() {
        title = (TextView) findViewById(R.id.tv_title);
        title.setText("用户信息");
        title.setTextSize(20);
        ImageButton backBtn = (ImageButton) findViewById(R.id.ibt_back);
        backBtn.setOnClickListener(this);
        backBtn.setVisibility(View.VISIBLE);
        TextView rightBtn = (TextView) findViewById(R.id.tv_rightBtn);
        rightBtn.setText("保存");
        rightBtn.setVisibility(View.GONE);
    }

    @Override
    public void initData() {
        super.initData();
        ImageLoaderConfig.setConfig(this);// 此句在正常使用中不需要
        RequestApi.getInstance(this).execSQL(SqlStringUtil.getuserInfoByUserid(Integer.valueOf(userid)), new MyStandardCallback(this) {
            @Override
            public void onResult(MyAnalysis analysis) throws Exception {
                String info = analysis.getResult();
                JsonAnalysis<UserModel> jsonAnalysis = new JsonAnalysis<UserModel>();
                List<UserModel> userModels = jsonAnalysis.listFromJson(info, UserModel.class);
                userModel = userModels.get(0);
                tv_nickname.setText(userModel.getUser_name());
                tv_tel.setText(userModel.getUser_tel());
                tv_qq.setText(userModel.getUser_qq());
                title.setText(userModel.getUser_name()+"的个人信息");
                ImageLoader.getInstance().displayImage(userModel.getUser_imageurl(), useImage, imageOptions);
                findViewById(R.id.rl_dochat).setOnClickListener(UserInfoActivity.this);
            }
            @Override
            public void onError(int type) {
            }
        });
    }
}
