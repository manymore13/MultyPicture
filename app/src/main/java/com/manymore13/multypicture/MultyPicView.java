/*
 * File Name：MultyView.java
 * Copyright：Copyright 2008-2014 CiWong.Inc. All Rights Reserved.
 * Description： MultyView.java
 * Modify By：res-zwyan
 * Modify Date：2014-9-13
 * Modify Type：Add
 */
package com.manymore13.multypicture;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.administrator.multypicture.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;

/**
 * 朋友圈九宫图控件
 * @author manymore13
 * @blog http://blog.csdn.net/manymore13/
 */
public class MultyPicView extends ViewGroup {

    /**
     * 单行最多图片数
     */
    public final static int LINE_MAX_COUNT = 3;

    /**
     * 这里是九宫图
     */
    public final static int MAX_IMG_COUNT = 9;

    /**
     * 每行最大图片数
     */
    public int mLineMaxCount = LINE_MAX_COUNT;

    /**
     * 图片地址
     */
    private String[] mImgUrls;

    /**
     * 图片的数量
     */
    private int mImgCount;

    /**
     * 图片之间的间距
     */
    private int mPicSpace = 5;

    /**
     * 子view边长度
     */
    private int mChildEdgeSize;

    /**
     * 子view可见个数
     */
    private int mChildVisibleCount;

    /**
     * 这里是九宫格 所以设置为数值9
     */
    private int mMaxChildCount = MAX_IMG_COUNT;

    /**
     * 是否截断点击事件
     */
    private boolean mIntercept = false;

    /**
     * 服务器上缩略图最大尺寸
     */
    private static final int maxPicSize = 250;

    /**
     * 单张图片宽度
     */
    private int mSingleSrcWidth;

    /**
     * 单张图片高度
     */
    private int mSingleSrcHeight;

    /**
     * 单张图片时控件期望的边的最大的大小
     */
    private int mSingleExpectMaxViewSize;

    private float mSingleExpectMaxViewRatio = 0.8f;

    /**
     * 单张图片时图片缩放比例
     */
    private float mSingleScaleRatio;

    /**
     * 各个图片点击事件回调
     */
    private ClickCallback mClickCallback;

    private DisplayImageOptions mOptions = new DisplayImageOptions.Builder()
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .bitmapConfig(Bitmap.Config.ARGB_8888)
            .imageScaleType(ImageScaleType.IN_SAMPLE_INT).build();

    private ImageLoader mLoader = ImageLoader.getInstance();

    private OnClickListener mClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            if (v instanceof ImageView) {
                Integer index = (Integer) v.getTag();
                if (mClickCallback != null) {
                    mClickCallback.callback(index, mImgUrls);
                }
            }
        }
    };

    public MultyPicView(Context context) {
        super(context);
    }

    public MultyPicView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.multy_pic_view, 0, 0);

        int len = a.getIndexCount();
        // 获取自定义属性
        for (int i = 0; i < len; i++) {
            int attr = a.getIndex(i);

            if (attr == R.styleable.multy_pic_view_pic_space) {
                float mul = a.getFloat(attr, 1.0f);
                mPicSpace = DeviceUtils.dp2px(mul, getContext());
            }
        }
        mSingleExpectMaxViewSize = Math.min(DeviceUtils.getScreenHeight((Activity) getContext()),
                DeviceUtils.getScreenWidth((Activity) getContext()));
        mSingleExpectMaxViewSize = (int) (mSingleExpectMaxViewSize
                * mSingleExpectMaxViewRatio);
        a.recycle();
    }

    /**
     * 初始化该控件
     *
     * @param len
     */
    public void setMaxChildCount(int len) {
        removeAllViews();
        mMaxChildCount = len;

        for (int i = 0; i < len; i++) {
            ImageViewChangeBg iv = new ImageViewChangeBg(getContext());
            LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT);
            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
            iv.setOnClickListener(mClickListener);
            iv.setTag(i);
            this.addView(iv, params);
        }
    }

    public String[] getImgUrl() {
        return mImgUrls;
    }

    /**
     * 单张图时调用 ，注意 这里宽高需要服务器提供
     * 因为在你下载图片时是不知道图片大小的
     * 所以需要服务器告诉你
     *
     * @param imgUrl    图片地址
     * @param srcWidth  图片宽度
     * @param srcHeight 图片高度
     */
    public void setSingleImg(String imgUrl, int srcWidth, int srcHeight) {
        mLineMaxCount = 1;
        int maxSize = Math.max(srcWidth, srcHeight);
        mSingleScaleRatio = 1f;
        if (maxSize > mSingleExpectMaxViewSize) {
            mSingleScaleRatio = maxSize * 1.0f / mSingleExpectMaxViewSize;
        }

        mSingleSrcWidth = (int) (srcWidth / mSingleScaleRatio);
        mSingleSrcHeight = (int) (srcHeight / mSingleScaleRatio);
        boolean request = false;
        mImgUrls = new String[]{imgUrl};
        if (mImgUrls != null && mImgUrls.length == 1) {
            request = true;
        }
        dealWithImgs(mImgUrls);
        if (request) {
            this.requestLayout();
        }
    }

    /**
     * 显示多张图片(两张以上)图片 传图片数组进去
     * 你在外部使用时需要把图片传进去
     * @param imgs 图片url数组
     */
    public void setImgs(String[] imgs) {
        mLineMaxCount = LINE_MAX_COUNT;
        mSingleSrcHeight = mSingleSrcWidth = 0;
        mSingleScaleRatio = 1;
        dealWithImgs(imgs);
    }

    /**
     * 设置是否拦截事件
     *
     * @param intercept true 事件拦截
     */
    public void setIntercept(boolean intercept) {
        mIntercept = intercept;
    }

    /**
     * 设置图片点击回调
     *
     * @param callback 事件回调
     */
    public void setClickCallback(ClickCallback callback) {
        mClickCallback = callback;
    }

    /**
     * 点击图片回调
     */
    public interface ClickCallback {
        /**
         * 回调方法
         * @param index 点击的索引
         * @param str   图片地址数组
         */
        void callback(int index, String[] str);
    }

    /**
     * 获取图片可能显示多少行数
     * @param imgSize 图片个数
     * @return 行数
     */
    public int getMultyImgLines(int imgSize) {
        if (imgSize == 0) {
            return 1;
        }
        return (imgSize + mLineMaxCount - 1) / mLineMaxCount;
    }

    /**
     * 获取当前图片数量
     *
     * @return 数量
     */
    public int getImgCount() {
        return mImgCount;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mIntercept;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        int lpadding = getPaddingLeft();
        int tpadding = getPaddingTop();
        int left = lpadding, top = tpadding;
        int childCount = getChildCount();
        int visibleChildCount = mChildVisibleCount;
        int breakLineC = 0; // 断行
        if (visibleChildCount == 4) {
            // 当四张图片时 两张时换行
            breakLineC = 2;
        } else {
            // 每行三张图片换行
            breakLineC = mLineMaxCount;
        }
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == View.GONE) {
                continue;
            }
            if (visibleChildCount == 1) {
                // 单张做特殊显示
                if (mLineMaxCount == 1) {
//                    left = (getMeasuredWidth() - mSingleSrcWidth) / 2;// 居中
                    left = lpadding; // 居左
                }
                if (mSingleSrcWidth == 0 || mSingleSrcHeight == 0) {
                    child.layout(left, top, left + mChildEdgeSize,
                            top + mChildEdgeSize);
                } else {
                    child.layout(left, top, left + mSingleSrcWidth,
                            top + mSingleSrcHeight);
                }
            } else {
                child.layout(left, top, left + mChildEdgeSize,
                        top + mChildEdgeSize);
                left += (mPicSpace + mChildEdgeSize);
                if ((i + 1) % breakLineC == 0) {
                    top += mChildEdgeSize + mPicSpace;
                    left = lpadding;
                }
            }
        }
    }

    /**
     * 确定九宫图控件自身的大小以及内部ImageView的大小
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        if (getChildCount() == 0) {
            setMaxChildCount(mMaxChildCount);
        }
        measureImgWidth(widthMeasureSpec);
        mChildVisibleCount = getVisibleChildCount();
        int lines = getMultyImgLines(mChildVisibleCount);
        int viewHeight = ((lines - 1) * mPicSpace + lines * mChildEdgeSize)
                + getPaddingTop() + getPaddingBottom();
        if (mChildVisibleCount == 1) {
            viewHeight = mSingleSrcHeight == 0 ? viewHeight : mSingleSrcHeight;
        }
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        setMeasuredDimension(widthSize, viewHeight);

        int heightSize = mChildEdgeSize;
        widthSize = heightSize;
        if (mChildVisibleCount == 1 && mSingleSrcWidth != 0) {
            widthSize = mSingleSrcWidth;
            heightSize = mSingleSrcHeight;
        }
        measureChildren(widthSize, heightSize);
    }

    /**
     * 计算图片的大小
     * @param widthMeasureSpec
     */
    protected void measureImgWidth(int widthMeasureSpec) {
        if (mChildEdgeSize == 0) {
            int measureSize = MeasureSpec.getSize(widthMeasureSpec);
            mChildEdgeSize = (measureSize - (LINE_MAX_COUNT - 1) * mPicSpace
                    - getPaddingLeft() - getPaddingRight()) / LINE_MAX_COUNT;
        }
    }

    /**
     * 获取可见图片数量
     */
    private int getVisibleChildCount() {
        int childCount = getChildCount();
        int count = 0;
        for (int i = 0; i < childCount; i++) {
            if (getChildAt(i).getVisibility() != View.GONE) {
                count++;
            }
        }
        return count;
    }

    /**
     * 处理图片
     *
     * @param imgs 图片地址列表
     */
    private void dealWithImgs(String[] imgs) {
        if (imgs == null || imgs.length == 0) {
            return;
        }
        mImgUrls = imgs;
        mImgCount = imgs.length;
        int imgLen = mImgCount;
        int maxChildCount = mMaxChildCount;
        for (int i = 0; i < maxChildCount; i++) {
            final ImageView chileIv = (ImageView) getChildAt(i);
            if (i < imgLen) {
                chileIv.setVisibility(View.VISIBLE);
                String url = imgs[i];
                ImageSize imageSize = null;
                if (i == 0 && mImgCount == 1 && mSingleSrcWidth != 0
                        && mSingleSrcWidth != 0) {
                    imageSize = new ImageSize(mSingleSrcWidth, mSingleSrcHeight);
                } else {
                    imageSize = new ImageSize(maxPicSize, maxPicSize);
                }
                loadImg(url, imageSize, chileIv);
            } else {
                chileIv.setVisibility(View.GONE);
            }
        }
    }

    private void loadImg(String url, ImageSize imageSize, final ImageView chileIv) {
        mLoader.displayImage(url,chileIv);
    }
}
