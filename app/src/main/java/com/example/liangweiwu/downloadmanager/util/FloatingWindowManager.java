package com.example.liangweiwu.downloadmanager.util;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;

import com.example.liangweiwu.downloadmanager.R;
import com.example.liangweiwu.downloadmanager.view.views.FloatingBtn;
import com.example.liangweiwu.downloadmanager.view.views.FloatingPopWin;


public class FloatingWindowManager {
    /**
     * 小悬浮窗View的实例
     */
    private static FloatingBtn smallWindow;

    /**
     * 大悬浮窗View的实例
     */
    private static FloatingPopWin bigWindow;

    /**
     * 小悬浮窗View的参数
     */
    private static WindowManager.LayoutParams smallWindowParams;

    /**
     * 大悬浮窗View的参数
     */
    private static WindowManager.LayoutParams bigWindowParams;

    /**
     * 用于控制在屏幕上添加或移除悬浮窗
     */
    private static WindowManager mWindowManager;


    /**
     * 创建一个小悬浮窗。初始位置为屏幕的右部中间位置。
     *
     * @param context
     *            必须为应用程序的Context.
     */
    public static void createFloatingIcon(Context context) {
        Log.i("state","Floating Icon Show");
        WindowManager windowManager = getWindowManager(context);
        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);
        int screenWidth = metrics.widthPixels;
        int screenHeight = metrics.heightPixels;
        if (smallWindow == null) {
            smallWindow = new FloatingBtn(context);
            if (smallWindowParams == null) {
                smallWindowParams = new WindowManager.LayoutParams();
                smallWindowParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
                smallWindowParams.format = PixelFormat.RGBA_8888;

                smallWindowParams.flags =   WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
                smallWindowParams.gravity = Gravity.TOP|Gravity.LEFT;
                smallWindowParams.width = FloatingBtn.viewWidth;
                smallWindowParams.height = FloatingBtn.viewHeight;
                smallWindowParams.x = screenWidth;
                smallWindowParams.y = screenHeight/2- FloatingBtn.viewHeight/2;
            }
            smallWindow.setParams(smallWindowParams);
            if(ApkInfoUtils.getInstance() == null){
                ApkInfoUtils.init(context);
            }
            updateFloatIcon(ApkInfoUtils.getInstance().getLatestDownloadedApkIcon());
            windowManager.addView(smallWindow, smallWindowParams);
        }
    }
    public static void updateFloatIcon(Drawable drawable){
        if(smallWindow == null || drawable == null){
            return;
        }
        drawable = drawable.getConstantState().newDrawable();
        smallWindow.findViewById(R.id.floating_btn).setBackground(drawable);
    }

    /**
     * 将小悬浮窗从屏幕上移除。
     *
     * @param context
     *            必须为应用程序的Context.
     */
    public static void removeFloatingIcon(Context context) {
        if (smallWindow != null) {
            WindowManager windowManager = getWindowManager(context);
            windowManager.removeView(smallWindow);
            smallWindow = null;
        }
    }

    /**
     * 创建一个大悬浮窗。位置为屏幕正中间。
     *
     * @param context
     *            必须为应用程序的Context.
     */
    public static void createPopupWindow(Context context) {
        Log.i("state","Floating Popup Window Show");
        WindowManager windowManager = getWindowManager(context);
        /*
        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);
        int screenWidth = metrics.widthPixels;
        int screenHeight = metrics.heightPixels;
        */
        if (bigWindow == null) {
            bigWindow = new FloatingPopWin(context);
            if (bigWindowParams == null) {
                bigWindowParams = new WindowManager.LayoutParams();
                bigWindowParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
                bigWindowParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                      | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

                bigWindowParams.format = PixelFormat.RGBA_8888;
                bigWindowParams.gravity = Gravity.CENTER;
                bigWindowParams.width = FloatingPopWin.viewWidth;
                bigWindowParams.height = FloatingPopWin.viewHeight;
            }
            windowManager.addView(bigWindow, bigWindowParams);
        }
    }

    /**
     * 将大悬浮窗从屏幕上移除。
     *
     * @param context
     *            必须为应用程序的Context.
     */
    public static void removePopupWindow(Context context) {
        if (bigWindow != null) {
            WindowManager windowManager = getWindowManager(context);
            windowManager.removeView(bigWindow);
            bigWindow = null;
        }
    }

    /**
     * 如果WindowManager还未创建，则创建一个新的WindowManager返回。否则返回当前已创建的WindowManager。
     *
     * @param context
     *            必须为应用程序的Context.
     * @return WindowManager的实例，用于控制在屏幕上添加或移除悬浮窗。
     */
    private static WindowManager getWindowManager(Context context) {
        if (mWindowManager == null) {
            mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        }
        return mWindowManager;
    }

}
