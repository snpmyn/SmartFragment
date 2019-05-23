package fragmentation.helper.internal;

import android.content.Context;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.zsp.fragmentation.R;

import fragmentation.animation.FragmentAnimator;

/**
 * @decs: AnimatorHelper
 * @author: 郑少鹏
 * @date: 2019/5/20 9:33
 */
public final class AnimatorHelper {
    public Animation enterAnim, exitAnim, popEnterAnim, popExitAnim;
    private Animation noneAnim, noneAnimFixed;
    private Context context;
    private FragmentAnimator fragmentAnimator;

    public AnimatorHelper(Context context, FragmentAnimator fragmentAnimator) {
        this.context = context;
        notifyChanged(fragmentAnimator);
    }

    public void notifyChanged(FragmentAnimator fragmentAnimator) {
        this.fragmentAnimator = fragmentAnimator;
        initEnterAnim();
        initExitAnim();
        initPopEnterAnim();
        initPopExitAnim();
    }

    public Animation getNoneAnim() {
        if (noneAnim == null) {
            noneAnim = AnimationUtils.loadAnimation(context, R.anim.fragmentation_no);
        }
        return noneAnim;
    }

    public Animation getNoneAnimFixed() {
        if (noneAnimFixed == null) {
            noneAnimFixed = new Animation() {
            };
        }
        return noneAnimFixed;
    }

    @Nullable
    public Animation compatChildFragmentExitAnim(Fragment fragment) {
        boolean flag = (fragment.getTag() != null && fragment.getTag().startsWith("android:switcher:") && fragment.getUserVisibleHint()) ||
                (fragment.getParentFragment() != null && fragment.getParentFragment().isRemoving() && !fragment.isHidden());
        if (flag) {
            Animation animation = new Animation() {
            };
            animation.setDuration(exitAnim.getDuration());
            return animation;
        }
        return null;
    }

    private Animation initEnterAnim() {
        if (fragmentAnimator.getEnter() == 0) {
            enterAnim = AnimationUtils.loadAnimation(context, R.anim.fragmentation_no);
        } else {
            enterAnim = AnimationUtils.loadAnimation(context, fragmentAnimator.getEnter());
        }
        return enterAnim;
    }

    private Animation initExitAnim() {
        if (fragmentAnimator.getExit() == 0) {
            exitAnim = AnimationUtils.loadAnimation(context, R.anim.fragmentation_no);
        } else {
            exitAnim = AnimationUtils.loadAnimation(context, fragmentAnimator.getExit());
        }
        return exitAnim;
    }

    private Animation initPopEnterAnim() {
        if (fragmentAnimator.getPopEnter() == 0) {
            popEnterAnim = AnimationUtils.loadAnimation(context, R.anim.fragmentation_no);
        } else {
            popEnterAnim = AnimationUtils.loadAnimation(context, fragmentAnimator.getPopEnter());
        }
        return popEnterAnim;
    }

    private Animation initPopExitAnim() {
        if (fragmentAnimator.getPopExit() == 0) {
            popExitAnim = AnimationUtils.loadAnimation(context, R.anim.fragmentation_no);
        } else {
            popExitAnim = AnimationUtils.loadAnimation(context, fragmentAnimator.getPopExit());
        }
        return popExitAnim;
    }
}
