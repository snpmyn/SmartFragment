package fragmentation.zhihu.fragment.second.other;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;

import com.google.android.material.appbar.MaterialToolbar;
import com.zsp.smartfragment.R;
import com.zsp.utilone.toast.ToastUtils;

import butterknife.BindView;
import fragmentation.base.BaseFragment;

/**
 * @decs: ZhiHuModifyDetailFragment
 * @author: 郑少鹏
 * @date: 2019/1/22 9:28
 */
public class ZhiHuModifyDetailFragment extends BaseFragment {
    /**
     * Key
     */
    private static final String KEY_TITLE = "keyTitle";
    @BindView(R.id.toolbarMt)
    MaterialToolbar toolbarMt;
    @BindView(R.id.zhiHuModifyDetailEtInput)
    EditText zhiHuModifyDetailEtInput;
    @BindView(R.id.zhiHuModifyDetailMbModify)
    Button zhiHuModifyDetailMbModify;
    @BindView(R.id.zhiHuModifyDetailMbLaunch)
    Button zhiHuModifyDetailMbLaunch;
    /**
     * 标题
     */
    private String mTitle;

    public static ZhiHuModifyDetailFragment newInstance(String title) {
        Bundle bundle = new Bundle();
        ZhiHuModifyDetailFragment fragment = new ZhiHuModifyDetailFragment();
        bundle.putString(KEY_TITLE, title);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mTitle = args.getString(KEY_TITLE);
        }
    }

    /**
     * 布局ID
     *
     * @return 布局ID
     */
    @Override
    protected int layoutResId() {
        return R.layout.fragment_zhihu_modify_detail;
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
        toolbarMt.setTitle(getString(R.string.testStartForResult));
        stepToolbarNavigation(toolbarMt);
        zhiHuModifyDetailEtInput.setText(mTitle);
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
        // 显软键盘（调该法后于onPause自隐）
        /*showSoftInput(zhiHuModifyDetailEtInput);*/
        zhiHuModifyDetailMbModify.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString(ZhiHuDetailFragment.RESULT_KEY_TITLE, zhiHuModifyDetailEtInput.getText().toString());
            setFragmentResult(RESULT_OK, bundle);
            ToastUtils.shortShow(fragmentationSupportActivity, getString(R.string.modifySuccess));
        });
        zhiHuModifyDetailMbLaunch.setOnClickListener(v -> {
            /*start(CycleFragment.newInstance(1));*/
            ToastUtils.shortShow(fragmentationSupportActivity, "待完成");
        });
    }

    /**
     * Fragment对用户不可见时调
     */
    @Override
    protected void invisibleToUser() {
        // 通于hide隐软键盘
        hideSoftInput();
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
