package fragmentation.zhihu.activity;

import android.os.Bundle;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.zsp.fragmentation.SupportFragment;
import com.zsp.smartfragment.R;

import base.BaseActivity;
import fragmentation.BottomBar;
import fragmentation.BottomBarTab;
import fragmentation.base.BaseFragment;
import fragmentation.zhihu.fragment.first.ZhiHuFirstFragment;
import fragmentation.zhihu.fragment.fourth.ZhiHuFourthFragment;
import fragmentation.zhihu.fragment.second.ZhiHuSecondFragment;
import fragmentation.zhihu.fragment.third.ZhiHuThirdFragment;

/**
 * @decs: 知乎
 * @author: 郑少鹏
 * @date: 2019/1/15 16:56
 */
public class ZhiHuActivity extends BaseActivity implements BaseFragment.OnBackToFirstListener {
    private BottomBar bottomBar;
    public static final int FIRST = 0;
    public static final int SECOND = 1;
    public static final int THIRD = 2;
    public static final int FOURTH = 3;
    private final SupportFragment[] supportFragments = new SupportFragment[4];

    /**
     * 加载视图
     *
     * @param savedInstanceState 状态保存
     */
    @Override
    protected void initContentView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_zhi_hu);
    }

    /**
     * 初始控件
     */
    @Override
    protected void stepUi() {
        bottomBar = findViewById(R.id.zhiHuActivityBr);
    }

    /**
     * 初始配置
     */
    @Override
    protected void initConfiguration() {
        bottomBar.addItem(new BottomBarTab(this, R.mipmap.ic_launcher))
                .addItem(new BottomBarTab(this, R.mipmap.ic_launcher))
                .addItem(new BottomBarTab(this, R.mipmap.ic_launcher))
                .addItem(new BottomBarTab(this, R.mipmap.ic_launcher));
        // 碎片
        SupportFragment firstFragment = findFragment(ZhiHuFirstFragment.class);
        if (firstFragment == null) {
            supportFragments[FIRST] = ZhiHuFirstFragment.newInstance();
            supportFragments[SECOND] = ZhiHuSecondFragment.newInstance();
            supportFragments[THIRD] = ZhiHuThirdFragment.newInstance();
            supportFragments[FOURTH] = ZhiHuFourthFragment.newInstance();
            loadMultipleRootFragment(R.id.zhiHuActivityFlContainer, FIRST,
                    supportFragments[FIRST],
                    supportFragments[SECOND],
                    supportFragments[THIRD],
                    supportFragments[FOURTH]);
        } else {
            // 此处库已做Fragment恢复（无需额外处理，无重叠问题）
            // 此处需拿到mFragments引用
            supportFragments[FIRST] = firstFragment;
            supportFragments[SECOND] = findFragment(ZhiHuSecondFragment.class);
            supportFragments[THIRD] = findFragment(ZhiHuThirdFragment.class);
            supportFragments[FOURTH] = findFragment(ZhiHuFourthFragment.class);
        }
    }

    /**
     * 设置监听
     */
    @Override
    protected void setListener() {
        bottomBar.setOnTabSelectedListener(new BottomBar.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position, int prePosition) {
                Toast.makeText(ZhiHuActivity.this, "onTabSelected" + position + "|" + prePosition, Toast.LENGTH_SHORT).show();
                showHideFragment(supportFragments[position], supportFragments[prePosition]);
            }

            @Override
            public void onTabUnselected(int position) {
                Toast.makeText(ZhiHuActivity.this, "onTabUnselected" + position, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTabReselected(int position) {
                Toast.makeText(ZhiHuActivity.this, "onTabReselected" + position, Toast.LENGTH_SHORT).show();
                /*final SupportFragment currentFragment = supportFragments[position];
                int count = currentFragment.getChildFragmentManager().getBackStackEntryCount();
                // 不在该类别Fragment主页则回主页
                if (count > 1) {
                    if (currentFragment instanceof ZhiHuFirstFragment) {
                        currentFragment.popToChild(FirstHomeFragment.class, false);
                    } else if (currentFragment instanceof ZhiHuSecondFragment) {
                        currentFragment.popToChild(ViewPagerFragment.class, false);
                    } else if (currentFragment instanceof ZhiHuThirdFragment) {
                        currentFragment.popToChild(ShopFragment.class, false);
                    } else if (currentFragment instanceof ZhiHuFourthFragment) {
                        currentFragment.popToChild(MeFragment.class, false);
                    }
                    return;
                }
                // 此处推荐用EventBus实现（解耦）
                if (count == 1) {
                    // FirstPagerFragment中接收（因嵌套的孙子Fragment故EventBus较方便）
                    // 主要为交互
                    // 重选tab（列表不在顶部则移到顶部，已在顶部则刷新）
                    EventBusActivityScope.getDefault(MainActivity.this).post(new TabSelectedEvent(position));
                }*/
            }
        });
    }

    /**
     * 开始逻辑
     */
    @Override
    protected void startLogic() {

    }

    /**
     * 任意Fragment之onBackPressedSupport()返true，该法都不被回调。
     */
    @Override
    public void onBackPressedSupport() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            pop();
        } else {
            ActivityCompat.finishAfterTransition(this);
        }
    }

    @Override
    public void onBackToFirstFragment() {
        bottomBar.setCurrentItem(0);
    }
    /*
      此处暂没实现（忽略）
     */
    /*@Subscribe
    public void onHiddenBottombarEvent(boolean hidden) {
        if (hidden) {
            bottomBar.hide();
        } else {
            bottomBar.show();
        }
    }*/
}

