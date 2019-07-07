package fragmentation.zhihu.fragment.second;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.zsp.smartfragment.R;

import fragmentation.base.BaseFragment;
import fragmentation.zhihu.fragment.second.child.ZhiHuViewPagerFragment;

/**
 * @decs: ZhiHuSecondFragment
 * @author: 郑少鹏
 * @date: 2019/1/15 17:24
 */
public class ZhiHuSecondFragment extends BaseFragment {
    public static ZhiHuSecondFragment newInstance() {
        Bundle bundle = new Bundle();
        ZhiHuSecondFragment fragment = new ZhiHuSecondFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (findChildFragment(ZhiHuViewPagerFragment.class) == null) {
            loadRootFragment(R.id.zhiHuSecondFragmentFl, ZhiHuViewPagerFragment.newInstance());
        }
    }

    /**
     * 布局ID
     *
     * @return 布局ID
     */
    @Override
    protected int layoutResId() {
        return R.layout.zhihu_fragment_second;
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

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        // Adapter场景下此处懒加载因同时加载子Fragment且子Fragment同时加载其Adapter内子Fragment致屏闪
        // Adapter场景下此处可不懒加载（因Adapter内子Fragment仅父Fragment是show时才Attach、Create）
    }
}
