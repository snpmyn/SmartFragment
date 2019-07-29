package fragmentation.base;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.appbar.MaterialToolbar;
import com.zsp.fragmentation.SupportFragment;
import com.zsp.smartfragment.R;
import com.zsp.utilone.activity.ActivitySuperviseManager;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import fragmentation.ISupportFragment;
import fragmentation.zhihu.fragment.first.ZhiHuFirstFragment;
import timber.log.Timber;

/**
 * Created on 2019/1/14.
 *
 * @author 郑少鹏
 * @desc BaseMainFragment
 * 用Fragment过程有些场景需懒加载，如FragmentAdapter懒加载、同级Fragment切换懒加载。库自0.8提供onLazyInitView(Bundle saveInstanceState)。
 * 该法于Fragment第一次对用户可见时（第一次onSupportVisible()）调。
 * View安全法（调时onCreateView()已被调）。
 * <p>
 * onSupportVisible()等生命周期调用顺序onActivityCreated() -> onResume() -> onSupportVisible -> onLazyInitView() -> onEnterAnimationEnd -> onSupportInvisible() -> onPause()。
 * Fragment可见时调（含嵌套子Fragment）。
 * A内子FragmentB，B通A startFragment(C)（此时A和C同级）。pop(C)都调A和B之onSupportVisible()。
 * View安全法（调时onCreateView()已被调）。
 * <p>
 * 复杂Fragment页面第一次start因该Fragment复杂初始和动画同进行致动画卡顿，库提供一解决方案onEnterAnimationEnd(Bundle saveInstanceState)。
 * 该法于转场动画结束后调，没动画则onActivityCreated调。此时于onEnterAnimationEnd(Bundle saveInstanceState)初始复杂数据以保Fragment动画流畅。
 * <p>
 * 竖直动画视觉上较横向动画流畅。
 * <p>
 * 按Back键后事件首传递至Activity内栈顶Fragment。该Fragment有子Fragment则传递至子栈内栈顶子Fragment，依次类推。
 * 栈顶子Fragment不处理该事件则向上传递（栈底还有子Fragment则向栈底传递，没则向父Fragment传递，最终至SupportActivity）。
 * <p>
 * startLoad于onLazyInitView打开新页面执行startLoad，打开已打开页面不再执行。
 * 于onCreateView初启应用执行所有页面逻辑操作，此后不再执行。
 * <p>
 * FragmentA（继承{@link SupportFragment}）嵌套FragmentB。
 * FragmentB通{@link #start(ISupportFragment)}开启FragmentC，FragmentB与FragmentC同栈且都嵌套于FragmentA。
 * 点返回键FragmentA出栈需复写{@link #onBackPressedSupport()}。
 * {@link #onBackPressedSupport()}中需{@link #pop()}不可_mActivity.onBackPressed()。
 * 其它处可_mActivity.onBackPressed()。
 */
public abstract class BaseFragment extends SupportFragment {
    private static final long WAIT_TIME = 2000L;
    /**
     * OnBackToFirstListener
     */
    private OnBackToFirstListener onBackToFirstListener;
    /**
     * Unbinder
     */
    private Unbinder unbinder;
    /**
     * 双击退出
     */
    private long touchDownTime = 0;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Timber.d("onAttach");
        if (context instanceof OnBackToFirstListener) {
            onBackToFirstListener = (OnBackToFirstListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implements OnBackToFirstListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Timber.d("onCreateView");
        View view = inflater.inflate(layoutResId(), container, false);
        // 返Unbinder解绑用
        // 此处this不可getActivity()
        unbinder = ButterKnife.bind(this, view);
        eventBusRegister();
        stepUi(view);
        return view;
    }

    /**
     * Called when the fragment is visible.
     * Fragment对用户可见时调
     * <p>
     * Is the combination of  [onHiddenChanged() + onResume()/onPause() + setUserVisibleHint()]
     */
    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        Timber.d("onSupportVisible");
        visibleToUser();
    }

    /**
     * Lazy initial，Called when fragment is first called.
     * <p>
     * 同级懒加载与ViewPager懒加载结合调。
     *
     * @param savedInstanceState savedInstanceState
     */
    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        Timber.d("onLazyInitView");
        startLoad();
    }

    /**
     * Called when the enter-animation end.
     * 入栈动画结束时调
     *
     * @param savedInstanceState savedInstanceState
     */
    @Override
    public void onEnterAnimationEnd(Bundle savedInstanceState) {
        super.onEnterAnimationEnd(savedInstanceState);
        Timber.d("onEnterAnimationEnd");
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
        if (getChildFragmentManager().getBackStackEntryCount() > 1) {
            Timber.d("onBackPressedSupport -> popChild");
            popChild();
        } else if (this instanceof ZhiHuFirstFragment) {
            Timber.d("onBackPressedSupport -> finish");
            // 第一Fragment退应用
            /*_mActivity.finish();*/
            appExit();
        } else {
            Timber.d("onBackPressedSupport -> onBackToFirstFragment");
            // 非第一则回第一Fragment
            onBackToFirstListener.onBackToFirstFragment();
        }
        return true;
    }

    /**
     * Called when the fragment is invisible.
     * Fragment对用户不可见时调
     * <p>
     * Is the combination of  [onHiddenChanged() + onResume()/onPause() + setUserVisibleHint()]
     */
    @Override
    public void onSupportInvisible() {
        super.onSupportInvisible();
        Timber.d("onSupportInvisible");
        invisibleToUser();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Timber.d("onDetach");
        onBackToFirstListener = null;
        unbinder.unbind();
    }

    /**
     * 布局ID
     *
     * @return 布局ID
     */
    protected abstract int layoutResId();

    /**
     * 初始ToolbarNavigation
     *
     * @param materialToolbar toolbar
     */
    protected void stepToolbarNavigation(MaterialToolbar materialToolbar) {
        materialToolbar.setNavigationIcon(R.drawable.back);
        materialToolbar.setNavigationOnClickListener(view -> fragmentationSupportActivity.onBackPressed());
    }

    /**
     * EventBus注册
     * <p>
     * onDestroyView反注册。
     */
    protected abstract void eventBusRegister();

    /**
     * 初始UI
     * <p>
     * 此处仅设Toolbar标题、返回箭头等轻量UI操作。
     *
     * @param view 视图
     */
    protected abstract void stepUi(View view);

    /**
     * Fragment对用户可见时调
     */
    protected abstract void visibleToUser();

    /**
     * 开始加载
     * <p>
     * 此处设Listener、各Adapter、请求数据等。
     * onDestroyView释放。
     */
    protected abstract void startLoad();

    /**
     * Fragment对用户不可见时调
     */
    protected abstract void invisibleToUser();

    /**
     * APP退出
     */
    private void appExit() {
        if (System.currentTimeMillis() - touchDownTime < WAIT_TIME) {
            ActivitySuperviseManager.appExit();
        } else {
            Toast.makeText(fragmentationSupportActivity, getString(R.string.exitAppHint), Toast.LENGTH_SHORT).show();
            touchDownTime = System.currentTimeMillis();
        }
    }

    public interface OnBackToFirstListener {
        /**
         * 回第一Fragment
         */
        void onBackToFirstFragment();
    }
}

