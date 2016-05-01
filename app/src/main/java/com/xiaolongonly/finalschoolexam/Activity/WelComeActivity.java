package com.xiaolongonly.finalschoolexam.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import com.xiaolongonly.finalschoolexam.R;

/**
 * Created by Administrator on 4/11/2016.
 */
public class WelComeActivity extends Activity {

    protected static final String TAG = "WelcomeActivity";
    private TextView mtextView;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        findView();
        init();
    }

    private void findView() {
        mtextView = (TextView) findViewById(R.id.tv_welcome);
    }

    @SuppressWarnings("static-access")
    private void init() {
        mtextView.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(WelComeActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }, 2000);
    }
}
