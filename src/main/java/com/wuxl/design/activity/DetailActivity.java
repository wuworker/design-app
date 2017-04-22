package com.wuxl.design.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.wuxl.design.R;
import com.wuxl.design.model.WifiDevice;
import com.wuxl.design.model.WifiDeviceConnectManager;

import static com.wuxl.design.utils.AppUtils.setStatusBarTransparent;

/**
 * Created by wuxingle on 2017/4/22 0022.
 * 设备详情页
 */
public class DetailActivity extends AppCompatActivity{

    private static final String TAG="DetailActivity";

    private Toolbar toolbar;
    private TextView idTxt;
    private TextView nameTxt;
    private TextView statusTxt;

    private WifiDeviceConnectManager manager;

    private WifiDevice device;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarTransparent(this);
        setContentView(R.layout.activity_detail);

        initView();
        initToolBar();

        Bundle bundle = getIntent().getExtras();
        device = bundle.getParcelable("device");

        manager = WifiDeviceConnectManager.getInstance();

        initDevice();
    }

    /**
     * 初始化设备
     */
    private void initDevice(){
        idTxt.setText(device.getHexId());
        nameTxt.setText(device.getName());
        statusTxt.setText(device.getStatus() == WifiDevice.ONLINE ? "良好":"断开");
    }

    /**
     * 初始化组件
     */
    private void initView(){
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        idTxt = (TextView)findViewById(R.id.id_text);
        nameTxt = (TextView)findViewById(R.id.name_text);
        statusTxt = (TextView)findViewById(R.id.status_text);
    }

    /**
     * 初始化ToolBar
     * */
    private void initToolBar(){
        toolbar.setTitle("设备详情");//设置Toolbar标题

        toolbar.setNavigationIcon(android.R.drawable.ic_menu_revert);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }



}
