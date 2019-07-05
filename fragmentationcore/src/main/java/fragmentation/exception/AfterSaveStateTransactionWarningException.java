package fragmentation.exception;

import android.util.Log;

/**
 * @decs: AfterSaveStateTransactionWarningException
 * Perform the transaction action after onSaveInstanceState.
 * <p>
 * This is dangerous because the action can be lost if the activity needs to later be restored from its state.
 * @author: 郑少鹏
 * @date: 2019/5/20 9:30
 */
public class AfterSaveStateTransactionWarningException extends RuntimeException {
    public AfterSaveStateTransactionWarningException(String action) {
        super("Warning: Perform this " + action + " action after onSaveInstanceState!");
        if (null == getMessage()) {
            return;
        }
        Log.w("Fragmentation", getMessage());
    }
}

