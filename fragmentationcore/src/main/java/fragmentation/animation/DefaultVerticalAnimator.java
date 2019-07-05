package fragmentation.animation;

import android.os.Parcel;
import android.os.Parcelable;

import com.zsp.fragmentationcore.R;

/**
 * @decs: DefaultVerticalAnimator
 * @author: 郑少鹏
 * @date: 2019/5/20 9:23
 */
public class DefaultVerticalAnimator extends FragmentAnimator implements Parcelable {
    public static final Creator<DefaultVerticalAnimator> CREATOR = new Creator<DefaultVerticalAnimator>() {
        @Override
        public DefaultVerticalAnimator createFromParcel(Parcel in) {
            return new DefaultVerticalAnimator(in);
        }

        @Override
        public DefaultVerticalAnimator[] newArray(int size) {
            return new DefaultVerticalAnimator[size];
        }
    };

    private DefaultVerticalAnimator(Parcel in) {
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

    public DefaultVerticalAnimator() {
        enter = R.anim.v_fragment_enter;
        exit = R.anim.v_fragment_exit;
        popEnter = R.anim.v_fragment_pop_enter;
        popExit = R.anim.v_fragment_pop_exit;
    }
}
