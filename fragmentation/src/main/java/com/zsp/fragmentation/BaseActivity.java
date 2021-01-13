package com.zsp.fragmentation;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;

import com.zsp.utilone.keyboard.KeyboardUtils;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 2021/1/13
 *
 * @author zsp
 * @desc BaseActivity
 * 优点：
 * 方便代码编写，减重复代码，加快开发；
 * 优化代码结构，降耦合度，方便修改；
 * 提代码可读性，显井井有条、优美。
 * <p>
 * 下抽象法子类须实现：
 * {{@link #initContentView(Bundle)}}
 * {{@link #stepUi()}}
 * {{@link #initConfiguration()}}
 * {{@link #setListener()}}
 * {{@link #startLogic()}}
 */
public abstract class BaseActivity extends SupportActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 加载视图
        initContentView(savedInstanceState);
        // 初始控件
        stepUi();
        // 初始配置
        initConfiguration();
        // 设置监听
        setListener();
        // 开始逻辑
        startLogic();
    }

    /**
     * 加载视图
     *
     * @param savedInstanceState 状态保存
     */
    protected abstract void initContentView(Bundle savedInstanceState);

    /**
     * 初始控件
     */
    protected abstract void stepUi();

    /**
     * 初始配置
     */
    protected abstract void initConfiguration();

    /**
     * 设置监听
     */
    protected abstract void setListener();

    /**
     * 开始逻辑
     */
    protected abstract void startLogic();

    /**
     * 清 EditText 焦点
     *
     * @param v   焦点所在 View
     * @param ids 输入框
     */
    protected void clearViewFocus(View v, int... ids) {
        if ((null != v) && (null != ids) && (ids.length > 0)) {
            for (int id : ids) {
                if (v.getId() == id) {
                    v.clearFocus();
                    break;
                }
            }
        }
    }

    /**
     * 隐键盘
     *
     * @param v   焦点所在 View
     * @param ids 输入框
     * @return true 表焦点在 EditText
     */
    protected boolean isFocusEditText(View v, int... ids) {
        if (v instanceof EditText) {
            EditText editText = (EditText) v;
            for (int id : ids) {
                if (editText.getId() == id) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 传 EditText 的 id
     * 没传入 EditText 不处理
     *
     * @return id 数组
     */
    protected int[] hideSoftByEditViewIds() {
        return null;
    }

    /**
     * 传需过滤 View
     * 过滤后点无隐软键盘操作
     *
     * @return id 数组
     */
    protected View[] filterViewByIds() {
        return null;
    }

    /**
     * 触摸指定 View 否（过滤控件）
     *
     * @param views 视图
     * @param ev    手势事件
     * @return boolean
     */
    protected boolean isTouchView(View[] views, MotionEvent ev) {
        if (views == null || views.length == 0) {
            return false;
        }
        int[] location = new int[2];
        for (View view : views) {
            view.getLocationOnScreen(location);
            int x = location[0];
            int y = location[1];
            if (ev.getX() > x && ev.getX() < (x + view.getWidth())
                    && ev.getY() > y && ev.getY() < (y + view.getHeight())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 触摸指定 View 否（过滤控件）
     *
     * @param ids 控件数组
     * @param ev  手势事件
     * @return boolean
     */
    protected boolean isTouchView(int[] ids, MotionEvent ev) {
        int[] location = new int[2];
        for (int id : ids) {
            View view = findViewById(id);
            if (view == null) {
                continue;
            }
            view.getLocationOnScreen(location);
            int x = location[0];
            int y = location[1];
            if (ev.getX() > x && ev.getX() < (x + view.getWidth()) && ev.getY() > y && ev.getY() < (y + view.getHeight())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Note: return supportActivityDelegate.dispatchTouchEvent(ev) || super.dispatchTouchEvent(ev);
     *
     * @param ev 手势事件
     * @return boolean
     */
    @Override
    public boolean dispatchTouchEvent(@NotNull MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            if (isTouchView(filterViewByIds(), ev)) {
                return super.dispatchTouchEvent(ev);
            }
            if (hideSoftByEditViewIds() == null || hideSoftByEditViewIds().length == 0) {
                return super.dispatchTouchEvent(ev);
            }
            View v = getCurrentFocus();
            if (isFocusEditText(v, hideSoftByEditViewIds())) {
                if (isTouchView(hideSoftByEditViewIds(), ev)) {
                    return super.dispatchTouchEvent(ev);
                }
                // 隐键盘
                KeyboardUtils.closeKeyboardInActivity(this);
                clearViewFocus(v, hideSoftByEditViewIds());
            }
        }
        return super.dispatchTouchEvent(ev);
    }
}


