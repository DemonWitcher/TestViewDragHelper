package com.witcher.testviewdraghelper;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class MyView3 extends View{
    public MyView3(Context context) {
        super(context);
    }

    public MyView3(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                L.i("view 333 onClick");
            }
        });
    }

    public MyView3(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        L.i("view 333  onTouchEvent  "+L.int2Str(event.getAction()));
//        super.onTouchEvent(event);
        return super.onTouchEvent(event);
//        return false;
//        return true;
    }

//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent event) {
//        L.i("view 333  onInterceptTouchEvent  "+L.int2Str(event.getAction()));
//        return super.onInterceptTouchEvent(event);
//    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        L.i("view 333  dispatchTouchEvent  "+L.int2Str(event.getAction()));
        return super.dispatchTouchEvent(event);
    }
}
