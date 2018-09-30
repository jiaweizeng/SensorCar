package com.example.admin.sensorcar;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

/**
 * Created by Administrator on 2017/7/25 0025.
 */

public class PopupWindowUtils {
    private static PopupWindowUtils mPopupWindowUtils;
    PopupWindow popupWindow;

    private PopupWindowUtils() {
    }

    public static PopupWindowUtils getInstance(){
        if (mPopupWindowUtils==null){
            synchronized (PopupWindowUtils.class){
                if (mPopupWindowUtils==null){
                    mPopupWindowUtils=new PopupWindowUtils();
                }
            }
        }
        return mPopupWindowUtils;
    }

    //要先调用showPopWindow方法
    public PopupWindow getPopInstance(){
            return popupWindow;
    }


    public View showPopWindow(int layout, View father, Context context){
//        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View v = inflater.inflate(R.layout.pop_login, null);
        View v = View.inflate(context,layout, null);


        popupWindow = new PopupWindow(v, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        //设置渐入、渐出动画效果
        popupWindow.setAnimationStyle(android.R.style.Animation_Toast);
        popupWindow.setTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAtLocation(father, Gravity.BOTTOM, 0, 0);
        return v;
    }
}
