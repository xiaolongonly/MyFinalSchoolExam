package com.xiaolongonly.finalschoolexam.Activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.u1city.module.base.BaseActivity;
import com.u1city.module.util.PreferencesUtils;
import com.u1city.module.util.ToastUtil;
import com.xiaolongonly.finalschoolexam.R;
import com.xiaolongonly.finalschoolexam.model.UserModel;
import com.xiaolongonly.finalschoolexam.utils.MyAnalysis;
import com.xiaolongonly.finalschoolexam.utils.MyStandardCallback;
import com.xiaolongonly.finalschoolexam.api.RequestApi;
import com.xiaolongonly.finalschoolexam.utils.SqlStringUtil;

/**
 * Created by Xiaolong on 4/4/2016.
 */
public class RegisterActivity extends BaseActivity implements View.OnClickListener{
    private final static String TAG="LoginActivity";
    private EditText account;
    private EditText password;
    private Button login;
    private TextView newUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_login, R.layout.title_default);
    }
    @Override
    public void initView() {
        super.initView();
        account= (EditText) findViewById(R.id.et_account);
        password = (EditText) findViewById(R.id.et_password);
        login= (Button) findViewById(R.id.btn_login);
        login.setText("注册");
        newUser= (TextView) findViewById(R.id.tv_newuser);
        newUser.setVisibility(View.GONE);
        login.setOnClickListener(this);
        initTitle();
    }

    private void register() {
        if(account.getText().equals(""))
        {
            ToastUtil.showToast(RegisterActivity.this,"请输入帐号！");
            return;
        }
        if(password.getText().equals(""))
        {
            ToastUtil.showToast(RegisterActivity.this,"请输入密码！");
            return;
        }
        RequestApi.getInstance(this).execSQL(SqlStringUtil.isAccountRegister(account.getText().toString()), new MyStandardCallback(this) {
            @Override
            public void onResult(MyAnalysis analysis) throws Exception {

                String json = analysis.getResult();
                if (json.equals("[]")) {
                    final UserModel userModel = new UserModel();
                    userModel.setUser_account(account.getText().toString());
                    userModel.setUser_password(password.getText().toString());
                    RequestApi.getInstance(RegisterActivity.this).execSQL(SqlStringUtil.insertIntoTableUser(userModel), new MyStandardCallback(RegisterActivity.this) {
                        @Override
                        public void onResult(MyAnalysis analysis) throws Exception {
//                String json = analysis.getResult();
                            ToastUtil.showToast(RegisterActivity.this, "注册成功");
                            PreferencesUtils.putStringPreferences(RegisterActivity.this, "account", userModel.getUser_account());
                            PreferencesUtils.putStringPreferences(RegisterActivity.this, "password", userModel.getUser_password());
                            finish();
                            finishAnimation();
                        }

                        @Override
                        public void onError(MyAnalysis baseAnalysis) {
                            super.onError(baseAnalysis);
                            ToastUtil.showToast(RegisterActivity.this, baseAnalysis.getStatus());
                        }

                        @Override
                        public void onError(int type) {

                        }
                    });
                } else {
                        showPhoneAlreadyRegister();
                }

            }

            @Override
            public void onError(int type) {

            }
        });


    }

    private void initTitle() {
        TextView title = (TextView) findViewById(R.id.tv_title);
        title.setText("新用户注册");
        findViewById(R.id.ibt_back).setVisibility(View.VISIBLE);//返回按钮可见
        findViewById(R.id.ibt_back).setOnClickListener(this);
    }


    private void showPhoneAlreadyRegister()
    {
        final AlertDialog alertDialog = new AlertDialog.Builder(RegisterActivity.this).create();
        alertDialog.show();
        alertDialog.getWindow().setContentView(R.layout.layout_dialog);// 加载自定义布局

        TextView title = (TextView) alertDialog.getWindow().findViewById(R.id.tv_title);
        SpannableString ss = new SpannableString("帐号：" + account.getText() + "已经是会员账号，您可以直接登录！");
        ss.setSpan(new ForegroundColorSpan(Color.parseColor("#FFA72D")), 3, ss.length()-16, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        title.setText(ss);
        alertDialog.getWindow().findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {// 取消按键监听

            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.getWindow().findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {// 确定按键监听

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent,true);
            }
        });
    }

    @Override
    public void initData() {
        super.initData();
        stopLoading();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.btn_login:
                register();
                break;
            case R.id.ibt_back:
                finishAnimation();
                break;
        }

    }
}
