package com.wuxl.design.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.wuxl.design.R;

/**
 * Created by wuxingle on 2017/4/22 0022.
 * 扫一扫界面
 */
public class ScanActivity extends AppCompatActivity{

    private static final String TAG = "ScanActivity";

    private DecoratedBarcodeView barcodeView;

    private CaptureManager captureManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        initView();

        captureManager = new CaptureManager(this, barcodeView);
        captureManager.initializeFromIntent(getIntent(),savedInstanceState);
        captureManager.decode();

        Log.i(TAG,"开始扫描");
    }


    @Override
    protected void onPause() {
        super.onPause();
        captureManager.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        captureManager.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        captureManager.onDestroy();
    }


    /**
     * 组件初始化
     */
    private void initView(){
        barcodeView = (DecoratedBarcodeView)findViewById(R.id.barcode);
    }

}
