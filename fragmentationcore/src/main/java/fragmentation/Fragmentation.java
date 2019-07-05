package fragmentation;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import fragmentation.helper.ExceptionHandler;

/**
 * @decs: Fragmentation
 * @author: 郑少鹏
 * @date: 2019/5/20 9:42
 */
public class Fragmentation {
    /**
     * Shake it to display stack view.
     */
    public static final int SHAKE = 1;
    /**
     * As a bubble display stack view.
     */
    public static final int BUBBLE = 2;
    /**
     * Do not display stack view.
     */
    static final int NONE = 0;
    private static volatile Fragmentation INSTANCE;
    private boolean debug;
    private int mode;
    private ExceptionHandler handler;

    static Fragmentation getDefault() {
        if (INSTANCE == null) {
            synchronized (Fragmentation.class) {
                if (INSTANCE == null) {
                    INSTANCE = new Fragmentation(new FragmentationBuilder());
                }
            }
        }
        return INSTANCE;
    }

    public static FragmentationBuilder builder() {
        return new FragmentationBuilder();
    }

    private Fragmentation(FragmentationBuilder builder) {
        debug = builder.debug;
        if (debug) {
            mode = builder.mode;
        } else {
            mode = NONE;
        }
        handler = builder.handler;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    ExceptionHandler getHandler() {
        return handler;
    }

    public void setHandler(ExceptionHandler handler) {
        this.handler = handler;
    }

    int getMode() {
        return mode;
    }

    public void setMode(@StackViewMode int mode) {
        this.mode = mode;
    }

    @IntDef({NONE, SHAKE, BUBBLE})
    @Retention(RetentionPolicy.SOURCE)
    @interface StackViewMode {

    }

    public static class FragmentationBuilder {
        private boolean debug;
        private int mode;
        private ExceptionHandler handler;

        /**
         * FragmentationBuilder
         *
         * @param debug Suppressed Exception("Can not perform this action after onSaveInstanceState!") when debug=false.
         * @return FragmentationBuilder
         */
        public FragmentationBuilder debug(boolean debug) {
            this.debug = debug;
            return this;
        }

        /**
         * stackViewMode
         * <p>
         * Sets the mode to display the stack view.
         * <p>
         * None if debug(false).
         * <p>
         * Default:NONE
         *
         * @param mode int
         * @return FragmentationBuilder
         */
        public FragmentationBuilder stackViewMode(@StackViewMode int mode) {
            this.mode = mode;
            return this;
        }

        /**
         * FragmentationBuilder
         *
         * @param exceptionHandler Handled Exception("Can not perform this action after onSaveInstanceState!") when debug=false.
         * @return FragmentationBuilder
         */
        public FragmentationBuilder handleException(ExceptionHandler exceptionHandler) {
            this.handler = exceptionHandler;
            return this;
        }

        public Fragmentation install() {
            Fragmentation.INSTANCE = new Fragmentation(this);
            return Fragmentation.INSTANCE;
        }
    }
}
