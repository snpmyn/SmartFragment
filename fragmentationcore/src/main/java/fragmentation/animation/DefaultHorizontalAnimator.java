package fragmentation.animation;

import android.os.Parcel;
import android.os.Parcelable;

import com.zsp.fragmentationcore.R;

/**
 * @decs: DefaultHorizontalAnimator
 * @author: 郑少鹏
 * @date: 2019/5/20 9:23
 */
public class DefaultHorizontalAnimator extends FragmentAnimator implements Parcelable {
    public static final Creator<DefaultHorizontalAnimator> CREATOR = new Creator<DefaultHorizontalAnimator>() {
        @Override
        public DefaultHorizontalAnimator createFromParcel(Parcel in) {
            return new DefaultHorizontalAnimator(in);
        }

        @Override
        public DefaultHorizontalAnimator[] newArray(int size) {
            return new DefaultHorizontalAnimator[size];
        }
    };

    private DefaultHorizontalAnimator(Parcel in) {
        super(in);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public DefaultHorizontalAnimator() {
        enter = R.anim.horizontal_fragment_enter;
        exit = R.anim.horizontal_fragment_exit;
        popEnter = R.anim.horizontal_fragment_pop_enter;
        popExit = R.anim.horizontal_fragment_pop_exit;
    }
}
