package fragmentation.zhihu.fragment.second.child;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.zsp.fragmentation.SupportFragment;
import com.zsp.smartfragment.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import fragmentation.zhihu.adapter.ZhiHuPagerFragmentAdapter;

/**
 * @decs: ZhiHuViewPagerFragment
 * @author: 郑少鹏
 * @date: 2019/1/21 16:34
 */
public class ZhiHuViewPagerFragment extends SupportFragment {
    @BindView(R.id.zhiHuViewPagerFragmentTl)
    TabLayout zhiHuViewPagerFragmentTl;
    @BindView(R.id.zhiHuViewPagerFragmentVp)
    ViewPager zhiHuViewPagerFragmentVp;
    private Unbinder unbinder;

    public static ZhiHuViewPagerFragment newInstance() {
        Bundle bundle = new Bundle();
        ZhiHuViewPagerFragment fragment = new ZhiHuViewPagerFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_zhihu_view_pager, container, false);
        unbinder = ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
        zhiHuViewPagerFragmentTl.addTab(zhiHuViewPagerFragmentTl.newTab().setText("Tab 1").setIcon(R.mipmap.ic_launcher));
        zhiHuViewPagerFragmentTl.addTab(zhiHuViewPagerFragmentTl.newTab().setText("Tab 2").setIcon(R.mipmap.ic_launcher));
        zhiHuViewPagerFragmentTl.addTab(zhiHuViewPagerFragmentTl.newTab().setText("Tab 3").setIcon(R.mipmap.ic_launcher));
        zhiHuViewPagerFragmentTl.addTab(zhiHuViewPagerFragmentTl.newTab().setText("Tab 4").setIcon(R.mipmap.ic_launcher));
        zhiHuViewPagerFragmentTl.setTabMode(TabLayout.MODE_FIXED);
        zhiHuViewPagerFragmentTl.setTabGravity(TabLayout.GRAVITY_FILL);
        zhiHuViewPagerFragmentVp.setAdapter(new ZhiHuPagerFragmentAdapter(getChildFragmentManager(), "Tab 1", "Tab 1", "Tab 1", "Tab 1"));
        zhiHuViewPagerFragmentTl.setupWithViewPager(zhiHuViewPagerFragmentVp);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
