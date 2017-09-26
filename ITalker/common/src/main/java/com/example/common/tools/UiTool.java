package com.example.common.tools;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Window;



/**
 * Created by Administrator on 2017/8/6.
 */

public class UiTool {
    private static int STATUS_BAR_HEIGHT = -1;

    public static int getStatusBarHeight(Activity activity){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && STATUS_BAR_HEIGHT == -1){
            try{

            final Resources res = activity.getResources();
            // 尝试获取status_bar_height这个属性的ID对应的资源的INT值
            int resourceId = res.getIdentifier("status_bar_height","dimen","android");
            if(resourceId <= 0) {
                Class<?> clazz = Class.forName("com.android.internal.R$dimen");
                Object object = clazz.newInstance();
                resourceId = Integer.parseInt(clazz.getField("status_bar_height").get(object).toString());
            }

            //如果拿到了就直接调用
            if(resourceId > 0){
                STATUS_BAR_HEIGHT = res.getDimensionPixelSize(resourceId);
            }

            //如果还是没有拿到
            if(STATUS_BAR_HEIGHT <= 0){
                // 通过window获取
                Rect rectangle = new Rect();
                Window window = activity.getWindow();
                window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
                STATUS_BAR_HEIGHT = rectangle.top;
            }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        return STATUS_BAR_HEIGHT;
    }

    public static int getScreenWidth(Activity activity){
        DisplayMetrics displayMetrics = activity.getResources().getDisplayMetrics();
        return displayMetrics.widthPixels;
    }

    public static int getScreenHeight(Activity activity){
        DisplayMetrics displayMetrics = activity.getResources().getDisplayMetrics();
        return displayMetrics.heightPixels;
    }
}
