package com.u1city.module.common;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/***************************************
 * 
 * @author lwli
 * @date 2014-7-7
 * @time 下午5:38:43 类说明:
 * 
 **************************************/
public class BaseAnalysis
{
    private String status = "";
    private String msg = "";
    private String result = "";
    private JSONObject json;

    /** @return 数据总量 */
    public int getTotal()
    {
        try
        {
            return getIntFromResult("total");
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        return 0;
    }

    /** 
     * @return 完整的json数据
     *  */
    public JSONObject getJson()
    {
        return json;
    }

    /***************************************
     * 
     * 基础解析类，根据与后台的约定，获取一些规定的数据
     * 
     * @author lwli
     * @modify zhengjb
     * @date 2014-7-7
     * @time 下午5:38:43
     * @param json 接口返回数据
     **************************************/
    public BaseAnalysis(JSONObject json)
    {
        this.json = json;
        status = getValue("Code");
        msg = getValue("Message");
        result = getValue("Result");
    }

    /**
     * @return 结果数据，一般主要的数据都放在这里
     * 
     * */
    public String getResult()
    {
        return result;
    }

    /** @return 是否获取结果数据成功 */
    public boolean success()
    {
        return "000".equals(status);
    }

    /**
     * @return 是否错误码001
     * */
    public boolean isErrorCodeOne()
    {
        return "001".equals(status);
    }

    /** 后台返回的关于请求的信息（错误信息或者其他） */
    public String msg()
    {
        return msg;
    }

    /**
     * @return 请求的返回码
     * */
    public String getStatus()
    {
        return status;
    }

    /**
     * 从接口数据中获取某个属性的字符串值
     * 
     * @param name 属性名称
     * @return 某个属性的字符串值
     * */
    public String getValue(String name)
    {
        return json.optString(name);
    }

    /**
     * 从接口数据中获取某个属性的json数组
     * * @param name 属性名称
     * 
     * @return 某个属性的json数组
     * */
    public JSONArray getJsonArray(String name)
    {
        return json.optJSONArray(name);
    }

    /**
     * 从接口数据中获取某个属性的json对象
     * * @param name 属性名称
     * 
     * @return 某个属性的json对象
     * */
    public JSONObject getJsonObject(String name)
    {
        return json.optJSONObject(name);
    }

    /** 获取服务器返回Result JSONObject对象 */
    public JSONObject getJsonObjectResult() throws JSONException
    {
        return new JSONObject(this.result);
    }

    /**
     * * @param name 属性名称
     * 从result获取整型数值
     * @return result数据中某个属性的整型数值
     */
    public int getIntFromResult(String name) throws JSONException
    {
        JSONObject jSONObject = new JSONObject(result);
        return jSONObject.optInt(name);
    }

    /**
     * 从result中获取字符串
     *     * @param name 属性名称
     *     @return result数据中某个属性的字符串值
     */
    public String getStringFromResult(String name) throws JSONException
    {
        JSONObject jSONObject = new JSONObject(result);
        return jSONObject.optString(name);
    }

    /**
     * 从result中获取双精度浮点型数值
     * @param name 属性名称
     * @return result数据中某个属性的双精度浮点型数值
     */
    public double getDoubleFromResult(String name) throws JSONException
    {
        JSONObject jSONObject = new JSONObject(result);
        return jSONObject.optDouble(name);
    }
}