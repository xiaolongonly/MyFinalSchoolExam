package com.xiaolongonly.finalschoolexam.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.u1city.module.base.BaseActivity;
import com.u1city.module.common.JsonAnalysis;
import com.xiaolongonly.finalschoolexam.R;
import com.xiaolongonly.finalschoolexam.model.UserModel;
import com.xiaolongonly.finalschoolexam.utils.MyAnalysis;
import com.xiaolongonly.finalschoolexam.utils.MyStandardCallback;
import com.xiaolongonly.finalschoolexam.utils.RequestApi;
import com.xiaolongonly.finalschoolexam.utils.SqlStringUtil;

import java.util.List;

/**
 * Created by Administrator on 4/5/2016.
 */
public class UserInfoActivity extends BaseActivity implements View.OnClickListener{
    private TextView tv_nickname;
    private TextView tv_qq;
    private TextView tv_tel;
    private String userid;
    private TextView title;
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
        }
    }

    @Override
    public void initView() {
        super.initView();
        tv_nickname= (TextView) findViewById(R.id.tv_nickname);
        tv_qq= (TextView) findViewById(R.id.tv_user_qq);
        tv_tel= (TextView) findViewById(R.id.tv_user_tel);
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
        RequestApi.getInstance(this).execSQL(SqlStringUtil.getuserInfoByUserid(Integer.valueOf(userid)), new MyStandardCallback(this) {
            @Override
            public void onResult(MyAnalysis analysis) throws Exception {
                String info = analysis.getResult();
                JsonAnalysis<UserModel> jsonAnalysis = new JsonAnalysis<UserModel>();
                List<UserModel> userModels = jsonAnalysis.listFromJson(info, UserModel.class);
                UserModel userModel = userModels.get(0);
                tv_nickname.setText(userModel.getUser_name());
                tv_tel.setText(userModel.getUser_tel());
                tv_qq.setText(userModel.getUser_qq());
                title.setText(userModel.getUser_name()+"的个人信息");
            }
            @Override
            public void onError(int type) {

            }
        });
    }
}
