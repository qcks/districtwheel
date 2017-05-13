package com.qckiss.chase.districtwheel;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.qckiss.chase.districtwheel.Dao.CityDao;
import com.qckiss.chase.districtwheel.cityBean.CityBean;
import com.qckiss.chase.districtwheel.wheelview.WheelView;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by qckiss on 2016/11/23.
 */
public class CityLayout extends LinearLayout {

    private WheelView mProvincePicker;
    private WheelView mCityPicker;
    private WheelView mDistrictPicker;

    private int mCurrProvinceIndex = -1;
    private int mCurrCityIndex = -1;
    private int mCurrDistrictIndex = -1;

    private ArrayList<String> mProvinceNameList = new ArrayList<>();
    private ArrayList<String> mProvinceCodeList = new ArrayList<>();

    private ArrayList<String> mCityNameList = new ArrayList<>();
    private ArrayList<String> mCityCodeList = new ArrayList<>();

    private ArrayList<String> mDistrictNameList = new ArrayList<>();
    private ArrayList<String> mDistrictCodeList = new ArrayList<>();
    private CityDao cityDao;

    public CityLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        cityDao = new CityDao(context);
        getProvinceData();
    }

    public CityLayout(Context context) {
        this(context, null);
    }

    private ArrayList<String> getProvinceData() {
        List<CityBean> mProvinceList = cityDao.selectProvince();
        for (CityBean bean : mProvinceList) {
            mProvinceNameList.add(bean.name);
            mProvinceCodeList.add(bean.code);
        }
        return mProvinceNameList;
    }

    private ArrayList<String> getCityData(String pcode) {
        mCityNameList.clear();
        mCityCodeList.clear();
        List<CityBean> mCityList = cityDao.selectCity(pcode);
        for (CityBean bean : mCityList) {
            mCityNameList.add(bean.name);
            mCityCodeList.add(bean.code);
        }
        return mCityNameList;
    }

    private ArrayList<String> getDistrictData(String cityCode) {
        mDistrictNameList.clear();
        mDistrictCodeList.clear();
        List<CityBean> mDistrictList = cityDao.selectDistrict(cityCode);
        for (CityBean bean : mDistrictList) {
            mDistrictNameList.add(bean.name);
            mDistrictCodeList.add(bean.code);
        }
        return mDistrictNameList;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        LayoutInflater.from(getContext()).inflate(R.layout.layout_city, this);

        mProvincePicker = (WheelView) findViewById(R.id.province_wv);
        mCityPicker = (WheelView) findViewById(R.id.city_wv);
        mDistrictPicker = (WheelView) findViewById(R.id.district_wv);
        //初始的省
        mProvincePicker.setData(mProvinceNameList);
        mProvincePicker.setDefault(0);
        //初始的市
        mCityPicker.setData(getCityData(mProvinceCodeList.get(0)));
        mCityPicker.setDefault(1);
        //初始区
        mDistrictPicker.setData(getDistrictData(mCityCodeList.get(1)));
        mDistrictPicker.setDefault(1);

        mProvincePicker.setOnSelectListener(new WheelView.OnSelectListener() {
            @Override
            public void endSelect(int id, String text) {
                if (text.equals("") || text == null)
                    return;//
                if (mCurrProvinceIndex != id) {
                    mCurrProvinceIndex = id;
                    ArrayList<String> city = getCityData(mProvinceCodeList.get(id));
                    mCityPicker.setData(city);
                    if (city.size() > 1) {
                        //if city is more than one,show start index == 1
                        mCityPicker.setDefault(1);
                        mCurrCityIndex = 1;
                    } else {
                        mCityPicker.setDefault(0);
                        mCurrCityIndex = 0;
                    }
                    ArrayList<String> district = getDistrictData(mCityCodeList.get(mCurrCityIndex));
                    mDistrictPicker.setData(district);
                    if (district.size() > 1) {
                        //if city is more than one,show start index == 1
                        mDistrictPicker.setDefault(1);
                    } else {
                        mDistrictPicker.setDefault(0);
                    }
                }

            }

            @Override
            public void selecting(int id, String text) {
            }
        });

        mCityPicker.setOnSelectListener(new WheelView.OnSelectListener() {
            @Override
            public void endSelect(int id, String text) {
                if (text.equals("") || text == null)
                    return;//
                if (mCurrCityIndex != id) {
                    mCurrCityIndex = id;
                    ArrayList<String> district = getDistrictData(mCityCodeList.get(id));
                    mDistrictPicker.setData(district);
                    if (district.size() > 1) {
                        //if city is more than one,show start index == 1
                        mDistrictPicker.setDefault(1);
                    } else {
                        mDistrictPicker.setDefault(0);
                    }
                }

            }

            @Override
            public void selecting(int id, String text) {
            }
        });
        mDistrictPicker.setOnSelectListener(new WheelView.OnSelectListener() {

            @Override
            public void endSelect(int id, String text) {
                if (text.equals("") || text == null)
                    return;
                if (mCurrDistrictIndex != id) {
                    mCurrDistrictIndex = id;
                    String selectDistrict = mDistrictPicker.getSelectedText();
                    if (selectDistrict == null || selectDistrict.equals(""))
                        return;
                    int lastIndex = mDistrictPicker.getListSize();
                    if (id > lastIndex) {
                        mDistrictPicker.setDefault(lastIndex - 1);
                    }
                }
            }

            @Override
            public void selecting(int id, String text) {

            }
        });
    }

    public String getProvince() {
        if (mProvincePicker == null) {
            return null;
        }
        return mProvincePicker.getSelectedText();
    }

    public String getCity() {
        if (mCityPicker == null) {
            return null;
        }
        return mCityPicker.getSelectedText();
    }

    public String getDistrict() {
        if (mDistrictPicker == null) {
            return null;
        }
        return mDistrictPicker.getSelectedText();
    }
}
