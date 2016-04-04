package com.u1city.module.widget.wheelview;

import android.app.Dialog;
import android.content.Context;
import android.content.res.AssetManager;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.u1city.module.R;
import com.u1city.module.common.Debug;
import com.u1city.module.model.CityModel;
import com.u1city.module.model.DistrictModel;
import com.u1city.module.model.ProvinceModel;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * @author linjy
 * @time 2015-12-18 16:13:08
 *       类说明：基于WheelView的年月日选择器(城市选择器)
 */
public class DateOrCityChooseLdy extends Dialog implements OnClickListener, OnWheelChangedListener
{
    /************* 日期选择 *****************/
    private final Context context;
    private final View contentView;
    private int visibleItems = 7;
    private WheelView yearWv;
    private WheelView monthWv;
    private WheelView dayWv;
    public Button cancelBtn;
    public Button confirmBtn;
    public TextView titleView;
    private DateOrCityChooseListener dateChooseListener;
    private String[] years;
    private String[] months;
    private String[] days;
    int minYear = 1940;//选择日期的最小年 默认1940
    private boolean isDatechoose;// 是否日期选择 否城市选择
    private static final String TAG = "DateChooseLdy";
    /************* 城市选择 *****************/
    /**
     * 所有省
     */
    public String[] provinceDatas;
    /** key - 省 value - 市 */
    public Map<String, String[]> citiesMap = new HashMap<String, String[]>();
    /** key - 市 values - 区 */
    public Map<String, String[]> districsMap = new HashMap<String, String[]>();
    /** key - 区 values - 邮编 */
    public Map<String, String> zipCodesMap = new HashMap<String, String>();

    /** 当前省的名称 */
    public String currentProvince;
    /** 当前市的名称 */
    public String currentCity;
    /** 当前区的名称 */
    public String currentDistrict = "";

    /** 当前区的邮政编码 */
    public String currentZipCode = "";
    private ArrayWheelAdapter<String> yearAdapter;//年份apapter

    public DateOrCityChooseLdy(Context context, final DateOrCityChooseListener dateChooseListener, boolean isDatechoose)
    {
        super(context);
        this.context = context;
        this.dateChooseListener = dateChooseListener;
        this.isDatechoose = isDatechoose;
        /*** 注：这边共用城市选择的wheelView ***/
        contentView = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.layout_city_choose_ldy, null);
        yearWv = (WheelView) contentView.findViewById(R.id.layout_city_choose_province_wv);
        yearWv.addChangingListener(this);
        monthWv = (WheelView) contentView.findViewById(R.id.layout_city_choose_city_wv);
        monthWv.addChangingListener(this);
        dayWv = (WheelView) contentView.findViewById(R.id.layout_city_choose_district_wv);
        dayWv.addChangingListener(this);
        yearWv.setShadowColor(0xf0f0f0, 0xf0f0f0, 0xf0f0f0);// 设置颜色
        monthWv.setShadowColor(0xf0f0f0, 0xf0f0f0, 0xf0f0f0);
        dayWv.setShadowColor(0xf0f0f0, 0xf0f0f0, 0xf0f0f0);
        setVisibleItems(visibleItems);

        cancelBtn = (Button) contentView.findViewById(R.id.layout_city_choose_cancel_btn);
        confirmBtn = (Button) contentView.findViewById(R.id.layout_city_choose_confirm_btn);

        cancelBtn.setOnClickListener(this);
        confirmBtn.setOnClickListener(this);

        titleView = (TextView) contentView.findViewById(R.id.layout_city_choose_title_tv);
        setContentView(contentView);
        initDates();
    }

    public DateOrCityChooseLdy(Context context, int style, final DateOrCityChooseListener dateChooseListener, boolean isDatechoose)
    {
        super(context, style);
        this.context = context;
        this.dateChooseListener = dateChooseListener;
        this.isDatechoose = isDatechoose;
        contentView = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.layout_city_choose_ldy, null);
        yearWv = (WheelView) contentView.findViewById(R.id.layout_city_choose_province_wv);
        yearWv.addChangingListener(this);
        monthWv = (WheelView) contentView.findViewById(R.id.layout_city_choose_city_wv);
        monthWv.addChangingListener(this);
        dayWv = (WheelView) contentView.findViewById(R.id.layout_city_choose_district_wv);
        dayWv.addChangingListener(this);
        yearWv.setShadowColor(0xf0f0f0, 0xf0f0f0, 0xf0f0f0);// 设置颜色
        monthWv.setShadowColor(0xf0f0f0, 0xf0f0f0, 0xf0f0f0);
        dayWv.setShadowColor(0xf0f0f0, 0xf0f0f0, 0xf0f0f0);
        setVisibleItems(visibleItems);
        cancelBtn = (Button) contentView.findViewById(R.id.layout_city_choose_cancel_btn);
        confirmBtn = (Button) contentView.findViewById(R.id.layout_city_choose_confirm_btn);
        cancelBtn.setOnClickListener(this);
        confirmBtn.setOnClickListener(this);
        titleView = (TextView) contentView.findViewById(R.id.layout_city_choose_title_tv);
        setContentView(contentView);

        initDates();
    }
    public DateOrCityChooseLdy(Context context, int style, final DateOrCityChooseListener dateChooseListener, boolean isDatechoose,int minYear)
    {
        super(context, style);
        this.context = context;
        this.dateChooseListener = dateChooseListener;
        this.isDatechoose = isDatechoose;
        this.minYear = minYear;
        contentView = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.layout_city_choose_ldy, null);
        yearWv = (WheelView) contentView.findViewById(R.id.layout_city_choose_province_wv);
        yearWv.addChangingListener(this);
        monthWv = (WheelView) contentView.findViewById(R.id.layout_city_choose_city_wv);
        monthWv.addChangingListener(this);
        dayWv = (WheelView) contentView.findViewById(R.id.layout_city_choose_district_wv);
        dayWv.addChangingListener(this);
        yearWv.setShadowColor(0xf0f0f0, 0xf0f0f0, 0xf0f0f0);// 设置颜色
        monthWv.setShadowColor(0xf0f0f0, 0xf0f0f0, 0xf0f0f0);
        dayWv.setShadowColor(0xf0f0f0, 0xf0f0f0, 0xf0f0f0);
        setVisibleItems(visibleItems);
        cancelBtn = (Button) contentView.findViewById(R.id.layout_city_choose_cancel_btn);
        confirmBtn = (Button) contentView.findViewById(R.id.layout_city_choose_confirm_btn);
        cancelBtn.setOnClickListener(this);
        confirmBtn.setOnClickListener(this);
        titleView = (TextView) contentView.findViewById(R.id.layout_city_choose_title_tv);
        setContentView(contentView);

        initDates();
    }

    public DateOrCityChooseLdy(Context context, int style, boolean isDatechoose,int minYear)
    {
        super(context, style);
        this.context = context;
        this.isDatechoose = isDatechoose;
        this.minYear = minYear;
        contentView = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.layout_city_choose_ldy, null);
        yearWv = (WheelView) contentView.findViewById(R.id.layout_city_choose_province_wv);
        yearWv.addChangingListener(this);
        monthWv = (WheelView) contentView.findViewById(R.id.layout_city_choose_city_wv);
        monthWv.addChangingListener(this);
        dayWv = (WheelView) contentView.findViewById(R.id.layout_city_choose_district_wv);
        dayWv.addChangingListener(this);
        yearWv.setShadowColor(0xf0f0f0, 0xf0f0f0, 0xf0f0f0);// 设置颜色
        monthWv.setShadowColor(0xf0f0f0, 0xf0f0f0, 0xf0f0f0);
        dayWv.setShadowColor(0xf0f0f0, 0xf0f0f0, 0xf0f0f0);
        setVisibleItems(visibleItems);
        cancelBtn = (Button) contentView.findViewById(R.id.layout_city_choose_cancel_btn);
        confirmBtn = (Button) contentView.findViewById(R.id.layout_city_choose_confirm_btn);
        cancelBtn.setOnClickListener(this);
        confirmBtn.setOnClickListener(this);
        titleView = (TextView) contentView.findViewById(R.id.layout_city_choose_title_tv);
        setContentView(contentView);
        initDates();
    }
    public DateOrCityChooseLdy(Context context, int style, boolean isDatechoose)
    {
        super(context, style);
        this.context = context;
        this.isDatechoose = isDatechoose;
        contentView = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.layout_city_choose_ldy, null);
        yearWv = (WheelView) contentView.findViewById(R.id.layout_city_choose_province_wv);
        yearWv.addChangingListener(this);
        monthWv = (WheelView) contentView.findViewById(R.id.layout_city_choose_city_wv);
        monthWv.addChangingListener(this);
        dayWv = (WheelView) contentView.findViewById(R.id.layout_city_choose_district_wv);
        dayWv.addChangingListener(this);
        yearWv.setShadowColor(0xf0f0f0, 0xf0f0f0, 0xf0f0f0);// 设置颜色
        monthWv.setShadowColor(0xf0f0f0, 0xf0f0f0, 0xf0f0f0);
        dayWv.setShadowColor(0xf0f0f0, 0xf0f0f0, 0xf0f0f0);
        setVisibleItems(visibleItems);
        cancelBtn = (Button) contentView.findViewById(R.id.layout_city_choose_cancel_btn);
        confirmBtn = (Button) contentView.findViewById(R.id.layout_city_choose_confirm_btn);
        cancelBtn.setOnClickListener(this);
        confirmBtn.setOnClickListener(this);
        titleView = (TextView) contentView.findViewById(R.id.layout_city_choose_title_tv);
        setContentView(contentView);
        initDates();
    }

    public DateOrCityChooseLdy(Context context)
    {
        this(context, null, true);
        this.isDatechoose = true;
    }

    /** 更新显示日 */
    private void updateData()
    {
        if (isDatechoose)
        {
            int currentItem = yearWv.getCurrentItem();
            String string = years[currentItem];
            String str1 = string.substring(0, string.length() - 1);

            int currentItem2 = monthWv.getCurrentItem();
            String string2 = months[currentItem2];
            String str2 = string2.substring(0, string2.length() - 1);

            if (Integer.parseInt(str1) % 4 == 0 && Integer.parseInt(str1) % 100 != 0 || Integer.parseInt(str1) % 400 == 0)
            {
                if (str2.equals("1") || str2.equals("3") || str2.equals("5") || str2.equals("7") || str2.equals("8") || str2.equals("10") || str2.equals("12"))
                {
                    days = new String[31];
                    for (int i = 0; i < 31; i++)
                    {
                        days[i] = (i + 1) + "日";
                    }
                }
                else if (str2.equals("4") || str2.equals("6") || str2.equals("9") || str2.equals("11"))
                {
                    days = new String[30];
                    for (int i = 0; i < 30; i++)
                    {
                        days[i] = (i + 1) + "日";
                    }
                }
                else
                {
                    days = new String[29];
                    for (int i = 0; i < 29; i++)
                    {
                        days[i] = (i + 1) + "日";
                        Debug.e(TAG, "year:" + " 2月：" + string + days[i]);
                        System.out.print("year:" + " 2月：" + string + days[i]);
                    }
                }
            }
            else
            {
                if (str2.equals("1") || str2.equals("3") || str2.equals("5") || str2.equals("7") || str2.equals("8") || str2.equals("10") || str2.equals("12"))
                {
                    days = new String[31];
                    for (int i = 0; i < 31; i++)
                    {
                        days[i] = (i + 1) + "日";
                    }
                }
                else if (str2.equals("4") || str2.equals("6") || str2.equals("9") || str2.equals("11"))
                {
                    days = new String[30];
                    for (int i = 0; i < 30; i++)
                    {
                        days[i] = (i + 1) + "日";
                    }
                }
                else
                {
                    days = new String[28];
                    for (int i = 0; i < 28; i++)
                    {
                        days[i] = (i + 1) + "日";
                        Debug.e(TAG, "year:" + " 2月：" + string + days[i]);

                    }
                }
            }
            dayWv.setViewAdapter(new ArrayWheelAdapter<String>(context, days));
            dayWv.setCurrentItem(0);
        }
        else
        {
            if (yearWv.getViewAdapter() == null)
            {
                yearWv.setViewAdapter(new ArrayWheelAdapter<String>(context, provinceDatas));
            }

            updateCities();

            updateDistrict();

            updateZipCode(0);
        }
    }

    /** 更新显示的城市数据 */
    private void updateCities()
    {
        int provinceItem = yearWv.getCurrentItem();

        currentProvince = provinceDatas[provinceItem];
        String[] cities = citiesMap.get(currentProvince);
        if (cities == null)
        {
            cities = new String[] {""};
        }

        monthWv.setViewAdapter(new ArrayWheelAdapter<String>(context, cities));
        monthWv.setCurrentItem(0);
    }

    /** 更新显示的区县数据 */
    private void updateDistrict()
    {
        int cityItem = monthWv.getCurrentItem();
        String[] cities = citiesMap.get(currentProvince);

        if (cities == null)
        {
            return;
        }

        currentCity = cities[cityItem];

        String[] areas = districsMap.get(currentCity);

        if (areas == null)
        {
            areas = new String[] {""};
        }

        dayWv.setViewAdapter(new ArrayWheelAdapter<String>(context, areas));
        dayWv.setCurrentItem(0);
        // 增加此句，主要修复只选市，没选区时，一直显示昌平区问题
        currentDistrict = districsMap.get(currentCity)[0];
    }

    /** 更新显示邮编 */
    private void updateZipCode(int position)
    {
        currentDistrict = districsMap.get(currentCity)[position];
        currentZipCode = zipCodesMap.get(currentDistrict);
    }

    public int getVisibleItems()
    {
        return visibleItems;
    }

    /** 设置可见条目数 */
    public void setVisibleItems(int visibleItems)
    {
        this.visibleItems = visibleItems;
        yearWv.setVisibleItems(visibleItems);
        monthWv.setVisibleItems(visibleItems);
        dayWv.setVisibleItems(visibleItems);
    }

    public DateOrCityChooseListener getDateChooseListener()
    {
        return dateChooseListener;
    }

    /** 设置选择器回调 */
    public void setDateOrCityChooseListener(DateOrCityChooseListener dateChooseListener)
    {
        this.dateChooseListener = dateChooseListener;
    }

    public View getContentView()
    {
        return contentView;
    }

    /** 获取当前年份、 获取当前省 */
    public String getCurrentYear()
    {
        if (isDatechoose)
        {
            int currentItem = yearWv.getCurrentItem();
            String string = years[currentItem];
            String str1 = string.substring(0, string.length() - 1);
            return str1;
        }
        else
        {
            return currentProvince;
        }
    }

    /** 获取当前月份 、 获取当前城市 */
    public String getCurrentMonth()
    {
        if (isDatechoose)
        {
            int currentItem2 = monthWv.getCurrentItem();
            String string2 = months[currentItem2];
            String str2 = string2.substring(0, string2.length() - 1);
            return str2;
        }
        else
        {
            return currentCity;
        }
    }

    /** 获取当前日 、 获取当前区 */
    public String getCurrentDay()
    {
        if (isDatechoose)
        {
            int currentItem3 = dayWv.getCurrentItem();
            String string3 = days[currentItem3];
            String str3 = string3.substring(0, string3.length() - 1);
            return str3;
        }
        else
        {
            return currentDistrict;
        }
    }

    /**
     * 初始化数据
     */
    public void initDates()
    {
        /**
         * 初始化年月
         */
        if (isDatechoose)
        {

            Time time = new Time();
            time.setToNow();
            int lenth = time.year - minYear;
            years = new String[lenth+1];
            int j = 0;
            for (int i = minYear; i < time.year+1; i++)
            {
                years[j] = i + "年";
                j++;
            }
            months = new String[12];
            for (int i = 0; i < 12; i++)
            {
                months[i] = (i + 1) + "月";
            }

            if (yearWv.getViewAdapter() == null)
            {
                yearAdapter = new ArrayWheelAdapter<String>(context, years);
                yearWv.setViewAdapter(yearAdapter);
            }
            if (monthWv.getViewAdapter() == null)
            {
                monthWv.setViewAdapter(new ArrayWheelAdapter<String>(context, months));
            }
            updateData();
        }
        else
        {
            /**
             * 初始化城市选择
             */
            List<ProvinceModel> provinceList = null;
            AssetManager asset = context.getAssets();
            try
            {
                InputStream input = asset.open("province_data.xml");
                // 创建一个解析xml的工厂对象
                SAXParserFactory spf = SAXParserFactory.newInstance();
                // 解析xml
                SAXParser parser = spf.newSAXParser();
                XmlParserHandler handler = new XmlParserHandler();
                parser.parse(input, handler);
                input.close();
                // 获取解析出来的数据
                provinceList = handler.getDataList();
                // 初始化默认选中的省、市、区
                if (provinceList != null && !provinceList.isEmpty())
                {
                    currentProvince = provinceList.get(0).getName();
                    List<CityModel> cityList = provinceList.get(0).getCityList();
                    if (cityList != null && !cityList.isEmpty())
                    {
                        currentCity = cityList.get(0).getName();
                        List<DistrictModel> districtList = cityList.get(0).getDistrictList();
                        currentDistrict = districtList.get(0).getName();
                        currentZipCode = districtList.get(0).getZipcode();
                    }
                }
                //
                provinceDatas = new String[provinceList.size()];
                for (int i = 0; i < provinceList.size(); i++)
                {
                    // 遍历所有省的数据
                    provinceDatas[i] = provinceList.get(i).getName();
                    List<CityModel> cityList = provinceList.get(i).getCityList();
                    String[] cityNames = new String[cityList.size()];
                    for (int j = 0; j < cityList.size(); j++)
                    {
                        // 遍历省下面的所有市的数据
                        cityNames[j] = cityList.get(j).getName();
                        List<DistrictModel> districtList = cityList.get(j).getDistrictList();
                        String[] distrinctNameArray = new String[districtList.size()];
                        DistrictModel[] distrinctArray = new DistrictModel[districtList.size()];
                        for (int k = 0; k < districtList.size(); k++)
                        {
                            // 遍历市下面所有区/县的数据
                            DistrictModel districtModel = new DistrictModel(districtList.get(k).getName(), districtList.get(k).getZipcode());
                            // 区/县对于的邮编，保存到zipCodesMap
                            zipCodesMap.put(districtList.get(k).getName(), districtList.get(k).getZipcode());
                            distrinctArray[k] = districtModel;
                            distrinctNameArray[k] = districtModel.getName();
                        }
                        // 市-区/县的数据，保存到districsMap
                        districsMap.put(cityNames[j], distrinctNameArray);
                    }
                    // 省-市的数据，保存到citiesMap
                    citiesMap.put(provinceList.get(i).getName(), cityNames);
                }

                updateData();
            }
            catch (Throwable e)
            {
                e.printStackTrace();
            }
            finally
            {

            }
        }
    }

    public interface DateOrCityChooseListener
    {
        /** 点击取消按钮 */
        public void onCancel(DateOrCityChooseLdy chooseLdy);

        /** 点击确认按钮 */
        public void onConfirm(String date, DateOrCityChooseLdy dateChooseLdy);
    }

    @Override
    public void onClick(View v)
    {
        if (dateChooseListener == null)
        {
            return;
        }

        if (v == cancelBtn)
        {
            dateChooseListener.onCancel(DateOrCityChooseLdy.this);
        }
        else if (v == confirmBtn)
        {
            dateChooseListener.onConfirm(getDate(), DateOrCityChooseLdy.this);
        }
    }

    /** 滚动条位置变化时的监听 */
    @Override
    public void onChanged(WheelView wheel, int oldValue, int newValue)
    {
        if (isDatechoose)
        {

            if (wheel == monthWv)
            {
                updateData();
            }
            else if (wheel == yearWv)
            {
                updateData();
            }
        }
        else
        {
            if (wheel == yearWv)
            {
                updateCities();
                updateDistrict();
                updateZipCode(0);
            }
            else if (wheel == monthWv)
            {
                updateDistrict();
                updateZipCode(0);
            }
            else if (wheel == dayWv)
            {
                updateZipCode(newValue);
            }
        }
    }

    public WheelView getYearWv()
    {
        return yearWv;
    }

    public WheelView getMonthWv()
    {
        return monthWv;
    }

    public WheelView getDayWv()
    {
        return dayWv;
    }

    /**
     * 获取日期
     */
    public String getDate()
    {
        if (isDatechoose)
        {

            String str2 = getCurrentMonth();
            String str3 = getCurrentDay();
            if (str2.length() != 2)
            {
                str2 = "0" + str2;
            }
            if (str3.length() != 2)
            {
                str3 = "0" + str3;
            }
            return getCurrentYear() + "-" + str2 + "-" + str3;
        }
        else
        {
            return getCurrentYear() + " " + getCurrentMonth() + " " + getCurrentDay();
        }
    }

    /**
     * 设置最后一个是否显示
     */
    public  void setLastViewVisible(int VISIBLE)
    {
        dayWv.setVisibility(VISIBLE);
    }
    /**
     * 设置标题
     */
    public  void setTitle(String title)
    {
        titleView.setText(title);
    }
    /**
     * 设置右边确认btn字
     */
    public  void setRightText(String Text)
    {
        confirmBtn.setText(Text);
    }
    /**
     * 设置右边确认btn颜色
     */
    public  void setRightColor(int color)
    {
        confirmBtn.setTextColor(color);
    }
    /**
     * 设置日期的最小年
     */
    public  void setMinYear(int MinYear)
    {
        if (isDatechoose)
        {
            this.minYear = MinYear;
        }
    }
    /**
     * 设置到指定的年
     */
    public  void setYear(int year)
    {
        if (isDatechoose)
        {
            for (int i = 0; i < years.length; i++)
            {
                if (year == Integer.parseInt(years[i].substring(0, 4)))
                {
                    yearWv.setCurrentItem(i);
                }
            }
        }
    }
    /**
     * 设置到指定月
     */
    public  void setMonth(int month)
    {
        if (isDatechoose)
        {
            for (int i = 0; i < months.length; i++)
            {
                String string = months[i];
                if (month == Integer.parseInt(string.substring(0, string.length()-1)))
                {
                    monthWv.setCurrentItem(i);
                }
            }
        }
    }
    /**
     * 设置到指定日
     */
    public  void setDay(int day)
    {
        if (isDatechoose)
        {
            for (int i = 0; i < days.length; i++)
            {
                String string = days[i];
                if (day == Integer.parseInt(string.substring(0, string.length()-1)))
                {
                    dayWv.setCurrentItem(i);
                }
            }
        }
    }

}
