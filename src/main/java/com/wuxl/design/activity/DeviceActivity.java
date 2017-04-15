package com.wuxl.design.activity;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.wuxl.design.R;
import com.wuxl.design.connect.ConnectorListener;
import com.wuxl.design.connect.DataExecutor;
import com.wuxl.design.connect.protocol.DataPackage;
import com.wuxl.design.model.WifiDevice;
import com.wuxl.design.model.WifiDeviceManager;
import com.wuxl.design.service.ConnectBinder;
import com.wuxl.design.service.TCPConnectService;

import java.util.ArrayList;

import static com.wuxl.design.utils.AppUtils.setStatusBarTransparent;

/**
 * 设备界面
 * Created by wuxingle on 2017/4/10 0010.
 */
public class DeviceActivity extends AppCompatActivity
    implements  AdapterView.OnItemLongClickListener,Toolbar.OnMenuItemClickListener{

    private static final String TAG = "DeviceActivity";

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private MaterialRefreshLayout refreshLayout;
    //增加设备对话框
    private AlertDialog additionDialog;
    //修改名字对话框
    private AlertDialog modifyDialog;
    //这个只有在无列表是才出现
    private LinearLayout hiddenAddLayout;

    private ListView deviceListView;
    private DeviceListAdapter deviceListAdapter;

    private DataExecutor dataExecutor;

    private WifiDeviceManager deviceManager;

    private ArrayList<WifiDevice> devices = new ArrayList<>();

    private ServiceConnection connection = new ServiceConnection() {
        private ConnectBinder binder;
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (ConnectBinder)service;
            binder.setConnectorListener(listener);
            dataExecutor = binder.getDataExecutor();
            deviceManager = WifiDeviceManager.getInstance(dataExecutor);
            Log.i(TAG,"service on bind");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.w(TAG,"service bind fail");
        }
    };

    private ConnectorListener listener = new ConnectorListener() {
        @Override
        public void connectResult(boolean success) {
            Log.i(TAG,"连接:"+success);
        }

        @Override
        public void arrivedMessage(byte[] bytes) {
            DataPackage dataPackage = dataExecutor.toDataPackage(bytes);
            Log.d(TAG,"收到数据origin:"+dataPackage.getHexOrigin());
            Log.d(TAG,"收到数据cmd:"+dataPackage.getCmd());
            Log.d(TAG,"收到数据data:"+dataPackage.getData());
        }

        @Override
        public void sendComplete(byte[] bytes) {
            Log.i(TAG,"发送回调");
        }

        @Override
        public void connectLost(String msg) {

        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarTransparent(this);
        setContentView(R.layout.activity_device);

        devices.add(new WifiDevice(null,"哈哈"));
        devices.add(new WifiDevice(null,"呵呵"));

        initView();
        initToolBar();
        initRefreshView();
        initListView();

        initAdditionDialog();
        initModifyDialog();

        bindService(new Intent(this,TCPConnectService.class),connection,BIND_AUTO_CREATE);
    }


    /**
     * 列表点击
     * @param parent parent
     * @param view view
     * @param position position
     * @param id id
     */
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

        modifyDialog.show();

        return true;
    }

    /**
     * 菜单点击
     * @param item item
     * @return true
     */
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_add:
                additionDialog.show();
                break;
        }
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("人物简介");
        //添加菜单项
        menu.add(0, Menu.FIRST,0,"特长");
        menu.add(0,Menu.FIRST+1,0,"战斗力");
        menu.add(0,Menu.FIRST+2,0,"经典语录");
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info=(AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        Log.i(TAG,"点击了菜单");
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(devices.size()>0){
            hiddenAddLayout.setVisibility(View.INVISIBLE);
        }
        Log.i(TAG,"device activity resume");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
        stopService(new Intent(this, TCPConnectService.class));
        Log.i(TAG,"device activity destroy");
    }

    /**
     * 刷新时的监听器
     */
    private class RefreshListener extends MaterialRefreshListener  {
        @Override
        public void onfinish() {
            super.onfinish();
        }

        @Override
        public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
            super.onRefreshLoadMore(materialRefreshLayout);
        }

        @Override
        public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {

        }
    }


    /**
     * 初始化组件
     */
    private void initView(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawerLayout = (DrawerLayout)findViewById(R.id.device_drawerlayout);
        refreshLayout = (MaterialRefreshLayout)findViewById(R.id.refresh_layout);
        deviceListView = (ListView)findViewById(R.id.device_list);
        hiddenAddLayout = (LinearLayout)findViewById(R.id.device_add_layout);
    }
    /**
     * 增加设备对话框
     */
    private void initAdditionDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("增加设备")
                .setView(new EditText(this))
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i(TAG,"点击了确定");
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                additionDialog.dismiss();
            }
        });
        additionDialog = builder.create();
    }

    /**
     * 修改名字对话框
     */
    private void initModifyDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("修改名字")
                .setView(new EditText(this))
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i(TAG,"点击了确定");
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                modifyDialog.dismiss();
            }
        });
        modifyDialog = builder.create();
    }
    /**
     * 初始化toolBar和drawerLayout
     * */
    private void initToolBar(){
        toolbar.setTitle("智能灯");//设置Toolbar标题
        toolbar.setSubtitle("未登录");
        toolbar.setSubtitleTextColor(Color.WHITE);

        toolbar.inflateMenu(R.menu.device_menu);
        toolbar.setOnMenuItemClickListener(this);
        //切换效果
        ActionBarDrawerToggle drawerToggle=new
                ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.bar_open,R.string.bar_close){
                    @Override
                    public void onDrawerOpened(View drawerView) {
                        super.onDrawerOpened(drawerView);
                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {
                        super.onDrawerClosed(drawerView);
                    }

                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {
                        super.onDrawerSlide(drawerView, slideOffset);
                        //获取drawerLayout的主布局
                        View mContent = drawerLayout.getChildAt(0);

                        float centerScale =1.0f - slideOffset * 0.3f;
                        float startScale = 0.7f + 0.3f * slideOffset;

                        mContent.setTranslationX(drawerView.getMeasuredWidth() * (slideOffset));
                        mContent.setScaleX(centerScale);
                        mContent.setScaleY(centerScale);
                        drawerView.setScaleY(startScale);
                        drawerView.setScaleX(startScale);
                    }
                };

        drawerToggle.syncState();
        drawerLayout.addDrawerListener(drawerToggle);
    }


    /**
     * 初始化刷新组件
     */
    private void initRefreshView(){
        //下拉刷新时调用
        refreshLayout.setMaterialRefreshListener(new RefreshListener());
    }

    /**
     * 初始化listView
     */
    private void initListView(){
        deviceListAdapter = new DeviceListAdapter();
        deviceListView.setAdapter(deviceListAdapter);
        //deviceListView.setOnItemLongClickListener(this);
        registerForContextMenu(deviceListView);
    }

    /**
     * 设备列表适配器
     */
    private class DeviceListAdapter extends BaseAdapter{

        public void notifyChange(){
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return devices.size();
        }

        @Override
        public WifiDevice getItem(int position) {
            return devices.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if(convertView==null){
                convertView= LayoutInflater.from(DeviceActivity.this).inflate(R.layout.list_item_device,null);
            }
            TextView nameTxt = (TextView)convertView.findViewById(R.id.name_txt);
            Switch deviceSwitch=(Switch)convertView.findViewById(R.id.device_switch);
            SeekBar deviceBar = (SeekBar)convertView.findViewById(R.id.device_bar);

            nameTxt.setText(devices.get(position).getName());
            deviceSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        Log.i(TAG,"led打开1");
                    }else {
                        Log.i(TAG,"led关闭");
                    }
                }
            });
            deviceBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    Log.d(TAG,"bar:"+progress);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    Log.d(TAG,"bar start");
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    Log.d(TAG,"bar end");
                }
            });

            return convertView;
        }
    }



}




