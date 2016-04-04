package com.u1city.module.widget;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import java.util.ArrayList;

public class PhotosPagerAdapter extends PagerAdapter
{

    private ViewPager mPager;
    ArrayList<View> layouts;

    /*
     * public NewProductPagerAdapter(Context context, ViewPager pager)
     * {
     * super(context);
     * this.mPager = pager;
     * }
     */

    public PhotosPagerAdapter(ViewPager pager, ArrayList<View> layouts)
    {
        this.mPager = pager;
        this.layouts = layouts;
    }

    public Object instantiateItem(View container, int position)
    {
        View layout = layouts.get(position);
        mPager.addView(layout);
        return layout;
    }

    public void destroyItem(View container, int position, Object object)
    {
        View layout = layouts.get(position);
        mPager.removeView(layout);
    }

    public boolean isViewFromObject(View arg0, Object arg1)
    {
        return arg0 == arg1;
    }

    public int getCount()
    {
        return layouts.size();
    }

}
