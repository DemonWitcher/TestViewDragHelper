package com.witcher.testviewdraghelper;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

public class ViewDragLayout extends LinearLayout {

    ViewDragHelper viewDragHelper;

    private View dragView;
    private View edgeDragView;
    private View autoBackView;
    private View rl;

    private int autoBackViewLeft, autoBackViewTop;

    public ViewDragLayout(@NonNull Context context) {
        super(context);
        init();
    }

    public ViewDragLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    public ViewDragLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
//https://www.jianshu.com/p/3f15352e4221
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        dragView = findViewById(R.id.view1);
        rl = findViewById(R.id.rl);
        edgeDragView = findViewById(R.id.view2);
        autoBackView = findViewById(R.id.view3);
        dragView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                L.i("dragView  onClick");
            }
        });
    }

    private void init() {
        viewDragHelper = ViewDragHelper.create(this, 1.0f, new ViewDragHelper.Callback() {
            //决定子view是否可以拖拽的回调
            @Override
            public boolean tryCaptureView(@NonNull View child, int pointerId) {
                return child==dragView||child==autoBackView||rl == child;
            }
            //触摸回调
            @Override
            public int clampViewPositionHorizontal(@NonNull View child, int left, int dx) {
                return left;
            }
            @Override
            public int clampViewPositionVertical(@NonNull View child, int top, int dy) {
                return top;
            }
            //子view触摸释放回调  UP事件给的
            @Override
            public void onViewReleased(@NonNull View releasedChild, float xvel, float yvel) {
                if (releasedChild == autoBackView) {
                    viewDragHelper.settleCapturedViewAt(autoBackViewLeft, autoBackViewTop);
                    invalidate();
                }
            }
            //父容器边缘触摸回调
            @Override
            public void onEdgeDragStarted(int edgeFlags, int pointerId) {
                //此函数可以无视tryCaptureView回调的返回值
                viewDragHelper.captureChildView(edgeDragView,pointerId);
            }
        });
        viewDragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_LEFT);
    }

    @Override
    public void computeScroll() {
        if (viewDragHelper.continueSettling(true)) {
            invalidate();
        }
    }
//滑动距离小于5像素算单击  不然算滑动
    float downX,downY;
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action){
            case MotionEvent.ACTION_DOWN:{
                viewDragHelper.processTouchEvent(ev);
                downX = ev.getX();
                downY = ev.getY();
            }break;
            case MotionEvent.ACTION_MOVE:{
                if(Math.abs(ev.getX()-downX)+Math.abs(ev.getY()-downY)>5){
                    return true;
                }else{
                    return false;
                }
            }
            case MotionEvent.ACTION_UP:{

            }break;
        }
        return false;
//        return true;
//        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        viewDragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        autoBackViewLeft = autoBackView.getLeft();
        autoBackViewTop = autoBackView.getTop();
    }
}
