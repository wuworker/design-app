package com.wuxl.design.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.wuxl.design.R;
import com.wuxl.design.common.utils.AppUtils;
import com.wuxl.design.common.utils.DateUtils;
import com.wuxl.design.connect.protocol.DataCmdSender;
import com.wuxl.design.connect.protocol.DataProtocol;
import com.wuxl.design.wifidevice.WifiDevice;
import com.wuxl.design.wifidevice.WifiDeviceConnectManager;
import com.wuxl.design.wifidevice.WifiListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import static com.wuxl.design.common.utils.AppUtils.setStatusBarTransparent;
import static com.wuxl.design.common.utils.DataUtils.toByte;
import static com.wuxl.design.wifidevice.WifiDevice.BUSY;
import static com.wuxl.design.wifidevice.WifiDevice.ONLINE;
import static com.wuxl.design.wifidevice.WifiDevice.UNONLINE;

/**
 * 设备界面
 * Created by wuxingle on 2017/4/10 0010.
 */
public class DeviceActivity extends AppCompatActivity
        implements Toolbar.OnMenuItemClickListener {

    private static final String TAG = "DeviceActivity";

    private static final String DEVICE_FILE = "devices.bat";

    //handler message
    private static final int REFRESH_OVER = 1;
    private static final int STATE_ONLINE = 2;
    private static final int CONNECT_STATUS = 3;
    private static final int STATE_CHANGE = 4;

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private MaterialRefreshLayout refreshLayout;

    //增加设备对话框
    private AlertDialog additionDialog;
    //修改名字对话框
    private AlertDialog modifyDialog;

    //这个只有在无列表时才出现
    private LinearLayout hiddenAddLayout;

    //listView有关
    private ListView deviceListView;
    private DeviceListAdapter deviceListAdapter;
    private int currentSelected;

    //server ip
    private String ip;
    //server port
    private int port;

    //wifi设备连接的管理
    private WifiDeviceConnectManager deviceManager;
    //命令发送接口
    private DataCmdSender cmdSender;

    //在线设备数
    private int onlineCount = 0;

    //是否正在刷新
    private boolean isRefreshing;

    //上一次按返回键的时间
    private long lastPressBackTime;

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case REFRESH_OVER:
                    Log.i(TAG, "handler到达");
                    refreshLayout.finishRefresh();
                    break;
                case STATE_ONLINE:
                    Log.i(TAG, "设备在线");
                    deviceListAdapter.modifyDeviceStatus(msg.arg1, ONLINE);
                    break;
                case CONNECT_STATUS:
                    if (msg.arg1 == 0) {
                        Toast.makeText(DeviceActivity.this, "连接服务器失败", Toast.LENGTH_SHORT).show();
                        deviceListAdapter.modifyAllDeviceStatus(UNONLINE);
                        refreshLayout.finishRefresh();
                        return true;
                    }
                    if (deviceListAdapter.getCount() == 0) {
                        return true;
                    }
                    //断开重连的
                    if (isRefreshing) {
                        refreshDevice();
                    }
                    //第一次连接成功的
                    else {
                        refreshLayout.autoRefresh();
                    }
                    break;
                case STATE_CHANGE:
                    deviceListAdapter.modifyDeviceStatus(msg.arg1, msg.arg2);
                    String tip = msg.arg2 == ONLINE ? "设备已上线" : "设备掉线";
                    Toast.makeText(DeviceActivity.this, tip, Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
            return true;
        }
    });


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarTransparent(this);
        setContentView(R.layout.activity_device);

        ip = getResources().getString(R.string.server_ip);
        port = Integer.parseInt(getResources().getString(R.string.server_port));

        ArrayList<WifiDevice> devices = readWifiDevice();
        Log.i(TAG, "读取的设备为：" + devices);

        initView();
        initToolBar();
        initRefreshView();
        initListView(devices);

        initAdditionDialog();
        initModifyDialog();

        deviceManager = WifiDeviceConnectManager.getInstance();
        deviceManager.setListener(listener);
       // deviceManager.ready(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (deviceListAdapter.getCount() > 0) {
            hiddenAddLayout.setVisibility(View.INVISIBLE);
        }
        Log.i(TAG, "device activity resume");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        deviceManager.close();
        writeWifiDevice();
        Log.i(TAG, "device activity destroy");
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case AppUtils.CAMERA_OK:
                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_GRANTED) {
                    openCamera();
                } else {
                    additionDialog.show();
                    Log.i(TAG, "调用摄像头被拒绝");
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG,"REQ:"+requestCode+","+resultCode);
        //定时界面的结果码
        if(resultCode == TimerActivity.TIMER_RESULT_OK){
            WifiDevice device = deviceListAdapter.getItem(requestCode);
            Bundle bundle = data.getExtras();
            if(bundle.getBoolean("timeEnable")){
                int year = bundle.getInt("year");
                int month = bundle.getInt("month");
                int day = bundle.getInt("day");
                int hour = bundle.getInt("hour");
                int minute = bundle.getInt("minute");
                Log.i(TAG,year+","+month+","+day+","+hour+","+minute);
                device.setTimeEnable(true);
                device.setTime(year+":"+month+":"+day+":"+hour+":"+minute);
                int sendMinute = DateUtils.toNowAfterMinutes(year,month,day,hour,minute);
                Log.i(TAG,"离现在"+sendMinute+"分钟");
                if(bundle.getBoolean("timeOn")){
                    int pwm = bundle.getInt("timePwm");
                    device.setTimeOn(true);
                    device.setTimePwm(pwm);
                    Log.i(TAG,"pwm:"+pwm);
                    if(cmdSender!=null){
                        cmdSender.onTime(device,sendMinute);
                    }
                }else {
                    device.setTimeOn(false);
                    if(cmdSender!=null){
                        cmdSender.offTime(device,sendMinute);
                    }
                }
            } else {
                if(device.isTimeEnable()){
                    device.setTimeEnable(false);
                    if(cmdSender!=null){
                        cmdSender.clearTime(device);
                    }
                }
            }
            deviceListAdapter.notifyDataSetChanged();
            return;
        }
        //扫描界面
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (intentResult != null) {
            // ScanResult 为获取到的字符串
            String scanResult = intentResult.getContents();
            if (scanResult != null) {
                String addResult = addDevice(scanResult);
                Toast.makeText(this, addResult, Toast.LENGTH_LONG).show();
            }
            Log.i(TAG, "拿到:" + scanResult);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onBackPressed() {
        if (isRefreshing) {
            refreshLayout.finishRefresh();
            return;
        }
        long now = System.currentTimeMillis();
        if (now - lastPressBackTime < 2000) {
            Log.i(TAG, "程序退出");
            finish();
            return;
        }
        lastPressBackTime = now;
        Toast.makeText(this, "再按一次返回键退出", Toast.LENGTH_SHORT).show();
    }

    /**
     * toolbar的菜单点击
     *
     * @param item item
     * @return true
     */
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add:
                if (AppUtils.haveCamerPerssion(this)) {
                    openCamera();
                } else {
                    AppUtils.applyCameraPerssion(this);
                }
                break;
            case R.id.menu_set:
                startActivity(new Intent(this, SettingActivity.class));
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * context菜单
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        //添加菜单项
        menu.add(0, Menu.FIRST, 0, "修改设备名");
        menu.add(0,Menu.FIRST + 1,0,"设置定时");
        menu.add(0, Menu.FIRST + 2, 0, "查看详情");
        menu.add(0, Menu.FIRST + 3, 0, "删除设备");
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    /**
     * context菜单点击
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        currentSelected = info.position;
        switch (item.getItemId()) {
            case Menu.FIRST:
                modifyDialog.show();
                break;
            case Menu.FIRST + 1:
                setDeviceTimer(currentSelected);
                break;
            case Menu.FIRST + 2:
                lookDetail(currentSelected);
                break;
            case Menu.FIRST + 3:
                deviceListAdapter.remove(currentSelected);
                break;
            default:
                break;
        }
        return super.onContextItemSelected(item);
    }

    /**
     * 连接状态监听
     */
    private WifiListener listener = new WifiListener() {

        //可以开始连接
        @Override
        public void canConnect() {
            Log.i(TAG, "可以连接");
            deviceManager.connect(ip, port);
            cmdSender = deviceManager.getCmdSender();
        }

        //连接结果
        @Override
        public void connectResult(boolean result) {
            Message message = handler.obtainMessage();
            message.what = CONNECT_STATUS;
            if (result) {
                message.arg1 = 1;
                Log.i(TAG, "连接成功");
                //发送自己对哪些设备感兴趣
                for (WifiDevice device : deviceListAdapter.getWifiDevices()) {
                    cmdSender.addInterested(device);
                }
            } else {
                message.arg1 = 0;
                Log.i(TAG, "连接失败");
            }
            handler.sendMessage(message);
        }

        //设备在线通知,app主动发送
        @Override
        public void isOnline(String hexId,int level) {
            if (!isRefreshing) {
                return;
            }
            for (int i = 0; i < deviceListAdapter.getCount(); i++) {
                WifiDevice device = deviceListAdapter.getItem(i);
                if (device.getStatus() != ONLINE &&
                        hexId.equals(device.getHexId())) {
                    //修改状态
                    device.setLightLevel(level);
                    Message message = handler.obtainMessage();
                    message.arg1 = i;
                    message.what = STATE_ONLINE;
                    handler.sendMessage(message);
                    onlineCount++;
                    break;
                }
            }
            if (onlineCount == deviceListAdapter.getCount()) {
                Log.i(TAG, "停止刷新,全部找到");
                refreshLayout.finishRefresh();
            }
        }

        //设备状态改变,服务器主动发送
        @Override
        public void changeStatus(String hexId, boolean status) {
            Log.i(TAG, "设备状态改变");
            int newStatus = status ? ONLINE : UNONLINE;
            for (int i = 0; i < deviceListAdapter.getCount(); i++) {
                WifiDevice device = deviceListAdapter.getItem(i);
                if (device.getHexId().equals(hexId)
                        && device.getStatus() != newStatus) {
                    //修改状态
                    Message message = handler.obtainMessage();
                    message.arg1 = i;
                    message.arg2 = newStatus;
                    message.what = STATE_CHANGE;
                    handler.sendMessage(message);
                    break;
                }
            }
        }
    };

    /**
     * 刷新时的监听器
     */
    private class RefreshListener extends MaterialRefreshListener {
        @Override
        public void onfinish() {
            super.onfinish();
            deviceListAdapter.refreshAllStatus();
            isRefreshing = false;
        }

        @Override
        public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
            super.onRefreshLoadMore(materialRefreshLayout);
        }

        @Override
        public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
            isRefreshing = true;
            //连接可用
            if (deviceManager.isConnectable()) {
                refreshDevice();
            } else if (!deviceManager.isConnecting()) {
                deviceManager.connect(ip, port);
                Toast.makeText(DeviceActivity.this, "尝试重新连接,请稍候", Toast.LENGTH_SHORT).show();
            }
            //因为连接速度很快，这种情况不多
            else {
                Toast.makeText(DeviceActivity.this, "已在连接中", Toast.LENGTH_SHORT).show();
            }
        }
    }


    /**
     * seekBar的监听
     * 由switch控制,发送通过这个
     */
    private class SeekBarListener implements SeekBar.OnSeekBarChangeListener {

        private WifiDevice device;

        public SeekBarListener(int position) {
            device = deviceListAdapter.getItem(position);
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (progress == 0) {
                cmdSender.off(device);
            } else {
                device.setLightLevel(progress);
                cmdSender.on(device, progress);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    }


    /**
     * led开关
     * 包含了一个seekBar，因为开关关闭时，seekBar必须无效
     */
    private class CommonButtonListener implements CompoundButton.OnCheckedChangeListener {

        private int position;
        private SeekBar seekBar;

        public CommonButtonListener(int position, SeekBar seekBar) {
            this.position = position;
            this.seekBar = seekBar;
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                seekBar.setEnabled(true);
                seekBar.setProgress(deviceListAdapter.getItem(position).getLightLevel());
                Log.i(TAG, "第" + position + "个led打开");
            } else {
                seekBar.setProgress(0);
                seekBar.setEnabled(false);
                Log.i(TAG, "第" + position + "个led关闭");
            }
        }
    }


    /**
     * 打开扫一扫
     */
    private void openCamera() {
        new IntentIntegrator(this)
                .setOrientationLocked(false)
                .setCaptureActivity(ScanActivity.class) // 设置自定义的activity是CustomActivity
                .initiateScan(); // 初始化扫描
    }

    /**
     * 添加设备
     *
     * @param idHex 输入的识别码
     */
    private String addDevice(String idHex) {
        byte[] origin = toByte(idHex);
        if (origin.length != DataProtocol.ORIGIN_LENGTH) {
            Log.i(TAG, "添加设备失败,id格式错误");
            return "识别码格式错误";
        }

        for (int i = 0; i < deviceListAdapter.getCount(); i++) {
            if (Arrays.equals(deviceListAdapter.getItem(i).getId(), origin)) {
                Log.i(TAG, "添加设备失败,重复添加");
                return "该设备已存在";
            }
        }

        WifiDevice wifiDevice = new WifiDevice(origin, idHex);
        deviceListAdapter.add(wifiDevice);
        if (cmdSender != null) {
            refreshDevice();
            cmdSender.addInterested(wifiDevice);
        }
        return "添加设备成功";
    }

    /**
     * 刷新设备状态
     */
    private void refreshDevice() {
        if (deviceListAdapter.getCount() == 0) {
            return;
        }
        //修改状态
        deviceListAdapter.modifyAllDeviceStatus(BUSY);
        onlineCount = 0;
        //发送数据
        for (int i = 0; i < deviceListAdapter.getCount(); i++) {
            WifiDevice device = deviceListAdapter.getItem(i);
            cmdSender.isOnline(device);
        }
        //3秒后停止
        handler.sendEmptyMessageDelayed(REFRESH_OVER, 3000);
    }

    /**
     * 查看设备细节
     */
    private void lookDetail(int index) {
        Intent intent = new Intent(this, DetailActivity.class);
        WifiDevice device = deviceListAdapter.getItem(index);
        Bundle bundle = new Bundle();
        bundle.putParcelable("device", device);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    /**
     * 设置设备定时
     */
    private void setDeviceTimer(int index){
        Intent intent = new Intent(this, TimerActivity.class);
        WifiDevice device = deviceListAdapter.getItem(index);
        Bundle bundle = new Bundle();
        bundle.putParcelable("device", device);
        intent.putExtras(bundle);
        startActivityForResult(intent,currentSelected);
    }

    /**
     * 读取设备列表
     */
    @SuppressWarnings("unchecked")
    private ArrayList<WifiDevice> readWifiDevice() {
        try {
            Object obj = AppUtils.readSerialize(getFilesDir().getPath() + "/" + DEVICE_FILE,
                    openFileInput(DEVICE_FILE));
            return obj == null ? new ArrayList<WifiDevice>() : (ArrayList<WifiDevice>) obj;
        } catch (IOException e) {
            Log.e(TAG, "读取设备异常", e);
        }
        return new ArrayList<>();
    }

    /**
     * 保存设备列表
     */
    private void writeWifiDevice() {
        try {
            boolean suc = AppUtils.writeSerialize(openFileOutput(DEVICE_FILE, Context.MODE_PRIVATE),
                    deviceListAdapter.getWifiDevices());
            Log.i(TAG, "保存设备结果:" + suc);
        } catch (IOException e) {
            Log.e(TAG, "保存设备数据失败");
        }
    }

    /**
     * 初始化组件
     */
    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.device_drawerlayout);
        refreshLayout = (MaterialRefreshLayout) findViewById(R.id.refresh_layout);
        deviceListView = (ListView) findViewById(R.id.device_list);
        hiddenAddLayout = (LinearLayout) findViewById(R.id.device_add_layout);
    }

    /**
     * 增加设备对话框
     */
    private void initAdditionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText editText = new EditText(this);
        builder.setTitle("增加设备")
                .setView(editText)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String idHex = editText.getText().toString().trim();
                        String result = addDevice(idHex);
                        Toast.makeText(DeviceActivity.this, result, Toast.LENGTH_SHORT).show();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                additionDialog.dismiss();
                editText.setText("");
            }
        });
        additionDialog = builder.create();
        additionDialog.setCanceledOnTouchOutside(false);
    }

    /**
     * 修改名字对话框
     */
    private void initModifyDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText editText = new EditText(this);
        builder.setTitle("修改名字")
                .setView(editText)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deviceListAdapter.modifyName(currentSelected,
                                editText.getText().toString().trim());
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                modifyDialog.dismiss();
            }
        });
        modifyDialog = builder.create();
        modifyDialog.setCanceledOnTouchOutside(false);
    }

    /**
     * 初始化toolBar和drawerLayout
     */
    private void initToolBar() {
        toolbar.setTitle("智能灯");//设置Toolbar标题
        toolbar.setSubtitle("未登录");
        toolbar.setSubtitleTextColor(Color.WHITE);

        toolbar.inflateMenu(R.menu.device_menu);
        toolbar.setOnMenuItemClickListener(this);
        //切换效果
        ActionBarDrawerToggle drawerToggle = new
                ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.bar_open, R.string.bar_close) {
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

                        float centerScale = 1.0f - slideOffset * 0.3f;
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
    private void initRefreshView() {
        //下拉刷新时调用
        refreshLayout.setMaterialRefreshListener(new RefreshListener());
    }

    /**
     * 初始化listView
     */
    private void initListView(ArrayList<WifiDevice> devices) {
        deviceListAdapter = new DeviceListAdapter(devices);
        deviceListView.setAdapter(deviceListAdapter);
        registerForContextMenu(deviceListView);
    }

    /**
     * 设备列表适配器
     */
    private class DeviceListAdapter extends BaseAdapter {

        private ArrayList<WifiDevice> wifiDevices;

        public DeviceListAdapter(ArrayList<WifiDevice> devices) {
            this.wifiDevices = devices;
        }

        /**
         * 得到传感器列表
         */
        public ArrayList<WifiDevice> getWifiDevices() {
            return wifiDevices;
        }

        /**
         * 添加设备
         */
        public void add(WifiDevice wifiDevice) {
            if (getCount() == 0) {
                hiddenAddLayout.setVisibility(View.INVISIBLE);
            }
            wifiDevices.add(wifiDevice);
            notifyDataSetChanged();
        }

        /**
         * 修改设备名
         */
        public void modifyName(int index, String name) {
            getItem(index).setName(name);
            notifyDataSetChanged();
        }

        /**
         * 设置是否在线
         */
        public void modifyDeviceStatus(int index, int status) {
            getItem(index).setStatus(status);
            notifyDataSetChanged();
        }

        /**
         * 刷新状态
         * 把busy改为unOnline
         */
        public void refreshAllStatus() {
            for (WifiDevice device : wifiDevices) {
                if (device.getStatus() == BUSY)
                    device.setStatus(UNONLINE);
            }
            notifyDataSetChanged();
        }

        /**
         * 修改所有状态
         */
        public void modifyAllDeviceStatus(int status) {
            for (WifiDevice device : wifiDevices) {
                device.setStatus(status);
            }
            notifyDataSetChanged();
        }

        /**
         * 移除设备
         */
        public void remove(int index) {
            wifiDevices.remove(index);
            notifyDataSetChanged();
            if (getCount() == 0) {
                hiddenAddLayout.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public int getCount() {
            return wifiDevices.size();
        }

        @Override
        public WifiDevice getItem(int position) {
            return wifiDevices.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = LayoutInflater.from(DeviceActivity.this).inflate(R.layout.list_item_device, null);

                Switch deviceSwitch = (Switch) convertView.findViewById(R.id.device_switch);
                SeekBar deviceBar = (SeekBar) convertView.findViewById(R.id.device_bar);
                deviceBar.setEnabled(false);

                deviceSwitch.setOnCheckedChangeListener(new CommonButtonListener(position, deviceBar));
                deviceBar.setOnSeekBarChangeListener(new SeekBarListener(position));
            }
            TextView nameTxt = (TextView) convertView.findViewById(R.id.name_txt);
            TextView timeTxt = (TextView) convertView.findViewById(R.id.time_txt);
            TextView statusTxt = (TextView) convertView.findViewById(R.id.status_txt);
            ImageView statusImg = (ImageView) convertView.findViewById(R.id.status_img);
            Switch deviceSwitch = (Switch) convertView.findViewById(R.id.device_switch);
            WifiDevice device = deviceListAdapter.getItem(position);

            nameTxt.setText(device.getName());
            switch (device.getStatus()) {
                case ONLINE:
                    statusImg.setImageResource(R.drawable.led_on);
                    statusTxt.setText("");
                    deviceSwitch.setEnabled(true);
                    deviceSwitch.setChecked(true);
                    break;
                case BUSY:
                    statusImg.setImageResource(R.drawable.led_busy);
                    statusTxt.setText("(正在连接...)");
                    break;
                case UNONLINE:
                    statusTxt.setText("(设备未在线)");
                    statusImg.setImageResource(R.drawable.led_off);
                    deviceSwitch.setChecked(false);
                    deviceSwitch.setEnabled(false);
                    break;
                default:
                    break;
            }
            if(device.isTimeEnable()){
                if(device.isTimeOn()){
                    timeTxt.setText("将在"+device.getTimeFormat()+"调整亮度为"+device.getTimePwm()+"%");
                }else {
                    timeTxt.setText("将在"+device.getTimeFormat()+"关闭");
                }
            }else {
                timeTxt.setText("未设置定时");
            }
            return convertView;
        }

    }


}




