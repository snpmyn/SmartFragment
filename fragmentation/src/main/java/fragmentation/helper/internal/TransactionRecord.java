package fragmentation.helper.internal;

import android.view.View;

import java.util.ArrayList;

/**
 * @decs: TransactionRecord
 * @author: 郑少鹏
 * @date: 2019/5/20 9:34
 */
public final class TransactionRecord {
    public String tag;
    public int targetFragmentEnter = Integer.MIN_VALUE;
    public int currentFragmentPopExit = Integer.MIN_VALUE;
    public int currentFragmentPopEnter = Integer.MIN_VALUE;
    public int targetFragmentExit = Integer.MIN_VALUE;
    public boolean doNotAddToBackStack = false;
    public ArrayList<SharedElement> sharedElementList;

    public static class SharedElement {
        public View sharedElement;
        public String sharedName;

        public SharedElement(View sharedElement, String sharedName) {
            this.sharedElement = sharedElement;
            this.sharedName = sharedName;
        }
    }
}
