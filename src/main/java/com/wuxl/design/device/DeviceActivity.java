package com.wuxl.design.device;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.wuxl.design.R;

import java.util.Timer;
import java.util.TimerTask;

import static com.wuxl.design.utils.AppUtils.setStatusBarTransparent;

/**
 * 设备界面
 * Created by wuxingle on 2017/4/10 0010.
 */
public class DeviceActivity extends AppCompatActivity{


    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private MaterialRefreshLayout refreshLayout;
    
    //test refresh
    private Timer timer = new Timer();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarTransparent(this);
        setContentView(R.layout.activity_device);

        initView();
        initToolBar();
        initRefreshView();
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
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    refreshLayout.finishRefresh();
                }
            },2000);
        }
    }

    /**
     * 初始化组件
     */
    private void initView(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawerLayout = (DrawerLayout)findViewById(R.id.device_drawerlayout);
        refreshLayout = (MaterialRefreshLayout)findViewById(R.id.refresh_layout);
    }

    /**
     * 初始化toolBar和drawerLayout
     * */
    private void initToolBar(){
        toolbar.setTitle("智能灯");//设置Toolbar标题
        toolbar.setSubtitle("未登录");
        toolbar.setSubtitleTextColor(Color.WHITE);

        toolbar.inflateMenu(R.menu.device_menu);
        //toolbar.setOnMenuItemClickListener(this);
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
}




