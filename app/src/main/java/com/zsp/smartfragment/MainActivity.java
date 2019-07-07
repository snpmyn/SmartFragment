package com.zsp.smartfragment;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.zsp.utilone.intent.IntentUtils;

import butterknife.ButterKnife;
import butterknife.OnClick;
import fragmentation.zhihu.activity.ZhiHuActivity;

/**
 * @decs: 主页
 * @author: 郑少鹏
 * @date: 2019/5/18 20:27
 */
public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.mainActivityMbZhiHu)
    public void onViewClicked(View view) {
        if (view.getId() == R.id.mainActivityMbZhiHu) {
            IntentUtils.jumpNoBundle(this, ZhiHuActivity.class);
        }
    }
}
