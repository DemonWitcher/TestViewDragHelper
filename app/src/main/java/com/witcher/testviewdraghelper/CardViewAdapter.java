package com.witcher.testviewdraghelper;

import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.view.View;

public abstract class CardViewAdapter<T> {

    private final DataSetObservable mDataSetObservable = new DataSetObservable();

    public void notifyDataSetChanged(){
        mDataSetObservable.notifyChanged();
    }

    public void registerDataSetObserver(DataSetObserver observer) {
        mDataSetObservable.registerObserver(observer);
    }

    public void unregisterDataSetObserver(DataSetObserver observer) {
        mDataSetObservable.unregisterObserver(observer);
    }

    /**
     * 指定布局ID  先这么写着  这么写的话 暂时不支持多种type
     * @return layoutID
     */
    public abstract int getLayoutId();

    /**
     * 指定数据数量
     * @return count
     */
    public abstract int getCount();

    /**
     * 指定数据
     * @param position position
     * @return 数据
     */
    public abstract T getItem(int position);

    public abstract void bindView(int position, View cardView);

}
