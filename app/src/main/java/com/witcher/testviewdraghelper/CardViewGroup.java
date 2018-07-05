package com.witcher.testviewdraghelper;

import android.content.Context;
import android.database.DataSetObserver;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class CardViewGroup extends ViewGroup {

    private List<View> mViewList = new ArrayList<>();

    private int mCardNumber = 3;//卡片的堆叠数量
    private int mViewCount = mCardNumber + 1;//总共的view数量 一张是垫底的
    private int mVerticalOffset = 40;//每张卡片向下偏移的Y轴距离
    private float mScaleOffset = 0.1f;//每张卡片减小的缩放

    private ViewDragHelper mViewDragHelper;
    private float mTouchSlop = 8;//滑动超过8像素判定为推拽卡片 不然放给内部view
    private float mDownX, mDownY;
    private long mDownTime;
    private int mAutoBackLeft, mAutoBackTop;//初始left top
    private int mMaxMoveDistance = 500;//滑动500像素远时下一张卡片变成最顶部状态
    private int mXMoveDistance = 300;//X轴移动超过300像素 就切换卡片 就算滑动再慢 只要距离够了 就切换
    private boolean isOuting;//当前是否处于划出屏幕动画中
    private boolean isAniming;//是否处于动画中

    private CardViewAdapter mCardViewAdapter;
    private int mCardIndex = -1;//代表当前最后一个有效view对应数据的index


    public CardViewGroup(Context context) {
        super(context);
        init();
    }

    public CardViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CardViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        /*
        4个view 循环利用
        1.先摆出3个view的样子
        2.给上面的view加手势
        3.手势拖动时候 变化剩下3个view
        4.手势松开时,切换卡片和退回原位
        5.view的循环利用
        6.接口 适配器

        7.优化 现在的快速连续的切换卡片  不够爽快 不够舒服
        8.修复 back或者out过程中单击主卡片造成动画停止的问题
        9.优化 支持多种type
         */
        L.i("init");

        mViewDragHelper = ViewDragHelper.create(this, callback);
//        ViewConfiguration viewConfiguration = ViewConfiguration.get(getContext());
//        mTouchSlop = viewConfiguration.getScaledTouchSlop();
    }

    private void bindAdapter() {
        L.i("bindAdapter");
        if (mCardViewAdapter == null) {
            return;
        }
        //创建view
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        for (int i = 0; i < mViewCount; ++i) {
            View cardView = layoutInflater.inflate(mCardViewAdapter.getLayoutId(), null);
            //这里宽度给一个具体数字  子view就是那个宽度了
            addView(cardView, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        }
        //倒插view
        for (int i = mViewCount - 1; i >= 0; --i) {
            mViewList.add(getChildAt(i));
        }
        //给view贴数据 大于等于4个数据就给前4个贴 不足4个就有几个贴几个 剩下的invisable了
        int count = mCardViewAdapter.getCount();//1
        int size = mViewList.size();//4
        for(int i=0;i<size;++i){
            if(i<count){
                mCardIndex++;
                mViewList.get(i).setVisibility(View.VISIBLE);
                mCardViewAdapter.bindView(i,mViewList.get(i));
            }else{
                mViewList.get(i).setVisibility(View.INVISIBLE);
            }
        }
    }

    private ViewDragHelper.Callback callback = new ViewDragHelper.Callback() {
        @Override
        public void onViewPositionChanged(@NonNull View changedView, int left, int top, int dx, int dy) {
            processMove(changedView);
        }

        @Override
        public boolean tryCaptureView(@NonNull View child, int pointerId) {
            return mViewList.size() > 0 && child == mViewList.get(0);
        }

        @Override
        public void onViewReleased(@NonNull View releasedChild, float xvel, float yvel) {
            if (mViewList.size() > 0 && releasedChild == mViewList.get(0)) {
                processReleased(releasedChild);
            }
        }

        @Override
        public int clampViewPositionHorizontal(@NonNull View child, int left, int dx) {
            return left;
        }

        @Override
        public int clampViewPositionVertical(@NonNull View child, int top, int dy) {
            return top;
        }

        @Override
        public int getViewHorizontalDragRange(@NonNull View child) {
            return 10000;//这个值越大 速度越快  但是再快也不能小于256毫秒  见 ViewDragHelper.BASE_SETTLE_DURATION
        }

        @Override
        public int getViewVerticalDragRange(@NonNull View child) {
            return 10000;
        }
    };

    private void processReleased(View releasedChild) {
        //x轴移动的速度够快 或者移动的距离够远 就切换卡片 不然就回到原位置
        isAniming = true;
        int xMoveDistance = Math.abs(releasedChild.getLeft() - mAutoBackLeft);
        long costTime = System.currentTimeMillis() - mDownTime;
        L.i("costTime:" + costTime + "  xMoveDistance:" + xMoveDistance);
        if (xMoveDistance > mXMoveDistance ||
                (costTime < 150 && xMoveDistance > 200)//移动的速度够快也可以
                ) {
            outAnim(releasedChild);
        } else {
            backAnim(releasedChild);
        }
    }

    private void outAnim(View releasedChild) {
        L.i("切换卡片");
        //卡片飞出屏幕的动画   现在飞行距离太远了  要调整成适当的距离 刚好飞出viewgroup就好了
        isOuting = true;
        int finalLeft;
        int finalTop =  releasedChild.getTop() - mAutoBackTop + releasedChild.getTop();
        if (releasedChild.getLeft() - mAutoBackLeft > 0) {
            //往右飞
            finalLeft = getWidth();//用viewgroup宽度
        } else {
            //往左飞
            finalLeft = -getWidth();//负的width
        }
        L.i("finalTop:" + finalTop);
        if (mViewDragHelper.smoothSlideViewTo(releasedChild, finalLeft, finalTop)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    private void backAnim(View releasedChild) {
        L.i("回到原位");
        mViewDragHelper.smoothSlideViewTo(releasedChild,mAutoBackLeft, mAutoBackTop);
        ViewCompat.postInvalidateOnAnimation(this);
    }

    private void processMove(View changedView) {
        //控制后面2层view的缩放 偏移量 和最后一层view的透明度
        int moveDistance = Math.abs(changedView.getLeft() - mAutoBackLeft)
                + Math.abs(changedView.getTop() - mAutoBackTop);
        //moveDistance为0时 UI不变 moveDistance为500时 每层卡片UI级别+1

        float percent = (float) moveDistance / mMaxMoveDistance;
        percent = Math.min(1.0f, percent);
        percent = Math.max(0.0f, percent);

        int size = mViewList.size();
        for (int i = 0; i < size; ++i) {
            if (i > 0) {
                View cardView = mViewList.get(i);
                if (i == size - 1) {
                    cardView.setAlpha(percent);
                } else {
                    int vOffset = i * mVerticalOffset;
                    float scale = 1 - mScaleOffset * i;//1 0.9 0.8 0.8
                    vOffset = (int) (vOffset - mVerticalOffset * percent);//40-40
                    scale = scale + mScaleOffset * percent;
                    cardView.setTranslationY(vOffset);//0 40 80 80
                    cardView.setScaleX(scale);
                    cardView.setScaleY(scale);
                }
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(isAniming){
            return true;
        }
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                mDownX = ev.getX();
                mDownY = ev.getY();
                mDownTime = System.currentTimeMillis();
                mViewDragHelper.processTouchEvent(ev);
            }
            break;
            case MotionEvent.ACTION_MOVE: {
                float moveDistance = Math.abs(mDownX - ev.getX()) + Math.abs(mDownY - ev.getY());
                return moveDistance > mTouchSlop;
            }
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mViewDragHelper.processTouchEvent(event);
        super.onTouchEvent(event);
        return true;
    }

    @Override
    public void computeScroll() {
        //1.初始化 2.回到原位 3.划出屏幕完成
        if (mViewDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        } else {
            if (mViewDragHelper.getViewDragState() == ViewDragHelper.STATE_IDLE) {
                isAniming = false;
                if (isOuting) {
                    orderView();
                    isOuting = false;
                }
            }
        }
    }

    private void orderView() {
        L.i("动画结束 重排序view");
        //刚才顶部的 去底部  刚才非底部的 挨个往前去一层
        View lastTopView = mViewList.remove(0);
        mViewList.add(lastTopView);
        //然后修改新的最后一层view 缩放和位移和倒数第2层一样  透明度为0
        //如果卡片数量小于4 得加些处理

        int i = mViewList.size() - 2;
        int vOffset = i * mVerticalOffset;
        float scale = 1 - mScaleOffset * i;
        lastTopView.offsetTopAndBottom(mAutoBackTop - lastTopView.getTop());
        lastTopView.offsetLeftAndRight(mAutoBackLeft - lastTopView.getLeft());
        lastTopView.setTranslationY(vOffset);
        lastTopView.setScaleX(scale);
        lastTopView.setScaleY(scale);
        lastTopView.setAlpha(0);
        //重新添加view
        LayoutParams lp = lastTopView.getLayoutParams();
        removeViewInLayout(lastTopView);
        addViewInLayout(lastTopView, 0, lp, true);

        /*
            切换一次后 如果还有数据 给lastTopView绑定新的数据
            如果没有数据 隐藏lastTopView
         */
        if(mCardViewAdapter!=null){
            if(mCardIndex==mCardViewAdapter.getCount()-1){
                L.i("没有新数据 隐藏当前卡片 mCardIndex:"+mCardIndex);
                lastTopView.setVisibility(View.INVISIBLE);
            }else{
                mCardIndex++;
                L.i("还有新数据 mCardIndex:"+mCardIndex+"  name"+((MyCard)mCardViewAdapter.getItem(mCardIndex)).name);
                mCardViewAdapter.bindView(mCardIndex,lastTopView);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureChildren(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        L.i("onLayout");
        int count = mViewList.size();
        for (int i = 0; i < count; ++i) {
            //layout 子view
            View childView = mViewList.get(i);
            int childWidth = childView.getMeasuredWidth();
            int offset = (getMeasuredWidth() - childWidth) / 2;
            int childLeft = left + offset;
            childView.layout(childLeft, top, childLeft + childWidth, top + childView.getMeasuredHeight());
            //给子view做偏移加缩放

            int vOffset = i * mVerticalOffset;
            float scale = 1 - mScaleOffset * i;

            if (i == count - 1) {
                childView.setAlpha(0);
                vOffset = (count - 2) * mVerticalOffset;
                scale = 1 - mScaleOffset * (count - 2);
            }
            childView.setTranslationY(vOffset);

            childView.setPivotX(childWidth / 2);
            childView.setPivotY(childView.getMeasuredHeight());

            childView.setScaleX(scale);
            childView.setScaleY(scale);

            if (i == 0) {
                mAutoBackLeft = childLeft;
                mAutoBackTop = top;
            }
        }

    }

    public void setAdapter(CardViewAdapter cardViewAdapter) {
        this.mCardViewAdapter = cardViewAdapter;
        bindAdapter();
        cardViewAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                L.i("onChanged");
                /*

                这里现在的设定是 比如有10个数据 划到最后一张了  续加了10张 那就保持当前位置不变后面再加10个卡片
                如果有10个数据 刷新后只有4个数据了 那就会只剩下最后一张卡片

                当前已经还在的 就刷新UI不做别的处理
                当前不在的 用新数据展示卡片
                 */
                int count = mCardViewAdapter.getCount();//1  20
                int size = mViewList.size();//4
                int offset = -1;
                for(int i=0;i<size;++i){
                    if(mViewList.get(i).getVisibility() == View.VISIBLE){
                        offset++;
                    }
                }
                for(int i=0;i<size;++i){
                    View cardView = mViewList.get(i);
                    if(cardView.getVisibility() == View.VISIBLE){
                        //刷新数据  顶部 -3
                        mCardViewAdapter.bindView(mCardIndex-offset+i,cardView);
                    }else {
                        //绑定向后的数据 如果向后还有数据
                        if(mCardIndex<count){
                            mCardIndex++;
                            mCardViewAdapter.bindView(mCardIndex,cardView);
                            cardView.setVisibility(View.VISIBLE);
                        }else{
                            break;
                        }
                    }
                }
            }
        });
    }

    public void test1() {

        TextView tv = mViewList.get(0).findViewById(R.id.tv_name);
        L.i("top view name:" + tv.getText().toString());
    }

    public void test2() {
//        mViewList.get(0).setVisibility(View.GONE);
//        mViewList.get(1).setVisibility(View.GONE);
//        mViewList.get(2).setVisibility(View.GONE);
//        mViewList.get(3).setAlpha(1);
        L.i("left:" + mViewList.get(3).getLeft() + "  top:" + mViewList.get(3).getTop());
    }

}
