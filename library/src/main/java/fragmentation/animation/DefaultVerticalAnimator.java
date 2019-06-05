package fragmentation.animation;

import android.os.Parcel;
import android.os.Parcelable;

import com.zsp.smart.fragment.library.R;

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

    public DefaultVerticalAnimator() {
        enter = R.anim.fragmentation_enter_two;
        exit = R.anim.fragmentation_exit_two;
        popEnter = R.anim.fragmentation_pop_enter_two;
        popExit = R.anim.fragmentation_pop_exit_two;
    }

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
}
