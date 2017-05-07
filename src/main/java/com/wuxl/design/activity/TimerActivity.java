package com.wuxl.design.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.wuxl.design.R;
import com.wuxl.design.wifidevice.WifiDevice;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import static com.wuxl.design.common.utils.AppUtils.setStatusBarTransparent;

/**
 * Created by wuxingle on 2017/5/6 0006.
 * 定时设置的界面
 */
public class TimerActivity extends AppCompatActivity {

    private static final String TAG = "TimerActivity";

    //
    public static final int TIMER_RESULT_OK = 1000;
    public static final int TIMER_RESULT_NO = 1001;

    private Toolbar toolbar;
    private Switch timeSwitch;
    private Switch ledSwitch;
    private Button dateBtn;
    private TextView dateText;
    private Button timeBtn;
    private TextView timeText;
    private TextView lightText;
    private SeekBar lightBar;

    private DatePickerDialog dateDialog;
    private TimePickerDialog timeDialog;

    private Calendar calendar;
    private int pwm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarTransparent(this);
        setContentView(R.layout.activity_timer);

        Bundle bundle = getIntent().getExtras();
        WifiDevice device = bundle.getParcelable("device");

        Log.i(TAG,"设备为:"+device);

        initView();
        initToolBar();

        initCalendarAndView(device);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        setResult(TIMER_RESULT_NO);
        super.onBackPressed();
    }

    /**
     * 日期选择按钮
     */
    public void openDateSelector(View v) {
        if (dateDialog == null) {
            dateDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    calendar.set(year,monthOfYear,dayOfMonth);
                    dateText.setText(getDate(year, monthOfYear + 1, dayOfMonth));
                }
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        }
        dateDialog.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        dateDialog.show();
    }

    /**
     * 时间选择按钮
     */
    public void openTimeSelector(View v) {

        if (timeDialog == null) {
            timeDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    calendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
                    calendar.set(Calendar.MINUTE,minute);
                    calendar.set(Calendar.SECOND,0);
                    timeText.setText(getTime(hourOfDay, minute));
                }
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
        }
        timeDialog.updateTime(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
        timeDialog.show();
    }

    /**
     * 点击yes按钮
     */
    public void okOnTime(View v) {
        if(checkDateTime()){
            Toast.makeText(this,"定时设置成功",Toast.LENGTH_SHORT).show();
            setResultOK();
            finish();
        }else {
            Toast.makeText(this,"请选择合适的时间",Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 设置结果码
     */
    private void setResultOK(){
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        if(!timeSwitch.isChecked()){
            bundle.putBoolean("timeEnable",false);
        }else {
            bundle.putBoolean("timeEnable",true);
            bundle.putLong("time",calendar.getTimeInMillis());
            if(ledSwitch.isChecked()){
                bundle.putBoolean("timeOn",true);
                bundle.putInt("timePwm",pwm);
            }else {
                bundle.putBoolean("timeOn",false);
            }
        }
        intent.putExtras(bundle);
        setResult(TIMER_RESULT_OK,intent);
    }

    /**
     * 根据设备初始化界面
     * @param device 设备
     */
    private void initCalendarAndView(WifiDevice device){
        calendar = new GregorianCalendar();
        if(device.isTimeEnable()){
            timeSwitch.setChecked(true);
            enableView(true);
            //yyyy:MM:dd:HH:mm
            long time = device.getTime();
            calendar.setTimeInMillis(time);
            //如果是定时开
            if(device.isTimeOn()){
                ledSwitch.setChecked(true);
                lightBar.setEnabled(true);
                lightBar.setProgress(device.getTimePwm());
                lightText.setText(String.valueOf(device.getTimePwm()));
            }else{
                ledSwitch.setChecked(false);
                lightBar.setEnabled(false);
            }

            dateText.setText(getDate(calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH) + 1,
                    calendar.get(Calendar.DAY_OF_MONTH)));
            timeText.setText(getTime(calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE)));

        }else {
            timeSwitch.setChecked(false);
            enableView(false);
        }
    }

    /**
     * 返回日期
     */
    private String getDate(int year, int month, int day) {
        return year + "-" + month + "-" + day;
    }

    /**
     * 返回时间字符串
     * 0~5   凌晨
     * 6~10  早上
     * 11~12 中午
     * 13~18 下午
     * 19~23 晚上
     */
    private String getTime(int hour, int minute) {
        String msg;
        if (hour <= 5) {
            msg = "凌晨";
        } else if (hour <= 10) {
            msg = "早上";
        } else if (hour <= 12) {
            msg = "中午";
        } else if (hour <= 18) {
            msg = "下午";
        } else {
            msg = "晚上";
        }
        return String.format(Locale.CHINA, "%s %02d:%02d", msg, hour, minute);
    }


    /**
     * 检查设置的日期和时间
     * 30*24*3600*1000 = 2592000000L
     */
    private boolean checkDateTime(){

        long now = System.currentTimeMillis();

        return calendar.getTimeInMillis() > now
                && calendar.getTimeInMillis() <= now + 2592000000L;
    }

    /**
     * 使能组件
     */
    private void enableView(boolean enable) {
        ledSwitch.setEnabled(enable);
        dateBtn.setEnabled(enable);
        timeBtn.setEnabled(enable);
        lightBar.setEnabled(enable && ledSwitch.isChecked());
    }

    /**
     * 初始化组件
     */
    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        timeSwitch = (Switch) findViewById(R.id.time_switch);
        ledSwitch = (Switch) findViewById(R.id.led_switch);
        dateText = (TextView) findViewById(R.id.date_tv);
        dateBtn = (Button) findViewById(R.id.date_btn);
        timeText = (TextView) findViewById(R.id.time_tv);
        timeBtn = (Button) findViewById(R.id.time_btn);
        lightText = (TextView) findViewById(R.id.light_tv);
        lightBar = (SeekBar) findViewById(R.id.light_bar);

        timeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                enableView(isChecked);
                if(!isChecked){
                    Toast.makeText(TimerActivity.this,"定时已取消",Toast.LENGTH_SHORT).show();
                }
            }
        });

        ledSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                lightBar.setEnabled(isChecked);
            }
        });

        lightBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                lightText.setText(String.valueOf(progress));
                pwm = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });


    }


    /**
     * 初始化ToolBar
     */
    private void initToolBar() {
        toolbar.setTitle("定时");//设置Toolbar标题

        toolbar.setNavigationIcon(android.R.drawable.ic_menu_revert);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(TIMER_RESULT_NO);
                finish();
            }
        });
    }


}










