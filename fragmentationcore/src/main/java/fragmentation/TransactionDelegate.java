package fragmentation;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.fragment.app.FragmentationMagician;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import fragmentation.exception.AfterSaveStateTransactionWarningException;
import fragmentation.helper.internal.ResultRecord;
import fragmentation.helper.internal.TransactionRecord;
import fragmentation.queue.ActionQueue;
import fragmentation.queue.BaseAction;
import timber.log.Timber;
import value.SmartFragmentFragmentationMagic;

/**
 * @decs: TransactionDelegate
 * @author: 郑少鹏
 * @date: 2019/5/20 9:55
 */
class TransactionDelegate {
    static final int DEFAULT_POP_TO_ANIM = Integer.MAX_VALUE;
    static final String FRAGMENTATION_ARG_RESULT_RECORD = "fragment_arg_result_record";
    static final String FRAGMENTATION_ARG_ROOT_STATUS = "fragmentation_arg_root_status";
    static final String FRAGMENTATION_ARG_IS_SHARED_ELEMENT = "fragmentation_arg_is_shared_element";
    static final String FRAGMENTATION_ARG_CONTAINER = "fragmentation_arg_container";
    static final String FRAGMENTATION_ARG_REPLACE = "fragmentation_arg_replace";
    static final String FRAGMENTATION_ARG_CUSTOM_ENTER_ANIM = "fragmentation_arg_custom_enter_anim";
    static final String FRAGMENTATION_ARG_CUSTOM_EXIT_ANIM = "fragmentation_arg_custom_exit_anim";
    static final String FRAGMENTATION_ARG_CUSTOM_POP_EXIT_ANIM = "fragmentation_arg_custom_pop_exit_anim";
    static final String FRAGMENTATION_STATE_SAVE_ANIMATOR = "fragmentation_state_save_animator";
    static final String FRAGMENTATION_STATE_SAVE_IS_HIDDEN = "fragmentation_state_save_status";
    private static final String FRAGMENTATION_STATE_SAVE_RESULT = "fragmentation_state_save_result";
    static final int TYPE_ADD = 0;
    static final int TYPE_ADD_RESULT = 1;
    static final int TYPE_ADD_WITHOUT_HIDE = 2;
    static final int TYPE_ADD_RESULT_WITHOUT_HIDE = 3;
    static final int TYPE_REPLACE = 10;
    static final int TYPE_REPLACE_DO_NOT_BACK = 11;
    private final ISupportActivity mSupport;
    private final FragmentActivity mActivity;
    private final Handler mHandler;
    ActionQueue mActionQueue;

    TransactionDelegate(ISupportActivity support) {
        this.mSupport = support;
        this.mActivity = (FragmentActivity) support;
        mHandler = new Handler(Looper.getMainLooper());
        mActionQueue = new ActionQueue(mHandler);
    }

    void post(final Runnable runnable) {
        mActionQueue.enqueue(new BaseAction() {
            @Override
            public void run() {
                runnable.run();
            }
        });
    }

    void loadRootTransaction(final FragmentManager fm, final int containerId, final ISupportFragment to, final boolean addToBackStack, final boolean allowAnimation) {
        enqueue(fm, new BaseAction(BaseAction.ACTION_LOAD) {
            @Override
            public void run() {
                bindContainerId(containerId, to);
                String toFragmentTag = to.getClass().getName();
                TransactionRecord transactionRecord = to.getSupportDelegate().mTransactionRecord;
                if (transactionRecord != null) {
                    if (transactionRecord.tag != null) {
                        toFragmentTag = transactionRecord.tag;
                    }
                }
                start(fm, null, to, toFragmentTag, !addToBackStack, null, allowAnimation, TYPE_REPLACE);
            }
        });
    }

    void loadMultipleRootTransaction(final FragmentManager fm, final int containerId, final int showPosition, final ISupportFragment... tos) {
        enqueue(fm, new BaseAction(BaseAction.ACTION_LOAD) {
            @Override
            public void run() {
                FragmentTransaction ft = fm.beginTransaction();
                for (int i = 0; i < tos.length; i++) {
                    Fragment to = (Fragment) tos[i];
                    Bundle args = getArguments(to);
                    args.putInt(FRAGMENTATION_ARG_ROOT_STATUS, SupportFragmentDelegate.STATUS_ROOT_ANIM_DISABLE);
                    bindContainerId(containerId, tos[i]);
                    String toName = to.getClass().getName();
                    ft.add(containerId, to, toName);
                    if (i != showPosition) {
                        ft.hide(to);
                    }
                }
                supportCommit(fm, ft);
            }
        });
    }

    /**
     * Dispatch the start transaction.
     */
    void dispatchStartTransaction(final FragmentManager fm, final ISupportFragment from, final ISupportFragment to, final int requestCode, final int launchMode, final int type) {
        enqueue(fm, new BaseAction(launchMode == ISupportFragment.SINGLE_TASK ? BaseAction.ACTION_POP_MOCK : BaseAction.ACTION_NORMAL) {
            @Override
            public void run() {
                doDispatchStartTransaction(fm, from, to, requestCode, launchMode, type);
            }
        });
    }

    /**
     * Show showFragment then hide hideFragment.
     */
    void showHideFragment(final FragmentManager fm, final ISupportFragment showFragment, final ISupportFragment hideFragment) {
        enqueue(fm, new BaseAction() {
            @Override
            public void run() {
                doShowHideFragment(fm, showFragment, hideFragment);
            }
        });
    }

    private static <T> void checkNotNull(T value, String message) {
        if (value == null) {
            throw new NullPointerException(message);
        }
    }

    /**
     * Start the target Fragment and pop itself.
     */
    void startWithPop(final FragmentManager fm, final ISupportFragment from, final ISupportFragment to) {
        enqueue(fm, new BaseAction(BaseAction.ACTION_POP_MOCK) {
            @Override
            public void run() {
                ISupportFragment top = getTopFragmentForStart(from, fm);
                if (top == null) {
                    throw new NullPointerException("There is no Fragment in the FragmentManager, maybe you need to call loadRootFragment() first!");
                }
                int containerId = top.getSupportDelegate().mContainerId;
                bindContainerId(containerId, to);
                handleAfterSaveInStateTransactionException(fm, "popTo()");
                FragmentationMagician.executePendingTransactionsAllowingStateLoss(fm);
                top.getSupportDelegate().mLockAnim = true;
                if (!FragmentationMagician.isStateSaved(fm)) {
                    mockStartWithPopAnim(SupportHelper.getTopFragment(fm), to, top.getSupportDelegate().mAnimHelper.popExitAnimation);
                }
                removeTopFragment(fm);
                FragmentationMagician.popBackStackAllowingStateLoss(fm);
                FragmentationMagician.executePendingTransactionsAllowingStateLoss(fm);
            }
        });
        dispatchStartTransaction(fm, from, to, 0, ISupportFragment.STANDARD, TransactionDelegate.TYPE_ADD);
    }

    void startWithPopTo(final FragmentManager fm, final ISupportFragment from, final ISupportFragment to, final String fragmentTag, final boolean includeTargetFragment) {
        enqueue(fm, new BaseAction(BaseAction.ACTION_POP_MOCK) {
            @Override
            public void run() {
                int flag = 0;
                if (includeTargetFragment) {
                    flag = FragmentManager.POP_BACK_STACK_INCLUSIVE;
                }
                List<Fragment> willPopFragments = SupportHelper.getWillPopFragments(fm, fragmentTag, includeTargetFragment);
                final ISupportFragment top = getTopFragmentForStart(from, fm);
                if (top == null) {
                    throw new NullPointerException("There is no Fragment in the FragmentManager, maybe you need to call loadRootFragment() first!");
                }
                int containerId = top.getSupportDelegate().mContainerId;
                bindContainerId(containerId, to);
                if (willPopFragments.size() <= 0) {
                    return;
                }
                handleAfterSaveInStateTransactionException(fm, "startWithPopTo()");
                FragmentationMagician.executePendingTransactionsAllowingStateLoss(fm);
                if (!FragmentationMagician.isStateSaved(fm)) {
                    mockStartWithPopAnim(SupportHelper.getTopFragment(fm), to, top.getSupportDelegate().mAnimHelper.popExitAnimation);
                }
                safePopTo(fragmentTag, fm, flag, willPopFragments);
            }
        });
        dispatchStartTransaction(fm, from, to, 0, ISupportFragment.STANDARD, TransactionDelegate.TYPE_ADD);
    }

    /**
     * remove
     */
    void remove(final FragmentManager fm, final Fragment fragment, final boolean showPreFragment) {
        enqueue(fm, new BaseAction(BaseAction.ACTION_POP, fm) {
            @Override
            public void run() {
                FragmentTransaction ft = fm.beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                        .remove(fragment);
                if (showPreFragment) {
                    ISupportFragment preFragment = SupportHelper.getPreFragment(fragment);
                    if (preFragment instanceof Fragment) {
                        ft.show((Fragment) preFragment);
                    }
                }
                supportCommit(fm, ft);
            }
        });
    }

    /**
     * pop
     */
    void pop(final FragmentManager fm) {
        enqueue(fm, new BaseAction(BaseAction.ACTION_POP, fm) {
            @Override
            public void run() {
                handleAfterSaveInStateTransactionException(fm, "pop()");
                FragmentationMagician.popBackStackAllowingStateLoss(fm);
                removeTopFragment(fm);
            }
        });
    }

    private void removeTopFragment(FragmentManager fm) {
        try {
            // Safe popBackStack()
            ISupportFragment top = SupportHelper.getBackStackTopFragment(fm);
            if (top != null) {
                fm.beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                        .remove((Fragment) top)
                        .commitAllowingStateLoss();
            }
        } catch (Exception ignored) {

        }
    }

    void popQuiet(final FragmentManager fm, final Fragment fragment) {
        enqueue(fm, new BaseAction(BaseAction.ACTION_POP_MOCK) {
            @Override
            public void run() {
                mSupport.getSupportDelegate().mPopMultipleNoAnim = true;
                removeTopFragment(fm);
                FragmentationMagician.popBackStackAllowingStateLoss(fm, fragment.getTag(), 0);
                FragmentationMagician.popBackStackAllowingStateLoss(fm);
                FragmentationMagician.executePendingTransactionsAllowingStateLoss(fm);
                mSupport.getSupportDelegate().mPopMultipleNoAnim = false;
            }
        });
    }

    /**
     * Pop the last fragment transition from the manager's fragment pop stack.
     *
     * @param targetFragmentTag     tag
     * @param includeTargetFragment whether it includes targetFragment
     */
    void popTo(final String targetFragmentTag, final boolean includeTargetFragment, final Runnable afterPopTransactionRunnable, final FragmentManager fm, final int popAnim) {
        enqueue(fm, new BaseAction(BaseAction.ACTION_POP_MOCK) {
            @Override
            public void run() {
                doPopTo(targetFragmentTag, includeTargetFragment, fm, popAnim);
                if (afterPopTransactionRunnable != null) {
                    afterPopTransactionRunnable.run();
                }
            }
        });
    }

    void handleResultRecord(Fragment from) {
        try {
            Bundle args = from.getArguments();
            if (args == null) {
                return;
            }
            final ResultRecord resultRecord = args.getParcelable(FRAGMENTATION_ARG_RESULT_RECORD);
            if (resultRecord == null) {
                return;
            }
            ISupportFragment targetFragment = null;
            if (from.getFragmentManager() != null) {
                targetFragment = (ISupportFragment) from.getFragmentManager().getFragment(from.getArguments(), FRAGMENTATION_STATE_SAVE_RESULT);
            }
            if (targetFragment != null) {
                targetFragment.onFragmentResult(resultRecord.requestCode, resultRecord.resultCode, resultRecord.resultBundle);
            }
        } catch (IllegalStateException ignored) {
            // Fragment no longer exists
        }
    }

    /**
     * Dispatch the pop-event.
     * <p>
     * Priority of the top of the stack of Fragment.
     */
    boolean dispatchBackPressedEvent(ISupportFragment activeFragment) {
        if (activeFragment != null) {
            boolean result = activeFragment.onBackPressedSupport();
            if (result) {
                return true;
            }
            Fragment parentFragment = ((Fragment) activeFragment).getParentFragment();
            return dispatchBackPressedEvent((ISupportFragment) parentFragment);
        }
        return false;
    }

    private void enqueue(FragmentManager fm, BaseAction action) {
        if (fm == null) {
            Timber.d("FragmentManager is null, skip the action!");
            return;
        }
        mActionQueue.enqueue(action);
    }

    private void doDispatchStartTransaction(FragmentManager fm, ISupportFragment from, ISupportFragment to, int requestCode, int launchMode, int type) {
        checkNotNull(to, "toFragment == null");
        boolean flag = (type == TYPE_ADD_RESULT || type == TYPE_ADD_RESULT_WITHOUT_HIDE) && from != null;
        if (flag) {
            if (!((Fragment) from).isAdded()) {
                Timber.d(((Fragment) from).getClass().getSimpleName(), " has not been attached yet! startForResult() converted to start()");
            } else {
                saveRequestCode(fm, (Fragment) from, (Fragment) to, requestCode);
            }
        }
        from = getTopFragmentForStart(from, fm);
        int containerId = getArguments((Fragment) to).getInt(FRAGMENTATION_ARG_CONTAINER, 0);
        if (from == null && containerId == 0) {
            Timber.d("There is no Fragment in the FragmentManager, maybe you need to call loadRootFragment()!");
            return;
        }
        if (from != null && containerId == 0) {
            bindContainerId(from.getSupportDelegate().mContainerId, to);
        }
        // process ExtraTransaction
        String toFragmentTag = to.getClass().getName();
        boolean doNotAddToBackStack = false;
        ArrayList<TransactionRecord.SharedElement> sharedElementList = null;
        TransactionRecord transactionRecord = to.getSupportDelegate().mTransactionRecord;
        if (transactionRecord != null) {
            if (transactionRecord.tag != null) {
                toFragmentTag = transactionRecord.tag;
            }
            doNotAddToBackStack = transactionRecord.doNotAddToBackStack;
            if (transactionRecord.sharedElementList != null) {
                sharedElementList = transactionRecord.sharedElementList;
            }
        }
        if (handleLaunchMode(fm, from, to, toFragmentTag, launchMode)) {
            return;
        }
        start(fm, from, to, toFragmentTag, doNotAddToBackStack, sharedElementList, false, type);
    }

    private ISupportFragment getTopFragmentForStart(ISupportFragment from, FragmentManager fm) {
        ISupportFragment top;
        if (from == null) {
            top = SupportHelper.getTopFragment(fm);
        } else {
            if (from.getSupportDelegate().mContainerId == 0) {
                Fragment fFrom = (Fragment) from;
                if (fFrom.getTag() != null && !fFrom.getTag().startsWith(SmartFragmentFragmentationMagic.STRING_ANDROID_SWITCHER)) {
                    throw new IllegalStateException("Can't find container, please call loadRootFragment() first!");
                }
            }
            top = SupportHelper.getTopFragment(fm, from.getSupportDelegate().mContainerId);
        }
        return top;
    }

    private void start(FragmentManager fm, final ISupportFragment from, ISupportFragment to, String toFragmentTag,
                       boolean doNotAddToBackStack, ArrayList<TransactionRecord.SharedElement> sharedElementList, boolean allowRootFragmentAnim, int type) {
        FragmentTransaction ft = fm.beginTransaction();
        boolean addMode = (type == TYPE_ADD || type == TYPE_ADD_RESULT || type == TYPE_ADD_WITHOUT_HIDE || type == TYPE_ADD_RESULT_WITHOUT_HIDE);
        Fragment fFrom = (Fragment) from;
        Fragment fTo = (Fragment) to;
        Bundle args = getArguments(fTo);
        args.putBoolean(FRAGMENTATION_ARG_REPLACE, !addMode);
        if (sharedElementList == null) {
            if (addMode) {
                // Replace mode forbidden animation, the replace animations exist overlapping Bug on support-v4.
                TransactionRecord record = to.getSupportDelegate().mTransactionRecord;
                if (record != null && record.targetFragmentEnter != Integer.MIN_VALUE) {
                    ft.setCustomAnimations(record.targetFragmentEnter, record.currentFragmentPopExit,
                            record.currentFragmentPopEnter, record.targetFragmentExit);
                    args.putInt(FRAGMENTATION_ARG_CUSTOM_ENTER_ANIM, record.targetFragmentEnter);
                    args.putInt(FRAGMENTATION_ARG_CUSTOM_EXIT_ANIM, record.targetFragmentExit);
                    args.putInt(FRAGMENTATION_ARG_CUSTOM_POP_EXIT_ANIM, record.currentFragmentPopExit);
                } else {
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                }
            } else {
                args.putInt(FRAGMENTATION_ARG_ROOT_STATUS, SupportFragmentDelegate.STATUS_ROOT_ANIM_DISABLE);
            }
        } else {
            args.putBoolean(FRAGMENTATION_ARG_IS_SHARED_ELEMENT, true);
            for (TransactionRecord.SharedElement item : sharedElementList) {
                ft.addSharedElement(item.sharedElement, item.sharedName);
            }
        }
        if (from == null) {
            ft.replace(args.getInt(FRAGMENTATION_ARG_CONTAINER), fTo, toFragmentTag);
            if (!addMode) {
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                args.putInt(FRAGMENTATION_ARG_ROOT_STATUS, allowRootFragmentAnim ?
                        SupportFragmentDelegate.STATUS_ROOT_ANIM_ENABLE : SupportFragmentDelegate.STATUS_ROOT_ANIM_DISABLE);
            }
        } else {
            if (addMode) {
                ft.add(from.getSupportDelegate().mContainerId, fTo, toFragmentTag);
                if (type != TYPE_ADD_WITHOUT_HIDE && type != TYPE_ADD_RESULT_WITHOUT_HIDE) {
                    ft.hide(fFrom);
                }
            } else {
                ft.replace(from.getSupportDelegate().mContainerId, fTo, toFragmentTag);
            }
        }
        if (!doNotAddToBackStack && type != TYPE_REPLACE_DO_NOT_BACK) {
            ft.addToBackStack(toFragmentTag);
        }
        supportCommit(fm, ft);
    }

    private void bindContainerId(int containerId, ISupportFragment to) {
        Bundle args = getArguments((Fragment) to);
        args.putInt(FRAGMENTATION_ARG_CONTAINER, containerId);
    }

    private Bundle getArguments(Fragment fragment) {
        Bundle bundle = fragment.getArguments();
        if (bundle == null) {
            bundle = new Bundle();
            fragment.setArguments(bundle);
        }
        return bundle;
    }

    private void supportCommit(FragmentManager fm, FragmentTransaction transaction) {
        handleAfterSaveInStateTransactionException(fm, "commit()");
        transaction.commitAllowingStateLoss();
    }

    private boolean handleLaunchMode(FragmentManager fm, ISupportFragment topFragment, final ISupportFragment to, String toFragmentTag, int launchMode) {
        if (topFragment == null) {
            return false;
        }
        final ISupportFragment stackToFragment = SupportHelper.findBackStackFragment(to.getClass(), toFragmentTag, fm);
        if (stackToFragment == null) {
            return false;
        }
        if (launchMode == ISupportFragment.SINGLE_TOP) {
            if (to == topFragment || to.getClass().getName().equals(topFragment.getClass().getName())) {
                handleNewBundle(to, stackToFragment);
                return true;
            }
        } else if (launchMode == ISupportFragment.SINGLE_TASK) {
            doPopTo(toFragmentTag, false, fm, DEFAULT_POP_TO_ANIM);
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    handleNewBundle(to, stackToFragment);
                }
            });
            return true;
        }
        return false;
    }

    private void handleNewBundle(ISupportFragment toFragment, ISupportFragment stackToFragment) {
        Bundle argsNewBundle = toFragment.getSupportDelegate().mNewBundle;
        Bundle args = getArguments((Fragment) toFragment);
        if (args.containsKey(FRAGMENTATION_ARG_CONTAINER)) {
            args.remove(FRAGMENTATION_ARG_CONTAINER);
        }
        if (argsNewBundle != null) {
            args.putAll(argsNewBundle);
        }
        stackToFragment.onNewBundle(args);
    }

    private void doShowHideFragment(FragmentManager fm, ISupportFragment showFragment, ISupportFragment hideFragment) {
        if (showFragment == hideFragment) {
            return;
        }
        FragmentTransaction ft = fm.beginTransaction().show((Fragment) showFragment);
        if (hideFragment == null) {
            List<Fragment> fragmentList = FragmentationMagician.getActiveFragments(fm);
            for (Fragment fragment : fragmentList) {
                if (fragment != null && fragment != showFragment) {
                    ft.hide(fragment);
                }
            }
        } else {
            ft.hide((Fragment) hideFragment);
        }
        supportCommit(fm, ft);
    }

    private void doPopTo(final String targetFragmentTag, boolean includeTargetFragment, FragmentManager fm, int popAnim) {
        handleAfterSaveInStateTransactionException(fm, "popTo()");
        Fragment targetFragment = fm.findFragmentByTag(targetFragmentTag);
        if (targetFragment == null) {
            Timber.d("Pop failure! Can't find FragmentTag:" + targetFragmentTag + " in the FragmentManager's Stack.");
            return;
        }
        int flag = 0;
        if (includeTargetFragment) {
            flag = FragmentManager.POP_BACK_STACK_INCLUSIVE;
        }
        List<Fragment> willPopFragments = SupportHelper.getWillPopFragments(fm, targetFragmentTag, includeTargetFragment);
        if (willPopFragments.size() <= 0) {
            return;
        }
        Fragment top = willPopFragments.get(0);
        mockPopToAnim(top, targetFragmentTag, fm, flag, willPopFragments, popAnim);
    }

    /**
     * Save requestCode.
     */
    private void saveRequestCode(FragmentManager fm, Fragment from, Fragment to, int requestCode) {
        Bundle bundle = getArguments(to);
        ResultRecord resultRecord = new ResultRecord();
        resultRecord.requestCode = requestCode;
        bundle.putParcelable(FRAGMENTATION_ARG_RESULT_RECORD, resultRecord);
        fm.putFragment(bundle, FRAGMENTATION_STATE_SAVE_RESULT, from);
    }

    private void mockPopToAnim(Fragment from, String targetFragmentTag, FragmentManager fm, int flag, List<Fragment> willPopFragments, int popAnim) {
        if (!(from instanceof ISupportFragment)) {
            safePopTo(targetFragmentTag, fm, flag, willPopFragments);
            return;
        }
        final ISupportFragment fromSupport = (ISupportFragment) from;
        final ViewGroup container = findContainerById(from, fromSupport.getSupportDelegate().mContainerId);
        if (container == null) {
            return;
        }
        final View fromView = from.getView();
        if (fromView == null) {
            return;
        }
        container.removeViewInLayout(fromView);
        final ViewGroup mock = addMockView(fromView, container);
        safePopTo(targetFragmentTag, fm, flag, willPopFragments);
        Animation animation;
        if (popAnim == DEFAULT_POP_TO_ANIM) {
            animation = fromSupport.getSupportDelegate().getExitAnim();
            if (animation == null) {
                animation = new Animation() {
                };
            }
        } else if (popAnim == 0) {
            animation = new Animation() {
            };
        } else {
            animation = AnimationUtils.loadAnimation(mActivity, popAnim);
        }
        fromView.startAnimation(animation);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    mock.removeViewInLayout(fromView);
                    container.removeViewInLayout(mock);
                } catch (Exception ignored) {
                }
            }
        }, animation.getDuration());
    }

    private void safePopTo(String fragmentTag, final FragmentManager fm, int flag, List<Fragment> willPopFragments) {
        mSupport.getSupportDelegate().mPopMultipleNoAnim = true;
        FragmentTransaction transaction = fm.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
        for (Fragment fragment : willPopFragments) {
            transaction.remove(fragment);
        }
        transaction.commitAllowingStateLoss();
        FragmentationMagician.popBackStackAllowingStateLoss(fm, fragmentTag, flag);
        FragmentationMagician.executePendingTransactionsAllowingStateLoss(fm);
        mSupport.getSupportDelegate().mPopMultipleNoAnim = false;
    }

    @NonNull
    private ViewGroup addMockView(View fromView, ViewGroup container) {
        ViewGroup mock = new ViewGroup(mActivity) {
            @Override
            protected void onLayout(boolean changed, int l, int t, int r, int b) {
            }
        };
        mock.addView(fromView);
        container.addView(mock);
        return mock;
    }

    private @Nullable ViewGroup findContainerById(Fragment fragment, int containerId) {
        if (fragment.getView() == null) {
            return null;
        }
        View container;
        Fragment parentFragment = fragment.getParentFragment();
        if (parentFragment != null) {
            if (parentFragment.getView() != null) {
                container = parentFragment.getView().findViewById(containerId);
            } else {
                container = findContainerById(parentFragment, containerId);
            }
        } else {
            container = mActivity.findViewById(containerId);
        }
        if (container instanceof ViewGroup) {
            return (ViewGroup) container;
        }
        return null;
    }

    private void mockStartWithPopAnim(final ISupportFragment from, ISupportFragment to, final Animation exitAnim) {
        final Fragment fragment = (Fragment) from;
        final ViewGroup container = findContainerById(fragment, from.getSupportDelegate().mContainerId);
        if (container == null) {
            return;
        }
        final View fromView = fragment.getView();
        if (fromView == null) {
            return;
        }
        container.removeViewInLayout(fromView);
        final ViewGroup mock = addMockView(fromView, container);
        to.getSupportDelegate().mEnterAnimListener = new SupportFragmentDelegate.EnterAnimListener() {
            @Override
            public void onEnterAnimStart() {
                fromView.startAnimation(exitAnim);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            mock.removeViewInLayout(fromView);
                            container.removeViewInLayout(mock);
                        } catch (Exception ignored) {
                        }
                    }
                }, exitAnim.getDuration());
            }
        };
    }

    private void handleAfterSaveInStateTransactionException(FragmentManager fm, String action) {
        boolean stateSaved = FragmentationMagician.isStateSaved(fm);
        if (stateSaved) {
            AfterSaveStateTransactionWarningException e = new AfterSaveStateTransactionWarningException(action);
            if (Fragmentation.getDefault().getHandler() != null) {
                Fragmentation.getDefault().getHandler().onException(e);
            }
        }
    }
}
