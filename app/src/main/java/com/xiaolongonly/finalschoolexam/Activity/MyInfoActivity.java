package com.xiaolongonly.finalschoolexam.activity;


import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import com.u1city.module.base.BaseActivity;
import com.u1city.module.util.StringUtils;
import com.u1city.module.util.ToastUtil;
import com.xiaolongonly.finalschoolexam.R;
import com.xiaolongonly.finalschoolexam.model.UserModel;
import com.xiaolongonly.finalschoolexam.utils.MyAnalysis;
import com.xiaolongonly.finalschoolexam.utils.MyStandardCallback;
import com.xiaolongonly.finalschoolexam.utils.RequestApi;
import com.xiaolongonly.finalschoolexam.utils.SqlStringUtil;
import com.xiaolongonly.finalschoolexam.utils.StringConstantUtils;


public class MyInfoActivity extends BaseActivity implements OnClickListener{
    private static final String TAG = "MyInfoActivity";

    private EditText userNickName;
    private EditText userQQ;
    private EditText userTel;

    //存放用户信息
    private UserModel userModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_myinfo, R.layout.title_default);
    }

    @Override
    public void initView() {
        super.initView();
        userNickName = (EditText) findViewById(R.id.et_user_nickname);
        userQQ = (EditText)findViewById(R.id.et_user_qq);
        userTel = (EditText) findViewById(R.id.et_user_tel);
        initTitle();
    }

    @Override
    public void initData() {
        super.initData();
        userModel = StringConstantUtils.getInstance();
        userNickName.setText(userModel.getUser_name());
        userQQ.setText(userModel.getUser_qq());
        userTel.setText(userModel.getUser_tel());
        stopLoading();
    }

    /**
     * 初始化标题信息
     */
    private void initTitle() {
        TextView title = (TextView) findViewById(R.id.tv_title);
        title.setText("我的资料");
        title.setTextSize(20);
        ImageButton backBtn = (ImageButton) findViewById(R.id.ibt_back);
        backBtn.setOnClickListener(this);
        backBtn.setVisibility(View.VISIBLE);
        TextView rightBtn = (TextView) findViewById(R.id.tv_rightBtn);
        rightBtn.setText("保存");
        rightBtn.setVisibility(View.VISIBLE);
        rightBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.tv_rightBtn:
                saveMyInfo();
                break;
            case R.id.ibt_back:
                finishAnimation();
                break;
        }

    }

    private void saveMyInfo() {
        if(StringUtils.isEmpty(userNickName.getText().toString()))
        {
            ToastUtil.showToast(MyInfoActivity.this,"请输入昵称");
            return;
        }
        if(StringUtils.isEmpty(userQQ.getText().toString()))
        {
            ToastUtil.showToast(MyInfoActivity.this,"QQ");
            return;
        }
        if(StringUtils.isEmpty(userTel.getText().toString()))
        {
            ToastUtil.showToast(MyInfoActivity.this,"请输入手机号码");
            return;
        }
        if(!StringUtils.isMobileNO(userTel.getText().toString()))
        {
            ToastUtil.showToast(MyInfoActivity.this,"手机号码不符规则");
            return;
        }
        if(userQQ.getText().toString().length()<5||userQQ.getText().toString().length()>12)
        {
            ToastUtil.showToast(MyInfoActivity.this,"QQ号码长度不符规则");
            return;
        }
        userModel.setUser_name(userNickName.getText().toString());
        userModel.setUser_qq(userQQ.getText().toString());
        userModel.setUser_tel(userTel.getText().toString());
        RequestApi.getInstance(this).execSQL(SqlStringUtil.modifyUserInfo(userModel.getUser_id(), userModel.getUser_name(), userModel.getUser_tel(),userModel.getUser_qq()), new MyStandardCallback(this) {
            @Override
            public void onResult(MyAnalysis analysis) throws Exception {
                ToastUtil.showToast(MyInfoActivity.this, "修改成功");
                StringConstantUtils.setUserModel(userModel);
                finishAnimation();
            }

            @Override
            public void onError(MyAnalysis baseAnalysis) {
                super.onError(baseAnalysis);
                ToastUtil.showToast(MyInfoActivity.this, "修改失败");
            }

            @Override
            public void onError(int type) {

            }
        });

    }
}

