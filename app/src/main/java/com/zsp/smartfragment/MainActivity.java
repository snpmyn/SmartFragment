package com.zsp.smartfragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;

import com.zsp.utilone.intent.IntentUtils;
import com.zsp.utilone.permission.SoulPermissionUtils;
import com.zsp.utilone.toast.ToastUtils;

import base.BaseActivity;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fragmentation.zhihu.activity.ZhiHuActivity;

/**
 * @decs: 主页
 * @author: 郑少鹏
 * @date: 2019/5/18 20:27
 */
public class MainActivity extends BaseActivity {
    private SoulPermissionUtils soulPermissionUtils;

    /**
     * 加载视图
     *
     * @param savedInstanceState 状态保存
     */
    @Override
    protected void initContentView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    /**
     * 初始控件
     */
    @Override
    protected void stepUi() {

    }

    /**
     * 初始配置
     */
    @Override
    protected void initConfiguration() {
        soulPermissionUtils = new SoulPermissionUtils();
    }

    /**
     * 设置监听
     */
    @Override
    protected void setListener() {

    }

    /**
     * 开始逻辑
     */
    @Override
    protected void startLogic() {
        soulPermissionUtils.checkAndRequestPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, soulPermissionUtils,
                true, new SoulPermissionUtils.CheckAndRequestPermissionCallBack() {
                    @Override
                    public void onPermissionOk() {

                    }

                    @Override
                    public void onPermissionDeniedNotRationaleInMiUi(String s) {
                        ToastUtils.shortShow(MainActivity.this, s);
                    }

                    @Override
                    public void onPermissionDeniedNotRationaleWithoutLoopHint(String s) {

                    }
                });
    }

    @SuppressLint("NonConstantResourceId")
    @OnClick(R.id.mainActivityMbZhiHu)
    public void onViewClicked(View view) {
        if (view.getId() == R.id.mainActivityMbZhiHu) {
            IntentUtils.jumpNoBundle(this, ZhiHuActivity.class);
        }
    }
}

