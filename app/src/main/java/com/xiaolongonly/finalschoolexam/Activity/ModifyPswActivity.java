package com.xiaolongonly.finalschoolexam.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.u1city.module.base.BaseActivity;
import com.u1city.module.util.StringUtils;
import com.u1city.module.util.ToastUtil;
import com.u1city.module.widget.LoadingDialog;
import com.xiaolongonly.finalschoolexam.R;
import com.xiaolongonly.finalschoolexam.utils.ConstantUtil;
import com.xiaolongonly.finalschoolexam.utils.MyAnalysis;
import com.xiaolongonly.finalschoolexam.utils.MyStandardCallback;
import com.xiaolongonly.finalschoolexam.api.RequestApi;
import com.xiaolongonly.finalschoolexam.utils.SqlStringUtil;

/**
 * Created by Administrator on 4/5/2016.
 */
public class ModifyPswActivity extends BaseActivity implements View.OnClickListener{

    private EditText etOldPassword;
    private EditText etNewPassword;
    private EditText etNewRePassword;
    private LoadingDialog loadingDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_passwordmodify, R.layout.title_default);
    }

    @Override
    public void initView() {
        super.initView();
        etNewPassword= (EditText) findViewById(R.id.et_newpassword);
        etOldPassword = (EditText) findViewById(R.id.et_oldpassword);
        etNewRePassword = (EditText) findViewById(R.id.et_repassword);
        loadingDialog = new LoadingDialog(this);
        initTitle();
    }

    /**
     * 初始化标题信息
     */
    private void initTitle() {
        TextView title = (TextView) findViewById(R.id.tv_title);
        title.setText("密码修改");
        title.setTextSize(20);
        ImageButton backBtn = (ImageButton) findViewById(R.id.ibt_back);
        backBtn.setOnClickListener(this);
        backBtn.setVisibility(View.VISIBLE);
        TextView rightBtn = (TextView) findViewById(R.id.tv_rightBtn);
        rightBtn.setText("确定");
        rightBtn.setOnClickListener(this);
        rightBtn.setVisibility(View.VISIBLE);
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
            case R.id.ibt_back:
                finishAnimation();
                break;
            case R.id.tv_rightBtn:
                saveData();
                break;
        }
    }

    private void saveData() {
        if(StringUtils.isEmpty(etOldPassword.getText().toString()))
        {
            ToastUtil.showToast(this,"旧密码不能为空！");
            return;
        }
        if(StringUtils.isEmpty(etNewPassword.getText().toString()))
        {
            ToastUtil.showToast(this,"新密码不能为空！");
            return;
        }
        if(StringUtils.isEmpty(etNewRePassword.getText().toString()))
        {
            ToastUtil.showToast(this,"新密码不能为空！");
            return;
        }
        if(etNewPassword.getText().length()<6&&etNewPassword.getText().length()>15)
        {
            ToastUtil.showToast(this,"密码长度不符规则");
            return;
        }
//        etNewRePassword.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//
//            }
//        });
        if(!etNewPassword.getText().toString().equals(etNewRePassword.getText().toString()))
        {
            ToastUtil.showToast(this,"两次输入的新密码不一致");
            return;
        }
        if(!etOldPassword.getText().toString().equals(ConstantUtil.getInstance().getUser_password()))
        {
            ToastUtil.showToast(this,"旧密码验证错误");
            return;
        }
        loadingDialog.show();
        RequestApi.getInstance(this).execSQL(SqlStringUtil.modifyUserPswByIP(ConstantUtil.getInstance().getUser_id(), etOldPassword.getText().toString(), etNewPassword.getText().toString()), new MyStandardCallback(this) {
            @Override
            public void onResult(MyAnalysis analysis) throws Exception {
                ToastUtil.showToast(ModifyPswActivity.this,"密码修改成功");
                loadingDialog.dismiss();
                finishAnimation();
            }

            @Override
            public void onError(int type) {
                ToastUtil.showToast(ModifyPswActivity.this,"密码修改失败");
                loadingDialog.dismiss();
            }
        });
    }
}
