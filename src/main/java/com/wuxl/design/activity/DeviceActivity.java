package com.wuxl.design.activity;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
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
import com.wuxl.design.connect.protocol.DataProtocol;
import com.wuxl.design.model.WifiDevice;
import com.wuxl.design.model.WifiDeviceConnectManager;
import com.wuxl.design.model.WifiListener;

import java.util.ArrayList;

import static com.wuxl.design.utils.AppUtils.setStatusBarTransparent;
import static com.wuxl.design.utils.DataUtils.toByte;

/**
 * 设备界面
 * Created by wuxingle on 2017/4/10 0010.
 */
public class DeviceActivity extends AppCompatActivity
        implements Toolbar.OnMenuItemClickListener {

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
    private int currentSelected;

    private String ip;

    private int port;

    //wifi连接的管理
    private WifiDeviceConnectManager deviceManager;

    private WifiListener listener = new WifiListener() {
        @Override
        public void canConnect() {
            Log.i(TAG, "可以连接");
            deviceManager.connect(ip, port);
        }
    };


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
        //deviceManager.ready();
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
        //todo
        //deviceManager.close();
        Log.i(TAG, "device activity destroy");
    }

    /**
     * 刷新时的监听器
     */
    private class RefreshListener extends MaterialRefreshListener {
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

        devices.add(new WifiDevice(null, "哈哈"));
        devices.add(new WifiDevice(null, "呵呵"));

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
        public void modifyName(int index,String name){
            getItem(index).setName(name);
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(DeviceActivity.this).inflate(R.layout.list_item_device, null);
            }
            TextView nameTxt = (TextView) convertView.findViewById(R.id.name_txt);
            Switch deviceSwitch = (Switch) convertView.findViewById(R.id.device_switch);
            SeekBar deviceBar = (SeekBar) convertView.findViewById(R.id.device_bar);

            nameTxt.setText(getItem(position).getName());
            //todo
            deviceSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        Log.i(TAG, "led打开1");
                    } else {
                        Log.i(TAG, "led关闭");
                    }
                }
            });
            deviceBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    Log.d(TAG, "bar:" + progress);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    Log.d(TAG, "bar start");
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    Log.d(TAG, "bar end");
                }
            });

            return convertView;
        }
    }


}




