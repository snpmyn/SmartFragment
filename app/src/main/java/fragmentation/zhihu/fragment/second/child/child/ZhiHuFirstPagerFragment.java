package fragmentation.zhihu.fragment.second.child.child;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zsp.fragmentation.SupportFragment;
import com.zsp.smartfragment.R;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import fragmentation.zhihu.fragment.second.other.ZhiHuDetailFragment;

/**
 * @decs: FirstPagerFragment
 * @author: 郑少鹏
 * @date: 2019/1/21 16:59
 */
public class ZhiHuFirstPagerFragment extends SupportFragment {
    private Unbinder unbinder;

    public static ZhiHuFirstPagerFragment newInstance() {
        Bundle bundle = new Bundle();
        ZhiHuFirstPagerFragment fragment = new ZhiHuFirstPagerFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_zhihu_first_pager, container, false);
        unbinder = ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {

    }

    @OnClick(R.id.mainActivityMbFatherFragmentLaunch)
    void onViewClicked(View view) {
        if (view.getId() == R.id.mainActivityMbFatherFragmentLaunch) {
            // 此处父Fragment启动（注意栈层级）
            if (getParentFragment() != null) {
                ((SupportFragment) getParentFragment()).start(ZhiHuDetailFragment.newInstance(getString(R.string.testTitle)));
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
