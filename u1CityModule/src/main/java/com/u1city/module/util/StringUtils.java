package com.u1city.module.util;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author linjy
 * @time 2015-5-5 14:32:44
 * @类说明 字符串工具类
 * 
 *      <pre>
 * 1.判断字符串是否为空 
 * 2.判断字符串是否为空(多了"null")
 * 3.对比两个字符串 
 * 4.如果字符串为null设置成"",不为空则返回本身
 * 5.首字母大写
 * 6.格式转换为utf-8
 * 7.得到html中的<a>标签内容
 * 8.在html中的特殊
 * 9.从文字的字节码中将全角空格的字节码替换为半角空格的字节码 
 * 10.从文字的字节码中将半角空格的字节码替换为全角空格的字节码 
 * 11.判断字符串是否为int
 * 12.空格替换为;
 * 13.检测是否有emoji字符
 * 14.检测是否有emoji字符
 * 15.过滤emoji 或者 其他非文字类型的字符
 * 16.计算两个日期型的时间相差多少时间
 * 17.设置创建时间，今天之内显示时间，今天之前显示日期
 * 18.将时间戳转换为时间
 * </pre>
 * 
 */
public class StringUtils
{

    /**
     * 1.判断字符串是否为空 is null or its length is 0 or it is made by space
     * 
     * <pre>
     * isBlank(null) = true;
     * isBlank(&quot;&quot;) = true;
     * isBlank(&quot;  &quot;) = true;
     * isBlank(&quot;a&quot;) = false;
     * isBlank(&quot;a &quot;) = false;
     * isBlank(&quot; a&quot;) = false;
     * isBlank(&quot;a b&quot;) = false;
     * </pre>
     * 
     * @param str
     * @return if string is null or its size is 0 or it is made by space, return
     *         true, else return false.
     */
    public static boolean isBlank(String str)
    {
        return (str == null || str.trim().length() == 0);
    }

    /**
     * 
     * 2.判断字符串是否为空(多了"null") is null or its length is 0
     * 
     * <pre>
     * isEmpty(null) = true;
     * isEmpty(&quot;&quot;) = true;
     * isEmpty(&quot;  &quot;) = false;
     * isEmpty(&quot;null&quot;) = true;
     * </pre>
     * 
     * @param str
     * @return if string is null or its size is 0, return true, else return
     *         false.
     */
    public static boolean isEmpty(String str)
    {
        return (str == null || str.length() == 0 || str.equals("null"));
    }

    /*
     * public static void setCreated(String created, TextView dateTv){
     * SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
     * 
     * if(!StringUtils.isEmpty(created)){ try{ Date date = sdf.parse(created);
     * 
     * Date now = new Date();
     * 
     * if(date.getDate() == now.getDate()){ dateTv.setText(created.substring(11,
     * 16)); }else{ dateTv.setText(created.substring(5, 10)); } }catch(Exception
     * e){ e.printStackTrace(); } } }
     */

    /**
     * 3.对比两个字符串 compare two string
     * 
     * @param actual
     * @param expected
     * @return
     * @see ObjectUtils#isEquals(Object, Object)
     */
    public static boolean isEquals(String actual, String expected)
    {
        return ObjectUtils.isEquals(actual, expected);
    }

    /**
     * 4.如果字符串为null设置成"",不为空则返回本身 null string to empty string
     * 
     * <pre>
     * nullStrToEmpty(null) = &quot;&quot;;
     * nullStrToEmpty(&quot;&quot;) = &quot;&quot;;
     * nullStrToEmpty(&quot;aa&quot;) = &quot;aa&quot;;
     * </pre>
     * 
     * @param str
     * @return
     */
    public static String nullStrToEmpty(String str)
    {
        return (str == null ? "" : str);
    }
    
    /**
     * 
     * "null"或者null 替换为""
     *<pre>
     * nullStrToEmpty(null) = &quot;&quot;;
     * nullStrToEmpty("null") = &quot;&quot;;
     * nullStrToEmpty(&quot;&quot;) = &quot;&quot;;
     * nullStrToEmpty(&quot;aa&quot;) = &quot;aa&quot;;
     * </pre>
     * @param str
     * @return
     */
    public static String nullOrStrToEmpty(String str)
    {
//    	return (str == null ? "" : str);
    	
    	if (str == null) {
    		return "";
		}else if (str.equals("null")) {
			return "";
		}else {
			return str;
		}
    }

    /**
     * 5.首字母大写 capitalize first letter
     * 
     * <pre>
     * capitalizeFirstLetter(null)     =   null;
     * capitalizeFirstLetter("")       =   "";
     * capitalizeFirstLetter("2ab")    =   "2ab"
     * capitalizeFirstLetter("a")      =   "A"
     * capitalizeFirstLetter("ab")     =   "Ab"
     * capitalizeFirstLetter("Abc")    =   "Abc"
     * </pre>
     * 
     * @param str
     * @return
     */
    public static String capitalizeFirstLetter(String str)
    {
        if (isEmpty(str))
        {
            return str;
        }

        char c = str.charAt(0);
        return (!Character.isLetter(c) || Character.isUpperCase(c)) ? str : new StringBuilder(str.length()).append(Character.toUpperCase(c)).append(str.substring(1)).toString();
    }

    /**
     * 6.格式转换为utf-8 encoded in utf-8
     * 
     * 将URL进行转码 无默认值
     * 
     * <pre>
     * utf8Encode(null)        =   null
     * utf8Encode("")          =   "";
     * utf8Encode("aa")        =   "aa";
     * utf8Encode("啊啊啊啊")   = "%E5%95%8A%E5%95%8A%E5%95%8A%E5%95%8A";
     * </pre>
     * 
     * @param str
     * @return
     * @throws UnsupportedEncodingException
     *             if an error occurs
     */
    public static String utf8Encode(String str)
    {
        return utf8Encode(str, null);
    }

    /**
     * 6.格式转换为utf-8,如果转换出错，则默认返回defultReturn encoded in utf-8, if exception,
     * 将URL进行转码 有默认值 return defultReturn
     * 
     * @param str
     * @param defultReturn
     * @return
     */
    public static String utf8Encode(String str, String defultReturn)
    {
        try
        {
            return URLEncoder.encode(str, "UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            if (defultReturn != null)
            {
                return defultReturn;
            }
            else
            {
                return str;
            }
        }
    }

    /**
     * 7.得到html中的<a>标签内容
     * 
     * get innerHtml from href
     * 
     * <pre>
     * getHrefInnerHtml(null)                                  = ""
     * getHrefInnerHtml("")                                    = ""
     * getHrefInnerHtml("mp3")                                 = "mp3";
     * getHrefInnerHtml("&lt;a innerHtml&lt;/a&gt;")                    = "&lt;a innerHtml&lt;/a&gt;";
     * getHrefInnerHtml("&lt;a&gt;innerHtml&lt;/a&gt;")                    = "innerHtml";
     * getHrefInnerHtml("&lt;a&lt;a&gt;innerHtml&lt;/a&gt;")                    = "innerHtml";
     * getHrefInnerHtml("&lt;a href="baidu.com"&gt;innerHtml&lt;/a&gt;")               = "innerHtml";
     * getHrefInnerHtml("&lt;a href="baidu.com" title="baidu"&gt;innerHtml&lt;/a&gt;") = "innerHtml";
     * getHrefInnerHtml("   &lt;a&gt;innerHtml&lt;/a&gt;  ")                           = "innerHtml";
     * getHrefInnerHtml("&lt;a&gt;innerHtml&lt;/a&gt;&lt;/a&gt;")                      = "innerHtml";
     * getHrefInnerHtml("jack&lt;a&gt;innerHtml&lt;/a&gt;&lt;/a&gt;")                  = "innerHtml";
     * getHrefInnerHtml("&lt;a&gt;innerHtml1&lt;/a&gt;&lt;a&gt;innerHtml2&lt;/a&gt;")        = "innerHtml2";
     * </pre>
     * 
     * @param href
     * @return <ul>
     *         <li>if href is null, return ""</li>
     *         <li>if not match regx, return source</li>
     *         <li>return the last string that match regx</li>
     *         </ul>
     */
    public static String getHrefInnerHtml(String href)
    {
        if (isEmpty(href))
        {
            return "";
        }

        String hrefReg = ".*<[\\s]*a[\\s]*.*>(.+?)<[\\s]*/a[\\s]*>.*";
        Pattern hrefPattern = Pattern.compile(hrefReg, Pattern.CASE_INSENSITIVE);
        Matcher hrefMatcher = hrefPattern.matcher(href);
        if (hrefMatcher.matches())
        {
            return hrefMatcher.group(1);
        }
        return href;
    }

/**
 * 
     * 8.在html中的特殊
     * process special char in html
     * 
     * <pre>
     * htmlEscapeCharsToString(null) = null;
     * htmlEscapeCharsToString("") = "";
     * htmlEscapeCharsToString("mp3") = "mp3";
     * htmlEscapeCharsToString("mp3&lt;") = "mp3<";
     * htmlEscapeCharsToString("mp3&gt;") = "mp3\>";
     * htmlEscapeCharsToString("mp3&amp;mp4") = "mp3&mp4";
     * htmlEscapeCharsToString("mp3&quot;mp4") = "mp3\"mp4";
     * htmlEscapeCharsToString("mp3&lt;&gt;&amp;&quot;mp4") = "mp3\<\>&\"mp4";
     * </pre>
     * 
     * @param source
     * @return
     */
    public static String htmlEscapeCharsToString(String source)
    {
        return StringUtils.isEmpty(source) ? source : source.replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("&amp;", "&").replaceAll("&quot;", "\"");
    }

    /**
     * 9.从文字的字节码中将全角空格的字节码替换为半角空格的字节码 transform half width char to full width
     * char
     * 
     * <pre>
     * fullWidthToHalfWidth(null) = null;
     * fullWidthToHalfWidth("") = "";
     * fullWidthToHalfWidth(new String(new char[] {12288})) = " ";
     * fullWidthToHalfWidth("！＂＃＄％＆) = "!\"#$%&";
     * </pre>
     * 
     * @param s
     * @return
     */
    public static String fullWidthToHalfWidth(String s)
    {
        if (isEmpty(s))
        {
            return s;
        }

        char[] source = s.toCharArray();
        for (int i = 0; i < source.length; i++)
        {
            if (source[i] == 12288)
            {
                source[i] = ' ';
                // } else if (source[i] == 12290) {
                // source[i] = '.';
            }
            else if (source[i] >= 65281 && source[i] <= 65374)
            {
                source[i] = (char) (source[i] - 65248);
            }
            else
            {
                source[i] = source[i];
            }
        }
        return new String(source);
    }

    /**
     * 10.从文字的字节码中将半角空格的字节码替换为全角空格的字节码 transform full width char to half width
     * char
     * 
     * <pre>
     * halfWidthToFullWidth(null) = null;
     * halfWidthToFullWidth("") = "";
     * halfWidthToFullWidth(" ") = new String(new char[] {12288});
     * halfWidthToFullWidth("!\"#$%&) = "！＂＃＄％＆";
     * </pre>
     * 
     * @param s
     * @return
     */
    public static String halfWidthToFullWidth(String s)
    {
        if (isEmpty(s))
        {
            return s;
        }

        char[] source = s.toCharArray();
        for (int i = 0; i < source.length; i++)
        {
            if (source[i] == ' ')
            {
                source[i] = (char) 12288;
                // } else if (source[i] == '.') {
                // source[i] = (char)12290;
            }
            else if (source[i] >= 33 && source[i] <= 126)
            {
                source[i] = (char) (source[i] + 65248);
            }
            else
            {
                source[i] = source[i];
            }
        }
        return new String(source);
    }

    /**
     * 11.判断字符串是否为int
     * 
     * @param str
     * @return
     */
    public static boolean IsIntegral(String str)
    {
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }

    /**
     * 12.空格替换为;
     * 
     * @param str
     * @return
     * 
     */
    public static String spaceReplace(String str)
    {
        if (str.contains(" "))
            return str.replace(" ", ";");
        return str;
    }

    // -------------------------检测是否有emoji字符-----------------------------
    /**
     * 13.检测是否有emoji字符
     * 
     * @param source
     * @return 一旦含有就抛出
     */
    public static boolean containsEmoji(String source)
    {
        if (StringUtils.isBlank(source))
        {
            return false;
        }
        int len = source.length();
        for (int i = 0; i < len; i++)
        {
            char codePoint = source.charAt(i);

            if (isEmojiCharacter(codePoint))
            {
                // do nothing，判断到了这里表明，确认有表情字符
                return true;
            }
        }

        return false;
    }

    /**
     * 14.检测是否有emoji字符
     * 
     * @param codePoint
     * @return
     */
    private static boolean isEmojiCharacter(char codePoint)
    {
        return (codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA) || (codePoint == 0xD) || ((codePoint >= 0x20) && (codePoint <= 0xD7FF)) || ((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) || ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF));
    }

    /**
     * 15.过滤emoji 或者 其他非文字类型的字符
     * 
     * @param source
     * @return
     */
    public static String filterEmoji(String source)
    {
        if (source.trim().length() == 0)
        {
            return " ";
        }

        if (source.length() == 2)
        {
            source = " " + source;
        }

        if (!containsEmoji(source))
        {
            return source;// 如果不包含，直接返回
        }
        // 到这里铁定包含
        StringBuilder buf = null;

        int len = source.length();

        for (int i = 0; i < len; i++)
        {
            char codePoint = source.charAt(i);

            if (isEmojiCharacter(codePoint))
            {
                if (buf == null)
                {
                    buf = new StringBuilder(source.length());
                }

                buf.append(codePoint);
            }
            else
            {
            }
        }

        if (buf == null)
        {
            return source;// 如果没有找到 emoji表情，则返回源字符串
        }
        else
        {
            if (buf.length() == len)
            {// 这里的意义在于尽可能少的toString，因为会重新生成字符串
                buf = null;
                return source;
            }
            else
            {
                return buf.toString();
            }
        }
    }

    /**
     * 16.计算两个日期型的时间相差多少时间
     * 
     * @param startDate
     *            开始日期
     * @param endDate
     *            结束日期
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    public static String twoDateDistance(String startDate)
    {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try
        {
            Date now = df.parse(df.format(new Date()));
            Date date = df.parse(startDate);
            long l = now.getTime() - date.getTime();
            long day = l / (24 * 60 * 60 * 1000);// 天
            long hours = l / (60 * 60 * 1000);// 小时
            long minu = l / (60 * 1000);// 分
            // long s=l/1000;//秒
            long year = day / 365;// 年
            long month = day / 30;// 月
            if (year > 0)
            {
                return year + "年前";
            }
            else if (month > 0 && month < 12)
            {
                return month + "个月前";
            }
            else if (day > 0 && day < 30)
            {
                return day + "天前";
            }
            else if (hours > 0 && hours < 24)
            {
                return hours + "小时前";
            }
            else if (minu > 0 && minu < 60)
            {
                return minu + "分前";
            }
            else
            {
                return "刚刚发布";
            }
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        return startDate;
    }

    /**
     * 17.设置创建时间，今天之内显示时间，今天之前显示日期
     * 
     * @param created
     * @param dateTv
     */
    public static void setCreated(String created, TextView dateTv)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        if (!StringUtils.isEmpty(created))
        {
            try
            {
                Date date = sdf.parse(created);

                Date now = new Date();

                if (date.getDate() == now.getDate())
                {
                    dateTv.setText(created.substring(11, 16));
                }
                else
                {
                    dateTv.setText(created.substring(5, 10));
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * 18.将时间戳转换为时间
     * 
     * @param times
     * @return
     */
    @SuppressLint("UseValueOf")
    public static String getSimpleDate(long times)
    {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Long time = new Long(times);
        return format.format(time);
    }

    /**
     * 转换日期格式
     * 
     * @param created
     * @return yyyy-MM-dd HH:mm格式日期字符串
     * @TODO
     * @2015-5-22
     */
    public static String parseDate(String created)
    {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = null;
        try
        {
            date = format.parse(created);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        return format.format(date);
    }

    /**
     * 获取着色过的文字
     * 
     * @param color 颜色字符，如#fff
     * @param start 开始的字符位置
     * @param end 结束的字符位置
     * @author zhengjb
     * */
    public static SpannableStringBuilder getColoredText(String text, String color, int start, int end)
    {
        SpannableStringBuilder style = new SpannableStringBuilder(text);
        style.setSpan(new ForegroundColorSpan(Color.parseColor(color)), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return style;
    }
    
    /**
     * 获取着色过的文字
     * 
     * @param color 颜色字符，如#fff
     * @param start 开始的字符位置
     * @author zhengjb
     * */
    public static SpannableStringBuilder getColoredText(SpannableStringBuilder builder, String color, int start)
    {
        builder.setSpan(new ForegroundColorSpan(Color.parseColor(color)), start, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return builder;
    }
    
    /**
     * 获取设置粗体的文字
     * 
     * @param start 开始的字符位置
     * @param end 结束的字符位置
     * @author zhengjb
     * */
    public static SpannableStringBuilder getBoldText(SpannableStringBuilder builder, int start, int end)
    {
        builder.setSpan(new StyleSpan(Typeface.BOLD), start, end,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);  
        return builder;
    }

    /**
     * 获取着色过的文字
     * 
     * @param color 颜色字符，如#fff
     * @param start 开始的字符位置，默认结束位置为字符串尾部
     * @author zhengjb
     * */
    public static SpannableStringBuilder getColoredText(String text, String color, int start)
    {
        SpannableStringBuilder style = new SpannableStringBuilder(text);
        style.setSpan(new ForegroundColorSpan(Color.parseColor(color)), start, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return style;
    }
    
    /**
     * 获取着色过的文字
     * 
     * @param color 颜色字符，如#fff
     * @param start 开始的字符位置
     * @param end 结束的字符位置
     * @author zhengjb
     * */
    public static SpannableStringBuilder getColoredText(SpannableStringBuilder builder, String color, int start, int end)
    {
        builder.setSpan(new ForegroundColorSpan(Color.parseColor(color)), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return builder;
    }
    
    /** 如果非空，则给视图设置文字 */
    public static void setText(TextView textView, String text)
    {
        if (!isEmpty(text))
        {
            textView.setText(text);
        }else{
            textView.setText("");
        }
    }

    /***
     * 
     * 验证手机格式 
     * 
     * @author Administrator
     * @param mobiles
     * @return true 格式正确
     */
    public static boolean isMobileNO(String mobiles) {  
        /* 
                移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188 
                联通：130、131、132、152、155、156、185、186 
                电信：133、153、180、189、（1349卫通） 
                总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9 
        */  
        String telRegex = "[1][34578]\\d{9}$";//"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。  
        if (com.u1city.module.util.StringUtils.isEmpty(mobiles)) 
            return false;  
        else 
            return mobiles.matches(telRegex);  
    }
    
    /** 处理距离数字，合理显示距离
          *  @param distance 距离（单位为米） */
    public static String getDistanceText(double distance)
    {
        double dis = 0;
        String result = "";
        DecimalFormat df = new DecimalFormat("0.00");
        DecimalFormat df2 = new DecimalFormat("0");

        if (distance >= 1000)
        {
            dis = distance / 1000;
            result = "距您" + df.format(dis) + "公里";
        }
        else
        {
            dis = distance;
            result = "距您" + df2.format(dis) + "米以内";
        }

        return result;
    }
    
    /** 处理距离数字，合理显示距离
     *  @param preText 在距离前面的文字
     *  @param distance 距离（单位为米）
     *  */
    public static String getDistanceText(String preText, double distance)
    {
        double dis = 0;
        String result = "";
        DecimalFormat df = new DecimalFormat("0.00");
        DecimalFormat df2 = new DecimalFormat("0");

        if (distance >= 1000)
        {
            dis = distance / 1000;
            result = preText + df.format(dis) + "公里";
        }
        else
        {
            dis = distance;
            result = preText + df2.format(dis) + "米";
        }

        return result;
    }
    
    public static void setDistanceText(TextView textView, double distance, String city){
        String distanceText = "";

        if (distance <= 0)
        {
            if (!isEmpty(city))
            {
                distanceText = city;
            }
        }
        else
        {
            distanceText = getDistanceText(distance);
        }

        textView.setText(distanceText);
    }
    
    public static SpannableStringBuilder getDifferentSizeText(SpannableStringBuilder spannableStringBuilder, int differentSize, int start, int end){
        spannableStringBuilder.setSpan(new AbsoluteSizeSpan(differentSize), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableStringBuilder;
    }
    
    /** 先对原始文字进行非空判断，然后添加一段文字并设置到视图中
     * @param textView 填充数据的视图
     * @param rawText 原始文字
     * @param appendText 添加的文字
     * @param appendBefore 是否是添加在前面
     *  */
    public static void setText(TextView textView, String rawText, String appendText, boolean appendBefore){
        String text = "";
        
        if(!isEmpty(rawText)){
            text = rawText;
        }
        
        if(appendBefore){
            text = appendText + text;
        }else{
            text = text + appendText;
        }
        
        textView.setText(text);
    }
    
    /** 先对原始文字进行非空判断，然后添加一段文字并设置到视图中，添加文字在前面
     * @param textView 填充数据的视图
     * @param rawText 原始文字
     * @param appendText 添加的文字
     *  */
    public static void setText(TextView textView, String rawText, String appendText){
        setText(textView, rawText, appendText, true);
    }
    
    /**
     * 验证密码是否合格
     * @param password 密码
     * @return true合格，false不合格
     * */
    public static boolean isPwdVerified(String password){
        //当前密码格式验证
        Pattern pattern = Pattern.compile("[0-9a-z]{0,16}");
        Matcher nextMatcher = pattern.matcher(password.toLowerCase());
        boolean hasNextMatch = nextMatcher.matches();
        
        return hasNextMatch && password.length() >= 6 && password.length() <= 16;
    }
    
    /**
     * 判断是否有数字 字母 中文 （仅支持数字 字母 中文）
     */
    public static boolean filterChineseNumberLetter(String str)
    {
        Pattern pattern = Pattern.compile("^[A-Za-z0-9\u4e00-\u9fa5]+$");
        return pattern.matcher(str).matches();
    }
    
    /**
     * 添加图片大小设置
     * @param url 图片地址
     * @param size 图片大小（注意不要超过原有图片大小）
     * 
     * */
    public static String appendImageParams(String url, int size){
        //必须是10的整数倍
        size = size / 10 * 10;
        
        if(url.indexOf(".jpg") == -1){
            return url;
        }else{
            return url + "_" + size + "x" + size + ".jpg";
        }
    }
}
