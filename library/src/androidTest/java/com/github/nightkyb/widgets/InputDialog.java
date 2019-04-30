package com.github.nightkyb.widgets;

import android.app.Dialog;
import android.content.Context;
import android.text.InputFilter;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.StringRes;
import androidx.annotation.StyleRes;

import com.github.nightkyb.R;

/**
 * 弹出评论输入框
 * Author: nightkyb
 */
public class InputDialog extends Dialog {
    private Context context;
    private KeyboardLayout klRoot;
    private View outsideView;
    private LinearLayout llContainer;
    private EditText etContent;
    private Button btnSubmit;
    private OnInputListener onInputListener;

    public InputDialog(Context context) {
        this(context, R.style.InputDialog);
    }

    private InputDialog(Context context, @StyleRes int theme) {
        super(context, theme);
        this.context = context;
        init();
    }

    private void init() {
        setContentView(R.layout.dialog_input);
        setCancelable(true);
        setCanceledOnTouchOutside(true);

        // 设置宽度
        Window window = getWindow();
        window.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(lp);
        // 这样设置软键盘不会遮住EditText
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        klRoot = findViewById(R.id.kl_root);
        outsideView = findViewById(R.id.outside_view);
        llContainer = findViewById(R.id.ll_container);
        etContent = findViewById(R.id.et_content);
        btnSubmit = findViewById(R.id.btn_submit);

        klRoot.setOnKeyboardLayoutListener(new KeyboardLayout.OnKeyboardLayoutListener() {
            @Override
            public void onKeyboardStateChanged(boolean isShow, int keyboardHeight) {
                if (isShow) {
                    if (onInputListener != null) {
                        int[] coordinate = new int[2];
                        llContainer.getLocationOnScreen(coordinate);
                        // 传入输入框距离屏幕顶部（不包括状态栏）的位置
                        onInputListener.onShow(coordinate);
                    }
                } else {
                    dismiss();

                    if (onInputListener != null) {
                        onInputListener.onDismiss();
                    }
                }
            }

            @Override
            public void onEditTextSizeChanged(boolean isShow, int keyboardHeight) {
                if (onInputListener != null) {
                    int[] coordinate = new int[2];
                    llContainer.getLocationOnScreen(coordinate);
                    // 传入输入框距离屏幕顶部（不包括状态栏）的位置
                    onInputListener.onShow(coordinate);
                }
            }
        });

        outsideView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();

                if (onInputListener != null) {
                    onInputListener.onDismiss();
                }
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onInputListener != null) {
                    onInputListener.onSubmit(InputDialog.this, etContent, btnSubmit);
                }
            }
        });

        // 支持多行，同时支持imeOptions设置，一定要在代码中设置，在xml中设置无效！
        etContent.setHorizontallyScrolling(false);
        etContent.setMaxLines(6);
        // 弹出键盘
        etContent.setFocusable(true);
        etContent.setFocusableInTouchMode(true);
        etContent.requestFocus();
        etContent.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                if (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER || actionId == EditorInfo.IME_ACTION_SEND) {
                    if (onInputListener != null) {
                        onInputListener.onSubmit(InputDialog.this, etContent, btnSubmit);
                    }

                    return true;
                }

                return false;
            }
        });
    }

    public InputDialog setContentMaxLength(int maxLength) {
        if (maxLength >= 0) {
            etContent.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
        } else {
            etContent.setFilters(new InputFilter[0]);
        }
        return this;
    }

    public InputDialog setContentText(String content) {
        etContent.setText(content);
        return this;
    }

    public String getContentText() {
        return etContent.getText().toString();
    }

    public InputDialog setHintText(String hintText) {
        etContent.setHint(hintText);
        return this;
    }

    public InputDialog setHintText(@StringRes int hintTextRes) {
        etContent.setHint(hintTextRes);
        return this;
    }

    public InputDialog setButtonText(String buttonText) {
        btnSubmit.setText(buttonText);
        return this;
    }

    public InputDialog setButtonText(@StringRes int buttonTextRes) {
        btnSubmit.setText(buttonTextRes);
        return this;
    }

    public InputDialog setOnInputListener(OnInputListener onInputListener) {
        this.onInputListener = onInputListener;
        return this;
    }

    /**
     * 输入对话框相关监听
     */
    public interface OnInputListener {
        void onSubmit(Dialog dialog, EditText etInput, Button btnSubmit);

        /**
         * onShow在输入法弹出后才调用
         *
         * @param inputViewCoordinateOnScreen 输入框距离屏幕顶部（不包括状态栏）的位置[left,top]
         */
        void onShow(int[] inputViewCoordinateOnScreen);

        void onDismiss();
    }
}
