package com.u1city.module.common;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhengjb
 * @date 2015-5-05
 * @time 下午5:29:29
 * 
 *       类说明:自由转化数据与json字符串的工具类 希望能替代繁杂的Analysis
 * */
public class JsonAnalysis<T>
{
    // private static final String TAG = JsonAnalysis.class.getSimpleName();
    public static final int PARSER_TYPE_GSON = 0;
    public static final int PARSER_TYPE_FAST_JSON = 1;

    Gson gson = new Gson();
    private int jsonType = PARSER_TYPE_GSON;

    /**
     * 设置使用的json工具类型
     * @param jsonType PARSER_TYPE_GSON 使用gson
     *                 PARSER_TYPE_FAST_JSON 使用fastJson
     * */
    public void setJsonType(int jsonType) {
        if(jsonType != PARSER_TYPE_GSON && jsonType != PARSER_TYPE_FAST_JSON){
            throw new IllegalArgumentException("jsonType should be PARSER_TYPE_GSON or PARSER_TYPE_FAST_JSON");
        }

        this.jsonType = jsonType;
    }

    /**
     * 将对象转换成json字符串
     * 
     * @param t 转化对象
     * @return json字符串
     * */
    public String toJson(T t)
    {

        if(jsonType == PARSER_TYPE_GSON){
            return gson.toJson(t);
        }else{
            return JSONObject.toJSONString(t);
        }
    }


    /**
     * 将json字符串转换成对象
     * 
     * @param json 字符串
     * @param clazz 类
     * @return 转换得到的对象
     * */
    public T fromJson(String json, Class<? extends T> clazz)
    {
        if(jsonType == PARSER_TYPE_GSON){
            return gson.fromJson(json, clazz);
        }else{
            return JSONObject.parseObject(json, clazz);
        }
    }

    /**
     * 将对象列表转换成json字符串
     * 
     * @param array 对象列表
     * @return json字符串
     * */
    public String listToJson(List<T> array)
    {
        if(jsonType == PARSER_TYPE_GSON) {
            return gson.toJson(array, new TypeToken<List<T>>() {
            }.getType());
        }else{
            return JSONObject.toJSONString(array);
        }
    }

    /**
     * 将json字符串转换成对象列表
     * 
     * @param json 字符串
     * @param clazz 类
     * @return 对象列表
     * */
    public List<T> listFromJson(String json, Class<? extends T> clazz)
    {
        if(jsonType == PARSER_TYPE_GSON) {
            List<T> lst = new ArrayList<T>();

            JsonArray array = new JsonParser().parse(json).getAsJsonArray();
            for (final JsonElement elem : array) {
                lst.add(gson.fromJson(elem, clazz));
            }

            return lst;

        }else{
            List<? extends T> lst = JSONArray.parseArray(json, clazz);
            return (List<T>) lst;
        }
    }

}
