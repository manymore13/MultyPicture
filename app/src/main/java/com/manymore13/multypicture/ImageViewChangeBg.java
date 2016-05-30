/*
 * File Name：ImageViewChangeBg.java
 * Copyright：Copyright 2008-2014 CiWong.Inc. All Rights Reserved.
 * Description： ImageViewChangeBg.java
 * Modify By：pla-gysu
 * Modify Date：2014-12-3
 * Modify Type：Add
 */
package com.manymore13.multypicture;

import android.content.Context;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.example.administrator.multypicture.R;


/**
 * 有点击效果的ImageView
 * 
 * @author pla-gysu
 * 
 */
public class ImageViewChangeBg extends ImageView
{

    // public final static float[] BT_NOT_SELECTED = new float[]{1, 0, 0, 0, 0,
    // 0,
    // 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0};

    public boolean isCanChangeBg = true;// 是否允许点击改变背景

    public ImageViewChangeBg(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    public ImageViewChangeBg(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public ImageViewChangeBg(Context context)
    {
        super(context);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (isCanChangeBg)
        {
            int action = event.getAction();

            if (action == MotionEvent.ACTION_DOWN)
            {
                this.setDrawingCacheEnabled(true);
                this.setColorFilter(
                        getResources().getColor(R.color.iv_change_bg_color),
                        PorterDuff.Mode.SRC_ATOP);
            }
            else if (action == MotionEvent.ACTION_UP
                    || action == MotionEvent.ACTION_CANCEL)
            {
                this.setColorFilter(null);
            }
        }
        return super.onTouchEvent(event);
    }

}
