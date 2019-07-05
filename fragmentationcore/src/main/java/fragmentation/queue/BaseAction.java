package fragmentation.queue;

import androidx.fragment.app.FragmentManager;

/**
 * @decs: BaseAction
 * @author: 郑少鹏
 * @date: 2019/5/20 9:36
 */
public abstract class BaseAction {
    static final long DEFAULT_POP_TIME = 300L;
    public static final int ACTION_NORMAL = 0;
    public static final int ACTION_POP = 1;
    public static final int ACTION_POP_MOCK = 2;
    public static final int ACTION_BACK = 3;
    public static final int ACTION_LOAD = 4;
    FragmentManager fragmentManager;
    public long duration = 0;
    int action = ACTION_NORMAL;

    protected BaseAction() {

    }

    protected BaseAction(int action) {
        this.action = action;
    }

    protected BaseAction(int action, FragmentManager fragmentManager) {
        this(action);
        this.fragmentManager = fragmentManager;
    }

    /**
     * run
     */
    public abstract void run();
}
