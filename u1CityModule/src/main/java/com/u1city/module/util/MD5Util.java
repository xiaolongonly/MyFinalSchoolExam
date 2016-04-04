package com.u1city.module.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Util
{
    /**
     * MD5加密
     */
    public static String encrypt(String val)
    {
        StringBuffer sb = new StringBuffer(32);
        try
        {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] byteArray;

            byteArray = val.getBytes("gb2312");
            byte[] m = md5.digest(byteArray);
            for (int i = 0; i < m.length; i++)
            {
                int ch = m[i];
                sb.append(org.apache.commons.lang.StringUtils.leftPad(Integer.toHexString(ch & 0xff), 2, '0'));
            }
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }

        return sb.toString();
    }
    
    public static String getMessageDigest(byte[] buffer) {
        char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
        try {
            MessageDigest mdTemp = MessageDigest.getInstance("MD5");
            mdTemp.update(buffer);
            byte[] md = mdTemp.digest();
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            return null;
        }
    }
}
