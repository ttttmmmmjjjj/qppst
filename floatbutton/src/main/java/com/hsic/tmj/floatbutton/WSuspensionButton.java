package com.hsic.tmj.floatbutton;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Scroller;

/**
 * Created by Administrator on 2019/4/1.
 */

public class WSuspensionButton extends View {
    private static final int SIZE = 48;
    private int size;
    private int sizeHalf;
    private int stateHeight;
    private Scroller mScroller;
    private int screenWidth;
    private int screenWidthHalf;
    private int screenHeight;
    private int screenHeightHalf;

    private int topLine;
    private int leftLine;
    private int rightLine;
    private int bottomLine;


    public WSuspensionButton(Context context) {
        this(context, null);

    }


    public WSuspensionButton(Context context, AttributeSet attrs) {
        super(context,attrs);
        // TODO Auto-generated constructor stub
        init();
    }

    private void init() {
        recordX = 0;
        recordY = 0;
        stateHeight = 0;
        mScroller = new Scroller(getContext());

        DisplayMetrics dm = getContext().getResources().getDisplayMetrics();
        size = (int)(SIZE * dm.density);
        sizeHalf = size / 2;
        screenWidth = dm.widthPixels;// 屏幕宽
        screenHeight = dm.heightPixels- getStatusBarHeight();
        screenWidthHalf = screenWidth / 2;
        screenHeightHalf = screenHeight / 2 + stateHeight;

        // 紧紧只会左右飞
        topLine = 0;
        bottomLine =  screenHeight -  topLine;
    }

    /**
     * state bar height
     * @return
     */
    public int getStatusBarHeight() {// 状态栏高度
        int result = 0;
        int resourceId = getContext().getResources().getIdentifier("status_bar_height",
                "dimen", "android");
        if (resourceId > 0) {
            result = getContext().getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
    public void setText(){

    }

    public int getSize() {
//    	Log.e("==",String.valueOf(size));
        return size;
    }

    float recordX;
    float recordY;
    float tempX;
    float tempY;
    int recordLeft;
    int recordTop;
    int recordRight;
    int recordBottom;
    int state;
    boolean isMoved;
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                state = 0;
                isMoved = false;
                recordX = event.getRawX();
                Log.e("recordX", String.valueOf(recordX));
                recordY = event.getRawY();
                Log.e("recordY", String.valueOf(recordX));
                tempX = recordX;
                tempY = recordY;
                break;
            case MotionEvent.ACTION_MOVE:
                int intx = (int)(event.getRawX() - tempX);
                int inty = (int)(event.getRawY() - tempY);
                tempX = event.getRawX();
                tempY = event.getRawY();
                if(Math.abs(tempX - recordX) < 5 && Math.abs(tempY - recordY) < 5) {
                    if(state == 0) {
                        state = 1;
                        return true;
                    }
                } else {
                    isMoved = true;
                }
                if(isMoved && state != 2)
                    state = 2;
                if(state != 2) return true;
                int left = getLeft() + intx;
                int top = getTop() + inty;
                int right = getRight() + intx;
                int bottom = getBottom() + inty;
                if(left < 0) {
                    left = 0;
                    right = getWidth();
                }
                if(right > screenWidth) {
                    left = screenWidth - getWidth();
                    right = screenWidth;
                }
                if(top < stateHeight) {
                    top = stateHeight;
                    bottom = stateHeight + getHeight();
                }
                if(bottom > screenHeight) {
                    top = screenHeight - getHeight();
                    bottom = screenHeight;
                }
                layout(left, top, right, bottom);
                break;
            case MotionEvent.ACTION_UP:
                if(state != 2) {
                    if(clickedlistener != null)
                        clickedlistener.onClick(this);
                    return true;
                }
                recordLeft = getLeft();
                recordTop = getTop();
                recordRight = getRight();
                recordBottom = getBottom();
                Log.v("AC", "left:"+recordLeft+"\nright:"+recordRight+"\ntop:"+recordTop+"\nbottom:"+recordBottom);
                if(getLeft() <= 0 || getTop() <= stateHeight ||
                        getRight() >= screenWidth || getBottom() >= screenHeight) {
                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)getLayoutParams();
                    int leftX = getLeft();
                    int topX = getTop();
                    params.setMargins(getLeft(), getTop(), 0, 0);
                    setLayoutParams(params);
                    if(completeMoveListener != null) completeMoveListener.onCompleteMove(this, leftX, topX);
                } else {
                    // 控件中心y，小于topline，向上飞
                    // 控件中心y，大于bottomLine，向下飞
                    // 控件中心x，小于屏幕可视中心x，向左飞
                    // 控件中心x，大于屏幕可视中心x，向右飞
                    if((getTop() + sizeHalf) <= topLine) {
                        scrollToTop();
                    } else if((getBottom() + sizeHalf) >= bottomLine) {
                        scrollToBottom();
                    } else if((getLeft() + sizeHalf) < screenWidthHalf) {
                        scrollToLeft();
                    } else {
                        scrollToRight();
                    }
                }
                break;
            default:
                break;
        }

        return true;
    }

    public void computeScroll() {
        if(mScroller.computeScrollOffset()) {
            layout(recordLeft + mScroller.getCurrX(),
                    recordTop + mScroller.getCurrY(),
                    recordRight + mScroller.getCurrX(),
                    recordBottom + mScroller.getCurrY());
            postInvalidate();
            if(mScroller.isFinished()) {
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)getLayoutParams();
                int left = recordLeft + mScroller.getCurrX();
                int top = recordTop + mScroller.getCurrY();
                params.setMargins(left, top, 0, 0);
                setLayoutParams(params);
                if(completeMoveListener != null) completeMoveListener.onCompleteMove(this, left, top);
            }
        }

    }

    private void scrollToLeft() {
        mScroller.startScroll(0, 0, -recordLeft, 0);
        postInvalidate();
    }

    private void scrollToTop() {
        mScroller.startScroll(0, 0, 0, -recordTop + stateHeight);
        postInvalidate();
    }

    private void scrollToRight() {
        mScroller.startScroll(0, 0, screenWidth - recordRight, 0);
        postInvalidate();
    }

    private void scrollToBottom() {
        mScroller.startScroll(0, 0, 0, screenHeight - recordBottom);
        postInvalidate();
    }

    //-------------------
    // 监听
    //-------------------
    private ClickListener clickedlistener;
    public void setClickListener(ClickListener clickedlistener) {
        this.clickedlistener = clickedlistener;
    }
    public interface ClickListener {
        public void onClick(View v);
    }

    private CompleteMoveListener completeMoveListener;
    public void setCompleteMoveListener(CompleteMoveListener completeMoveListener) {
        this.completeMoveListener = completeMoveListener;
    }
    public interface CompleteMoveListener {
        public void onCompleteMove(View v, int left, int top);
    }
}
