package fragmentation.zhihu.fragment.second.other;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.zsp.smartfragment.R;
import com.zsp.utilone.toast.ToastUtils;

import butterknife.BindView;
import fragmentation.base.BaseFragment;

/**
 * @decs: ZhiHuDetailFragment
 * @author: 郑少鹏
 * @date: 2019/1/21 17:45
 */
public class ZhiHuDetailFragment extends BaseFragment {
    /**
     * RequestKey
     */
    static final String RESULT_KEY_TITLE = "requestKeyTitle";
    /**
     * Key
     */
    private static final String KEY_TITLE = "keyTitle";
    /**
     * RequestCode
     */
    private static final int REQUEST_CODE_MODIFY_FRAGMENT = 100;
    @BindView(R.id.toolbarMt)
    MaterialToolbar materialToolbar;
    @BindView(R.id.zhiHuDetailFragmentTv)
    TextView zhiHuDetailFragmentTv;
    @BindView(R.id.zhiHuDetailFragmentFab)
    FloatingActionButton zhiHuDetailFragmentFab;
    /**
     * 标题
     */
    private String mTitle;

    public static ZhiHuDetailFragment newInstance(String title) {
        ZhiHuDetailFragment fragment = new ZhiHuDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_TITLE, title);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mTitle = bundle.getString(KEY_TITLE);
        }
    }

    /**
     * 布局ID
     *
     * @return 布局ID
     */
    @Override
    protected int layoutResId() {
        return R.layout.fragment_zhihu_detail;
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
        materialToolbar.setTitle(mTitle);
        stepToolbarNavigation(materialToolbar);
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
        zhiHuDetailFragmentTv.setText(getString(R.string.testContent));
        zhiHuDetailFragmentFab.setOnClickListener(v -> startForResult(ZhiHuModifyDetailFragment.newInstance(mTitle), REQUEST_CODE_MODIFY_FRAGMENT));
    }

    /**
     * Fragment对用户不可见时调
     */
    @Override
    protected void invisibleToUser() {

    }

    /**
     * 较复杂Fragment页第一次start时致动画卡顿
     * Fragmentation提供onEnterAnimationEnd()（入栈动画结束时调）
     * 故onCreateView进行一些简单View初始化（如toolbar标题、返回按钮、显加载数据进度条等）
     * 然后onEnterAnimationEnd()进行复杂耗时初始化（如FragmentPagerAdapter初始化、加载数据等）
     *
     * @param savedInstanceState savedInstanceState
     */
    @Override
    public void onEnterAnimationEnd(Bundle savedInstanceState) {
        super.onEnterAnimationEnd(savedInstanceState);
        initDelayView();
    }

    private void initDelayView() {

    }

    @Override
    public void onFragmentResult(int requestCode, int resultCode, Bundle data) {
        super.onFragmentResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_MODIFY_FRAGMENT && resultCode == RESULT_OK && data != null) {
            mTitle = data.getString(RESULT_KEY_TITLE);
            materialToolbar.setTitle(mTitle);
            // 存改动内容
            if (getArguments() != null) {
                getArguments().putString(KEY_TITLE, mTitle);
            }
            ToastUtils.shortShow(fragmentationSupportActivity, getString(R.string.modifySuccess));
        }
    }

    /**
     * 处理回退事件
     * <p>
     * 返true消费该事件，不再向上传递。
     * 返false向上最终传递至Fragment宿主Activity。此时宿主Activity复写onBackPressedSupport则执行，没复写不执行。
     * Fragment宿主Activity之基类复写onKeyUp时同执行。
     *
     * @return boolean
     */
    @Override
    public boolean onBackPressedSupport() {
        pop();
        return true;
    }
}
