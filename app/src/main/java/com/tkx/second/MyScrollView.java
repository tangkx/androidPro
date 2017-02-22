package com.tkx.second;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Scroller;

/**
 * Created by tkx.
 * e-mail is 993296096@qq.com
 * time is 2017/2/21
 */

public class MyScrollView extends ViewGroup{

    int mScreenHeight = 0;
    int mLastY = 0;
    int mStart = 0;
    int mEnd = 0;
    Scroller mScroller;
    public MyScrollView(Context context) {
        super(context);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mScreenHeight = wm.getDefaultDisplay().getHeight();
        mScroller = new Scroller(context);

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        int childCount = getChildCount();
        MarginLayoutParams mlp = (MarginLayoutParams) getLayoutParams();
        mlp.height = mScreenHeight * childCount;
        setLayoutParams(mlp);
        for(int i = 0; i < childCount; i++){
            View cview = getChildAt(i);

            if(cview.getVisibility() != View.GONE){
                cview.layout(l, i * mScreenHeight, r, (i+1) * mScreenHeight);
            }
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int count = getChildCount();
        for(int i = 0; i < count; i++){

            View childView = getChildAt(i);
            measureChild(childView, widthMeasureSpec, heightMeasureSpec);

        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int y = (int) event.getY();
        switch (event.getAction()){

            case MotionEvent.ACTION_DOWN:
                mLastY = y;
                mStart = getScrollY();
                break;
            case MotionEvent.ACTION_MOVE:
                if(!mScroller.isFinished()){
                    mScroller.abortAnimation();
                }

                int dy = mLastY - y;

                if(getScaleY() < 0){
                    dy = 0;
                }

                if(getScaleY() > (getHeight() - mScreenHeight)){
                    dy = 0;
                }
                scrollBy(0, dy);
                mLastY = y;
                break;
            case MotionEvent.ACTION_UP:

                break;
        }

        postInvalidate();
        return true;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if(mScroller.computeScrollOffset()){
            scrollTo(0, mScroller.getCurrY());
            postInvalidate();
        }
    }
}
