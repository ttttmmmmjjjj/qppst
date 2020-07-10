package com.hsic.tmj.listener;

import android.view.MotionEvent;

import com.hsic.tmj.provincewheelview.ProvinceWheelView;


/**
 * 手势监听
 */
public final class LoopViewGestureListener extends android.view.GestureDetector.SimpleOnGestureListener {

    private final ProvinceWheelView wheelView;


    public LoopViewGestureListener(ProvinceWheelView wheelView) {
        this.wheelView = wheelView;
    }

    @Override
    public final boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        wheelView.scrollBy(velocityY);
        return true;
    }
}
