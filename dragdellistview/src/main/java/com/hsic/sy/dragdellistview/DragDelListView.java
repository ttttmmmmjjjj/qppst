package com.hsic.sy.dragdellistview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

/**
 * Created by Administrator on 2018/8/29.
 */

public class DragDelListView extends ListView {
    private boolean moveable = false;
    private boolean closed = true;
    private float mDownX, mDownY;
    private int mTouchPosition, oldPosition = -1;
    private DragDelItem mTouchView, oldView;
    private Context context;
    private DelButtonClickListener mDelListener = null;
    public DragDelListView(Context context) {
        super(context);
        init(context);
    }

    public DragDelListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public DragDelListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        try{
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mTouchPosition = pointToPosition((int) ev.getX(), (int) ev.getY());
                    mTouchView = (DragDelItem) getChildAt(mTouchPosition
                            - getFirstVisiblePosition());
                    mDownX = ev.getX();
                    mDownY = ev.getY();
                    if (oldPosition == mTouchPosition || closed) {
                        moveable = true;
                        mTouchView.mDownX = (int) mDownX;
                    } else {
                        moveable = false;
                        if (oldView != null) {
                            oldView.smoothCloseMenu();
                        }
                    }
                    oldPosition = mTouchPosition;
                    oldView = mTouchView;
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (Math.abs(mDownX - ev.getX()) < Math.abs(mDownY - ev.getY())
                            * dp2px(2)) {
                        break;
                    }
                    if (moveable) {
                        int dis = (int) (mTouchView.mDownX - ev.getX());
                        if (mTouchView.state == mTouchView.STATE_OPEN)
                            dis += mTouchView.mMenuView.getWidth();
                        mTouchView.swipe(dis);
                        ev.setAction(MotionEvent.ACTION_CANCEL);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (moveable) {
                        if ((mTouchView.mDownX - ev.getX()) > (mTouchView.mMenuView
                                .getWidth() / 2)) {
                            // open
                            mTouchView.smoothOpenMenu();
                            closed = false;
                        } else {
                            // close
                            mTouchView.smoothCloseMenu();
                            closed = true;
                        }
                        ev.setAction(MotionEvent.ACTION_CANCEL);
                    }
                    break;
            }
        }catch(Exception ex){
            ex.toString();
        }

        return super.onTouchEvent(ev);
    }
    /**
     * 设置删除按钮点击事件监听
     *
     * @param listener
     *            DelButtonClickListener 删除按钮监听接口对象
     */
    public void setDelButtonClickListener(DelButtonClickListener listener) {
        mDelListener = listener;
    }

    /**
     * 删除按钮监听接口
     */
    public interface DelButtonClickListener {
        void onDelClick(int position);
    }
    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getContext().getResources().getDisplayMetrics());
    }
}
