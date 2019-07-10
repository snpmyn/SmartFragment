package fragmentation;

import android.app.Activity;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import fragmentation.animation.FragmentAnimator;
import fragmentation.helper.internal.AnimatorHelper;
import fragmentation.helper.internal.ResultRecord;
import fragmentation.helper.internal.TransactionRecord;
import fragmentation.helper.internal.VisibleDelegate;

/**
 * @decs: SupportFragmentDelegate
 * @author: 郑少鹏
 * @date: 2019/5/20 9:52
 */
public class SupportFragmentDelegate {
    private static final long NOT_FOUND_ANIM_TIME = 300L;
    private static final int STATUS_UN_ROOT = 0;
    static final int STATUS_ROOT_ANIM_DISABLE = 1;
    static final int STATUS_ROOT_ANIM_ENABLE = 2;
    int mContainerId;
    TransactionRecord mTransactionRecord;
    FragmentAnimator mFragmentAnimator;
    AnimatorHelper mAnimHelper;
    boolean mLockAnim;
    private int mCustomEnterAnim = Integer.MIN_VALUE, mCustomExitAnim = Integer.MIN_VALUE, mCustomPopExitAnim = Integer.MIN_VALUE;
    private Handler mHandler;
    private boolean mFirstCreateView = true;
    private boolean mReplaceMode;
    private boolean mIsHidden = true;
    Bundle mNewBundle;
    private TransactionDelegate mTransactionDelegate;
    boolean mAnimByActivity = true;
    EnterAnimListener mEnterAnimListener;
    private int mRootStatus = STATUS_UN_ROOT;
    private Bundle mSaveInstanceState;
    private boolean mIsSharedElement;
    private Fragment mFragment;
    /**
     * SupportVisible
     */
    private VisibleDelegate mVisibleDelegate;
    private ISupportActivity mSupport;
    private ISupportFragment iSupportFragment;
    private FragmentActivity mFragmentActivity;
    private boolean mRootViewClickable;
    private Runnable mNotifyEnterAnimEndRunnable = new Runnable() {
        @Override
        public void run() {
            if (mFragment == null) {
                return;
            }
            iSupportFragment.onEnterAnimationEnd(mSaveInstanceState);
            if (mRootViewClickable) {
                return;
            }
            final View view = mFragment.getView();
            if (view == null) {
                return;
            }
            ISupportFragment preFragment = SupportHelper.getPreFragment(mFragment);
            if (preFragment == null) {
                return;
            }
            long prePopExitDuration = preFragment.getSupportDelegate().getPopExitAnimDuration();
            long enterDuration = getEnterAnimDuration();
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    view.setClickable(false);
                }
            }, prePopExitDuration - enterDuration);
        }
    };

    public SupportFragmentDelegate(ISupportFragment support) {
        if (!(support instanceof Fragment)) {
            throw new RuntimeException("Must extends Fragment");
        }
        this.iSupportFragment = support;
        this.mFragment = (Fragment) support;
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
        if (mTransactionDelegate == null) {
            throw new RuntimeException(mFragment.getClass().getSimpleName() + " not attach!");
        }
        return new BaseExtraTransaction.BaseExtraTransactionImpl<>((FragmentActivity) mSupport, iSupportFragment, mTransactionDelegate, false);
    }

    public void onAttach(Activity activity) {
        if (activity instanceof ISupportActivity) {
            this.mSupport = (ISupportActivity) activity;
            this.mFragmentActivity = (FragmentActivity) activity;
            mTransactionDelegate = mSupport.getSupportDelegate().getTransactionDelegate();
        } else {
            throw new RuntimeException(activity.getClass().getSimpleName() + " must impl ISupportActivity!");
        }
    }

    public void onCreate(@Nullable Bundle savedInstanceState) {
        getVisibleDelegate().onCreate(savedInstanceState);
        Bundle bundle = mFragment.getArguments();
        if (bundle != null) {
            mRootStatus = bundle.getInt(TransactionDelegate.FRAGMENTATION_ARG_ROOT_STATUS, STATUS_UN_ROOT);
            mIsSharedElement = bundle.getBoolean(TransactionDelegate.FRAGMENTATION_ARG_IS_SHARED_ELEMENT, false);
            mContainerId = bundle.getInt(TransactionDelegate.FRAGMENTATION_ARG_CONTAINER);
            mReplaceMode = bundle.getBoolean(TransactionDelegate.FRAGMENTATION_ARG_REPLACE, false);
            mCustomEnterAnim = bundle.getInt(TransactionDelegate.FRAGMENTATION_ARG_CUSTOM_ENTER_ANIM, Integer.MIN_VALUE);
            mCustomExitAnim = bundle.getInt(TransactionDelegate.FRAGMENTATION_ARG_CUSTOM_EXIT_ANIM, Integer.MIN_VALUE);
            mCustomPopExitAnim = bundle.getInt(TransactionDelegate.FRAGMENTATION_ARG_CUSTOM_POP_EXIT_ANIM, Integer.MIN_VALUE);
        }
        if (savedInstanceState == null) {
            getFragmentAnimator();
        } else {
            savedInstanceState.setClassLoader(getClass().getClassLoader());
            mSaveInstanceState = savedInstanceState;
            mFragmentAnimator = savedInstanceState.getParcelable(TransactionDelegate.FRAGMENTATION_STATE_SAVE_ANIMATOR);
            mIsHidden = savedInstanceState.getBoolean(TransactionDelegate.FRAGMENTATION_STATE_SAVE_IS_HIDDEN);
            mContainerId = savedInstanceState.getInt(TransactionDelegate.FRAGMENTATION_ARG_CONTAINER);
        }
        mAnimHelper = new AnimatorHelper(mFragmentActivity.getApplicationContext(), mFragmentAnimator);
        final Animation enter = getEnterAnim();
        if (enter == null) {
            return;
        }
        getEnterAnim().setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // 开启防抖
                mSupport.getSupportDelegate().mFragmentClickable = false;
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSupport.getSupportDelegate().mFragmentClickable = true;
                    }
                }, enter.getDuration());
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void onSaveInstanceState(Bundle outState) {
        getVisibleDelegate().onSaveInstanceState(outState);
        outState.putParcelable(TransactionDelegate.FRAGMENTATION_STATE_SAVE_ANIMATOR, mFragmentAnimator);
        outState.putBoolean(TransactionDelegate.FRAGMENTATION_STATE_SAVE_IS_HIDDEN, mFragment.isHidden());
        outState.putInt(TransactionDelegate.FRAGMENTATION_ARG_CONTAINER, mContainerId);
    }

    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if ((mSupport.getSupportDelegate().mPopMultipleNoAnim || mLockAnim)) {
            if (transit == FragmentTransaction.TRANSIT_FRAGMENT_CLOSE && enter) {
                return mAnimHelper.getNoneAnimationFixed();
            }
            return mAnimHelper.getNoneAnimation();
        }
        if (transit == FragmentTransaction.TRANSIT_FRAGMENT_OPEN) {
            if (enter) {
                Animation enterAnim;
                if (mRootStatus == STATUS_ROOT_ANIM_DISABLE) {
                    enterAnim = mAnimHelper.getNoneAnimation();
                } else {
                    enterAnim = mAnimHelper.enterAnimation;
                    fixAnimationListener(enterAnim);
                }
                return enterAnim;
            } else {
                return mAnimHelper.popExitAnimation;
            }
        } else if (transit == FragmentTransaction.TRANSIT_FRAGMENT_CLOSE) {
            return enter ? mAnimHelper.popEnterAnimation : mAnimHelper.exitAnimation;
        } else {
            if (mIsSharedElement && enter) {
                compatSharedElements();
            }
            if (!enter) {
                return mAnimHelper.compatChildFragmentExitAnim(mFragment);
            }
            return null;
        }
    }

    public void onResume() {
        getVisibleDelegate().onResume();
    }

    public void onPause() {
        getVisibleDelegate().onPause();
    }

    public void onDestroyView() {
        mSupport.getSupportDelegate().mFragmentClickable = true;
        getVisibleDelegate().onDestroyView();
        getHandler().removeCallbacks(mNotifyEnterAnimEndRunnable);
    }

    public void onDestroy() {
        mTransactionDelegate.handleResultRecord(mFragment);
    }

    public void onHiddenChanged(boolean hidden) {
        getVisibleDelegate().onHiddenChanged(hidden);
    }

    public void setUserVisibleHint(boolean isVisibleToUser) {
        getVisibleDelegate().setUserVisibleHint(isVisibleToUser);
    }

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getVisibleDelegate().onActivityCreated(savedInstanceState);
        View view = mFragment.getView();
        if (view != null) {
            mRootViewClickable = view.isClickable();
            view.setClickable(true);
            setBackground(view);
        }
        boolean flag = savedInstanceState != null
                || mRootStatus == STATUS_ROOT_ANIM_DISABLE
                || (mFragment.getTag() != null && mFragment.getTag().startsWith("android:switcher:"))
                || (mReplaceMode && !mFirstCreateView);
        if (flag) {
            notifyEnterAnimEnd();
        } else if (mCustomEnterAnim != Integer.MIN_VALUE) {
            fixAnimationListener(mCustomEnterAnim == 0 ?
                    mAnimHelper.getNoneAnimation() : AnimationUtils.loadAnimation(mFragmentActivity, mCustomEnterAnim));
        }
        if (mFirstCreateView) {
            mFirstCreateView = false;
        }
    }

    /**
     * Causes the Runnable r to be added to the action queue.
     * <p>
     * The runnable will be run after all the previous action has been run.
     * <p>
     * 前面事务全执行后执行该Action。
     *
     * @deprecated Use {@link #post(Runnable)} instead.
     */
    @Deprecated
    public void enqueueAction(Runnable runnable) {
        post(runnable);
    }

    /**
     * Causes the Runnable r to be added to the action queue.
     * <p>
     * The runnable will be run after all the previous action has been run.
     * <p>
     * 前面事务全执行后执行该Action。
     */
    public void post(final Runnable runnable) {
        mTransactionDelegate.post(runnable);
    }

    /**
     * Called when the enter-animation end.
     * 入栈动画，结束时调。
     */
    public void onEnterAnimationEnd(Bundle savedInstanceState) {

    }

    /**
     * Lazy initial，Called when fragment is first visible.
     * <p>
     * 同级下懒加载 ＋ ViewPager下懒加载结合回调。
     */
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {

    }

    /**
     * Called when the fragment is visible.
     * <p>
     * Fragment对用户可见时调
     * <p>
     * Is the combination of  [onHiddenChanged() + onResume()/onPause() + setUserVisibleHint()]
     */
    public void onSupportVisible() {

    }

    /**
     * Return true if the fragment has been supportVisible.
     */
    final public boolean isSupportVisible() {
        return getVisibleDelegate().isSupportVisible();
    }

    /**
     * Called when the fragment is invisible.
     * <p>
     * Is the combination of  [onHiddenChanged() + onResume()/onPause() + setUserVisibleHint()]
     */
    public void onSupportInvisible() {

    }

    /**
     * onCreateFragmentAnimator
     * <p>
     * Set fragment animation with a higher priority than the ISupportActivity.
     */
    public FragmentAnimator onCreateFragmentAnimator() {
        return mSupport.getFragmentAnimator();
    }

    /**
     * 获所设全局动画
     *
     * @return FragmentAnimator
     */
    public FragmentAnimator getFragmentAnimator() {
        if (mSupport == null) {
            throw new RuntimeException("Fragment has not been attached to Activity!");
        }
        if (mFragmentAnimator == null) {
            mFragmentAnimator = iSupportFragment.onCreateFragmentAnimator();
            if (mFragmentAnimator == null) {
                mFragmentAnimator = mSupport.getFragmentAnimator();
            }
        }
        return mFragmentAnimator;
    }

    /**
     * Set the fragment animation.
     */
    public void setFragmentAnimator(FragmentAnimator fragmentAnimator) {
        this.mFragmentAnimator = fragmentAnimator;
        if (mAnimHelper != null) {
            mAnimHelper.notifyChanged(fragmentAnimator);
        }
        mAnimByActivity = false;
    }

    /**
     * setFragmentResult
     * <p>
     * 似{@link Activity#setResult(int, Intent)}。
     *
     * @see #startForResult(ISupportFragment, int)
     */
    public void setFragmentResult(int resultCode, Bundle bundle) {
        Bundle args = mFragment.getArguments();
        if (args == null || !args.containsKey(TransactionDelegate.FRAGMENTATION_ARG_RESULT_RECORD)) {
            return;
        }
        ResultRecord resultRecord = args.getParcelable(TransactionDelegate.FRAGMENTATION_ARG_RESULT_RECORD);
        if (resultRecord != null) {
            resultRecord.resultCode = resultCode;
            resultRecord.resultBundle = bundle;
        }
    }

    /**
     * onFragmentResult
     * <p>
     * 似{@link Activity#onActivityResult(int, int, Intent)}。
     *
     * @see #startForResult(ISupportFragment, int)
     */
    public void onFragmentResult(int requestCode, int resultCode, Bundle data) {

    }

    /**
     * onNewBundle
     * <p>
     * start(TargetFragment,LaunchMode)且启动模式SingleTask/SingleTop，回调TargetFragment该法。
     * 似{@link Activity#onNewIntent(Intent)}。
     *
     * @param args putNewBundle(Bundle newBundle)
     * @see #start(ISupportFragment, int)
     */
    public void onNewBundle(Bundle args) {

    }

    /**
     * 添NewBundle
     * <p>
     * 用于SingleTask/SingleTop启动模式。
     *
     * @see #start(ISupportFragment, int)
     */
    public void putNewBundle(Bundle newBundle) {
        this.mNewBundle = newBundle;
    }

    /*
     **********************************************************************************************
     */

    /**
     * onBackPressedSupport
     *
     * @return false继向上传递；true消费事件
     */
    public boolean onBackPressedSupport() {
        return false;
    }

    /**
     * 隐软键盘
     */
    public void hideSoftInput() {
        Activity activity = mFragment.getActivity();
        if (activity == null) {
            return;
        }
        View view = activity.getWindow().getDecorView();
        SupportHelper.hideSoftInput(view);
    }

    /**
     * 显软键盘
     * <p>
     * 调后于onPause自隐。
     *
     * @param view 视图
     */
    public void showSoftInput(View view) {
        SupportHelper.showSoftInput(view);
    }

    public void loadRootFragment(int containerId, ISupportFragment toFragment, boolean addToBackStack, boolean allowAnim) {
        mTransactionDelegate.loadRootTransaction(getChildFragmentManager(), containerId, toFragment, addToBackStack, allowAnim);
    }

    /**
     * 加载根Fragment
     * <p>
     * 即Activity内头Fragment或Fragment内头子Fragment。
     *
     * @param containerId 容器ID
     * @param toFragment  目标Fragment
     */
    public void loadRootFragment(int containerId, ISupportFragment toFragment) {
        loadRootFragment(containerId, toFragment, true, false);
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
        mTransactionDelegate.loadMultipleRootTransaction(getChildFragmentManager(), containerId, showPosition, toFragments);
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

    public void start(ISupportFragment toFragment) {
        start(toFragment, ISupportFragment.STANDARD);
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
        mTransactionDelegate.showHideFragment(getChildFragmentManager(), showFragment, hideFragment);
    }

    /**
     * @param launchMode Similar to Activity's LaunchMode.
     */
    public void start(final ISupportFragment toFragment, @ISupportFragment.LaunchMode int launchMode) {
        mTransactionDelegate.dispatchStartTransaction(mFragment.getFragmentManager(), iSupportFragment, toFragment, 0, launchMode, TransactionDelegate.TYPE_ADD);
    }

    /**
     * Launch an fragment for which you would like a result when it poped.
     */
    public void startForResult(ISupportFragment toFragment, int requestCode) {
        mTransactionDelegate.dispatchStartTransaction(mFragment.getFragmentManager(), iSupportFragment, toFragment, requestCode, ISupportFragment.STANDARD, TransactionDelegate.TYPE_ADD_RESULT);
    }

    /**
     * Start the target Fragment and pop itself.
     */
    public void startWithPop(ISupportFragment toFragment) {
        mTransactionDelegate.startWithPop(mFragment.getFragmentManager(), iSupportFragment, toFragment);
    }

    public void startWithPopTo(ISupportFragment toFragment, Class<?> targetFragmentClass, boolean includeTargetFragment) {
        mTransactionDelegate.startWithPopTo(mFragment.getFragmentManager(), iSupportFragment, toFragment, targetFragmentClass.getName(), includeTargetFragment);
    }

    public void startChild(ISupportFragment toFragment) {
        startChild(toFragment, ISupportFragment.STANDARD);
    }

    private void startChild(final ISupportFragment toFragment, @ISupportFragment.LaunchMode int launchMode) {
        mTransactionDelegate.dispatchStartTransaction(getChildFragmentManager(), getTopFragment(), toFragment, 0, launchMode, TransactionDelegate.TYPE_ADD);
    }

    public void startChildForResult(ISupportFragment toFragment, int requestCode) {
        mTransactionDelegate.dispatchStartTransaction(getChildFragmentManager(), getTopFragment(), toFragment, requestCode, ISupportFragment.STANDARD, TransactionDelegate.TYPE_ADD_RESULT);
    }

    public void startChildWithPop(ISupportFragment toFragment) {
        mTransactionDelegate.startWithPop(getChildFragmentManager(), getTopFragment(), toFragment);
    }

    public void replaceChildFragment(ISupportFragment toFragment, boolean addToBackStack) {
        mTransactionDelegate.dispatchStartTransaction(getChildFragmentManager(), getTopFragment(), toFragment, 0, ISupportFragment.STANDARD, addToBackStack ? TransactionDelegate.TYPE_REPLACE : TransactionDelegate.TYPE_REPLACE_DO_NOT_BACK);
    }

    public void pop() {
        mTransactionDelegate.pop(mFragment.getFragmentManager());
    }

    /**
     * Pop the child fragment.
     */
    public void popChild() {
        mTransactionDelegate.pop(getChildFragmentManager());
    }

    public void replaceFragment(ISupportFragment toFragment, boolean addToBackStack) {
        mTransactionDelegate.dispatchStartTransaction(mFragment.getFragmentManager(), iSupportFragment, toFragment, 0, ISupportFragment.STANDARD, addToBackStack ? TransactionDelegate.TYPE_REPLACE : TransactionDelegate.TYPE_REPLACE_DO_NOT_BACK);
    }

    /**
     * If you want to begin another FragmentTransaction immediately after popTo(), use this method.
     * 若你想出栈后立刻FragmentTransaction操作，用该法。
     */
    public void popTo(Class<?> targetFragmentClass, boolean includeTargetFragment, Runnable afterPopTransactionRunnable) {
        popTo(targetFragmentClass, includeTargetFragment, afterPopTransactionRunnable, TransactionDelegate.DEFAULT_POP_TO_ANIM);
    }

    public void popTo(Class<?> targetFragmentClass, boolean includeTargetFragment, Runnable afterPopTransactionRunnable, int popAnim) {
        mTransactionDelegate.popTo(targetFragmentClass.getName(), includeTargetFragment, afterPopTransactionRunnable, mFragment.getFragmentManager(), popAnim);
    }

    public void popToChild(Class<?> targetFragmentClass, boolean includeTargetFragment) {
        popToChild(targetFragmentClass, includeTargetFragment, null);
    }

    public void popToChild(Class<?> targetFragmentClass, boolean includeTargetFragment, Runnable afterPopTransactionRunnable) {
        popToChild(targetFragmentClass, includeTargetFragment, afterPopTransactionRunnable, TransactionDelegate.DEFAULT_POP_TO_ANIM);
    }

    public void popToChild(Class<?> targetFragmentClass, boolean includeTargetFragment, Runnable afterPopTransactionRunnable, int popAnim) {
        mTransactionDelegate.popTo(targetFragmentClass.getName(), includeTargetFragment, afterPopTransactionRunnable, getChildFragmentManager(), popAnim);
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

    private FragmentManager getChildFragmentManager() {
        return mFragment.getChildFragmentManager();
    }

    private ISupportFragment getTopFragment() {
        return SupportHelper.getTopFragment(getChildFragmentManager());
    }

    private void fixAnimationListener(Animation enterAnim) {
        // AnimationListener is not reliable.
        getHandler().postDelayed(mNotifyEnterAnimEndRunnable, enterAnim.getDuration());
        mSupport.getSupportDelegate().mFragmentClickable = true;
        if (mEnterAnimListener != null) {
            getHandler().post(new Runnable() {
                @Override
                public void run() {
                    mEnterAnimListener.onEnterAnimStart();
                    mEnterAnimListener = null;
                }
            });
        }
    }

    public void popQuiet() {
        mTransactionDelegate.popQuiet(mFragment.getFragmentManager(), mFragment);
    }

    private void compatSharedElements() {
        notifyEnterAnimEnd();
    }

    private void setBackground(View view) {
        boolean flag = (mFragment.getTag() != null && mFragment.getTag().startsWith("android:switcher:")) ||
                mRootStatus != STATUS_UN_ROOT ||
                view.getBackground() != null;
        if (flag) {
            return;
        }
        int defaultBg = mSupport.getSupportDelegate().getDefaultFragmentBackground();
        if (defaultBg == 0) {
            int background = getWindowBackground();
            view.setBackgroundResource(background);
        } else {
            view.setBackgroundResource(defaultBg);
        }
    }

    private int getWindowBackground() {
        TypedArray a = mFragmentActivity.getTheme().obtainStyledAttributes(new int[]{
                android.R.attr.windowBackground
        });
        int background = a.getResourceId(0, 0);
        a.recycle();
        return background;
    }

    private void notifyEnterAnimEnd() {
        getHandler().post(mNotifyEnterAnimEndRunnable);
        mSupport.getSupportDelegate().mFragmentClickable = true;
    }

    private Handler getHandler() {
        if (mHandler == null) {
            mHandler = new Handler(Looper.getMainLooper());
        }
        return mHandler;
    }

    public VisibleDelegate getVisibleDelegate() {
        if (mVisibleDelegate == null) {
            mVisibleDelegate = new VisibleDelegate(iSupportFragment);
        }
        return mVisibleDelegate;
    }

    public FragmentActivity getActivity() {
        return mFragmentActivity;
    }

    private Animation getEnterAnim() {
        if (mCustomEnterAnim == Integer.MIN_VALUE) {
            if (mAnimHelper != null && mAnimHelper.enterAnimation != null) {
                return mAnimHelper.enterAnimation;
            }
        } else {
            try {
                return AnimationUtils.loadAnimation(mFragmentActivity, mCustomEnterAnim);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private long getEnterAnimDuration() {
        Animation enter = getEnterAnim();
        if (enter != null) {
            return enter.getDuration();
        }
        return NOT_FOUND_ANIM_TIME;
    }

    public long getExitAnimDuration() {
        if (mCustomExitAnim == Integer.MIN_VALUE) {
            if (mAnimHelper != null && mAnimHelper.exitAnimation != null) {
                return mAnimHelper.exitAnimation.getDuration();
            }
        } else {
            try {
                return AnimationUtils.loadAnimation(mFragmentActivity, mCustomExitAnim).getDuration();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return NOT_FOUND_ANIM_TIME;
    }

    private long getPopExitAnimDuration() {
        if (mCustomPopExitAnim == Integer.MIN_VALUE) {
            if (mAnimHelper != null && mAnimHelper.popExitAnimation != null) {
                return mAnimHelper.popExitAnimation.getDuration();
            }
        } else {
            try {
                return AnimationUtils.loadAnimation(mFragmentActivity, mCustomPopExitAnim).getDuration();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return NOT_FOUND_ANIM_TIME;
    }

    @Nullable
    Animation getExitAnim() {
        if (mCustomExitAnim == Integer.MIN_VALUE) {
            if (mAnimHelper != null && mAnimHelper.exitAnimation != null) {
                return mAnimHelper.exitAnimation;
            }
        } else {
            try {
                return AnimationUtils.loadAnimation(mFragmentActivity, mCustomExitAnim);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    interface EnterAnimListener {
        /**
         * onEnterAnimStart
         */
        void onEnterAnimStart();
    }
}