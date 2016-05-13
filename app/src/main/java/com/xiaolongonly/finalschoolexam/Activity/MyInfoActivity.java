package com.xiaolongonly.finalschoolexam.activity;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.u1city.module.base.BaseActivity;
import com.u1city.module.common.picturetaker.PictureTakeDialog;
import com.u1city.module.common.picturetaker.PictureTaker;
import com.u1city.module.util.ImageUtils;
import com.u1city.module.util.SimpleImageOption;
import com.u1city.module.util.StringUtils;
import com.u1city.module.util.ToastUtil;
import com.u1city.module.widget.RoundedImageView;
import com.xiaolongonly.finalschoolexam.listener.ResponseListener;
import com.xiaolongonly.finalschoolexam.R;
import com.xiaolongonly.finalschoolexam.api.RequestApi;
import com.xiaolongonly.finalschoolexam.api.UploadApi;
import com.xiaolongonly.finalschoolexam.model.UserModel;
import com.xiaolongonly.finalschoolexam.utils.ConstantUtil;
import com.xiaolongonly.finalschoolexam.utils.ImageLoaderConfig;
import com.xiaolongonly.finalschoolexam.utils.MyAnalysis;
import com.xiaolongonly.finalschoolexam.utils.MyStandardCallback;
import com.xiaolongonly.finalschoolexam.utils.SqlStringUtil;
import com.xiaolongonly.finalschoolexam.utils.VolleyUtil;

import org.json.JSONException;
import org.json.JSONObject;


public class MyInfoActivity extends BaseActivity implements OnClickListener {
    private static final String TAG = "MyInfoActivity";

    private EditText userNickName;
    private EditText userQQ;
    private EditText userTel;

    //存放用户信息
    private UserModel userModel;

    private PopupWindow popup;
    //遮罩层暂时没什么用
//    private View layoutShade;// 遮罩层
    private RoundedImageView myInfoImage;// 我的头像圆形控件
    private ImageView ivGuiderLogo;//头像右下角的小相机

    // 图片相关

    // 请求码
    public static final int REQUEST_CODE_SHARE_EXPERIENCE = 1;
    public static final int REQUEST_CODE_DESCRIBLE_PHOTO = 2;
    public static final int REQUEST_CODE_GETIMAGE_BYCROP = 6;
    public static final int REQUEST_CODE_SHARED_EXPERIENCE = 8;
    /**
     * 上传完图片后，才能保存信息，防止保存错误信息
     */
    private PictureTaker pictureTaker;
    private PictureTakeDialog pictureTakeDialog;
    private DisplayImageOptions imageOptions = SimpleImageOption.create(R.drawable.ic_default_avatar_guider);
    private ProgressDialog mDialog;
    private String imageUrl; //存放imageUrl
    private boolean isUploading;//判别是否正在加载中

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_myinfo, R.layout.title_default);
        mDialog = new ProgressDialog(this);
        mDialog.setCanceledOnTouchOutside(false);
    }

    @Override
    public void initView() {
        super.initView();
        userNickName = (EditText) findViewById(R.id.et_user_nickname);
        userQQ = (EditText) findViewById(R.id.et_user_qq);
        userTel = (EditText) findViewById(R.id.et_user_tel);
        initTitle();
        myInfoImage = (RoundedImageView) findViewById(R.id.my_info_image);
        myInfoImage.setOnClickListener(this);
        ivGuiderLogo = (ImageView) findViewById(R.id.iv_guiderLogo);
        ivGuiderLogo.setOnClickListener(this);
        myInfoImage.setOnClickListener(this);
        pictureTaker = new PictureTaker(this, "/final");
        ImageLoaderConfig.setConfig(this);// 此句在正常使用中不需要
        //设置获取图片后剪裁
        pictureTaker.setEnableCrop(true);
        pictureTaker.setOnTakePictureListener(new PictureTaker.OnTakePictureListener() {
            @Override
            public void onPictureTaked(Bitmap bitmap) {
                myInfoImage.setImageBitmap(bitmap);
//                String bitmapToString = ImageUtils.bitmapToString(bitmap, 75);
                Bitmap resizeBitmap = ImageUtils.comp(bitmap);
                saveData(resizeBitmap);//pictureTaker图片选择完成之后上传图片
            }
        });
    }

    @Override
    public void initData() {
        super.initData();
        userModel = ConstantUtil.getInstance();
        userNickName.setText(userModel.getUser_name());
        userQQ.setText(userModel.getUser_qq());
        userTel.setText(userModel.getUser_tel());
        if (userModel.getUser_imageurl() != null) {
            imageUrl = userModel.getUser_imageurl();
            ImageLoader.getInstance().displayImage(userModel.getUser_imageurl(), myInfoImage, imageOptions);
        }
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
        switch (view.getId()) {
            case R.id.tv_rightBtn:
                if(isUploading) {
                    ToastUtil.showToast(MyInfoActivity.this,"正在上传头像");
                    return;
                }
                saveMyInfo();
                break;
            case R.id.ibt_back:
                finishAnimation();
                break;
            case R.id.my_info_image://修改头像
            case R.id.iv_guiderLogo://修改头像
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                updateLogo(imm);
                break;
        }

    }

    private void saveMyInfo() {
        if (StringUtils.isEmpty(userNickName.getText().toString())) {
            ToastUtil.showToast(MyInfoActivity.this, "请输入昵称");
            return;
        }
        if (StringUtils.isEmpty(userQQ.getText().toString())) {
            ToastUtil.showToast(MyInfoActivity.this, "请输入QQ");
            return;
        }
        if (StringUtils.isEmpty(userTel.getText().toString())) {
            ToastUtil.showToast(MyInfoActivity.this, "请输入手机号码");
            return;
        }
        if (!StringUtils.isMobileNO(userTel.getText().toString())) {
            ToastUtil.showToast(MyInfoActivity.this, "手机号码不符规则");
            return;
        }
        if (userQQ.getText().toString().length() < 5 || userQQ.getText().toString().length() > 12) {
            ToastUtil.showToast(MyInfoActivity.this, "QQ号码长度不符规则");
            return;
        }
        userModel.setUser_name(userNickName.getText().toString());
        userModel.setUser_qq(userQQ.getText().toString());
        userModel.setUser_tel(userTel.getText().toString());
        userModel.setUser_imageurl(imageUrl);
        RequestApi.getInstance(this).execSQL(SqlStringUtil.modifyUserInfo(userModel.getUser_id(), userModel.getUser_name(), userModel.getUser_tel(), userModel.getUser_qq(),userModel.getUser_imageurl()), new MyStandardCallback(this) {
            @Override
            public void onResult(MyAnalysis analysis) throws Exception {
                ToastUtil.showToast(MyInfoActivity.this, "修改成功");
                ConstantUtil.setUserModel(userModel);
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

    public void updateLogo(InputMethodManager imm) {
        popupWinDismiss();
        imm.hideSoftInputFromWindow(MyInfoActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        showPicturePickDialog();
    }

    /**
     * 修改头像的弹窗
     */
    public void showPicturePickDialog() {
        if (pictureTakeDialog == null) {
            pictureTakeDialog = new PictureTakeDialog(this, pictureTaker);
        }
        pictureTakeDialog.show();
    }


    /*
     * 按回退键先关闭弹出框，再关闭当前页
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == 4) {
            if (popup != null && popup.isShowing()) {
                popup.dismiss();
            } else {
                finishAnimation();
            }
        }
        return true;
    }

    /*
     * 关闭popupWindow
     */
    public void popupWinDismiss() {
        if (popup != null && popup.isShowing()) {
            popup.dismiss();
            popup = null;
//            layoutShade.setVisibility(View.GONE);// 隐藏遮罩层
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 图片返回时的请求码
        switch (requestCode) {
            case REQUEST_CODE_GETIMAGE_BYCROP:
                break;
            default:
                break;
        }
        pictureTaker.onActivityResult(data, requestCode);
    }

    /*
 * 上传图片上传完成后更新头像链接信息
 *
 * @param imageName 名称
 *
 * @param bitmapToString
 *
 * @return
 */
    private void saveData(Bitmap bitmap) {
        VolleyUtil.initialize(MyInfoActivity.this);
        isUploading=true;
        mDialog.setMessage("图片上传中...");
        UploadApi.uploadImg(bitmap, new ResponseListener<String>() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, "===========VolleyError=========" + error);
                Log.i(TAG, "ErrorResponse\n" + error.getMessage());
                Toast.makeText(MyInfoActivity.this, "上传失败", Toast.LENGTH_SHORT).show();
                isUploading = false;
                mDialog.dismiss();
            }

            @Override
            public void onResponse(String response) {
                try {
                    MyAnalysis myAnalysis = new MyAnalysis(new JSONObject(response));
                    String url = myAnalysis.getStringFromResult("url");
                    imageUrl = url;
                    Log.i("ImageUrl", url);
                    mDialog.dismiss();
                    Toast.makeText(MyInfoActivity.this, "上传成功", Toast.LENGTH_SHORT).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                isUploading = false;
            }
        });
    }
}

