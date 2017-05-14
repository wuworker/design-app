package com.wuxl.design.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.wuxl.design.R;

import java.util.Random;

import static com.wuxl.design.common.utils.AppUtils.setStatusBarTransparent;


/**
 * 登录界面
 * Created by wuxingle on 2017/4/10 0010.
 */
public class LoginActivity extends AppCompatActivity{

    private static final String TAG = "LoginActivity";

    private static final String USER_PREFERENCE = "user";

    private SharedPreferences sharedPreferences;

    private Toolbar toolbar;
    private EditText numEt;
    private EditText phoneEt;

    private Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarTransparent(this);
        setContentView(R.layout.activity_login);

        sharedPreferences = getSharedPreferences(USER_PREFERENCE,MODE_PRIVATE);

        initView();
        initToolBar();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.i(TAG,"login activity destroy");
    }

    public void login(View v){
        if(numEt.getText().toString().length()!=6){
            Toast.makeText(this,"请先获取账号",Toast.LENGTH_SHORT).show();
            return;
        }
        if(phoneEt.getText().toString().length()!=11){
            Toast.makeText(this,"请输入11位号码",Toast.LENGTH_SHORT).show();
            return;
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("user_num",numEt.getText().toString());
        editor.putString("user_phone",phoneEt.getText().toString());
        editor.apply();

        Intent intent = new Intent(this, DeviceActivity.class);
        intent.putExtra("user",numEt.getText().toString());
        startActivity(intent);
        finish();
    }

    public void getNumber(View v){
        int num = random.nextInt(900000) + 100000;
        numEt.setText(String.valueOf(num));
    }

    /**
     * 初始化组件
     */
    private void initView(){
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        numEt = (EditText) findViewById(R.id.num_et);
        phoneEt = (EditText)findViewById(R.id.phone_et);
    }

    /**
     * 初始化ToolBar
     * */
    private void initToolBar(){
        toolbar.setTitle("登录");//设置Toolbar标题

        toolbar.setNavigationIcon(android.R.drawable.ic_menu_revert);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }



}
