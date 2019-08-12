package fragmentation.zhihu.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import fragmentation.zhihu.fragment.second.child.child.ZhiHuFirstPagerFragment;
import fragmentation.zhihu.fragment.second.child.child.ZhiHuOtherPagerFragment;

/**
 * Created on 2019/1/21.
 *
 * @author 郑少鹏
 * @desc ZhiHuPagerFragmentAdapter
 */
public class ZhiHuPagerFragmentAdapter extends FragmentPagerAdapter {
    private String[] mTitles;

    public ZhiHuPagerFragmentAdapter(FragmentManager fm, String... titles) {
        super(fm);
        mTitles = titles;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return ZhiHuFirstPagerFragment.newInstance();
        } else {
            return ZhiHuOtherPagerFragment.newInstance(mTitles[position]);
        }
    }

    @Override
    public int getCount() {
        return mTitles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles[position];
    }
}

