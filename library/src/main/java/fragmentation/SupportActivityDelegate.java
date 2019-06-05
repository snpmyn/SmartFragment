package fragmentation;

import androidx.annotation.DrawableRes;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentationMagician;

import fragmentation.animation.DefaultVerticalAnimator;
import fragmentation.animation.FragmentAnimator;
import fragmentation.debug.DebugStackDelegate;
import fragmentation.queue.BaseAction;

/**
 * @decs: SupportActivityDelegate
 * @author: 郑少鹏
 * @date: 2019/5/20 9:48
 */
public class SupportActivityDelegate {
    boolean mPopMultipleNoAnim = false;
    boolean mFragmentClickable = true;
    private ISupportActivity mSupport;
    private FragmentActivity mActivity;
    private TransactionDelegate mTransactionDelegate;
    private FragmentAnimator mFragmentAnimator;
    private int mDefaultFragmentBackground = 0;
    private DebugStackDelegate mDebugStackDelegate;

    public SupportActivityDelegate(ISupportActivity support) {
        if (!(support instanceof FragmentActivity)) {
            throw new RuntimeException("Must extends FragmentActivity/AppCompatActivity");
        }
        this.mSupport = support;
        this.mActivity = (FragmentActivity) support;
    }

    /**
     * Perform some extra transactions.
     * 额外的事务：自定义Tag，添加SharedElement动画，操作非回退栈Fragment
     */
    public BaseExtraTransaction extraTransaction() {
        return new BaseExtraTransaction.BaseExtraTransactionImpl<>((FragmentActivity) mSupport, getTopFragment(), getTransactionDelegate(), true);
    }

    public void onCreate() {
        mTransactionDelegate = getTransactionDelegate();
        mDebugStackDelegate = new DebugStackDelegate(mActivity);
        mFragmentAnimator = mSupport.onCreateFragmentAnimator();
        mDebugStackDelegate.onCreate(Fragmentation.getDefault().getMode());
    }

    TransactionDelegate getTransactionDelegate() {
        if (mTransactionDelegate == null) {
            mTransactionDelegate = new TransactionDelegate(mSupport);
        }
        return mTransactionDelegate;
    }

    public void onPostCreate() {
        mDebugStackDelegate.onPostCreate(Fragmentation.getDefault().getMode());
    }

    /**
     * 获取设置的全局动画copy
     *
     * @return FragmentAnimator
     */
    public FragmentAnimator getFragmentAnimator() {
        return mFragmentAnimator.copy();
    }

    /**
     * Set all fragments animation.
     * 设置Fragment内的全局动画
     */
    public void setFragmentAnimator(FragmentAnimator fragmentAnimator) {
        this.mFragmentAnimator = fragmentAnimator;
        for (Fragment fragment : FragmentationMagician.getActiveFragments(getSupportFragmentManager())) {
            if (fragment instanceof ISupportFragment) {
                ISupportFragment iF = (ISupportFragment) fragment;
                SupportFragmentDelegate delegate = iF.getSupportDelegate();
                if (delegate.mAnimByActivity) {
                    delegate.mFragmentAnimator = fragmentAnimator.copy();
                    if (delegate.mAnimHelper != null) {
                        delegate.mAnimHelper.notifyChanged(delegate.mFragmentAnimator);
                    }
                }
            }
        }
    }

    /**
     * Set all fragments animation.
     * 构建Fragment转场动画
     * <p/>
     * 如果是在Activity内实现，则构建的是Activity内所有Fragment的转场动画，
     * 如果是在Fragment内实现，则构建的是该Fragment的转场动画，此时优先>Activity的onCreateFragmentAnimator()
     *
     * @return FragmentAnimator对象
     */
    public FragmentAnimator onCreateFragmentAnimator() {
        return new DefaultVerticalAnimator();
    }

    int getDefaultFragmentBackground() {
        return mDefaultFragmentBackground;
    }

    /**
     * 当Fragment根布局没有设定background属性时，
     * Fragmentation默认使用Theme的android:windowbackground作为Fragment的背景，
     * 可以通过该方法改变Fragment背景。
     */
    public void setDefaultFragmentBackground(@DrawableRes int backgroundRes) {
        mDefaultFragmentBackground = backgroundRes;
    }

    /**
     * 显示栈视图dialog，调试时使用
     */
    void showFragmentStackHierarchyView() {
        mDebugStackDelegate.showFragmentStackHierarchyView();
    }

    /**
     * 显示栈视图日志，调试时使用
     */
    void logFragmentStackHierarchy(String tag) {
        mDebugStackDelegate.logFragmentRecords(tag);
    }

    /**
     * Causes the Runnable r to be added to the action queue.
     * <p>
     * The runnable will be run after all the previous action has been run.
     * <p>
     * 前面的事务全部执行后执行该Action
     */
    public void post(final Runnable runnable) {
        mTransactionDelegate.post(runnable);
    }

    /**
     * 不建议复写该方法，请使用{@link #onBackPressedSupport}代替
     */
    public void onBackPressed() {
        mTransactionDelegate.mActionQueue.enqueue(new BaseAction(BaseAction.ACTION_BACK) {
            @Override
            public void run() {
                if (!mFragmentClickable) {
                    mFragmentClickable = true;
                }
                // 获取activeFragment：即从栈顶开始状态为show的那个Fragment
                ISupportFragment activeFragment = SupportHelper.getActiveFragment(getSupportFragmentManager());
                if (mTransactionDelegate.dispatchBackPressedEvent(activeFragment)) {
                    return;
                }
                mSupport.onBackPressedSupport();
            }
        });
    }

    /**
     * 该方法回调时机为，Activity回退栈内Fragment的数量小于等于1时，默认finish Activity
     * 请尽量复写该方法，避免复写onBackPress()，以保证SupportFragment内的onBackPressedSupport()回退事件正常执行
     */
    public void onBackPressedSupport() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            pop();
        } else {
            ActivityCompat.finishAfterTransition(mActivity);
        }
    }

    public void onDestroy() {
        mDebugStackDelegate.onDestroy();
    }

    public boolean dispatchTouchEvent() {
        // 防抖动（防止点击速度过快）
        return !mFragmentClickable;
    }

    /********************************************************************************************/

    /**
     * 加载根Fragment，即Activity内的第一个Fragment或Fragment内的第一个子Fragment
     */
    public void loadRootFragment(int containerId, ISupportFragment toFragment) {
        loadRootFragment(containerId, toFragment, true, false);
    }

    public void loadRootFragment(int containerId, ISupportFragment toFragment, boolean addToBackStack, boolean allowAnimation) {
        mTransactionDelegate.loadRootTransaction(getSupportFragmentManager(), containerId, toFragment, addToBackStack, allowAnimation);
    }

    /**
     * 加载多个同级根Fragment，类似WeChat、QQ主页的场景
     */
    public void loadMultipleRootFragment(int containerId, int showPosition, ISupportFragment... toFragments) {
        mTransactionDelegate.loadMultipleRootTransaction(getSupportFragmentManager(), containerId, showPosition, toFragments);
    }

    /**
     * show一个Fragment，hide其他同栈所有Fragment
     * 使用该方法时，要确保同级栈内无多余的Fragment（只有通过loadMultipleRootFragment()载入的Fragment）
     * <p>
     * 建议使用更明确的{@link #showHideFragment(ISupportFragment, ISupportFragment)}
     *
     * @param showFragment 需要show的Fragment
     */
    public void showHideFragment(ISupportFragment showFragment) {
        showHideFragment(showFragment, null);
    }

    /**
     * show一个Fragment，hide一个Fragment；主要用于类似微信主页那种切换tab的情况
     *
     * @param showFragment 需要show的Fragment
     * @param hideFragment 需要hide的Fragment
     */
    public void showHideFragment(ISupportFragment showFragment, ISupportFragment hideFragment) {
        mTransactionDelegate.showHideFragment(getSupportFragmentManager(), showFragment, hideFragment);
    }

    public void start(ISupportFragment toFragment) {
        start(toFragment, ISupportFragment.STANDARD);
    }

    /**
     * @param launchMode Similar to Activity's LaunchMode.
     */
    public void start(ISupportFragment toFragment, @ISupportFragment.LaunchMode int launchMode) {
        mTransactionDelegate.dispatchStartTransaction(getSupportFragmentManager(), getTopFragment(), toFragment, 0, launchMode, TransactionDelegate.TYPE_ADD);
    }

    /**
     * Launch an fragment for which you would like a result when it poped.
     */
    public void startForResult(ISupportFragment toFragment, int requestCode) {
        mTransactionDelegate.dispatchStartTransaction(getSupportFragmentManager(), getTopFragment(), toFragment, requestCode, ISupportFragment.STANDARD, TransactionDelegate.TYPE_ADD_RESULT);
    }

    /**
     * Start the target Fragment and pop itself.
     */
    public void startWithPop(ISupportFragment toFragment) {
        mTransactionDelegate.startWithPop(getSupportFragmentManager(), getTopFragment(), toFragment);
    }

    public void startWithPopTo(ISupportFragment toFragment, Class<?> targetFragmentClass, boolean includeTargetFragment) {
        mTransactionDelegate.startWithPopTo(getSupportFragmentManager(), getTopFragment(), toFragment, targetFragmentClass.getName(), includeTargetFragment);
    }

    public void replaceFragment(ISupportFragment toFragment, boolean addToBackStack) {
        mTransactionDelegate.dispatchStartTransaction(getSupportFragmentManager(), getTopFragment(), toFragment, 0, ISupportFragment.STANDARD, addToBackStack ? TransactionDelegate.TYPE_REPLACE : TransactionDelegate.TYPE_REPLACE_DO_NOT_BACK);
    }

    /**
     * Pop the child fragment.
     */
    public void pop() {
        mTransactionDelegate.pop(getSupportFragmentManager());
    }

    /**
     * Pop the last fragment transition from the manager's fragment
     * back stack.
     * <p>
     * 出栈至目标Fragment。
     *
     * @param targetFragmentClass   目标Fragment
     * @param includeTargetFragment 含目标Fragment否
     */
    public void popTo(Class<?> targetFragmentClass, boolean includeTargetFragment) {
        popTo(targetFragmentClass, includeTargetFragment, null);
    }

    /**
     * If you want to begin another FragmentTransaction immediately after popTo(), use this method.
     * 若你想出栈后立刻FragmentTransaction操作，用该法。
     */
    public void popTo(Class<?> targetFragmentClass, boolean includeTargetFragment, Runnable afterPopTransactionRunnable) {
        popTo(targetFragmentClass, includeTargetFragment, afterPopTransactionRunnable, TransactionDelegate.DEFAULT_POP_TO_ANIM);
    }

    public void popTo(Class<?> targetFragmentClass, boolean includeTargetFragment, Runnable afterPopTransactionRunnable, int popAnim) {
        mTransactionDelegate.popTo(targetFragmentClass.getName(), includeTargetFragment, afterPopTransactionRunnable, getSupportFragmentManager(), popAnim);
    }

    private FragmentManager getSupportFragmentManager() {
        return mActivity.getSupportFragmentManager();
    }

    private ISupportFragment getTopFragment() {
        return SupportHelper.getTopFragment(getSupportFragmentManager());
    }
}
