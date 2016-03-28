        /*
 * 系统: LaiDianYi
 * 文件名: StoreOrderModel.java
 * 版权: U1CITY Corporation 2015
 * 描述:
 * 创建人: zhengjb
 * 创建时间: 2015-8-31 下午3:35:04
 */
package com.xiaolongonly.finalschoolexam.model;

import java.io.Serializable;

/**
 * 门店消费订单
 * 
 * @author zhengjb
 */
public class StoreOrderModel implements Serializable
{
    private String tmallShopName;// 品牌商名称
    private String tmallShopId;// 品牌商ID
    private String storeName; // 门店名称
    private double consumpMoney;// 消费金额
    private String useCouponInfo; // 使用优惠券信息（数据格式：有使用满￥200减￥20优惠券 有使用满￥100代金券）
    private String smallTicket; // 小票号
    private String time; // 消费时间
    private String recordId; // 消费记录ID
    private double integralNum; // 获得积分数

    public String getTmallShopName()
    {
        return tmallShopName;
    }

    public void setTmallShopName(String tmallShopName)
    {
        this.tmallShopName = tmallShopName;
    }

    public String getTmallShopId()
    {
        return tmallShopId;
    }

    public void setTmallShopId(String tmallShopId)
    {
        this.tmallShopId = tmallShopId;
    }

    public String getStoreName()
    {
        return storeName;
    }

    public void setStoreName(String storeName)
    {
        this.storeName = storeName;
    }

    public double getConsumpMoney()
    {
        return consumpMoney;
    }

    public void setConsumpMoney(double consumpMoney)
    {
        this.consumpMoney = consumpMoney;
    }

    public String getUseCouponInfo()
    {
        return useCouponInfo;
    }

    public void setUseCouponInfo(String useCouponInfo)
    {
        this.useCouponInfo = useCouponInfo;
    }

    public String getSmallTicket()
    {
        return smallTicket;
    }

    public void setSmallTicket(String smallTicket)
    {
        this.smallTicket = smallTicket;
    }

    public String getTime()
    {
        return time;
    }

    public void setTime(String time)
    {
        this.time = time;
    }

    public String getRecordId()
    {
        return recordId;
    }

    public void setRecordId(String recordId)
    {
        this.recordId = recordId;
    }

    public double getIntegralNum()
    {
        return integralNum;
    }

    public void setIntegralNum(double integralNum)
    {
        this.integralNum = integralNum;
    }

}
