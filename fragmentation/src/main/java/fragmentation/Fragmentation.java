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
     * Dont display stack view.
     */
    static final int NONE = 0;
    private static volatile Fragmentation INSTANCE;
    private boolean debug;
    private int mode;
    private ExceptionHandler handler;

    private Fragmentation(FragmentationBuilder builder) {
        debug = builder.debug;
        if (debug) {
            mode = builder.mode;
        } else {
            mode = NONE;
        }
        handler = builder.handler;
    }

    public static Fragmentation getDefault() {
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

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public ExceptionHandler getHandler() {
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
         * @param debug Suppressed Exception("Can not perform this action after onSaveInstanceState!") when debug=false.
         */
        public FragmentationBuilder debug(boolean debug) {
            this.debug = debug;
            return this;
        }

        /**
         * FragmentationBuilder
         * <p>
         * Sets the mode to display the stack view.
         * None if debug(false).
         * Default:NONE.
         *
         * @param mode mode
         * @return FragmentationBuilder
         */
        public FragmentationBuilder stackViewMode(@StackViewMode int mode) {
            this.mode = mode;
            return this;
        }

        /**
         * @param handler Handled Exception("Can not perform this action after onSaveInstanceState!") when debug=false.
         */
        public FragmentationBuilder handleException(ExceptionHandler handler) {
            this.handler = handler;
            return this;
        }

        public Fragmentation install() {
            synchronized (Fragmentation.class) {
                if (Fragmentation.INSTANCE != null) {
                    throw new RuntimeException("Default instance already exists." + " It may be only set once before it's used the first time to ensure consistent behavior.");
                }
                Fragmentation.INSTANCE = new Fragmentation(this);
                return Fragmentation.INSTANCE;
            }
        }
    }
}
