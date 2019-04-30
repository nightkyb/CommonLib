package com.github.nightkyb.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

/**
 * <p>
 * getSize和getRealSize只能获取显示尺寸。<br/>
 * getMetrics和getRealMetrics方法，除了可以获取屏幕尺寸，还有其他屏幕相关信息，例如屏幕密度、文字缩放比例等。<br/>
 * 1、getSize/getMetrics返回的尺寸是应用显示区域，会除去那些一直显示的系统装饰元素，例如虚拟导航栏。<br/>
 * 2、getRealSize/getRealMetrics获取到的是实际屏幕显示尺寸，包括虚拟导航栏。<br/>
 * </p>
 * <p>
 * 不同的WindowManager实例获取到的Display对象也不同：<br/>
 * 1、如果是通过非Activity类型的上下文获取到的WindowManager，getSize/getMetrics返回的尺寸是基于当前屏幕旋转方向的全部显示尺寸，当然还要除去系统装饰区域。<br/>
 * 2、如果是通过Activity类型的上下文获取到WindowManager，getSize/getMetrics返回的尺寸和应用窗口尺寸一致。例如在多窗口模式下，应用窗口尺寸比物理尺寸小。<br/>
 * 我们在开发时往往并不需要包含虚拟按键区域的真实屏幕高度，应用显示区域高度足够了。<br/>
 * </p>
 *
 * Author: nightkyb
 */
public class DimensionUtil {
    public static int getScreenHeight(Context context) {
        DisplayMetrics metric = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metric);
        return metric.heightPixels;
    }

    public static int getScreenWidth(Context context) {
        DisplayMetrics metric = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metric);
        return metric.widthPixels;
    }

    /**
     * 获取屏幕宽高，不包括虚拟导航栏的高度
     *
     * @param context 注意非Activity类型的上下文跟Activity类型的上下文的区别
     * @return
     */
    public static Point getScreenSize(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point p = new Point();
        //不包含虚拟导航栏的高度
        display.getSize(p);
        return p;
    }

    /**
     * 获取屏幕宽高，包括虚拟导航栏的高度，如果有的话
     *
     * @param context 注意非Activity类型的上下文跟Activity类型的上下文的区别
     * @return
     */
    public static Point getScreenRealSize(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point p = new Point();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            //不包含底部导航栏的高度
            display.getSize(p);
        } else {
            //包含了底部导航栏的高度
            display.getRealSize(p);
        }
        return p;
    }

    /**
     * 直接读取系统状态栏高度的值，但是无法判断状态栏是否显示
     *
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        Resources resources = context.getResources();
        int height = 0;
        //获取status_bar_height资源的ID
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            height = resources.getDimensionPixelSize(resourceId);
        }
        LogUtil.i("状态栏高度为：" + height);
        return height;
    }

    /**
     * 直接读取底部导航栏高度的值，但是无法判断导航栏是否显示
     *
     * @param context
     * @return
     */
    public static int getNavigationBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        int height = resources.getDimensionPixelSize(resourceId);
        LogUtil.i("导航栏高度为：" + height);
        return height;
    }

    /**
     * 获取底部导航栏的高度，能判断底部导航栏是否已经显示
     *
     * @param context
     * @return 底部导航栏的高度或者0
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static int getNavigationBarHeightIfShow(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Display display = windowManager.getDefaultDisplay();
            DisplayMetrics metrics = new DisplayMetrics();
            //这个方法获取的可能不是真实屏幕的高度，如果有导航栏的话
            display.getMetrics(metrics);
            int usableHeight = metrics.heightPixels;
            //获取当前屏幕的真实高度
            display.getRealMetrics(metrics);
            int realHeight = metrics.heightPixels;
            if (realHeight > usableHeight) {
                return realHeight - usableHeight;
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }

    /**
     * 判断底部导航栏是否已经显示
     *
     * @param context
     * @return
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static boolean isNavigationBarShow(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        //这个方法获取的可能不是真实屏幕的高度，如果有导航栏的话
        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);
        //获取当前屏幕的真实高度
        DisplayMetrics realDisplayMetrics = new DisplayMetrics();
        display.getRealMetrics(realDisplayMetrics);

        int displayHeight = displayMetrics.heightPixels;
        int displayWidth = displayMetrics.widthPixels;

        int realHeight = realDisplayMetrics.heightPixels;
        int realWidth = realDisplayMetrics.widthPixels;

        return (realWidth - displayWidth) > 0 || (realHeight - displayHeight) > 0;
    }

    /**
     * dp 转 px
     *
     * @param dpValue dp 值
     * @return px 值
     */
    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * px 转 dp
     *
     * @param context
     * @param pxValue px 值
     * @return dp 值
     */
    public static int px2dp(Context context, float pxValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static void setMargins(View v, int left, int top, int right, int bottom) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(left, top, right, bottom);
            v.requestLayout();
        }
    }

    public static float getFontWidth(Paint paint, String text) {
        return paint.measureText(text);
    }

    /**
     * @return 返回指定的文字高度
     */
    public static float getFontHeight(Paint paint) {
        Paint.FontMetrics fm = paint.getFontMetrics();
        //文字基准线的下部距离-文字基准线的上部距离 = 文字高度
        return fm.descent - fm.ascent;
    }
}