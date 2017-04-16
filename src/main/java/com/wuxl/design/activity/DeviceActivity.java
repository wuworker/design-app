package com.wuxl.design.activity;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.wuxl.design.R;
import com.wuxl.design.connect.protocol.DataProtocol;
import com.wuxl.design.model.WifiDevice;
import com.wuxl.design.model.WifiDeviceConnectManager;
import com.wuxl.design.model.WifiListener;

import java.util.ArrayList;
import java.util.Arrays;

import static com.wuxl.design.model.WifiDevice.BUSY;
import static com.wuxl.design.model.WifiDevice.ONLINE;
import static com.wuxl.design.model.WifiDevice.UNONLINE;
import static com.wuxl.design.utils.AppUtils.setStatusBarTransparent;
import static com.wuxl.design.utils.DataUtils.toByte;

/**
 * 设备界面
 * Created by wuxingle on 2017/4/10 0010.
 */
public class DeviceActivity extends AppCompatActivity
        implements Toolbar.OnMenuItemClickListener{

    private static final String TAG = "DeviceActivity";

    private static final int REFRESH_OVER = 1;

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

    //在线设备数
    private int onlineCount = 0;

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case REFRESH_OVER:
                    Log.i(TAG, "handler到达");
                    refreshLayout.finishRefresh();
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

        initView();
        initToolBar();
        initRefreshView();
        initListView();

        initAdditionDialog();
        initModifyDialog();

        deviceManager = WifiDeviceConnectManager.getInstance(this);
        deviceManager.setListener(listener);
        deviceManager.ready();
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
        Log.i(TAG, "device activity destroy");
    }

    /**
     * 是否可连接
     */
    private WifiListener listener = new WifiListener() {

        @Override
        public void canConnect() {
            Log.i(TAG, "可以连接");
            deviceManager.connect(ip, port);
        }

        @Override
        public void isOnline(String hexId) {
            if(onlineCount==deviceListAdapter.getCount()){
                refreshLayout.finishRefresh();
            }
            for (int i = 0; i < deviceListAdapter.getCount(); i++) {
                WifiDevice device = deviceListAdapter.getItem(i);
                if (device.getStatus()!=ONLINE &&
                        hexId.equals(device.getHexId())) {
                    //修改状态但不更新
                    deviceListAdapter.getItem(i).setStatus(ONLINE);
                    onlineCount++;
                }
            }
        }
    };

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
                additionDialog.show();
                break;
        }
        return true;
    }

    /**
     * listView的context菜单
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        //menu.setHeaderTitle("人物简介");
        //添加菜单项
        menu.add(0, Menu.FIRST, 0, "修改设备名");
        menu.add(0, Menu.FIRST + 1, 0, "删除设备");
        menu.add(0, Menu.FIRST + 2, 0, "查看详情");
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    /**
     * listView的菜单点击事件
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
                deviceListAdapter.remove(currentSelected);
                break;
            case Menu.FIRST + 2:
                Log.i(TAG, "点击了第3项,list:" + info.position);
                break;
            default:
                break;
        }
        return super.onContextItemSelected(item);
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
            switch (progress) {
                case 0:
                    deviceManager.off(device);
                    break;
                case 100:
                    deviceManager.on(device);
                    break;
                default:
                    deviceManager.setPwm(device, progress);
                    break;
            }
            Log.i(TAG, "seekBar改变" + progress);
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
     * 刷新时的监听器
     */
    private class RefreshListener extends MaterialRefreshListener {
        @Override
        public void onfinish() {
            super.onfinish();
            deviceListAdapter.refreshAllStatus();
        }

        @Override
        public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
            super.onRefreshLoadMore(materialRefreshLayout);
        }

        @Override
        public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
            //修改状态
            deviceListAdapter.modifyAllDeviceStatus(BUSY);
            //发送数据
            for (int i = 0; i < deviceListAdapter.getCount(); i++) {
                WifiDevice device = deviceListAdapter.getItem(i);
                deviceManager.isOnline(device);
            }
            //3秒后停止
            handler.sendEmptyMessageDelayed(REFRESH_OVER, 3000);
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
                        byte[] origin = toByte(idHex);
                        if (origin.length < DataProtocol.ORIGIN_LENGTH) {
                            Log.i(TAG, "输入错误");
                            //test
                            //todo cancel
                            WifiDevice wifiDevice = new WifiDevice(origin, idHex);
                            deviceListAdapter.add(wifiDevice);
                            Log.i(TAG, "添加了设备");
                        } else {
                            WifiDevice wifiDevice = new WifiDevice(origin, idHex);
                            deviceListAdapter.add(wifiDevice);
                            Log.i(TAG, "添加了设备");
                        }
                        Log.i(TAG, "点击了确定");
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
                        Log.i(TAG, "点击了确定");
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                modifyDialog.dismiss();
                editText.setText("");
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
    private void initListView() {
        ArrayList<WifiDevice> devices = new ArrayList<>();

        byte[] target1 = new byte[6];
        Arrays.fill(target1,(byte)0xab);
        byte[] target2 = new byte[6];
        Arrays.fill(target2,(byte)0xef);

        devices.add(new WifiDevice(target1, "哈哈"));
        devices.add(new WifiDevice(target2, "呵呵"));

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
            TextView statusTxt = (TextView) convertView.findViewById(R.id.status_txt);
            ImageView statusImg = (ImageView) convertView.findViewById(R.id.status_img);
            Switch deviceSwitch = (Switch) convertView.findViewById(R.id.device_switch);
            WifiDevice device = deviceListAdapter.getItem(position);

            nameTxt.setText(device.getName());
            deviceSwitch.setEnabled(device.getStatus() == ONLINE);
            switch (device.getStatus()) {
                case ONLINE:
                    statusImg.setImageResource(R.drawable.led_on);
                    statusTxt.setText("");
                    break;
                case BUSY:
                    statusImg.setImageResource(R.drawable.led_busy);
                    statusTxt.setText("(正在连接...)");
                    break;
                case UNONLINE:
                    statusTxt.setText("(设备未在线)");
                    statusImg.setImageResource(R.drawable.led_off);
                    break;
                default:
                    break;
            }

            return convertView;
        }

    }


}




