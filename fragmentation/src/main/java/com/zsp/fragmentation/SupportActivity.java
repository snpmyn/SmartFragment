package com.zsp.fragmentation;

import android.os.Bundle;
import android.view.MotionEvent;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import fragmentation.BaseExtraTransaction;
import fragmentation.ISupportActivity;
import fragmentation.ISupportFragment;
import fragmentation.SupportActivityDelegate;
import fragmentation.SupportHelper;
import fragmentation.animation.FragmentAnimator;

/**
 * Created on 2017/7/19.
 *
 * @author 郑少鹏
 * @desc SupportActivity
 */
public class SupportActivity extends AppCompatActivity implements ISupportActivity {
    /**
     * SupportActivityDelegate
     */
    final SupportActivityDelegate supportActivityDelegate = new SupportActivityDelegate(this);

    @Override
    public SupportActivityDelegate getSupportDelegate() {
        return supportActivityDelegate;
    }

    /**
     * extraTransaction
     * <p>
     * Perform some extra transactions.
     * 自定Tag添SharedElement动画，操作非回退栈Fragment。
     *
     * @return BaseExtraTransaction
     */
    @Override
    public BaseExtraTransaction extraTransaction() {
        return supportActivityDelegate.extraTransaction();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportActivityDelegate.onCreate(savedInstanceState);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        supportActivityDelegate.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        supportActivityDelegate.onDestroy();
        super.onDestroy();
    }

    /**
     * Note: return supportActivityDelegate.dispatchTouchEvent(ev) || super.dispatchTouchEvent(ev);
     *
     * @param ev 手势事件
     * @return boolean
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return supportActivityDelegate.dispatchTouchEvent(ev) || super.dispatchTouchEvent(ev);
    }

    /**
     * onBackPressed
     * <p>
     * 不建复写该方法，{@link #onBackPressedSupport}替。
     */
    @Override
    final public void onBackPressed() {
        supportActivityDelegate.onBackPressed();
    }

    /**
     * onBackPressedSupport
     * <p>
     * 回调时机为Activity回退栈内Fragment数小等1时默finish Activity。
     * 尽量复写该法而非onBackPress()保SupportFragment内onBackPressedSupport()回退事件正常执行。
     */
    @Override
    public void onBackPressedSupport() {
        supportActivityDelegate.onBackPressedSupport();
    }

    /**
     * 获所设全局动画
     *
     * @return FragmentAnimator
     */
    @Override
    public FragmentAnimator getFragmentAnimator() {
        return supportActivityDelegate.getFragmentAnimator();
    }

    /**
     * Set all fragments animation.
     *
     * @param fragmentAnimator FragmentAnimator
     */
    @Override
    public void setFragmentAnimator(FragmentAnimator fragmentAnimator) {
        supportActivityDelegate.setFragmentAnimator(fragmentAnimator);
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
    @Override
    public FragmentAnimator onCreateFragmentAnimator() {
        return supportActivityDelegate.onCreateFragmentAnimator();
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
    @Override
    public void post(Runnable runnable) {
        supportActivityDelegate.post(runnable);
    }

    /****************************************以下为可选法（Optional methods）****************************************/

    /**
     * 加载根Fragment
     * <p>
     * Activity内头Fragment或Fragment内头子Fragment。
     *
     * @param containerId 容器ID
     * @param toFragment  目标Fragment
     */
    public void loadRootFragment(int containerId, @NonNull ISupportFragment toFragment) {
        supportActivityDelegate.loadRootFragment(containerId, toFragment);
    }

    public void loadRootFragment(int containerId, ISupportFragment toFragment, boolean addToBackStack, boolean allowAnimation) {
        supportActivityDelegate.loadRootFragment(containerId, toFragment, addToBackStack, allowAnimation);
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
        supportActivityDelegate.loadMultipleRootFragment(containerId, showPosition, toFragments);
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
        supportActivityDelegate.showHideFragment(showFragment);
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
        supportActivityDelegate.showHideFragment(showFragment, hideFragment);
    }

    /**
     * It is recommended to use {@link SupportFragment#start(ISupportFragment)}.
     */
    public void start(ISupportFragment toFragment) {
        supportActivityDelegate.start(toFragment);
    }

    /**
     * It is recommended to use {@link SupportFragment#start(ISupportFragment, int)}.
     *
     * @param launchMode Similar to Activity's LaunchMode.
     */
    public void start(ISupportFragment toFragment, @ISupportFragment.LaunchMode int launchMode) {
        supportActivityDelegate.start(toFragment, launchMode);
    }

    /**
     * It is recommended to use {@link SupportFragment#startForResult(ISupportFragment, int)}.
     * Launch an fragment for which you would like a result when it poped.
     */
    public void startForResult(ISupportFragment toFragment, int requestCode) {
        supportActivityDelegate.startForResult(toFragment, requestCode);
    }

    /**
     * It is recommended to use {@link SupportFragment#startWithPop(ISupportFragment)}.
     * Start the target fragment and pop itself.
     */
    public void startWithPop(ISupportFragment toFragment) {
        supportActivityDelegate.startWithPop(toFragment);
    }

    /**
     * It is recommended to use {@link SupportFragment#startWithPopTo(ISupportFragment, Class, boolean)}.
     *
     * @see #popTo(Class, boolean)
     * @see #start(ISupportFragment)
     */
    public void startWithPopTo(ISupportFragment toFragment, Class<?> targetFragmentClass, boolean includeTargetFragment) {
        supportActivityDelegate.startWithPopTo(toFragment, targetFragmentClass, includeTargetFragment);
    }

    /**
     * replaceFragment
     * <p>
     * It is recommended to use {@link SupportFragment#replaceFragment(ISupportFragment, boolean)}.
     *
     * @param toFragment     目标Fragment
     * @param addToBackStack 添至回退栈否
     */
    public void replaceFragment(ISupportFragment toFragment, boolean addToBackStack) {
        supportActivityDelegate.replaceFragment(toFragment, addToBackStack);
    }

    /**
     * pop
     * <p>
     * Pop the fragment.
     */
    public void pop() {
        supportActivityDelegate.pop();
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
        supportActivityDelegate.popTo(targetFragmentClass, includeTargetFragment);
    }

    /**
     * popTo
     * <p>
     * If you want to begin another FragmentTransaction immediately after popTo(), use this method.
     * 若你想出栈后立刻FragmentTransaction操作，用该法。
     *
     * @param targetFragmentClass         目标Fragment
     * @param includeTargetFragment       含目标Fragment否
     * @param afterPopTransactionRunnable afterPopTransactionRunnable
     */
    public void popTo(Class<?> targetFragmentClass, boolean includeTargetFragment, Runnable afterPopTransactionRunnable) {
        supportActivityDelegate.popTo(targetFragmentClass, includeTargetFragment, afterPopTransactionRunnable);
    }

    /**
     * popTo
     *
     * @param targetFragmentClass         目标Fragment
     * @param includeTargetFragment       含目标Fragment否
     * @param afterPopTransactionRunnable afterPopTransactionRunnable
     * @param popAnim                     popAnim
     */
    public void popTo(Class<?> targetFragmentClass, boolean includeTargetFragment, Runnable afterPopTransactionRunnable, int popAnim) {
        supportActivityDelegate.popTo(targetFragmentClass, includeTargetFragment, afterPopTransactionRunnable, popAnim);
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
        supportActivityDelegate.setDefaultFragmentBackground(backgroundRes);
    }

    /**
     * 栈顶SupportFragment
     *
     * @return ISupportFragment
     */
    public ISupportFragment getTopFragment() {
        return SupportHelper.getTopFragment(getSupportFragmentManager());
    }

    /**
     * Same as fragmentManager.findFragmentByTag(fragmentClass.getName());
     * <p>
     * Find Fragment from FragmentStack.
     */
    public <T extends ISupportFragment> T findFragment(Class<T> fragmentClass) {
        return SupportHelper.findFragment(getSupportFragmentManager(), fragmentClass);
    }
}
