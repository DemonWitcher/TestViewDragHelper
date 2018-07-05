package com.witcher.testviewdraghelper;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CardActivity extends AppCompatActivity {

    private CardViewGroup cardViewGroup;
    private CardViewAdapter cardViewAdapter;
    private List<MyCard> mMyCardList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);
        cardViewGroup = findViewById(R.id.card_view_group);
        initTestData();
        initTest();
        setData();
    }

    private void setData(){
        cardViewAdapter = new CardViewAdapter<MyCard>() {
            @Override
            public int getLayoutId() {
                return R.layout.item_card;
            }

            @Override
            public int getCount() {
                return mMyCardList.size();
            }

            @Override
            public MyCard getItem(int position) {
                return mMyCardList.get(position);
            }

            @Override
            public void bindView(int position, View cardView) {
                ImageView iv = cardView.findViewById(R.id.iv);
                iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        L.i("点击了图片");
                    }
                });
                iv.setImageResource(getItem(position).imgRes);
                TextView tv = cardView.findViewById(R.id.tv_name);
                tv.setText(getItem(position).name);
            }
        };
        cardViewGroup.setAdapter(cardViewAdapter);
    }

    private void initTest() {
        findViewById(R.id.bt1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardViewGroup.test1();
//                setData();
            }
        });
        findViewById(R.id.bt2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardViewGroup.test2();
            }
        });
        findViewById(R.id.bt3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int size = mMyCardList.size();
                for (int i = size; i < size+5; ++i) {
                    mMyCardList.add(new MyCard(getRandomRes(), "name:" + i ));
                }
                cardViewAdapter.notifyDataSetChanged();
            }
        });
    }
    private void initTestData() {
        for (int i = 0; i < 5; ++i) {
            mMyCardList.add(new MyCard(getRandomRes(), "name:" + i ));
        }
    }
    static int getRandomRes() {
        int i = new Random().nextInt(11);
        switch (i) {
            case 0: {
                return R.drawable.img1;
            }
            case 1: {
                return R.drawable.img2;
            }
            case 2: {
                return R.drawable.img3;
            }
            case 3: {
                return R.drawable.img4;
            }
            case 4: {
                return R.drawable.img5;
            }
            case 5: {
                return R.drawable.img7;
            }
            case 6: {
                return R.drawable.img8;
            }
            case 7: {
                return R.drawable.img9;
            }
            case 8: {
                return R.drawable.img10;
            }
            case 9: {
                return R.drawable.img11;
            }
            case 10: {
                return R.drawable.img12;
            }
        }
        return R.drawable.img11;
    }
}
