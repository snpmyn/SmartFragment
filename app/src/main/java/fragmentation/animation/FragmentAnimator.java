package fragmentation.animation;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.AnimRes;

/**
 * @decs: 碎片动画实体类
 * @author: 郑少鹏
 * @date: 2019/5/20 9:24
 */
public class FragmentAnimator implements Parcelable {
    public static final Creator<FragmentAnimator> CREATOR = new Creator<FragmentAnimator>() {
        @Override
        public FragmentAnimator createFromParcel(Parcel in) {
            return new FragmentAnimator(in);
        }

        @Override
        public FragmentAnimator[] newArray(int size) {
            return new FragmentAnimator[size];
        }
    };
    @AnimRes
    int enter;
    @AnimRes
    int exit;
    @AnimRes
    int popEnter;
    @AnimRes
    int popExit;

    public FragmentAnimator() {

    }

    public FragmentAnimator(int enter, int exit) {
        this.enter = enter;
        this.exit = exit;
    }

    public FragmentAnimator(int enter, int exit, int popEnter, int popExit) {
        this.enter = enter;
        this.exit = exit;
        this.popEnter = popEnter;
        this.popExit = popExit;
    }

    protected FragmentAnimator(Parcel in) {
        enter = in.readInt();
        exit = in.readInt();
        popEnter = in.readInt();
        popExit = in.readInt();
    }

    public FragmentAnimator copy() {
        return new FragmentAnimator(getEnter(), getExit(), getPopEnter(), getPopExit());
    }

    public int getEnter() {
        return enter;
    }

    public FragmentAnimator setEnter(int enter) {
        this.enter = enter;
        return this;
    }

    public int getExit() {
        return exit;
    }

    /**
     * enter animation
     *
     * @param exit exit
     * @return FragmentAnimator
     */
    public FragmentAnimator setExit(int exit) {
        this.exit = exit;
        return this;
    }

    public int getPopEnter() {
        return popEnter;
    }

    public FragmentAnimator setPopEnter(int popEnter) {
        this.popEnter = popEnter;
        return this;
    }

    public int getPopExit() {
        return popExit;
    }

    public FragmentAnimator setPopExit(int popExit) {
        this.popExit = popExit;
        return this;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(enter);
        dest.writeInt(exit);
        dest.writeInt(popEnter);
        dest.writeInt(popExit);
    }
}
