package com.u1city.module.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * BannerViewpager，自动滚动，循环滑动、分页指示器
 * 
 * @author zhengjb
 * 
 *         2015-3-24
 */
public class BannerViewPager extends ViewPager implements Runnable
{
    /** 页面切换的间隔时间 */
    private static final int POST_DELAYED_TIME = 1000 * 3;
    private boolean isTouching = false;

    public BannerViewPager(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        postDelayed(this, POST_DELAYED_TIME);
    }

    @Override
    public void run()
    {
        //被触摸的时候不能切换
        if (getAdapter() != null && getAdapter().getCount() > 1 && !isTouching)
        {
            setCurrentItem((getCurrentItem() + 1) % getAdapter().getCount(), true);
        }

        postDelayed(this, POST_DELAYED_TIME);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {

        int height = 0;
        for (int i = 0; i < getChildCount(); i++)
        {
            View child = getChildAt(i);
            MeasureSpec.getSize(heightMeasureSpec);

            if (child.getLayoutParams().height > 0)
            {
                child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(child.getLayoutParams().height, MeasureSpec.EXACTLY));
            }
            else
            {
                child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            }

            int h = child.getMeasuredHeight();
            if (h > height)
                height = h;
        }

        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        //设置是否正在触摸中
        if (event.getAction() == MotionEvent.ACTION_DOWN)
        {
            this.isTouching = true;
        }
        else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL)
        {
            this.isTouching = false;
        }

        return super.onTouchEvent(event);
    }
}
