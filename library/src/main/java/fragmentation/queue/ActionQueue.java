package fragmentation.queue;

import android.os.Handler;
import android.os.Looper;

import java.util.LinkedList;
import java.util.Queue;

import fragmentation.ISupportFragment;
import fragmentation.SupportHelper;

/**
 * @decs: ActionQueue
 * The queue of perform action.
 * @author: 郑少鹏
 * @date: 2019/5/20 9:38
 */
public class ActionQueue {
    private Queue<BaseAction> mQueue = new LinkedList<>();
    private Handler mMainHandler;

    public ActionQueue(Handler mainHandler) {
        this.mMainHandler = mainHandler;
    }

    public void enqueue(final BaseAction baseAction) {
        if (isThrottleBack(baseAction)) {
            return;
        }
        if (baseAction.action == BaseAction.ACTION_LOAD && mQueue.isEmpty()
                && Thread.currentThread() == Looper.getMainLooper().getThread()) {
            baseAction.run();
            return;
        }
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                enqueueAction(baseAction);
            }
        });
    }

    private void enqueueAction(BaseAction baseAction) {
        mQueue.add(baseAction);
        if (mQueue.size() == 1) {
            handleAction();
        }
    }

    private void handleAction() {
        if (mQueue.isEmpty()) {
            return;
        }
        BaseAction baseAction = mQueue.peek();
        if (baseAction != null) {
            baseAction.run();
            executeNextAction(baseAction);
        }
    }

    private void executeNextAction(BaseAction baseAction) {
        if (baseAction.action == BaseAction.ACTION_POP) {
            ISupportFragment top = SupportHelper.getBackStackTopFragment(baseAction.fragmentManager);
            baseAction.duration = top == null ? BaseAction.DEFAULT_POP_TIME : top.getSupportDelegate().getExitAnimDuration();
        }
        mMainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mQueue.poll();
                handleAction();
            }
        }, baseAction.duration);
    }

    private boolean isThrottleBack(BaseAction baseAction) {
        if (baseAction.action == BaseAction.ACTION_BACK) {
            BaseAction head = mQueue.peek();
            return head != null && head.action == BaseAction.ACTION_POP;
        }
        return false;
    }
}
