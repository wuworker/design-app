package com.wuxl.design.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.wuxl.design.R;

import java.util.Timer;
import java.util.TimerTask;

import static com.wuxl.design.common.utils.AppUtils.setStatusBarColor;

/**
 * 首页
 * Created by wuxingle on 2017/4/10 0010.
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarColor(this,Color.rgb(0x87,0xce,0xeb));
        setContentView(R.layout.activity_main);

        Log.i(TAG,"app start");

        Timer timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                startActivity(new Intent(MainActivity.this,LoginActivity.class));
                finish();
            }
        }, 2000);
    }

}
