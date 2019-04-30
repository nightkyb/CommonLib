package com.github.nightkyb.widgets;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

import com.github.nightkyb.utils.DimensionUtil;

/**
 * Author: nightkyb
 */
public class KeyboardLayout extends LinearLayout {
    private OnKeyboardLayoutListener mListener;
    private boolean mIsKeyboardActive = false;// 输入法是否激活
    private int mKeyboardHeight = 0;// 输入法高度

    public KeyboardLayout(Context context) {
        this(context, null, 0);
    }

    public KeyboardLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public KeyboardLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // 通过视图树监听布局变化
        getViewTreeObserver().addOnGlobalLayoutListener(new KeyboardOnGlobalChangeListener());
    }

    private class KeyboardOnGlobalChangeListener implements ViewTreeObserver.OnGlobalLayoutListener {
        int mScreenHeight = 0;
        Rect mRect = new Rect();

        private int getScreenHeight() {
            if (mScreenHeight > 0) {
                return mScreenHeight;
            }

            mScreenHeight = DimensionUtil.getScreenHeight(getContext());

            return mScreenHeight;
        }

        @Override
        public void onGlobalLayout() {
            // 获取当前页面窗口的显示范围
            getWindowVisibleDisplayFrame(mRect);

            int screenHeight = getScreenHeight(); //屏幕高度
            int keyboardHeight = screenHeight - mRect.bottom; // 输入法的高度
            boolean isActive = false;

            if (Math.abs(keyboardHeight) > screenHeight / 5) {
                isActive = true; // 超过屏幕五分之一则表示弹出了输入法
                mKeyboardHeight = keyboardHeight;
            }

            // LogUtil.i("键盘打开：" + isActive + "，键盘高度：" + keyboardHeight);

            // 打开时会多次回调该方法，所以要过滤无效的回调
            if (!mIsKeyboardActive && isActive) {
                mIsKeyboardActive = true;

                if (mListener != null) {
                    mListener.onKeyboardStateChanged(true, keyboardHeight);
                }
            } else if (mIsKeyboardActive && !isActive) {
                mIsKeyboardActive = false;

                if (mListener != null) {
                    mListener.onKeyboardStateChanged(false, keyboardHeight);
                }
            } else if (mIsKeyboardActive && isActive) {
                if (mListener != null) {
                    mListener.onEditTextSizeChanged(true, keyboardHeight);
                }
            }
        }
    }

    public void setOnKeyboardLayoutListener(OnKeyboardLayoutListener listener) {
        mListener = listener;
    }

    public OnKeyboardLayoutListener getOnKeyboardLayoutListener() {
        return mListener;
    }

    public boolean isKeyboardActive() {
        return mIsKeyboardActive;
    }

    /**
     * 获取输入法高度
     *
     * @return 输入法面板高度
     */
    public int getKeyboardHeight() {
        return mKeyboardHeight;
    }

    public interface OnKeyboardLayoutListener {
        /**
         * 输入法打开和关闭时回调
         *
         * @param isShow         输入法是否打开
         * @param keyboardHeight 输入法面板高度
         */
        void onKeyboardStateChanged(boolean isShow, int keyboardHeight);

        /**
         * EditText高度改变时回调，第一次打开也会回调
         *
         * @param isShow       输入法是否打开
         * @param keyboardHeight 输入法面板高度
         */
        void onEditTextSizeChanged(boolean isShow, int keyboardHeight);
    }
}
