package com.witcher.testviewdraghelper;

import android.util.Log;
import android.view.MotionEvent;

public class L {

    public static void i(String log){
        Log.i("witcher",log);
    }

    public static String int2Str(int action){
        if(action == MotionEvent.ACTION_DOWN){
            return "down";
        }else if(action == MotionEvent.ACTION_MOVE){
            return "move";
        }else if(action == MotionEvent.ACTION_UP){
            return "up";
        }else if(action == MotionEvent.ACTION_CANCEL){
            return "cancel";
        }
        return "..."+action;
    }
}
