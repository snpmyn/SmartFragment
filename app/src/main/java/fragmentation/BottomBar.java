package fragmentation;

import android.content.Context;
import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;

import androidx.core.view.ViewCompat;

/**
 * @decs: BottomBar
 * @author: 郑少鹏
 * @date: 2019/1/15 16:12
 */
public class BottomBar extends LinearLayout {
    private static final int TRANSLATE_DURATION_MILLIS = 200;
    private final Interpolator mInterpolator = new AccelerateDecelerateInterpolator();
    private boolean mVisible = true;
    private LinearLayout mTabLayout;
    private LayoutParams mTabParams;
    private int mCurrentPosition = 0;
    private OnTabSelectedListener mTabSelectedListener;

    public BottomBar(Context context) {
        this(context, null);
    }

    public BottomBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BottomBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setOrientation(VERTICAL);
        mTabLayout = new LinearLayout(context);
        mTabLayout.setBackgroundColor(Color.WHITE);
        mTabLayout.setOrientation(LinearLayout.HORIZONTAL);
        addView(mTabLayout, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mTabParams = new LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        mTabParams.weight = 1;
    }

    public BottomBar addItem(final BottomBarTab bottomBarTab) {
        bottomBarTab.setOnClickListener(v -> {
            if (mTabSelectedListener == null) {
                return;
            }
            int pos = bottomBarTab.getTabPosition();
            if (mCurrentPosition == pos) {
                mTabSelectedListener.onTabReselected(pos);
            } else {
                mTabSelectedListener.onTabSelected(pos, mCurrentPosition);
                bottomBarTab.setSelected(true);
                mTabSelectedListener.onTabUnselected(mCurrentPosition);
                mTabLayout.getChildAt(mCurrentPosition).setSelected(false);
                mCurrentPosition = pos;
            }
        });
        bottomBarTab.setTabPosition(mTabLayout.getChildCount());
        bottomBarTab.setLayoutParams(mTabParams);
        mTabLayout.addView(bottomBarTab);
        return this;
    }

    public void setOnTabSelectedListener(OnTabSelectedListener onTabSelectedListener) {
        this.mTabSelectedListener = onTabSelectedListener;
    }

    public void setCurrentItem(final int position) {
        mTabLayout.post(() -> mTabLayout.getChildAt(position).performClick());
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        return new SavedState(superState, mCurrentPosition);
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        if (mCurrentPosition != ss.position) {
            mTabLayout.getChildAt(mCurrentPosition).setSelected(false);
            mTabLayout.getChildAt(ss.position).setSelected(true);
        }
        mCurrentPosition = ss.position;
    }

    public int getCurrentItemPosition() {
        return mCurrentPosition;
    }

    public void hide() {
        hide(true);
    }

    public void show() {
        show(true);
    }

    public void hide(boolean anim) {
        toggle(false, anim, false);
    }

    public void show(boolean anim) {
        toggle(true, anim, false);
    }

    public boolean isVisible() {
        return mVisible;
    }

    private void toggle(final boolean visible, final boolean animate, boolean force) {
        if (mVisible != visible || force) {
            mVisible = visible;
            int height = getHeight();
            if (height == 0 && !force) {
                ViewTreeObserver vto = getViewTreeObserver();
                if (vto.isAlive()) {
                    // View树完成测量并分配空间而绘过程还没开始时放动画
                    vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                        @Override
                        public boolean onPreDraw() {
                            ViewTreeObserver currentVto = getViewTreeObserver();
                            if (currentVto.isAlive()) {
                                currentVto.removeOnPreDrawListener(this);
                            }
                            toggle(visible, animate, true);
                            return true;
                        }
                    });
                    return;
                }
            }
            int yTranslation = visible ? 0 : height;
            if (animate) {
                animate().setInterpolator(mInterpolator).setDuration(TRANSLATE_DURATION_MILLIS).translationY(yTranslation);
            } else {
                ViewCompat.setTranslationY(this, yTranslation);
            }
        }
    }

    public interface OnTabSelectedListener {
        /**
         * onTabSelected
         *
         * @param position    位
         * @param prePosition 前位
         */
        void onTabSelected(int position, int prePosition);

        /**
         * onTabUnselected
         *
         * @param position 位
         */
        void onTabUnselected(int position);

        /**
         * onTabReselected
         *
         * @param position 位
         */
        void onTabReselected(int position);
    }

    static class SavedState extends BaseSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
        private final int position;

        SavedState(Parcel source) {
            super(source);
            position = source.readInt();
        }

        SavedState(Parcelable superState, int position) {
            super(superState);
            this.position = position;
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(position);
        }
    }
}
