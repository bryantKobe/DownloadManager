package com.example.liangweiwu.downloadmanager.view.views;

import android.content.Context;
import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.example.liangweiwu.downloadmanager.util.FloatingWindowManager;
import com.example.liangweiwu.downloadmanager.R;

import java.lang.reflect.Field;

public class FloatingBtn extends LinearLayout {
    /**
     * 记录小悬浮窗的宽度
     */
    public static int viewWidth;

    /**
     * 记录小悬浮窗的高度
     */
    public static int viewHeight;

    /**
     * 记录系统状态栏的高度
     */
    private static int statusBarHeight;

    /**
     * 用于更新小悬浮窗的位置
     */
    private WindowManager windowManager;
    /**
     * 用于获取横竖屏状态
     */
    private Configuration configuration;

    /**
     * 小悬浮窗的参数
     */
    private WindowManager.LayoutParams mParams;

    /**
     * 记录当前手指位置在屏幕上的横坐标值
     */
    private float xInScreen;

    /**
     * 记录当前手指位置在屏幕上的纵坐标值
     */
    private float yInScreen;

    /**
     * 记录手指按下时在屏幕上的横坐标的值
     */
    private float xDownInScreen;

    /**
     * 记录手指按下时在屏幕上的纵坐标的值
     */
    private float yDownInScreen;

    /**
     * 记录手指按下时在小悬浮窗的View上的横坐标的值
     */
    private float xInView;
    /**
     * 记录手指按下时在小悬浮窗的View上的纵坐标的值
     */
    private float yInView;
    /**
     * 屏幕宽度
     */
    private int screenWidth;
    /**
     * 屏幕高度
     */
    private int screenHeight;

    public FloatingBtn(Context context) {
        super(context);
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        configuration = context.getResources().getConfiguration();
        LayoutInflater.from(context).inflate(R.layout.floating_btn, this);
        View view = findViewById(R.id.floating_layout);
        view.measure(0,0);
        viewWidth = view.getMeasuredWidth();
        viewHeight = view.getMeasuredWidth();
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //Log.i("motion","move floating icon");
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 手指按下时记录必要数据,纵坐标的值都需要减去状态栏高度
                xInView = event.getX();
                yInView = event.getY();
                xDownInScreen = event.getRawX();
                yDownInScreen = event.getRawY() - getStatusBarHeight();
                xInScreen = event.getRawX();
                yInScreen = event.getRawY() - getStatusBarHeight();
                break;
            case MotionEvent.ACTION_MOVE:
                xInScreen = event.getRawX();
                yInScreen = event.getRawY() - getStatusBarHeight();
                // 手指移动的时候更新小悬浮窗的位置
                updateViewPosition();
                break;
            case MotionEvent.ACTION_UP:
                // 如果手指离开屏幕时，xDownInScreen和xInScreen相等，且yDownInScreen和yInScreen相等，则视为触发了单击事件。
                if (xDownInScreen == xInScreen && yDownInScreen == yInScreen) {
                    openBigWindow();
                }else{
                    slideToSide();
                }
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 将小悬浮窗的参数传入，用于更新小悬浮窗的位置。
     *
     * @param params
     *            小悬浮窗的参数
     */
    public void setParams(WindowManager.LayoutParams params) {
        mParams = params;
    }

    /**
     * 更新小悬浮窗在屏幕中的位置。
     */
    private void updateViewPosition() {
        mParams.x = (int) (xInScreen - xInView);
        mParams.y = (int) (yInScreen - yInView);
        windowManager.updateViewLayout(this, mParams);
    }
    /**
     * 更新小悬浮窗在屏幕中的位置。
     */
    private void setScreenSize(){
        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);
        screenWidth = metrics.widthPixels;
        screenHeight = metrics.heightPixels;
        /*
        if(configuration.orientation == Configuration.ORIENTATION_PORTRAIT){
            screenWidth = metrics.widthPixels;
            screenHeight = metrics.heightPixels;
        }else if(configuration.orientation == Configuration.ORIENTATION_LANDSCAPE){
            System.out.println(metrics.heightPixels);
            System.out.println(metrics.widthPixels);
            screenWidth = metrics.heightPixels;
            screenHeight = metrics.widthPixels;
        }
        */
    }
    private void slideToSide(){
        setScreenSize();
        if(xInScreen > screenWidth/2){
            mParams.x = screenWidth - viewHeight;
        }else{
            mParams.x = 0;
        }
        /*
        Animation animation = new TranslateAnimation(0,500,0,0);
        animation.setFillAfter(true);
        animation.setDuration(1000);
        animation.setStartOffset(1000);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }
            @Override
            public void onAnimationEnd(Animation animation) {
                updateLayout(mParams);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        this.setAnimation(animation);
        */
        updateLayout(mParams);
    }
    private void updateLayout(WindowManager.LayoutParams params){
        windowManager.updateViewLayout(this,params);
    }
    /**
     * 打开大悬浮窗，同时关闭小悬浮窗。
     */
    private void openBigWindow() {
        FloatingWindowManager.createPopupWindow(getContext());
        FloatingWindowManager.removeFloatingIcon(getContext());
    }
    /**
     * 用于获取状态栏的高度。
     *
     * @return 返回状态栏高度的像素值。
     * */
    private int getStatusBarHeight() {
        if (statusBarHeight == 0) {
            try {
                Class<?> c = Class.forName("com.android.internal.R$dimen");
                Object o = c.newInstance();
                Field field = c.getField("status_bar_height");
                int x = (Integer) field.get(o);
                statusBarHeight = getResources().getDimensionPixelSize(x);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return statusBarHeight;
    }

}
