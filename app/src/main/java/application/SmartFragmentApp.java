package application;

import android.app.Application;

import com.zsp.smartfragment.BuildConfig;
import com.zsp.utilone.timber.configure.TimberInitConfigure;

/**
 * Created on 2019/7/5.
 *
 * @author 郑少鹏
 * @desc 应用
 */
public class SmartFragmentApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // 初始化配置
        initConfiguration();
    }

    /**
     * 初始化配置
     */
    private void initConfiguration() {
        // timber
        TimberInitConfigure.initTimber(BuildConfig.DEBUG);
        // fragmentation
        FragmentationInitConfig.initFragmentation();
    }
}
