package fragmentation;

import android.os.Build;
import android.view.View;

import androidx.annotation.AnimRes;
import androidx.annotation.AnimatorRes;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import java.util.ArrayList;

import fragmentation.helper.internal.TransactionRecord;

/**
 * @decs: BaseExtraTransaction
 * @author: 郑少鹏
 * @date: 2019/5/20 9:41
 */
public abstract class BaseExtraTransaction {
    /**
     * setTag
     *
     * @param tag Optional tag name for the fragment,
     *            to later retrieve the fragment with {@link SupportHelper#findFragment(FragmentManager, String)},
     *            pop(String) or FragmentManager.findFragmentByTag(String).
     * @return BaseExtraTransaction
     */
    public abstract BaseExtraTransaction setTag(String tag);

    /**
     * setCustomAnimations
     * <p>
     * Set specific animation resources to run for the fragments that are entering and exiting in this transaction.
     * These animations will not be played when popping the back stack.
     *
     * @param targetFragmentEnter    targetFragmentEnter
     * @param currentFragmentPopExit currentFragmentPopExit
     * @return BaseExtraTransaction
     */
    public abstract BaseExtraTransaction setCustomAnimations(@AnimatorRes @AnimRes int targetFragmentEnter, @AnimatorRes @AnimRes int currentFragmentPopExit);

    /**
     * setCustomAnimations
     * <p>
     * Set specific animation resources to run for the fragments that are entering and exiting in this transaction.
     * The <code>currentFragmentPopEnter</code>
     * and <code>targetFragmentExit</code> animations will be played for targetFragmentEnter/currentFragmentPopExit operations specifically when popping the back stack.
     *
     * @param targetFragmentEnter     targetFragmentEnter
     * @param currentFragmentPopExit  currentFragmentPopExit
     * @param currentFragmentPopEnter currentFragmentPopEnter
     * @param targetFragmentExit      targetFragmentExit
     * @return BaseExtraTransaction
     */
    public abstract BaseExtraTransaction setCustomAnimations(@AnimatorRes @AnimRes int targetFragmentEnter,
                                                             @AnimatorRes @AnimRes int currentFragmentPopExit,
                                                             @AnimatorRes @AnimRes int currentFragmentPopEnter,
                                                             @AnimatorRes @AnimRes int targetFragmentExit);

    /**
     * addSharedElement
     * <p>
     * Used with custom Transitions to map a View from a removed or hidden Fragment to a View from a shown or added Fragment.
     * <var>sharedElement</var> must have a unique transitionName in the View hierarchy.
     *
     * @param sharedElement A View in a disappearing Fragment to match with a View in an appearing Fragment.
     * @param sharedName    The transitionName for a View in an appearing Fragment to match to the shared element.
     * @return BaseExtraTransaction
     * @see Fragment#setSharedElementEnterTransition(Object)
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    public abstract BaseExtraTransaction addSharedElement(View sharedElement, String sharedName);

    /**
     * loadRootFragment
     *
     * @param containerId containerId
     * @param toFragment  toFragment
     */
    public abstract void loadRootFragment(int containerId, ISupportFragment toFragment);

    /**
     * loadRootFragment
     *
     * @param containerId    containerId
     * @param toFragment     toFragment
     * @param addToBackStack addToBackStack
     * @param allowAnim      allowAnim
     */
    public abstract void loadRootFragment(int containerId, ISupportFragment toFragment, boolean addToBackStack, boolean allowAnim);

    /**
     * start
     *
     * @param toFragment toFragment
     */
    public abstract void start(ISupportFragment toFragment);

    /**
     * startDoNotHideSelf
     *
     * @param toFragment toFragment
     */
    public abstract void startDoNotHideSelf(ISupportFragment toFragment);

    /**
     * startDoNotHideSelf
     *
     * @param toFragment toFragment
     * @param launchMode launchMode
     */
    public abstract void startDoNotHideSelf(ISupportFragment toFragment, @ISupportFragment.LaunchMode int launchMode);

    /**
     * start
     *
     * @param toFragment toFragment
     * @param launchMode launchMode
     */
    public abstract void start(ISupportFragment toFragment, @ISupportFragment.LaunchMode int launchMode);

    /**
     * startForResult
     *
     * @param toFragment  toFragment
     * @param requestCode requestCode
     */
    public abstract void startForResult(ISupportFragment toFragment, int requestCode);

    /**
     * startForResultDoNotHideSelf
     *
     * @param toFragment  toFragment
     * @param requestCode requestCode
     */
    public abstract void startForResultDoNotHideSelf(ISupportFragment toFragment, int requestCode);

    /**
     * startWithPop
     *
     * @param toFragment toFragment
     */
    public abstract void startWithPop(ISupportFragment toFragment);

    /**
     * startWithPopTo
     *
     * @param toFragment            toFragment
     * @param targetFragmentTag     targetFragmentTag
     * @param includeTargetFragment includeTargetFragment
     */
    public abstract void startWithPopTo(ISupportFragment toFragment, String targetFragmentTag, boolean includeTargetFragment);

    /**
     * replace
     *
     * @param toFragment toFragment
     */
    public abstract void replace(ISupportFragment toFragment);

    /**
     * popTo
     * <p>
     * setTag()自定Tag时通下popTo()／popToChild()出栈。
     *
     * @param targetFragmentTag     通setTag()所设tag
     * @param includeTargetFragment 含目标（Tag为targetFragmentTag）Fragment否
     */
    public abstract void popTo(String targetFragmentTag, boolean includeTargetFragment);

    /**
     * popTo
     *
     * @param targetFragmentTag           targetFragmentTag
     * @param includeTargetFragment       includeTargetFragment
     * @param afterPopTransactionRunnable afterPopTransactionRunnable
     * @param popAnim                     popAnim
     */
    public abstract void popTo(String targetFragmentTag, boolean includeTargetFragment, Runnable afterPopTransactionRunnable, int popAnim);

    /**
     * popToChild
     *
     * @param targetFragmentTag     targetFragmentTag
     * @param includeTargetFragment includeTargetFragment
     */
    public abstract void popToChild(String targetFragmentTag, boolean includeTargetFragment);

    /**
     * popToChild
     *
     * @param targetFragmentTag           targetFragmentTag
     * @param includeTargetFragment       includeTargetFragment
     * @param afterPopTransactionRunnable afterPopTransactionRunnable
     * @param popAnim                     popAnim
     */
    public abstract void popToChild(String targetFragmentTag, boolean includeTargetFragment, Runnable afterPopTransactionRunnable, int popAnim);

    /**
     * doNotAddToBackStack
     * <p>
     * Don't add this extraTransaction to the back stack.
     *
     * @return DoNotAddToBackStackTransaction
     */
    public abstract DoNotAddToBackStackTransaction doNotAddToBackStack();

    /**
     * 移除
     * <p>
     * doNotAddToBackStack()加载Fragment时通remove()移Fragment。
     *
     * @param fragment        fragment
     * @param showPreFragment showPreFragment
     */
    public abstract void remove(ISupportFragment fragment, boolean showPreFragment);

    public interface DoNotAddToBackStackTransaction {
        /**
         * start
         * <p>
         * add() + hide(preFragment)
         *
         * @param toFragment toFragment
         */
        void start(ISupportFragment toFragment);

        /**
         * add
         * <p>
         * Only add().
         *
         * @param toFragment toFragment
         */
        void add(ISupportFragment toFragment);

        /**
         * replace
         *
         * @param toFragment toFragment
         */
        void replace(ISupportFragment toFragment);
    }

    /**
     * Impl
     *
     * @param <T> ISupportFragment
     */
    final static class BaseExtraTransactionImpl<T extends ISupportFragment> extends BaseExtraTransaction implements DoNotAddToBackStackTransaction {
        private FragmentActivity mActivity;
        private T fSupport;
        private Fragment mFragment;
        private TransactionDelegate mTransactionDelegate;
        private boolean mFromActivity;
        private TransactionRecord mRecord;

        BaseExtraTransactionImpl(FragmentActivity activity, T fSupport, TransactionDelegate transactionDelegate, boolean fromActivity) {
            this.mActivity = activity;
            this.fSupport = fSupport;
            this.mFragment = (Fragment) fSupport;
            this.mTransactionDelegate = transactionDelegate;
            this.mFromActivity = fromActivity;
            mRecord = new TransactionRecord();
        }

        @Override
        public BaseExtraTransaction setTag(String tag) {
            mRecord.tag = tag;
            return this;
        }

        @Override
        public BaseExtraTransaction setCustomAnimations(@AnimRes int targetFragmentEnter, @AnimRes int currentFragmentPopExit) {
            mRecord.targetFragmentEnter = targetFragmentEnter;
            mRecord.currentFragmentPopExit = currentFragmentPopExit;
            mRecord.currentFragmentPopEnter = 0;
            mRecord.targetFragmentExit = 0;
            return this;
        }

        @Override
        public BaseExtraTransaction setCustomAnimations(@AnimRes int targetFragmentEnter,
                                                        @AnimRes int currentFragmentPopExit,
                                                        @AnimRes int currentFragmentPopEnter,
                                                        @AnimRes int targetFragmentExit) {
            mRecord.targetFragmentEnter = targetFragmentEnter;
            mRecord.currentFragmentPopExit = currentFragmentPopExit;
            mRecord.currentFragmentPopEnter = currentFragmentPopEnter;
            mRecord.targetFragmentExit = targetFragmentExit;
            return this;
        }

        @Override
        public BaseExtraTransaction addSharedElement(View sharedElement, String sharedName) {
            if (mRecord.sharedElementList == null) {
                mRecord.sharedElementList = new ArrayList<>();
            }
            mRecord.sharedElementList.add(new TransactionRecord.SharedElement(sharedElement, sharedName));
            return this;
        }

        @Override
        public void loadRootFragment(int containerId, ISupportFragment toFragment) {
            loadRootFragment(containerId, toFragment, true, false);
        }

        @Override
        public void loadRootFragment(int containerId, ISupportFragment toFragment, boolean addToBackStack, boolean allowAnim) {
            toFragment.getSupportDelegate().mTransactionRecord = mRecord;
            mTransactionDelegate.loadRootTransaction(getFragmentManager(), containerId, toFragment, addToBackStack, allowAnim);
        }

        @Override
        public DoNotAddToBackStackTransaction doNotAddToBackStack() {
            mRecord.doNotAddToBackStack = true;
            return this;
        }

        @Override
        public void remove(ISupportFragment fragment, boolean showPreFragment) {
            mTransactionDelegate.remove(getFragmentManager(), (Fragment) fragment, showPreFragment);
        }

        @Override
        public void popTo(String targetFragmentTag, boolean includeTargetFragment) {
            popTo(targetFragmentTag, includeTargetFragment, null, TransactionDelegate.DEFAULT_POP_TO_ANIM);
        }

        @Override
        public void popTo(String targetFragmentTag, boolean includeTargetFragment, Runnable afterPopTransactionRunnable, int popAnim) {
            mTransactionDelegate.popTo(targetFragmentTag, includeTargetFragment, afterPopTransactionRunnable, getFragmentManager(), popAnim);
        }

        @Override
        public void popToChild(String targetFragmentTag, boolean includeTargetFragment) {
            popToChild(targetFragmentTag, includeTargetFragment, null, TransactionDelegate.DEFAULT_POP_TO_ANIM);
        }

        @Override
        public void popToChild(String targetFragmentTag, boolean includeTargetFragment, Runnable afterPopTransactionRunnable, int popAnim) {
            if (mFromActivity) {
                popTo(targetFragmentTag, includeTargetFragment, afterPopTransactionRunnable, popAnim);
            } else {
                mTransactionDelegate.popTo(targetFragmentTag, includeTargetFragment, afterPopTransactionRunnable, mFragment.getChildFragmentManager(), popAnim);
            }
        }

        @Override
        public void add(ISupportFragment toFragment) {
            toFragment.getSupportDelegate().mTransactionRecord = mRecord;
            mTransactionDelegate.dispatchStartTransaction(getFragmentManager(), fSupport, toFragment, 0, ISupportFragment.STANDARD, TransactionDelegate.TYPE_ADD_WITHOUT_HIDE);
        }

        @Override
        public void start(ISupportFragment toFragment) {
            start(toFragment, ISupportFragment.STANDARD);
        }

        @Override
        public void startDoNotHideSelf(ISupportFragment toFragment) {
            toFragment.getSupportDelegate().mTransactionRecord = mRecord;
            mTransactionDelegate.dispatchStartTransaction(getFragmentManager(), fSupport, toFragment, 0, ISupportFragment.STANDARD, TransactionDelegate.TYPE_ADD_WITHOUT_HIDE);
        }

        @Override
        public void startDoNotHideSelf(ISupportFragment toFragment, @ISupportFragment.LaunchMode int launchMode) {
            toFragment.getSupportDelegate().mTransactionRecord = mRecord;
            mTransactionDelegate.dispatchStartTransaction(getFragmentManager(), fSupport, toFragment, 0, launchMode, TransactionDelegate.TYPE_ADD_WITHOUT_HIDE);
        }

        @Override
        public void start(ISupportFragment toFragment, @ISupportFragment.LaunchMode int launchMode) {
            toFragment.getSupportDelegate().mTransactionRecord = mRecord;
            mTransactionDelegate.dispatchStartTransaction(getFragmentManager(), fSupport, toFragment, 0, launchMode, TransactionDelegate.TYPE_ADD);
        }

        @Override
        public void startForResult(ISupportFragment toFragment, int requestCode) {
            toFragment.getSupportDelegate().mTransactionRecord = mRecord;
            mTransactionDelegate.dispatchStartTransaction(getFragmentManager(), fSupport, toFragment, requestCode, ISupportFragment.STANDARD, TransactionDelegate.TYPE_ADD_RESULT);
        }

        @Override
        public void startForResultDoNotHideSelf(ISupportFragment toFragment, int requestCode) {
            toFragment.getSupportDelegate().mTransactionRecord = mRecord;
            mTransactionDelegate.dispatchStartTransaction(getFragmentManager(), fSupport, toFragment, requestCode, ISupportFragment.STANDARD, TransactionDelegate.TYPE_ADD_RESULT_WITHOUT_HIDE);
        }

        @Override
        public void startWithPop(ISupportFragment toFragment) {
            toFragment.getSupportDelegate().mTransactionRecord = mRecord;
            mTransactionDelegate.startWithPop(getFragmentManager(), fSupport, toFragment);
        }

        @Override
        public void startWithPopTo(ISupportFragment toFragment, String targetFragmentTag, boolean includeTargetFragment) {
            toFragment.getSupportDelegate().mTransactionRecord = mRecord;
            mTransactionDelegate.startWithPopTo(getFragmentManager(), fSupport, toFragment, targetFragmentTag, includeTargetFragment);
        }

        @Override
        public void replace(ISupportFragment toFragment) {
            toFragment.getSupportDelegate().mTransactionRecord = mRecord;
            mTransactionDelegate.dispatchStartTransaction(getFragmentManager(), fSupport, toFragment, 0, ISupportFragment.STANDARD, TransactionDelegate.TYPE_REPLACE);
        }

        private FragmentManager getFragmentManager() {
            if (mFragment == null) {
                return mActivity.getSupportFragmentManager();
            }
            return mFragment.getFragmentManager();
        }
    }
}
