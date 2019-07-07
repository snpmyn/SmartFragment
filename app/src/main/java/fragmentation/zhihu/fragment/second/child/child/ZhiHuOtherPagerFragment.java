package fragmentation.zhihu.fragment.second.child.child;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zsp.fragmentation.SupportFragment;
import com.zsp.smartfragment.R;

/**
 * @decs: ZhiHuOtherPagerFragment
 * @author: 郑少鹏
 * @date: 2019/1/21 17:00
 */
public class ZhiHuOtherPagerFragment extends SupportFragment {
    /**
     * Key
     */
    private static final String KEY_TITLE = "keyTitle";
    /**
     * 标题
     */
    private String mTitle;

    public static ZhiHuOtherPagerFragment newInstance(String title) {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_TITLE, title);
        ZhiHuOtherPagerFragment fragment = new ZhiHuOtherPagerFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTitle = getArguments().getString(KEY_TITLE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_zhihu_other_pager, container, false);
        initView();
        return view;
    }

    private void initView() {
        Log.e("initView", mTitle);
    }
}
