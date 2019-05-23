package fragmentation;

import android.view.MotionEvent;

import fragmentation.animation.FragmentAnimator;

/**
 * @decs: ISupportActivity
 * @author: 郑少鹏
 * @date: 2019/5/20 9:43
 */
public interface ISupportActivity {
    /**
     * getSupportDelegate
     *
     * @return SupportActivityDelegate
     */
    SupportActivityDelegate getSupportDelegate();

    /**
     * extraTransaction
     *
     * @return BaseExtraTransaction
     */
    BaseExtraTransaction extraTransaction();

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
     * onCreateFragmentAnimator
     *
     * @return FragmentAnimator
     */
    FragmentAnimator onCreateFragmentAnimator();

    /**
     * post
     *
     * @param runnable runnable
     */
    void post(Runnable runnable);

    /**
     * onBackPressed
     */
    void onBackPressed();

    /**
     * onBackPressedSupport
     */
    void onBackPressedSupport();

    /**
     * dispatchTouchEvent
     *
     * @param ev 手势事件
     * @return 分发否
     */
    boolean dispatchTouchEvent(MotionEvent ev);
}
