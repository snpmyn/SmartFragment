package fragmentation;

import android.os.Bundle;

import androidx.annotation.IntDef;
import androidx.annotation.Nullable;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import fragmentation.animation.FragmentAnimator;

/**
 * @decs: ISupportFragment
 * @author: 郑少鹏
 * @date: 2019/5/20 9:44
 */
public interface ISupportFragment {
    /**
     * LaunchMode
     */
    int STANDARD = 0;
    int SINGLE_TOP = 1;
    int SINGLE_TASK = 2;
    /**
     * ResultCode
     */
    int RESULT_CANCELED = 0;
    int RESULT_OK = -1;

    @IntDef({STANDARD, SINGLE_TOP, SINGLE_TASK})
    @Retention(RetentionPolicy.SOURCE)
    @interface LaunchMode {

    }

    /**
     * getSupportDelegate
     *
     * @return SupportFragmentDelegate
     */
    SupportFragmentDelegate getSupportDelegate();

    /**
     * extraTransaction
     *
     * @return BaseExtraTransaction
     */
    BaseExtraTransaction extraTransaction();

    /**
     * enqueueAction
     *
     * @param runnable runnable
     */
    void enqueueAction(Runnable runnable);

    /**
     * post
     *
     * @param runnable runnable
     */
    void post(Runnable runnable);

    /**
     * onEnterAnimationEnd
     *
     * @param savedInstanceState savedInstanceState
     */
    void onEnterAnimationEnd(@Nullable Bundle savedInstanceState);

    /**
     * onLazyInitView
     *
     * @param savedInstanceState savedInstanceState
     */
    void onLazyInitView(@Nullable Bundle savedInstanceState);

    /**
     * onSupportVisible
     */
    void onSupportVisible();

    /**
     * onSupportInvisible
     */
    void onSupportInvisible();

    /**
     * isSupportVisible
     *
     * @return 支持可见否
     */
    boolean isSupportVisible();

    /**
     * onCreateFragmentAnimator
     *
     * @return FragmentAnimator
     */
    FragmentAnimator onCreateFragmentAnimator();

    /**
     * getFragmentAnimator
     *
     * @return FragmentAnimator
     */
    FragmentAnimator getFragmentAnimator();

    /**
     * setFragmentAnimator
     *
     * @param fragmentAnimator fragmentAnimator
     */
    void setFragmentAnimator(FragmentAnimator fragmentAnimator);

    /**
     * setFragmentResult
     *
     * @param resultCode resultCode
     * @param bundle     bundle
     */
    void setFragmentResult(int resultCode, Bundle bundle);

    /**
     * onFragmentResult
     *
     * @param requestCode 响应码
     * @param resultCode  结果码
     * @param data        数据
     */
    void onFragmentResult(int requestCode, int resultCode, Bundle data);

    /**
     * onNewBundle
     *
     * @param args args
     */
    void onNewBundle(Bundle args);

    /**
     * putNewBundle
     *
     * @param newBundle newBundle
     */
    void putNewBundle(Bundle newBundle);

    /**
     * onBackPressedSupport
     *
     * @return 支持回退否
     */
    boolean onBackPressedSupport();
}
