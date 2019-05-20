package fragmentation.helper;

import androidx.annotation.NonNull;

/**
 * @decs: ExceptionHandler
 * @author: 郑少鹏
 * @date: 2019/5/20 9:36
 */
public interface ExceptionHandler {
    /**
     * onException
     *
     * @param e Exception
     */
    void onException(@NonNull Exception e);
}
