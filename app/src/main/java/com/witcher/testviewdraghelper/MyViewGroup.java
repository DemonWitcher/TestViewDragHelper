package com.witcher.testviewdraghelper;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

public class MyViewGroup extends FrameLayout{
    public MyViewGroup(Context context) {
        super(context);
    }

    public MyViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
//        setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                L.i("view 111 onClick");
//            }
//        });
    }

    public MyViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        L.i("view 111  onTouchEvent  "+L.int2Str(event.getAction()));
        super.onTouchEvent(event);
//        return super.onTouchEvent(event);
        return false;
//        return true;
//        if(event.getX()<450){
//            return false;
//        }else{
//            return true;
//        }
    }
    float downX,downY;
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        L.i("view 111  onInterceptTouchEvent  "+L.int2Str(event.getAction()));
//        return super.onInterceptTouchEvent(event);
//        return false;
//        return true;

        int action = event.getAction();
        switch (action){
            case MotionEvent.ACTION_DOWN:{
                downX = event.getX();
                downY = event.getY();
                return false;
            }
            case MotionEvent.ACTION_MOVE:{
                if(Math.abs(event.getX()-downX)+Math.abs(event.getY()-downY)>5){
                    return true;
                }else{
                    return false;
                }
            }
            case MotionEvent.ACTION_UP:{
                return false;
            }
        }
        return false;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        L.i("view 111  dispatchTouchEvent  "+L.int2Str(event.getAction()));
//        super.dispatchTouchEvent(event);
        return super.dispatchTouchEvent(event);
//        return true;
//        return false;
    }
}
