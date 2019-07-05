package fragmentation.helper.internal;

import android.content.Context;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.zsp.fragmentationcore.R;

import fragmentation.animation.FragmentAnimator;

/**
 * @decs: AnimatorHelper
 * @author: 郑少鹏
 * @date: 2019/5/20 9:33
 */
public final class AnimatorHelper {
    public Animation enterAnimation, exitAnimation, popEnterAnimation, popExitAnimation;
    private Animation noneAnimation, noneAnimationFixed;
    private Context context;
    private FragmentAnimator fragmentAnimator;

    public AnimatorHelper(Context context, FragmentAnimator fragmentAnimator) {
        this.context = context;
        notifyChanged(fragmentAnimator);
    }

    public void notifyChanged(FragmentAnimator fragmentAnimator) {
        this.fragmentAnimator = fragmentAnimator;
        initEnterAnimation();
        initExitAnimation();
        initPopEnterAnimation();
        initPopExitAnimation();
    }

    public Animation getNoneAnimation() {
        if (noneAnimation == null) {
            noneAnimation = AnimationUtils.loadAnimation(context, R.anim.no_anim);
        }
        return noneAnimation;
    }

    public Animation getNoneAnimationFixed() {
        if (noneAnimationFixed == null) {
            noneAnimationFixed = new Animation() {
            };
        }
        return noneAnimationFixed;
    }

    @Nullable
    public Animation compatChildFragmentExitAnim(Fragment fragment) {
        boolean flag = (fragment.getTag() != null && fragment.getTag().startsWith("android:switcher:") && fragment.getUserVisibleHint()) ||
                (fragment.getParentFragment() != null && fragment.getParentFragment().isRemoving() && !fragment.isHidden());
        if (flag) {
            Animation animation = new Animation() {
            };
            animation.setDuration(exitAnimation.getDuration());
            return animation;
        }
        return null;
    }

    private Animation initEnterAnimation() {
        if (fragmentAnimator.getEnter() == 0) {
            enterAnimation = AnimationUtils.loadAnimation(context, R.anim.no_anim);
        } else {
            enterAnimation = AnimationUtils.loadAnimation(context, fragmentAnimator.getEnter());
        }
        return enterAnimation;
    }

    private Animation initExitAnimation() {
        if (fragmentAnimator.getExit() == 0) {
            exitAnimation = AnimationUtils.loadAnimation(context, R.anim.no_anim);
        } else {
            exitAnimation = AnimationUtils.loadAnimation(context, fragmentAnimator.getExit());
        }
        return exitAnimation;
    }

    private Animation initPopEnterAnimation() {
        if (fragmentAnimator.getPopEnter() == 0) {
            popEnterAnimation = AnimationUtils.loadAnimation(context, R.anim.no_anim);
        } else {
            popEnterAnimation = AnimationUtils.loadAnimation(context, fragmentAnimator.getPopEnter());
        }
        return popEnterAnimation;
    }

    private Animation initPopExitAnimation() {
        if (fragmentAnimator.getPopExit() == 0) {
            popExitAnimation = AnimationUtils.loadAnimation(context, R.anim.no_anim);
        } else {
            popExitAnimation = AnimationUtils.loadAnimation(context, fragmentAnimator.getPopExit());
        }
        return popExitAnimation;
    }
}
