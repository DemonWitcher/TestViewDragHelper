package com.witcher.testviewdraghelper;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

public class MyViewGroup2 extends FrameLayout{
    public MyViewGroup2(Context context) {
        super(context);
    }

    public MyViewGroup2(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyViewGroup2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        L.i("view 222  onTouchEvent  "+L.int2Str(event.getAction()));
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        L.i("view 222  onInterceptTouchEvent  "+L.int2Str(event.getAction()));
//        return super.onInterceptTouchEvent(event);
//        return true;
        return false;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        L.i("view 222  dispatchTouchEvent  "+L.int2Str(event.getAction()));
        return super.dispatchTouchEvent(event);
    }
}
