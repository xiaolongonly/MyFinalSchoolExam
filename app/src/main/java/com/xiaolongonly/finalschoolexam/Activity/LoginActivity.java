package com.xiaolongonly.finalschoolexam.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.u1city.module.base.BaseActivity;
import com.u1city.module.common.JsonAnalysis;
import com.u1city.module.util.PreferencesUtils;
import com.u1city.module.util.ToastUtil;
import com.u1city.module.widget.LoadingDialog;
import com.xiaolongonly.finalschoolexam.R;
import com.xiaolongonly.finalschoolexam.model.UserModel;
import com.xiaolongonly.finalschoolexam.utils.ConstantUtil;
import com.xiaolongonly.finalschoolexam.utils.MyAnalysis;
import com.xiaolongonly.finalschoolexam.utils.MyStandardCallback;
import com.xiaolongonly.finalschoolexam.api.RequestApi;
import com.xiaolongonly.finalschoolexam.utils.SqlStringUtil;

import java.util.List;


/**
 * Created by Xiaolong on 4/4/2016.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private final static String TAG = "LoginActivity";
    private EditText account;
    private EditText password;
    private Button login;
    private TextView newUser;
    private LoadingDialog loadingDialog;//加载弹窗
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_login, 0);
    }

    @Override
    public void initView() {
        super.initView();
        Intent intent = getIntent();
        String isLogout=intent.getStringExtra("logout");
        account = (EditText) findViewById(R.id.et_account);
        password = (EditText) findViewById(R.id.et_password);
        login = (Button) findViewById(R.id.btn_login);
        newUser = (TextView) findViewById(R.id.tv_newuser);
        newUser.setOnClickListener(this);
        login.setOnClickListener(this);
        loadingDialog =new LoadingDialog(LoginActivity.this);
        loadingDialog.setLoadingText("正在登录...");
        String useraccount = PreferencesUtils.getStringValue(LoginActivity.this, "account");
        String userpassword = PreferencesUtils.getStringValue(LoginActivity.this, "password");
        if(isLogout==null) {
            if (!useraccount.equals("") && !userpassword.equals("")) {
                account.setText(useraccount);
                password.setText(userpassword
                );
                RequestApi.getInstance(LoginActivity.this).execSQL(SqlStringUtil.getuserInfoByAP(useraccount, userpassword), myStandardCallback);
                loadingDialog.show();
            }
        }else
        {
            account.setText(useraccount);
            PreferencesUtils.putStringPreferences(LoginActivity.this,"password","");
        }
    }

    @Override
    public void initData() {
        super.initData();
        stopLoading();
    }

    private void login() {
        if (account.getText().equals("")) {
            ToastUtil.showToast(LoginActivity.this, "请输入帐号！");
            return;
        }
        if (password.getText().equals("")) {
            ToastUtil.showToast(LoginActivity.this, "请输入密码！");
            return;
        }
        RequestApi.getInstance(LoginActivity.this).execSQL(SqlStringUtil.getuserInfoByAP(account.getText().toString(), password.getText().toString()), myStandardCallback);
        loadingDialog.show();
    }



    private MyStandardCallback myStandardCallback = new MyStandardCallback(this) {
        @Override
        public void onResult(MyAnalysis analysis) throws Exception {
            String json = analysis.getResult();
            if(json.equals("[]"))
            {
                ToastUtil.showToast(LoginActivity.this,"账号或者密码错误...");
                loadingDialog.dismiss();
                return;
            }
            else {
                JsonAnalysis<UserModel> jsonAnalysis = new JsonAnalysis<UserModel>();
                List<UserModel> userModels = jsonAnalysis.listFromJson(json, UserModel.class);
                PreferencesUtils.putStringPreferences(LoginActivity.this, "account", userModels.get(0).getUser_account());
                PreferencesUtils.putStringPreferences(LoginActivity.this, "password", userModels.get(0).getUser_password());
//                Log.i(TAG, json);
//                Log.i(TAG, userModels.get(0).getUser_account() + "------" + userModels.get(0).getUser_password());
                ConstantUtil.setUserModel(userModels.get(0));
                Log.i(TAG, ConstantUtil.getInstance().toString());
                Intent it = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(it, true);
                loadingDialog.dismiss();
            }
        }

        @Override
        public void onError(MyAnalysis baseAnalysis) {
            super.onError(baseAnalysis);
            loadingDialog.dismiss();
            ToastUtil.showToast(LoginActivity.this, baseAnalysis.getStatus());
        }

        @Override
        public void onError(int type) {
            loadingDialog.dismiss();
            Log.i(TAG,String.valueOf(type));
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_newuser:
                Intent it = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(it, false);
                break;
            case R.id.btn_login:
                login();
                break;
        }

    }
}
