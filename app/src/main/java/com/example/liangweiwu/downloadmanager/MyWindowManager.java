package com.example.liangweiwu.downloadmanager;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.PixelFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;


public class MyWindowManager {
    /**
     * 小悬浮窗View的实例
     */
    private static FloatingBtnView smallWindow;

    /**
     * 大悬浮窗View的实例
     */
    private static FloatingPopupWindowView bigWindow;

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
    public static void createSmallWindow(Context context) {
        Log.i("state","Floating Icon Show");
        WindowManager windowManager = getWindowManager(context);
        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);
        int screenWidth = metrics.widthPixels;
        int screenHeight = metrics.heightPixels;
        if (smallWindow == null) {
            smallWindow = new FloatingBtnView(context);
            if (smallWindowParams == null) {
                smallWindowParams = new WindowManager.LayoutParams();
                smallWindowParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
                smallWindowParams.format = PixelFormat.RGBA_8888;

                smallWindowParams.flags =   WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
                smallWindowParams.gravity = Gravity.TOP|Gravity.LEFT;
                smallWindowParams.width = FloatingBtnView.viewWidth;
                smallWindowParams.height = FloatingBtnView.viewHeight;
                smallWindowParams.x = screenWidth;
                smallWindowParams.y = screenHeight/2-FloatingBtnView.viewHeight/2;
            }
            smallWindow.setParams(smallWindowParams);
            windowManager.addView(smallWindow, smallWindowParams);
        }
    }

    /**
     * 将小悬浮窗从屏幕上移除。
     *
     * @param context
     *            必须为应用程序的Context.
     */
    public static void removeSmallWindow(Context context) {
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
    public static void createBigWindow(Context context) {
        Log.i("state","Floating Popup Window Show");
        WindowManager windowManager = getWindowManager(context);
        /*
        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);
        int screenWidth = metrics.widthPixels;
        int screenHeight = metrics.heightPixels;
        */
        if (bigWindow == null) {
            bigWindow = new FloatingPopupWindowView(context);
            if (bigWindowParams == null) {
                bigWindowParams = new WindowManager.LayoutParams();
                bigWindowParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
                bigWindowParams.flags =    WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                      | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                      |WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

                bigWindowParams.format = PixelFormat.RGBA_8888;
                bigWindowParams.gravity = Gravity.CENTER;
                bigWindowParams.width = FloatingPopupWindowView.viewWidth;
                bigWindowParams.height = FloatingPopupWindowView.viewHeight;
            }
            windowManager.addView(bigWindow, bigWindowParams);
        }
    }

    public static void createGameDetailWindow(Context context){
        Log.i("state","Game Detail Show");
        WindowManager windowManager = getWindowManager(context);
        
    }

    /**
     * 将大悬浮窗从屏幕上移除。
     *
     * @param context
     *            必须为应用程序的Context.
     */
    public static void removeBigWindow(Context context) {
        if (bigWindow != null) {
            WindowManager windowManager = getWindowManager(context);
            windowManager.removeView(bigWindow);
            bigWindow = null;
        }
    }

    /**
     * 是否有悬浮窗(包括小悬浮窗和大悬浮窗)显示在屏幕上。
     *
     * @return 有悬浮窗显示在桌面上返回true，没有的话返回false。
     */
    public static boolean isWindowShowing() {
        return smallWindow != null || bigWindow != null;
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
