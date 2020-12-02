package fragmentation;

import android.os.Bundle;
import android.view.MotionEvent;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
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
    private final ISupportActivity mSupport;
    private final FragmentActivity mActivity;
    boolean mPopMultipleNoAnim = false;
    boolean mFragmentClickable = true;
    private TransactionDelegate mTransactionDelegate;
    private FragmentAnimator mFragmentAnimator;
    private int mDefaultFragmentBackground = 0;
    private final DebugStackDelegate mDebugStackDelegate;

    public SupportActivityDelegate(ISupportActivity support) {
        if (!(support instanceof FragmentActivity)) {
            throw new RuntimeException("Must extends FragmentActivity/AppCompatActivity");
        }
        this.mSupport = support;
        this.mActivity = (FragmentActivity) support;
        this.mDebugStackDelegate = new DebugStackDelegate(this.mActivity);
    }

    /**
     * extraTransaction
     * <p>
     * Perform some extra transactions.
     * 自定Tag添SharedElement动画，操作非回退栈Fragment。
     *
     * @return BaseExtraTransaction
     */
    public BaseExtraTransaction extraTransaction() {
        return new BaseExtraTransaction.BaseExtraTransactionImpl<>((FragmentActivity) mSupport, getTopFragment(), getTransactionDelegate(), true);
    }

    public void onCreate(@Nullable Bundle savedInstanceState) {
        mTransactionDelegate = getTransactionDelegate();
        mFragmentAnimator = mSupport.onCreateFragmentAnimator();
        mDebugStackDelegate.onCreate(Fragmentation.getDefault().getMode());
    }

    TransactionDelegate getTransactionDelegate() {
        if (mTransactionDelegate == null) {
            mTransactionDelegate = new TransactionDelegate(mSupport);
        }
        return mTransactionDelegate;
    }

    public void onPostCreate(@Nullable Bundle savedInstanceState) {
        mDebugStackDelegate.onPostCreate(Fragmentation.getDefault().getMode());
    }

    /**
     * 获所设全局动画
     *
     * @return FragmentAnimator
     */
    public FragmentAnimator getFragmentAnimator() {
        return mFragmentAnimator.copy();
    }

    /**
     * Set all fragments animation.
     *
     * @param fragmentAnimator FragmentAnimator
     */
    public void setFragmentAnimator(FragmentAnimator fragmentAnimator) {
        this.mFragmentAnimator = fragmentAnimator;
        for (Fragment fragment : FragmentationMagician.getActiveFragments(getSupportFragmentManager())) {
            if (fragment instanceof ISupportFragment) {
                ISupportFragment iSupportFragment = (ISupportFragment) fragment;
                SupportFragmentDelegate delegate = iSupportFragment.getSupportDelegate();
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
     * onCreateFragmentAnimator
     * <p>
     * 构建Fragment转场动画。
     * <p>
     * Activity内实现构建Activity内所有Fragment转场动画。
     * Fragment内实现构建该Fragment转场动画，此时优先Activity之onCreateFragmentAnimator()。
     *
     * @return FragmentAnimator
     */
    public FragmentAnimator onCreateFragmentAnimator() {
        return new DefaultVerticalAnimator();
    }

    int getDefaultFragmentBackground() {
        return mDefaultFragmentBackground;
    }

    /**
     * Fragment默背景
     * <p>
     * Fragment根布局没设background属性时背景默Theme之android:windowBackground。
     * 通该法可改其内所有Fragment默背景。
     *
     * @param backgroundRes 背景资源
     */
    public void setDefaultFragmentBackground(@DrawableRes int backgroundRes) {
        mDefaultFragmentBackground = backgroundRes;
    }

    /**
     * 显栈视图对话框
     * <p>
     * 调试用。
     */
    void showFragmentStackHierarchyView() {
        mDebugStackDelegate.showFragmentStackHierarchyView();
    }

    /**
     * 显栈视图日志
     * <p>
     * 调试用。
     */
    void logFragmentStackHierarchy(String tag) {
        mDebugStackDelegate.logFragmentRecords(tag);
    }

    /**
     * Causes the Runnable r to be added to the action queue.
     * <p>
     * The runnable will be run after all the previous action has been run.
     * <p>
     * 前面事务全执行后执行该Action。
     *
     * @param runnable 线程
     */
    public void post(final Runnable runnable) {
        mTransactionDelegate.post(runnable);
    }

    /**
     * onBackPressed
     * <p>
     * 不建复写该方法，{@link #onBackPressedSupport}替。
     */
    public void onBackPressed() {
        mTransactionDelegate.mActionQueue.enqueue(new BaseAction(BaseAction.ACTION_BACK) {
            @Override
            public void run() {
                if (!mFragmentClickable) {
                    mFragmentClickable = true;
                }
                // 获activeFragment（栈顶开始，状show的那个）
                ISupportFragment activeFragment = SupportHelper.getActiveFragment(getSupportFragmentManager());
                if (mTransactionDelegate.dispatchBackPressedEvent(activeFragment)) {
                    return;
                }
                mSupport.onBackPressedSupport();
            }
        });
    }

    /**
     * onBackPressedSupport
     * <p>
     * 回调时机为Activity回退栈内Fragment数小等1时默finish Activity。
     * 尽量复写该法而非onBackPress()保SupportFragment内onBackPressedSupport()回退事件正常执行。
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

    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        // 防点速过快致抖
        return !mFragmentClickable;
    }

    /*
     **********************************************************************************************
     */

    /**
     * 加载根Fragment
     * <p>
     * Activity内头Fragment或Fragment内头子Fragment。
     *
     * @param containerId 容器ID
     * @param toFragment  目标Fragment
     */
    public void loadRootFragment(int containerId, ISupportFragment toFragment) {
        loadRootFragment(containerId, toFragment, true, false);
    }

    public void loadRootFragment(int containerId, ISupportFragment toFragment, boolean addToBackStack, boolean allowAnimation) {
        mTransactionDelegate.loadRootTransaction(getSupportFragmentManager(), containerId, toFragment, addToBackStack, allowAnimation);
    }

    /**
     * 加载多同级根Fragment
     * <p>
     * 似Wechat、QQ主页场景。
     *
     * @param containerId  容器ID
     * @param showPosition 显位
     * @param toFragments  目标Fragment
     */
    public void loadMultipleRootFragment(int containerId, int showPosition, ISupportFragment... toFragments) {
        mTransactionDelegate.loadMultipleRootTransaction(getSupportFragmentManager(), containerId, showPosition, toFragments);
    }

    /**
     * showHideFragment
     * <p>
     * 显一Fragment，隐其它同栈所有Fragment。
     * 用该法时保同级栈内无多余Fragment（仅通loadMultipleRootFragment()载入的Fragment）。
     * <p>
     * 建用更明确{@link #showHideFragment(ISupportFragment, ISupportFragment)}。
     *
     * @param showFragment 需显Fragment
     */
    public void showHideFragment(ISupportFragment showFragment) {
        showHideFragment(showFragment, null);
    }

    /**
     * showHideFragment
     * <p>
     * 显一Fragment，隐一Fragment。
     * 主于似微信主页切tab场景。
     *
     * @param showFragment 需显Fragment
     * @param hideFragment 需隐Fragment
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
     * Pop the last fragment transition from the manager's fragment back stack.
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

    public void popTo(Class<?> targetFragmentClass, boolean includeTargetFragment, Runnable afterPopTransactionRunnable, int popAnimation) {
        mTransactionDelegate.popTo(targetFragmentClass.getName(), includeTargetFragment, afterPopTransactionRunnable, getSupportFragmentManager(), popAnimation);
    }

    private FragmentManager getSupportFragmentManager() {
        return mActivity.getSupportFragmentManager();
    }

    private ISupportFragment getTopFragment() {
        return SupportHelper.getTopFragment(getSupportFragmentManager());
    }
}