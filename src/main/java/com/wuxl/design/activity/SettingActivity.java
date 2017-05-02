package com.wuxl.design.activity;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.wuxl.design.R;

import static com.wuxl.design.common.utils.AppUtils.setStatusBarTransparent;

/**
 * Created by wuxingle on 2017/4/22 0022.
 * 设置界面
 */
public class SettingActivity extends AppCompatActivity{

    private Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarTransparent(this);
        setContentView(R.layout.activity_setting);

        initView();
        initToolBar();

        getFragmentManager().beginTransaction().replace(
                R.id.frame_layout,new SettingFragment()).commit();
    }

    /**
     * 初始化view
     */
    private void initView(){
        toolbar = (Toolbar)findViewById(R.id.toolbar);
    }

    /**
     * 初始化ToolBar
     * */
    private void initToolBar(){
        toolbar.setTitle("设置");//设置Toolbar标题

        toolbar.setNavigationIcon(android.R.drawable.ic_menu_revert);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public static class SettingFragment extends PreferenceFragment {

        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.setting_heads);
        }

    }

}
