/*
 * 系统: U1CityModule
 * 文件名: ParseUtil.java
 * 版权: U1CITY Corporation 2015
 * 描述: 
 * 创建人: zhengjb
 * 创建时间: 2015-9-23 上午10:33:23
 */
package com.u1city.module.util;

/**
 * 基本数据类型的解析类
 * 
 * @author zhengjb
 */
public class BaseParser
{
    /**
     * 解析int类型
     * 默认值为0
     * @param text 解析字段
     * */
    public static final int parseInt(String text){
        return parseInt(0, text);
    }
    
    /**
     * 解析int类型
     * @param defaultValue 默认值
     * @param text 解析字段
     * */
    public static final int parseInt(int defaultValue, String text){
        int intValue = defaultValue;
        if(text != null) {
            try {
                intValue = Integer.parseInt(text);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                intValue = defaultValue;
            }
        }
        return intValue;
    }
    
    /**
     * 解析double类型
     * 默认值为0.00
     * @param text 解析字段
     * 
     * @author zhengjb
     */
    public static final double parseDouble(String text)
    {   
        return parseDouble(0.00d, text);
    }
    
    /**
     * 解析double类型
     * @param defaultValue 默认值
     * @param text 解析字段
     * 
     * @author zhengjb
     */
    public static final double parseDouble(double defaultValue, String text){
        double doubleValue = defaultValue;

        if(text != null) {
            try {
                doubleValue = Double.parseDouble(text);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                doubleValue = defaultValue;
            }
        }
        return doubleValue;
    }
    
    
}
