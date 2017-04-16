package com.wuxl.design.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.wuxl.design.R;
import com.wuxl.design.connect.service.TCPConnectService;

import static com.wuxl.design.utils.AppUtils.*;


/**
 * 登录界面
 * Created by wuxingle on 2017/4/10 0010.
 */
public class LoginActivity extends AppCompatActivity{

    private static final String TAG = "LoginActivity";

    private Toolbar toolbar;

    private boolean exit = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarTransparent(this);
        setContentView(R.layout.activity_login);

        initView();
        initToolBar();
    }


    public void login(View v){
        exit = false;
        Intent intent = new Intent(this, DeviceActivity.class);
        startActivity(intent);
        Log.i(TAG,"进入设备页面");
        finish();
    }

    /**
     * 初始化组件
     */
    private void initView(){
        toolbar = (Toolbar)findViewById(R.id.toolbar);
    }

    /**
     * 初始化ToolBar
     * */
    private void initToolBar(){
        toolbar.setTitle("登录");//设置Toolbar标题

        //toolbar.inflateMenu(R.menu.sensor_menu);
        toolbar.setNavigationIcon(android.R.drawable.ic_menu_revert);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //toolbar.setOnMenuItemClickListener(this);
    }


    @Override
    public void onDestroy(){
        super.onDestroy();
        if(exit){
            Intent intent = new Intent(this,TCPConnectService.class);
            stopService(intent);
            Log.i(TAG,"login stop service");
        }
        Log.i(TAG,"login activity destroy");
    }


}
