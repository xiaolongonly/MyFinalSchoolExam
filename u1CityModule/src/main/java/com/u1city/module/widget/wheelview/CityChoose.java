package com.u1city.module.widget.wheelview;

import android.content.Context;
import android.content.res.AssetManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.u1city.module.R;
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
 * @author zhengjb
 * @date 2015-9-15
 *       类说明：基于WheelView的省、市、区、邮编选择器
 */
public class CityChoose implements OnClickListener, OnWheelChangedListener
{
    private final Context context;

    private final View contentView;

    private int visibleItems = 7;

    private WheelView provinceWv;
    private WheelView cityWv;
    private WheelView districtWv;

    public Button cancelBtn;
    public Button confirmBtn;
    public TextView titleView;

    private CityChooseListener cityChooseListener;

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

    public CityChoose(Context context, final CityChooseListener cityChooseListener)
    {
        super();
        this.context = context;
        contentView = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.layout_city_choose, null);

        provinceWv = (WheelView) contentView.findViewById(R.id.layout_city_choose_province_wv);
        provinceWv.addChangingListener(this);
        cityWv = (WheelView) contentView.findViewById(R.id.layout_city_choose_city_wv);
        cityWv.addChangingListener(this);
        districtWv = (WheelView) contentView.findViewById(R.id.layout_city_choose_district_wv);
        districtWv.addChangingListener(this);

        setVisibleItems(visibleItems);

        cancelBtn = (Button) contentView.findViewById(R.id.layout_city_choose_cancel_btn);
        confirmBtn = (Button) contentView.findViewById(R.id.layout_city_choose_confirm_btn);

        cancelBtn.setOnClickListener(this);
        confirmBtn.setOnClickListener(this);

        titleView = (TextView)contentView.findViewById(R.id.layout_city_choose_title_tv);
        
        initProvinceDatas();
    }
    
    public CityChoose(Context context)
    {
       this(context, null);
    }

    /** 更新显示的数据 */
    private void updateData()
    {
        if (provinceWv.getViewAdapter() == null)
        {
            provinceWv.setViewAdapter(new ArrayWheelAdapter<String>(context, provinceDatas));
        }

        updateCities();

        updateDistrict();
        
        updateZipCode(0);
    }

    /** 更新显示的区县数据 */
    private void updateDistrict()
    {
        int cityItem = cityWv.getCurrentItem();
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

        districtWv.setViewAdapter(new ArrayWheelAdapter<String>(context, areas));
        districtWv.setCurrentItem(0);
        // 增加此句，主要修复只选市，没选区时，一直显示昌平区问题
        currentDistrict = districsMap.get(currentCity)[0];
    }

    /** 更新显示的城市数据 */
    private void updateCities()
    {
        int provinceItem = provinceWv.getCurrentItem();

        currentProvince = provinceDatas[provinceItem];
        String[] cities = citiesMap.get(currentProvince);
        if (cities == null)
        {
            cities = new String[] {""};
        }

        cityWv.setViewAdapter(new ArrayWheelAdapter<String>(context, cities));
        cityWv.setCurrentItem(0);
    }

    public int getVisibleItems()
    {
        return visibleItems;
    }

    /** 设置可见条目数 */
    public void setVisibleItems(int visibleItems)
    {
        this.visibleItems = visibleItems;
        provinceWv.setVisibleItems(visibleItems);
        cityWv.setVisibleItems(visibleItems);
        districtWv.setVisibleItems(visibleItems);
    }

    public CityChooseListener getCityChooseListener()
    {
        return cityChooseListener;
    }

    /** 设置选择器回调 */
    public void setCityChooseListener(CityChooseListener cityChooseListener)
    {
        this.cityChooseListener = cityChooseListener;
    }

    public View getContentView()
    {
        return contentView;
    }

    /** 获取当前省份 */
    public String getCurrentProvince()
    {
        return currentProvince;
    }

    /** 获取当前城市 */
    public String getCurrentCity()
    {
        return currentCity;
    }

    /** 获取当前区县 */
    public String getCurrentDistrict()
    {
        return currentDistrict;
    }

    /** 获取当前邮编 */
    public String getCurrentZipCode()
    {
        return currentZipCode;
    }

    /**
     * 解析省市区的XML数据
     */

    public void initProvinceDatas()
    {
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

    public interface CityChooseListener
    {
        /** 点击取消按钮 */
        public void onCancel();

        /** 点击确认按钮 */
        public void onConfirm(String currentProvince, String currentCity, String currentDistrict, String currentZipCode);
    }

    @Override
    public void onClick(View v)
    {
        if (cityChooseListener == null)
        {
            return;
        }

        if (v == cancelBtn)
        {
            cityChooseListener.onCancel();
        }
        else if (v == confirmBtn)
        {
            cityChooseListener.onConfirm(currentProvince, currentCity, currentDistrict, currentZipCode);
        }
    }

    /** 滚动条位置变化时的监听 */
    @Override
    public void onChanged(WheelView wheel, int oldValue, int newValue)
    {
      if (wheel == provinceWv)
      {
          updateCities();
          updateDistrict();
          updateZipCode(0);
      }
      else if (wheel == cityWv)
      {
          updateDistrict();
          updateZipCode(0);
      }
      else if (wheel == districtWv)
      {
          updateZipCode(newValue);
      }
    }
    
    private void updateZipCode(int position){
        currentDistrict = districsMap.get(currentCity)[position];
        currentZipCode = zipCodesMap.get(currentDistrict);
    }

	public WheelView getProvinceWv() {
		return provinceWv;
	}

	public WheelView getCityWv() {
		return cityWv;
	}

	public WheelView getDistrictWv() {
		return districtWv;
	}
    
    
}
