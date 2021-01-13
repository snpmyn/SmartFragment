package fragmentation;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentationMagician;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @decs: SupportHelper
 * @author: 郑少鹏
 * @date: 2019/5/20 9:53
 */
public class SupportHelper {
    private static final long SHOW_SPACE = 200L;

    private SupportHelper() {

    }

    /**
     * 显软键盘
     *
     * @param view 视图
     */
    static void showSoftInput(final View view) {
        if (view == null || view.getContext() == null) {
            return;
        }
        final InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        view.requestFocus();
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (imm != null) {
                    imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
                }
            }
        }, SHOW_SPACE);
    }

    /**
     * 隐软键盘
     *
     * @param view 视图
     */
    static void hideSoftInput(View view) {
        if (view == null || view.getContext() == null) {
            return;
        }
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * 显栈视图对话框
     * 调试用。
     */
    public static void showFragmentStackHierarchyView(ISupportActivity support) {
        support.getSupportDelegate().showFragmentStackHierarchyView();
    }

    /**
     * 显栈视图日志
     * 调试用。
     */
    public static void logFragmentStackHierarchy(ISupportActivity support, String tag) {
        support.getSupportDelegate().logFragmentStackHierarchy(tag);
    }

    /**
     * 栈顶SupportFragment
     *
     * @param fragmentManager 碎片管理器
     * @return ISupportFragment
     */
    public static ISupportFragment getTopFragment(FragmentManager fragmentManager) {
        return getTopFragment(fragmentManager, 0);
    }

    static @Nullable ISupportFragment getTopFragment(FragmentManager fragmentManager, int containerId) {
        List<Fragment> fragmentList = FragmentationMagician.getActiveFragments(fragmentManager);
        for (int i = fragmentList.size() - 1; i >= 0; i--) {
            Fragment fragment = fragmentList.get(i);
            if (fragment instanceof ISupportFragment) {
                ISupportFragment iFragment = (ISupportFragment) fragment;
                if (containerId == 0) {
                    return iFragment;
                }
                if (containerId == iFragment.getSupportDelegate().mContainerId) {
                    return iFragment;
                }
            }
        }
        return null;
    }

    /**
     * 目标Fragment前一SupportFragment
     *
     * @param fragment 目标Fragment
     */
    public static @Nullable ISupportFragment getPreFragment(Fragment fragment) {
        FragmentManager fragmentManager = fragment.getFragmentManager();
        if (fragmentManager == null) {
            return null;
        }
        List<Fragment> fragmentList = FragmentationMagician.getActiveFragments(fragmentManager);
        int index = fragmentList.indexOf(fragment);
        for (int i = index - 1; i >= 0; i--) {
            Fragment preFragment = fragmentList.get(i);
            if (preFragment instanceof ISupportFragment) {
                return (ISupportFragment) preFragment;
            }
        }
        return null;
    }

    /**
     * Same as fragmentManager.findFragmentByTag(fragmentClass.getName());
     * <p>
     * Find Fragment from FragmentStack.
     */
    public static <T extends ISupportFragment> T findFragment(FragmentManager fragmentManager, Class<T> fragmentClass) {
        return findStackFragment(fragmentClass, null, fragmentManager);
    }

    /**
     * Same as fragmentManager.findFragmentByTag(fragmentTag);
     * <p>
     * Find Fragment from FragmentStack.
     */
    static <T extends ISupportFragment> T findFragment(FragmentManager fragmentManager, String fragmentTag) {
        return findStackFragment(null, fragmentTag, fragmentManager);
    }

    /**
     * 栈顶开始找FragmentManager及其所有子栈，直至找到状show & userVisible的Fragment
     *
     * @param fragmentManager 碎片管理器
     * @return ISupportFragment
     */
    static ISupportFragment getActiveFragment(FragmentManager fragmentManager) {
        return getActiveFragment(fragmentManager, null);
    }

    @SuppressWarnings("unchecked")
    private static <T extends ISupportFragment> T findStackFragment(Class<T> fragmentClass, String toFragmentTag, FragmentManager fragmentManager) {
        Fragment fragment = null;
        if (toFragmentTag == null) {
            List<Fragment> fragmentList = FragmentationMagician.getActiveFragments(fragmentManager);
            int sizeChildFrgList = fragmentList.size();
            for (int i = sizeChildFrgList - 1; i >= 0; i--) {
                Fragment brotherFragment = fragmentList.get(i);
                if (brotherFragment instanceof ISupportFragment && brotherFragment.getClass().getName().equals(fragmentClass.getName())) {
                    fragment = brotherFragment;
                    break;
                }
            }
        } else {
            fragment = fragmentManager.findFragmentByTag(toFragmentTag);
            if (fragment == null) {
                return null;
            }
        }
        return (T) fragment;
    }

    private static ISupportFragment getActiveFragment(FragmentManager fragmentManager, ISupportFragment parentFragment) {
        List<Fragment> fragmentList = FragmentationMagician.getActiveFragments(fragmentManager);
        for (int i = fragmentList.size() - 1; i >= 0; i--) {
            Fragment fragment = fragmentList.get(i);
            if (fragment instanceof ISupportFragment) {
                if (fragment.isResumed() && !fragment.isHidden() && fragment.getUserVisibleHint()) {
                    return getActiveFragment(fragment.getChildFragmentManager(), (ISupportFragment) fragment);
                }
            }
        }
        return parentFragment;
    }

    /**
     * Get the topFragment from BackStack.
     */
    public static ISupportFragment getBackStackTopFragment(FragmentManager fragmentManager) {
        return getBackStackTopFragment(fragmentManager, 0);
    }

    /**
     * Get the topFragment from BackStack.
     */
    private static @Nullable ISupportFragment getBackStackTopFragment(FragmentManager fragmentManager, int containerId) {
        int count = fragmentManager.getBackStackEntryCount();
        for (int i = count - 1; i >= 0; i--) {
            FragmentManager.BackStackEntry entry = fragmentManager.getBackStackEntryAt(i);
            Fragment fragment = fragmentManager.findFragmentByTag(entry.getName());
            if (fragment instanceof ISupportFragment) {
                ISupportFragment supportFragment = (ISupportFragment) fragment;
                if (containerId == 0) {
                    return supportFragment;
                }
                if (containerId == supportFragment.getSupportDelegate().mContainerId) {
                    return supportFragment;
                }
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    static <T extends ISupportFragment> T findBackStackFragment(Class<T> fragmentClass, String toFragmentTag, FragmentManager fragmentManager) {
        int count = fragmentManager.getBackStackEntryCount();
        if (toFragmentTag == null) {
            toFragmentTag = fragmentClass.getName();
        }
        for (int i = count - 1; i >= 0; i--) {
            FragmentManager.BackStackEntry entry = fragmentManager.getBackStackEntryAt(i);
            if (toFragmentTag.equals(entry.getName())) {
                Fragment fragment = fragmentManager.findFragmentByTag(entry.getName());
                if (fragment instanceof ISupportFragment) {
                    return (T) fragment;
                }
            }
        }
        return null;
    }

    static List<Fragment> getWillPopFragments(FragmentManager fm, String targetTag, boolean includeTarget) {
        Fragment target = fm.findFragmentByTag(targetTag);
        List<Fragment> willPopFragments = new ArrayList<>();
        List<Fragment> fragmentList = FragmentationMagician.getActiveFragments(fm);
        int size = fragmentList.size();
        int startIndex = -1;
        for (int i = size - 1; i >= 0; i--) {
            if (target == fragmentList.get(i)) {
                if (includeTarget) {
                    startIndex = i;
                } else if (i + 1 < size) {
                    startIndex = i + 1;
                }
                break;
            }
        }
        if (startIndex == -1) {
            return willPopFragments;
        }
        for (int i = size - 1; i >= startIndex; i--) {
            Fragment fragment = fragmentList.get(i);
            if (fragment != null && fragment.getView() != null) {
                willPopFragments.add(fragment);
            }
        }
        return willPopFragments;
    }
}
