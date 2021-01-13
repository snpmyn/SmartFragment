package base;

import android.os.Bundle;

/**
 * Created on 2021/1/13
 *
 * @author zsp
 * @desc BaseActivity
 * 启应用后 {@link com.zsp.smartfragment.MainActivity} 直存至应用杀死时销毁。
 */
public abstract class BaseActivity extends com.zsp.fragmentation.BaseActivity {
    /**
     * 加载视图
     *
     * @param savedInstanceState 状态保存
     */
    @Override
    protected void initContentView(Bundle savedInstanceState) {

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

    }
}
