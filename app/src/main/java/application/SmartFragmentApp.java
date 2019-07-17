package application;

import android.app.Application;

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
        FragmentationInitConfig.initFragmentation();
    }
}
