package fragmentation.zhihu.fragment.third;

import android.os.Bundle;
import android.view.View;

import com.zsp.smartfragment.R;

import fragmentation.base.BaseFragment;

/**
 * @decs: ZhiHuThirdFragment
 * @author: 郑少鹏
 * @date: 2019/1/15 17:26
 */
public class ZhiHuThirdFragment extends BaseFragment {
    public static ZhiHuThirdFragment newInstance() {
        Bundle bundle = new Bundle();
        ZhiHuThirdFragment fragment = new ZhiHuThirdFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    /**
     * 布局ID
     *
     * @return 布局ID
     */
    @Override
    protected int layoutResId() {
        return R.layout.zhihu_fragment_third;
    }

    /**
     * EventBus注册
     * <p>
     * onDestroyView反注册。
     */
    @Override
    protected void eventBusRegister() {

    }

    /**
     * 初始UI
     * <p>
     * 此处仅设Toolbar标题、返回箭头等轻量UI操作。
     *
     * @param view 视图
     */
    @Override
    protected void stepUi(View view) {

    }

    /**
     * Fragment对用户可见时调
     */
    @Override
    protected void visibleToUser() {

    }

    /**
     * 开始加载
     * <p>
     * 此处设Listener、各Adapter、请求数据等。
     * onDestroyView释放。
     */
    @Override
    protected void startLoad() {

    }

    /**
     * Fragment对用户不可见时调
     */
    @Override
    protected void invisibleToUser() {

    }
}
