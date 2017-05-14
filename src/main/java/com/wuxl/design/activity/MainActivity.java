package com.wuxl.design.activity;

import android.content.Intent;
import android.content.SharedPreferences;
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

    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarColor(this,Color.rgb(0x87,0xce,0xeb));
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = getSharedPreferences("user",MODE_PRIVATE);
        userId = sharedPreferences.getString("user_num","000000");

        Log.i(TAG,"user id is "+userId);

        Timer timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(userId.equals("000000")){
                    startActivity(new Intent(MainActivity.this,LoginActivity.class));
                }else {
                    Intent intent = new Intent(MainActivity.this,DeviceActivity.class);
                    intent.putExtra("user",userId);
                    startActivity(intent);
                }
                finish();
            }
        }, 2000);

    }

}
